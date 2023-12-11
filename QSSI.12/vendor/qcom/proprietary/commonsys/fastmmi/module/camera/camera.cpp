/*
* Copyright (c) 2014-2015 Qualcomm Technologies, Inc. All Rights Reserved.
* Qualcomm Technologies Proprietary and Confidential.
*
* Not a Contribution.
* Apache license notifications and license are retained
* for attribution purposes only.
*/

 /*
  * Copyright (C) 2008 The Android Open Source Project
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
#include <hardware/hardware.h>
#include <utils/Timers.h>
#include "mmi_module.h"
#include "view.h"
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/stat.h>    
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include "android/native_window.h"

#define MAX_CAM_PREVIEW_BUFFERS 5

typedef enum {
    CAM_PAD_NONE = 1,
    CAM_PAD_TO_2 = 2,
    CAM_PAD_TO_4 = 4,
    CAM_PAD_TO_WORD = CAM_PAD_TO_4,
    CAM_PAD_TO_8 = 8,
    CAM_PAD_TO_16 = 16,
    CAM_PAD_TO_32 = 32,
    CAM_PAD_TO_64 = 64,
    CAM_PAD_TO_128 = 128,
    CAM_PAD_TO_256 = 256,
    CAM_PAD_TO_512 = 512,
    CAM_PAD_TO_1K = 1024,
    CAM_PAD_TO_2K = 2048,
    CAM_PAD_TO_4K = 4096,
    CAM_PAD_TO_8K = 8192
} cam_pad_format_t;

#define PAD_TO_SIZE(size, padding) \
        ((size + (typeof(size))(padding - 1)) & \
        (typeof(size))(~(padding - 1)))

#define DRAW_H 1080
#define DRAW_W 720
#define PREVIEW_DEFAULT_WIDTH 1280
#define PREVIEW_DEFAULT_HEIGHT 960
#ifdef __LP64__
#define PREVIEW_ALIGN_WIDTH 1536
#define PREVIEW_ALIGN_HEIGHT 1024
#else
#define PREVIEW_ALIGN_WIDTH 1280
#define PREVIEW_ALIGN_HEIGHT 960
#endif
volatile bool pid_flag = true;
static draw_control_t g_cam_draw;
ANativeWindow_Buffer outBuffer;

static pthread_t get_camera_data_tid;
static pthread_t g_cam_preview_tid;
static int g_sock = -1;
static char cur_module_name[64];
typedef struct{
    int camera_id;         
    char msg[1024];
    int format;
    int frameNum;
}camera_msg;

camera_msg msg;


static uint8_t *g_pPreviewYUV420;
static uint8_t *g_pPreview_Y;
static uint8_t *g_pPreview_UV;
static uint8_t *g_pPreviewRGB8888;
static uint8_t *g_pRotate90_tmp;

static int mp0len = 0;
static int mp1len = 0;
int32_t cam_idx = -1;
static unsigned char *cam_buf[MAX_CAM_PREVIEW_BUFFERS];

static void allocate_buffers(int width, int height) {
    int preview_pixels = height * width;
    int stride, scanline;

    stride = PAD_TO_SIZE(width, CAM_PAD_TO_16);
    scanline = PAD_TO_SIZE(height, CAM_PAD_TO_2);
    mp0len = stride * scanline;
    ALOGI("fct camera:stride:%d,scanline:%d mp0len:%d", stride, scanline, mp0len);
    stride = PAD_TO_SIZE(width, CAM_PAD_TO_16);
    scanline = PAD_TO_SIZE(height / 2, CAM_PAD_TO_2);
    mp1len = stride * scanline;
    ALOGI("fct camera:stride:%d,scanline:%d mp1len:%d", stride, scanline, mp1len);
#ifdef __LP64__
    g_pPreviewYUV420 =  new uint8_t[PREVIEW_ALIGN_WIDTH * PREVIEW_ALIGN_HEIGHT *3/2];
#else
    g_pPreviewYUV420 = new uint8_t[mp0len + mp1len];
#endif
    return;
}

static int mmi_cam_create_buffers() {
    int width = PREVIEW_DEFAULT_WIDTH;
    int height = PREVIEW_DEFAULT_HEIGHT;
    int preview_pixels = height * width;
    allocate_buffers(width, height);
    return SUCCESS;
}

void camera_signal_handler(int signal) {
    ALOGI("server:start run camera_signal_handler");
    pthread_exit(NULL);
}

static void *cam_start_preview(void *m) {
    char fname[256];
    signal(SIGUSR1, camera_signal_handler);
    int buf_index = 0;
#ifdef __LP64__
    int buf_size = mp0len + mp1len;//PREVIEW_ALIGN_WIDTH * PREVIEW_ALIGN_HEIGHT *3/2;
    int preview_buf_size = PREVIEW_DEFAULT_WIDTH * PREVIEW_DEFAULT_HEIGHT * 4;
    uint8_t *preview_buf = (uint8_t *)malloc(preview_buf_size);
    int x,y;
    if (!preview_buf) {
    	ALOGE("[%d]%s(): preview_buf malloc fail!!! \n", __LINE__, __func__);
    	return NULL;
    }
    bzero(preview_buf, preview_buf_size);
#else
    int buf_size = mp0len + mp1len;
#endif
    int rotation = 0;
    bool notice_mmi = false;
    int ret = -1;
    int index = 50;
    int n = 0;
    char buf_zero[32] = {0};
    ALOGI("server:preview thread started mp0len(%d) + mp1len(%d) = buf_size(%d)",mp0len,mp1len,buf_size);
    int preview_width = PREVIEW_DEFAULT_WIDTH;
    int preview_height = PREVIEW_DEFAULT_HEIGHT;
    create_surface(&g_cam_draw); 
    while(1 && pid_flag) {
        uint8_t *dst_data = NULL;
        uint8_t *dump_data = NULL;
        uint8_t *src_data = NULL;
        ALOGI("server:%s, displaying buffer idx: %d", __func__, buf_index);
        index++;
        ALOGI("server:camera run memcpy");
        g_pPreviewYUV420=cam_buf[buf_index];
        //skip all the 0 "green" picture data...
        if (!memcmp(g_pPreviewYUV420, buf_zero, sizeof(buf_zero))) {
            usleep(50 * 1000);
            continue;
        }
        //skip some unstable data at the beginning
        if (n < 10) {
            n++;
            usleep(50 * 1000);
            continue;
        }

#ifdef __LP64__
        bzero(preview_buf, preview_buf_size);
        //copy Y
        for (y = 0; y < PREVIEW_DEFAULT_HEIGHT; y++) {
        	memcpy(preview_buf + y*PREVIEW_DEFAULT_WIDTH, g_pPreviewYUV420+y*PREVIEW_ALIGN_WIDTH, PREVIEW_DEFAULT_WIDTH);
        }
        //copy UV
		for (y = 0; y < PREVIEW_DEFAULT_HEIGHT / 2; y++) {
			memcpy(preview_buf + PREVIEW_DEFAULT_HEIGHT * PREVIEW_DEFAULT_WIDTH + y * PREVIEW_DEFAULT_WIDTH,
				   g_pPreviewYUV420 + PREVIEW_ALIGN_WIDTH * PREVIEW_ALIGN_HEIGHT + y * PREVIEW_ALIGN_WIDTH, PREVIEW_DEFAULT_WIDTH);
		}
        g_pPreviewYUV420 = preview_buf;
#endif
        while(index --) continue;
#if 0
        dump_data = cam_buf[buf_index];
        char fname[256];
        snprintf(fname, sizeof(fname), "/data/vendor/camera/cam_buf_%d.yuv", n++);

        FILE* fd = fopen(fname, "wb");
        fwrite(dump_data, 1280 * 960 * 4, 1, fd);
        fclose(fd);
#endif
#if 0
        dst_data = g_pPreviewYUV420;
        src_data = cam_buf[buf_index];
        for(int i=0;i<960;i++){
            memcpy(dst_data,src_data,1280);
            src_data += 1280;
            dst_data += 1280;
        }
     //   src_data = src_data + 0x18000;

        for(int i=0;i<960/2;i++){
            memcpy(dst_data,src_data,1280);
            src_data += 1280;
            dst_data += 1280;
        }
#endif
        ALOGI("server:start to draw preview");
        sp < ANativeWindow > anw(g_cam_draw.surface);
	//native_window_set_buffers_transform(anw.get(), get_buffer_transform_mask(270, false));
        ANativeWindow_setBuffersTransform(anw.get(),ANATIVEWINDOW_TRANSFORM_ROTATE_270);
        ANativeWindow_setBuffersGeometry(anw.get(), \
                                         PAD_TO_SIZE(preview_width, CAM_PAD_TO_16), /*stride*/ \
                                         PAD_TO_SIZE(preview_height, CAM_PAD_TO_2), /*scanline */ \
                                         0x11); /*pixel format*/
        ANativeWindow_lock(anw.get(), &outBuffer, NULL);

        memcpy(outBuffer.bits, g_pPreviewYUV420, buf_size);
        ANativeWindow_unlockAndPost(anw.get());
        buf_index = (buf_index + 1) % MAX_CAM_PREVIEW_BUFFERS;
        usleep(50 * 1000);
        if (!notice_mmi) {
            char buf[SIZE_512] = { 0 };
            snprintf(buf, sizeof(buf), " ");
            mmi_module_t * module = (mmi_module_t *)m;
            if (module)
                module->cb_print(cur_module_name, SUBCMD_MMI, buf, strlen(buf), PRINT);
            notice_mmi = true;
        }
    }
#ifdef __LP64__
    free(preview_buf);
#endif
    ALOGI("server:camera preview exit loop");
    return NULL;
}

static int cam_prepare_preview(void *mod) {
    ALOGI("%s\n", __FUNCTION__);
    int fd = -1;
    int retval = -1;
    void *map;
    int buf_size = mp0len + mp1len;
    int preview_buf_size = PREVIEW_DEFAULT_WIDTH * PREVIEW_DEFAULT_HEIGHT * 4;
    int mmap_size = preview_buf_size * MAX_CAM_PREVIEW_BUFFERS;
    ALOGE("server:prepare preview mp0len:%d, mp1len:%d",mp0len ,mp1len);

    fd = open(TEMP_MMAP_FILE, O_CREAT | O_RDWR | O_TRUNC, 00777);
    if(fd < 0) {
        ALOGE("server:Could not open [%s]:%s",TEMP_MMAP_FILE,strerror(errno));
        return -1;
    }
    lseek(fd, mmap_size - 1, SEEK_SET);
    write(fd, "", 1);

    map = mmap(NULL, mmap_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if(map == MAP_FAILED) {
        ALOGE("server:Could not mmap %s:  %s", TEMP_MMAP_FILE, strerror(errno));
        close(fd);
        return -1;
    }

    ALOGI("server:%s, mapped buffer @%p, size: %d", __func__, map, mmap_size);

    for(int i = 0; i < MAX_CAM_PREVIEW_BUFFERS; i++) {
        cam_buf[i] = (unsigned char *) map + preview_buf_size * i;
    }

    ALOGI("server:prepare preview %s", __FUNCTION__);
    close(fd);

    retval = pthread_create(&g_cam_preview_tid, NULL, cam_start_preview, mod);
    if(retval < 0)
        return -1;

    return 0;
}

void init_draw()
{
    strcpy(g_cam_draw.name, "CAM");
    g_cam_draw.layer = 0x7FFFFFFF;
    g_cam_draw.surface_h = DRAW_H;
    g_cam_draw.surface_w = DRAW_W;
}

static int connect_camera_socket() {
    g_sock = connect_server("/dev/socket/mmi_camera");
    if(g_sock < 0) {
        ALOGE("client:camera connect to server(/dev/socket/mmi_camera) fail");
        return -1;
    }
    return 0;
}

pid_t getProcessPidByName(const char *proc_name)
{
    FILE *fp;
    char buf[255];
    char cmd[200] = {'\0'};
    pid_t pid = -1;
    sprintf(cmd, "pidof %s", proc_name);
    if((fp = popen(cmd, "r")) != NULL)
    {
        if(fgets(buf, 255, fp) != NULL)
        {
            pid = atoi(buf);
        }
    }
    printf("pid = %d \n", pid);
    pclose(fp);
    return pid;
}

static void stop_get_data()
{
    int pid=-1;
    pid=getProcessPidByName("camx-hal3-test");
    kill_proc(pid);
    system("rm -rf /dev/socket/mmi_camera");
}

static void *get_camera_data_thread(void *mod)
{
    
    mmi_module_t *module = (mmi_module_t *) mod;
    int recv_size = -1;
    int ret= -1;
    char file_name[128];
    int cnt=0;
    signal(SIGUSR1, signal_handler);
    char print_buf[256]={0};
    memset(&msg, 0, sizeof(camera_msg));
    strlcpy(msg.msg, "camera start", sizeof(msg.msg));
    //msg.type=5;
    msg.camera_id=cam_idx;
    ret = write(g_sock, &msg, sizeof(camera_msg));
    if(ret < 0) {
        ALOGE("client:camera write msg:camera start fail, error=%s", strerror(errno));
        return NULL;
    } 
    ALOGI("client:camera write msg:camera start ok");
    usleep(1000*200);
    cam_prepare_preview(mod);
    return NULL;
}

static int32_t module_run_mmi(const mmi_module_t * module, unordered_map < string, string > &params) {
    ALOGI("%s start ", __FUNCTION__);
    int ret = FAILED;
    pid_flag = true;
    strlcpy(cur_module_name, params[KEY_MODULE_NAME].c_str(), sizeof(cur_module_name));
    ret = pthread_create(&get_camera_data_tid, NULL, get_camera_data_thread, (void *) module);
    if(ret < 0)
    {
        ALOGE("can not create get_key_data_thread");
        return ret;
    }
    pthread_join(get_camera_data_tid,NULL);
    return SUCCESS;
}

static int32_t module_run_pcba(const mmi_module_t * module, unordered_map < string, string > &params) {
    ALOGI("%s start", __FUNCTION__);
    int ret = FAILED;
    return ret;
}

static int32_t system_prerun() {

    int count = 100;
    int ret;
#ifdef __LP64__
    system("/vendor/bin/camx-hal3-test64 &");
#else
    system("/vendor/bin/camx-hal3-test32 &");
#endif
    while(count--) {
        if(access("/dev/socket/mmi_camera",0)==0)
            break;
        else
            usleep(1000*200);
    }
    ret = connect_camera_socket();
    if(ret < 0) {
        ALOGE("[%s]:connect camx socket failed ", __FUNCTION__);
        return FAILED;
    }
    return SUCCESS;
}


static int32_t module_init(const mmi_module_t * module, unordered_map < string, string > &params) {
    ALOGI("%s start camera", __FUNCTION__);
    init_draw();
    init_surface(&g_cam_draw);
    system("stop vendor.camera-provider-2-4");
#ifdef __LP64__
    ALOGI("%s: Is 64bit system.", __FUNCTION__);
#else
    ALOGI("%s: Is 32bit system.", __FUNCTION__);
#endif
    system_prerun();
    return SUCCESS;
}

static int32_t module_deinit(const mmi_module_t * module) {
    ALOGI("%s start.", __FUNCTION__);
    if(module == NULL) {
        ALOGE("%s NULL point  received ", __FUNCTION__);
        return FAILED;
    }
    stop_get_data();
    return SUCCESS;
}

static int32_t module_stop(const mmi_module_t * module) {
    ALOGI("%s start.", __FUNCTION__);
    if(module == NULL) {
        ALOGE("%s NULL point  received ", __FUNCTION__);
        return FAILED;
    }
    
    memset(&msg, 0, sizeof(camera_msg));
    strlcpy(msg.msg, "camera stop", sizeof(msg.msg));
    write(g_sock, &msg, sizeof(camera_msg));
    remove_surface(&g_cam_draw);
    //kill_thread(g_cam_preview_tid);
    pid_flag = false;
    usleep(100 * 1000);
    return SUCCESS;
}

/**
* Before call Run function, caller should call module_init first to initialize the module.
* the "cmd" passd in MUST be defined in cmd_list ,mmi_agent will validate the cmd before run.
*
*/
static int32_t module_run(const mmi_module_t * module, const char *cmd, unordered_map < string, string > &params) {
    int ret = FAILED;
    int socket_check = 0;
    int32_t count = 100;

    if(!module || !cmd) {
        ALOGE("%s NULL point received ", __FUNCTION__);
        return FAILED;
    }
    ALOGI("%s start.command : %s", __FUNCTION__, cmd);
#ifdef __LP64__
    ALOGI("%s: Is 64bit system.", __FUNCTION__);
    //system_prerun();
#else
    ALOGI("%s: Is 32bit system.", __FUNCTION__);
#endif
    if(mmi_cam_create_buffers() != SUCCESS) {
        ALOGE("server:fail to create buffers");
        return FAILED;
    }

    const char *str_cam_idx = params["cam-idx"].c_str();
    if(str_cam_idx != NULL) {
        cam_idx = atoi(str_cam_idx);
        cam_idx = (cam_idx < 0) ? 0 : (cam_idx);
    }

    if(!strcmp(cmd, SUBCMD_MMI))
        ret = module_run_mmi(module, params);
    else if(!strcmp(cmd, SUBCMD_PCBA))
        ret = module_run_pcba(module, params);
    else {
        ALOGE("%s Invalid command: %s  received ", __FUNCTION__, cmd);
        ret = FAILED;
    }
    return ret;
}

/**
* Methods must be implemented by module.
*/
static struct mmi_module_methods_t module_methods = {
    .module_init = module_init,
    .module_deinit = module_deinit,
    .module_run = module_run,
    .module_stop = module_stop,
};

/**
* Every mmi module must have a data structure named MMI_MODULE_INFO_SYM
* and the fields of this data structure must be initialize in strictly sequence as definition,
* please don't change the sequence as g++ not supported in CPP file.
*/
mmi_module_t MMI_MODULE_INFO_SYM = {
    .version_major = 1,
    .version_minor = 0,
    .name = "Camera",
    .author = "Qualcomm Technologies, Inc.",
    .methods = &module_methods,
    .module_handle = NULL,
    .supported_cmd_list = NULL,
    .supported_cmd_list_size = 0,
    .cb_print = NULL, /**it is initialized by mmi agent*/
    .run_pid = -1,
};
