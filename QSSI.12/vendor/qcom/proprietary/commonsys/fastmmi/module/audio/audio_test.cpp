#include <errno.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <signal.h>
#include <syslog.h>
#include <sys/time.h>
#include <getopt.h>
#include <semaphore.h>
#include <string.h>
#include <stdlib.h>
#include <utils/Log.h>
#include <android/log.h>
#include <cutils/log.h>
#include <cutils/klog.h>
#include <tinyalsa/asoundlib.h>
#include <stdio.h>

#define FTM_FILE      "/system_ext/etc/mmi/ftm_test"

struct audio_path {
    const char *control;
    const char *value;
};

static char *audio_contrl;
static char *audio_value;

struct pcm_config_data {
    struct pcm *pcm_dev;
    int size;
};
static struct pcm_config_data cap_pcm;
static struct pcm_config_data play_pcm;
static bool thread_run = false;

static int isnumber(const char *str) {
    char *end;

    if (str == NULL || strlen(str) == 0)
        return 0;

    strtol(str, &end, 0);
    return strlen(end) == 0;
}

static int tinymix_set_value(struct mixer *mixer, const char *control,
                          const char *values, unsigned int num_values)
{
    struct mixer_ctl *ctl;
    enum mixer_ctl_type type;
    unsigned int num_ctl_values;
    unsigned int i;

    if (isnumber(control))
        ctl = mixer_get_ctl(mixer, atoi(control));
    else
        ctl = mixer_get_ctl_by_name(mixer, control);

    if (!ctl) {
        printf("Invalid mixer control: %s\n", control);
        return ENOENT;
    }

    type = mixer_ctl_get_type(ctl);
    num_ctl_values = mixer_ctl_get_num_values(ctl);

    if (isnumber(values)) {
        if (num_values == 1) {
            /* Set all values the same */
            int value = atoi(values);

            for (i = 0; i < num_ctl_values; i++) {
                if (mixer_ctl_set_value(ctl, i, value)) {
                    printf("Error: invalid value\n");
                    return EINVAL;
                }
            }
        }
    } else {
        if (type == MIXER_CTL_TYPE_ENUM) {
            if (num_values != 1) {
                printf("Enclose strings in quotes and try again\n");
                return EINVAL;
            }
            if (mixer_ctl_set_enum_by_string(ctl, values)) {
                printf("Error: invalid enum value\n");
                return EINVAL;
            }
        } else {
            printf("Error: only enum types can be set with strings\n");
            return EINVAL;
        }
    }

    return 0;
}

static int config_path_func(int card,const char *control,const char *values)
{
    struct mixer *mixer_dev;
    int ret = 0;

    mixer_dev = mixer_open(card);
    if (!mixer_dev) {
        printf("Failed to open mixer\n");
        return -1;
    }
    ret = tinymix_set_value(mixer_dev,control,values,1);
    if(ret != 0){
        printf("set control:%s, value:%s, failed\n",control,values);
        //return -1;
    }
    mixer_close(mixer_dev);
    return 0;
}

static int set_play_param(unsigned int card,unsigned int device, unsigned int channels,
                 unsigned int rate, enum pcm_format format, unsigned int period_size,
                 unsigned int period_count)
{
    struct pcm_config config;
    char *buffer;
    unsigned int size, read_sz;
    int num_read;
    struct pcm *pcm_dev;

    memset(&config, 0, sizeof(config));
    config.channels = channels;
    config.rate = rate;
    config.period_size = period_size;
    config.period_count = period_count;
    config.format = format;
    config.start_threshold = 0;
    config.stop_threshold = 0;
    config.silence_threshold = 0;

    pcm_dev = pcm_open(0, device, PCM_OUT, &config);
    if (!pcm_dev || !pcm_is_ready(pcm_dev)) {
        printf("card %d Unable to open PCM device %u (%s)\n",card,
                device, pcm_get_error(pcm_dev));
        return -1;
    }
    play_pcm.pcm_dev = pcm_dev;
    play_pcm.size = pcm_frames_to_bytes(pcm_dev, pcm_get_buffer_size(pcm_dev));
    printf("play buf size %d\n",play_pcm.size);
    return 0;
}

static int set_capture_param(unsigned int card,unsigned int device,unsigned int channels,
        unsigned int rate,enum pcm_format format, unsigned int period_size,unsigned int period_count)
{
    struct pcm_config config;
    unsigned int size;
    struct pcm *pcm_dev;

    memset(&config, 0, sizeof(config));
    config.channels = channels;
    config.rate = rate;
    config.period_size = period_size;
    config.period_count = period_count;
    config.format = format;
    config.start_threshold = 0;
    config.stop_threshold = 0;
    config.silence_threshold = 0;

    pcm_dev = pcm_open(card, device, PCM_IN, &config);
    if (!pcm_dev || !pcm_is_ready(pcm_dev)) {
        printf("Unable to open PCM device (%s)\n",
                pcm_get_error(pcm_dev));
        return 0;
    }
    cap_pcm.pcm_dev = pcm_dev;
    cap_pcm.size = pcm_frames_to_bytes(pcm_dev, pcm_get_buffer_size(pcm_dev));
    printf("capture buf size %d\n",cap_pcm.size);
    printf("Capturing sample: %u ch, %u hz, %u bit\n", channels, rate,
           pcm_format_to_bits(format));

    return 0;
}

static int dump_raw_data(char *buffer)
{    
    return 0;
}

static void *pcm_loopback_thread(void *arg)
{
    char *buffer;
    unsigned int size;

    if(!cap_pcm.pcm_dev){
        printf("capture pcm dev is NULL\n");
        return NULL;
    }
    if(!play_pcm.pcm_dev){
        printf("play pcm dev is NULL\n");
        return NULL;
    }
    size = cap_pcm.size > play_pcm.size? cap_pcm.size : play_pcm.size;
    printf("buffer size %d\n",size);
    buffer = (char *)malloc(size);
    if(!buffer){
        printf("malloc buf failed,size %d\n",size);
        return NULL;
    }
    while(thread_run){
        if(pcm_read(cap_pcm.pcm_dev, buffer, cap_pcm.size)){
            printf("Error capture\n");
            break;
        }
        if (pcm_write(play_pcm.pcm_dev, buffer, play_pcm.size)) {
            printf("Error playing sample\n");
            break;
        }
    }
    free(buffer);
    return NULL;
}

int main(int argc, char *argv[])
{
    int ret = 0;
    int wait_time = 0;
    unsigned int card = 0;
    unsigned int cap_device = 0;  //MultiMedia1
    unsigned int play_device = 1;  //MultiMedia2
    unsigned int channels = 2;
    unsigned int rate = 44100;
    unsigned int bits = 16;
    unsigned int frames;
    unsigned int period_size = 1024;
    unsigned int period_count = 4;
    unsigned int cap_time = 0;
    enum pcm_format format = PCM_FORMAT_S16_LE;

    char line_text[64] = {0};
    char *q;
    FILE *fp = NULL;
    int  i,j=0;
    bool tc_lock, tc_enable = false;

    pthread_t pcm_lb_thread;

    memset(&cap_pcm,0,sizeof(struct pcm_config_data));
    memset(&play_pcm,0,sizeof(struct pcm_config_data));
    play_pcm.pcm_dev = NULL;
    cap_pcm.pcm_dev = NULL;
    if(argc < 5) {
        printf("Error !\nusage: ./test ftm-file tc-n enable/disable time\n");
        return -1;
    }
    thread_run = true;
 
    fp=fopen(argv[1], "r");
    if(fp == NULL) {
        return -1;
    }
    audio_contrl = (char *)malloc(64);
    audio_value = (char *)malloc(64);
    while(fgets(line_text, 64, fp)!=NULL) {
        memset(audio_contrl, 0, 64);
        memset(audio_value, 0, 64);
        //printf("ctl:%ssize:%dline:%d\n", line_text, strlen(line_text), __LINE__);
        if(!(strlen(line_text) -1)) continue;
        if(strstr(line_text, "#") != NULL) continue;

        if(strstr(line_text, argv[2]) != NULL && !tc_lock){
            printf("tc select: %s\n", argv[1]);
            tc_lock = true;
            continue;

        }
        if(strstr(line_text, argv[3]) != NULL && !tc_enable && tc_lock){
            //printf("enable value: %sline: %d\n", line_text,  __LINE__);
            tc_enable = true;
            continue;
        }

        if(tc_enable && tc_lock){
            if((q = strtok(line_text, ":")) != NULL) {
                //printf("ctrl name:%s\n", q);
                if(!strlen(q)) continue;
                memcpy(audio_contrl, q, strlen(q));
            }
            if((q = strtok(NULL, ":")) != NULL) {
                //printf("ctrl value:%s: size: %d\n", q, strlen(q));
                memcpy(audio_value, q, strlen(q)-1);
                printf("ctrl name:%s; ctrl value:%s\n", audio_contrl, audio_value);
                ret = config_path_func(card, audio_contrl, audio_value);
                if(ret != 0){
                    return 0;
                }
            } else {
                break;
            }
           
            i++;
        }
    }
    fclose(fp);
    printf("Set CTRL CONFIG Complete\n");
    if(!strcmp(argv[3], "disable"))
        goto CLOSE;

    set_capture_param(card,cap_device,channels,rate,format,period_size,period_count);
    set_play_param(card,play_device,channels,rate,format,period_size,period_count);

    ret = pthread_create(&pcm_lb_thread,NULL,pcm_loopback_thread,NULL);
    if(ret != 0){
        printf("create thread failed\n");
    }
    printf("create thread success\n");
    wait_time = atoi(argv[4]);
    sleep(wait_time);

CLOSE:
    thread_run = false;
    pthread_join(pcm_lb_thread, NULL);
    if(play_pcm.pcm_dev)
        pcm_close(play_pcm.pcm_dev);
    if(cap_pcm.pcm_dev)
        pcm_close(cap_pcm.pcm_dev);
    return 0;
}

