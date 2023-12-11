/*
 * Copyright (c) 2013-2017, Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

#include <sys/statfs.h>
#include "utils.h"
#include "config.h"
#include "input.h"
#include "mmi.h"
#include "module.h"
#include "controller.h"
#include "lang.h"
#ifdef ANDROID
#include "draw.h"
#endif

#include "mmi_cfg.h"
#include "interface.h"
#include "ate_rtest.h"
#include "Test.h"
using namespace android;

static const char *boot_mode_string[] = {
    "normal",
    "ffbm-00",
    "ffbm-01",
    "ffbm-02"
};

static const char *test_mode_string[] = {
    "none",
    "pcba",
    "ui",
};

/**global export var*/
char g_res_file[PATH_MAX] = { 0 };
static int reset_sim_num = 0;
static int lcd_num = 0;
static int Pmi_status = 1;
int w_ce_ds_ss = 0;
char _version[256]={0};
struct Pmi_stat{
    char * name;
    char * result;
};
/*
 * This is the layout pool,module could find
 * it's own layout via "layout" param
 * Key:    module name
 * Value:  layout
 */
unordered_map < string, layout * >g_layout_map;

/*
 * used to store all modules
 * key:module name
 */
unordered_map < string, module_info * >g_modules_map;
/*
 * used to store diag,debug module
 */
unordered_map < string, module_info * >g_controller_map;

/*
 * g_ordered_modules is the same thing as g_modules,
 * the different is sorted or not
 */
static list < module_info * >g_ordered_modules;

/*
 * g_nodup_modules: it is a duplicate hast_set from g_ordered_modules with group the module with
 * the same lib_name.
 * Key: lib_name
 * Value: module_info
 */
static unordered_map < string, module_info * >g_nodup_modules_map;

/*
 * used to store modules and it's sock id
 * key:module name
 */
unordered_map < string, int >g_sock_client_map;

sem_t g_data_print_sem;
sem_t g_result_sem;

/* used for clients that launched successfully;
 * Its content is part of g_modules
 */
static list < module_info * >g_clients;

/* home screen list; used for switch main screen by scroll
 */
static list < layout * >g_home_screens;

static pthread_t g_draw_tid;
static pthread_t g_accept_tid;
static pthread_t g_waiting_event_tid;
static pthread_t g_msg_handle_tid;
//#ifdef ATE_TEST_OK
static pthread_t ate_test_tid;
//#endif
static sem_t g_sem_exit;
static sem_t g_sem_accept_ready;
static int g_max_fd = -1;

static msg_queue_t g_msg_queue;
static sem_t g_msg_sem;

static layout *g_cur_layout = NULL;
static pthread_mutex_t g_cur_layout_mutex;
static module_info *g_main_module = NULL;
static module_mode_t g_test_mode = TEST_MODE_NONE;
static int shipmode_status = -1;
bool res_tab = false;
bool is_factory_tab = false;

int uart_fd = -1;
bool is_main_screen() {
    return g_cur_layout == get_main_layout();
}

layout *get_layout(string layout_name) {
    /*Get the layout */
    return g_layout_map[layout_name];
}

layout *get_layout(module_info * mod) {
    if(mod == NULL) {
        MMI_ALOGE("Invalid parameter, Not main screen OR Null point\n");
        return NULL;
    }

    /*Get the layout */
    return g_layout_map[mod->config_list[KEY_LAYOUT]];
}

layout *get_main_layout() {
    /*Get the layout */
    return get_layout(get_main_module());
}

module_info *get_main_module() {
    return g_main_module;
}

void set_main_module(module_info * mod) {
    g_main_module = mod;
}

layout *acquire_cur_layout() {
    pthread_mutex_lock(&g_cur_layout_mutex);
    return g_cur_layout;
}

void release_cur_layout() {
    pthread_mutex_unlock(&g_cur_layout_mutex);
}

layout *switch_cur_layout_locked(layout * lay, module_info * mod) {

    acquire_cur_layout();
    if(NULL != lay) {
        MMI_ALOGI("switch layout to:'%s'\n", lay->get_layout_path());
    }
    if(lay != NULL) {
        g_cur_layout = lay;
        g_cur_layout->module = mod;
    }
    release_cur_layout();
    return g_cur_layout;
}

static bool is_in_home_screen(layout * lay) {
    list < layout * >::iterator iter;

    for(iter = g_home_screens.begin(); iter != g_home_screens.end(); iter++) {
        layout *laytarget = (layout *) (*iter);

        if(lay == laytarget)
            return true;
    }

    return false;
}

void register_home_screen(layout * lay) {

    if(!is_in_home_screen(lay))
        g_home_screens.push_back(lay);

    MMI_ALOGI("home screen size: %d", g_home_screens.size());
}

bool is_cur_home_screen() {
    return is_in_home_screen(g_cur_layout);
}

void switch_home_screen(bool is_right) {

    layout *target_lay = NULL;

    list < layout * >::iterator iter;

    for(iter = g_home_screens.begin(); iter != g_home_screens.end(); iter++) {
        layout *lay = (layout *) (*iter);

        if(lay == g_cur_layout) {
            target_lay = lay;

            if(is_right) {
                if((++iter) != g_home_screens.end()) {
                    target_lay = (layout *) (*iter);
                }
            } else {
                if(iter != g_home_screens.begin()) {
                    iter--;
                    target_lay = (layout *) (*iter);
                }
            }
            break;
        }
    }

    if(target_lay != NULL) {
        MMI_ALOGI("home target layout: %s", target_lay->get_layout_path());
        switch_cur_layout_locked(target_lay, get_main_module());
        invalidate();
    } else {
        MMI_ALOGW("Not find home screen layout");
    }
}

void set_boot_mode(boot_mode_type mode) {
    int fd;
    const char *dev = NULL;

    module_info *mod = get_main_module();

    if(mod == NULL || mod->config_list["misc_dev"].empty()) {
        MMI_ALOGE("No main module error");
        return;
    }

    dev = mod->config_list["misc_dev"].c_str();
    fd = open(dev, O_WRONLY);
    if(fd < 0) {
        MMI_ALOGE("misc:'%s' open fail, error=%s", MMI_STR(dev), strerror(errno));
    } else {
        if(write(fd, boot_mode_string[mode], strlen(boot_mode_string[mode])) != strlen(boot_mode_string[mode])) {
            MMI_ALOGE("misc:'%s' write fail, error=%s", MMI_STR(dev), strerror(errno));
        } else {
            MMI_ALOGI("Write '%s' to misc:'%s'", boot_mode_string[mode], MMI_STR(dev));
        }
        fsync(fd);
        close(fd);
    }

    /**Add more for compatible new policy*/
    if(mode == BOOT_MODE_NORMAL)
        property_set("vendor.sys.boot_mode", "normal");
    else
        property_set("vendor.sys.boot_mode", "ffbm");
}

/**Flush result file*/
void flush_result() {
    char tmp[SIZE_1K] = { 0 };
    char pmi_tmp[SIZE_1K] = {0};
    int fp = -1;
	char sim_buf[256]={0};
    if(g_main_module == NULL) {
        MMI_ALOGE("No main layout\n");
        return;
    }

    /*Get the layout */
    layout *lay = get_main_layout();

    if(lay == NULL || lay->m_listview == NULL) {
        MMI_ALOGE("Not find main layout\n");
        return;
    }
    list < item_t * >*item_list = lay->m_listview->get_items();

    fp = open(g_res_file, O_RDWR|O_CREAT|O_SYNC|O_TRUNC,0666);
    if(fp < 0) {
        MMI_ALOGE("file(%s) open fail, error=%s\n", g_res_file, strerror(errno));
        return;
    }

    MMI_ALOGI("start to update test result to '%s'", g_res_file);
    if(Pmi_status == 0){
        Pmi_stat pmic_status = {"PMI_STATAS","no_exist"};
        MMI_ALOGI("start to update test result to '%s'", g_res_file);
        snprintf(pmi_tmp, sizeof(pmi_tmp), "\n[%s]\nResult = %s\n", pmic_status.name, pmic_status.result);
        write(fp,pmi_tmp,strlen(pmi_tmp));
    }else{
        Pmi_stat pmic_status = {"PMI_STATAS","exist"};
        MMI_ALOGI("start to update test result to '%s'", g_res_file);
        snprintf(pmi_tmp, sizeof(pmi_tmp), "\n[%s]\nResult = %s\n", pmic_status.name, pmic_status.result);
        write(fp,pmi_tmp,strlen(pmi_tmp));
    }
    if(reset_sim_num == 1){
        strcpy(sim_buf,"\n[SIM_TYPE]\nResult = ssss\n");
        write(fp,sim_buf,strlen(sim_buf));
    }else if(reset_sim_num == 2){
        strcpy(sim_buf,"\n[SIM_TYPE]\nResult = dsds\n");
        write(fp,sim_buf,strlen(sim_buf));
    }else if(reset_sim_num == 0){
        strcpy(sim_buf,"\n[SIM_TYPE]\nResult = w_version\n");
        write(fp,sim_buf,strlen(sim_buf));
    }
    list < item_t * >::iterator iter;
    for(iter = item_list->begin(); iter != item_list->end(); iter++) {
        item_t *obj = (item_t *) (*iter);

        /**if the module already tested*/
        if(obj != NULL && obj->mod != NULL && obj->mod->result != ERR_UNKNOW) {
            snprintf(tmp, sizeof(tmp), "\n[%s]\n%s = %lu\n%s = %s\n%s = %3.4f\n", obj->mod->module, KEY_TIMESTAMP_WORDS,
                obj->mod->last_time, KEY_RESULT_WORDS, obj->mod->result == SUCCESS ? KEY_PASS : KEY_FAIL,
                KEY_TESTTIME_WORDS, obj->mod->duration);
            strlcat(tmp, obj->mod->data, sizeof(tmp));
            write(fp,tmp,strlen(tmp));
            MMI_ALOGI("update module:[%s] test result to '%s'", obj->mod->module, g_res_file);
        }
    }
    fsync(fp);
    if(close(fp)<0) {
       MMI_ALOGE("file(%s) close fail!!\n", g_res_file);
    }
    system("chmod 777 /mnt/vendor/persist/FTM_AP");	
    system("chmod 777 /mnt/vendor/persist/FTM_AP/mmi.res");	
    system("chmod 777 /mnt/vendor/persist/FTM_AP/mmi-auto.res");

}

/**Restore latest result */
static int restore_result(char *filepath) {
    char module[1024] = { 0, };
    char line[1024] = { 0, };
    char indicator = '=';
    module_info *cur_mod = NULL;

    if(filepath == NULL) {
        MMI_ALOGE("Invalid parameter");
        return -1;
    }

    FILE *file = fopen(filepath, "r");

    if(file == NULL) {
        MMI_ALOGE("file(%s) open failed, error=%s\n", filepath, strerror(errno));
        return -1;
    }

    MMI_ALOGI("Parse result file: '%s'\n", filepath);
    while(fgets(line, sizeof(line), file) != NULL) {
        char name[1024] = { 0, }, value[1024] = {
        0,};

        if(line[0] == '#')
            continue;

        if(line[0] == '[') {
            parse_module(line, module, sizeof(module));
            cur_mod = g_modules_map[(string) module];
            MMI_ALOGI("parse module: [%s]\n", module);
            if(cur_mod != NULL)
                memset(cur_mod->data, 0, sizeof(cur_mod->data));    //initial the data field
            continue;
        }

        if(module[0] != '\0' && cur_mod != NULL) {
            parse_value(line, indicator, name, sizeof(name), value, sizeof(value));
            char *pname = trim(name);
            char *pvalue = trim(value);

            if(*pname != '\0' && *pvalue != '\0') {

                if(!strcmp(pname, KEY_TIMESTAMP_WORDS)) {
                    cur_mod->last_time = string_to_ulong(pvalue);
                } else if(!strcmp(pname, KEY_RESULT_WORDS)) {
                    if(!strcmp(pvalue, KEY_PASS)) {
                        cur_mod->result = SUCCESS;
                        MMI_ALOGI("module:[%s] test pass\n", module);
                    } else {
                        cur_mod->result = FAILED;
                        MMI_ALOGI("module:[%s] test fail\n", module);
                    }
                } else if(!strcmp(pname, KEY_TESTTIME_WORDS)) {
                    cur_mod->start_time = cur_mod->last_time;
                } else {
                    strlcat(cur_mod->data, line, sizeof(cur_mod->data));
                }
            }
        }
    }

    fclose(file);
    return 0;
}

static module_info *get_module_by_name(char *module_name) {
    module_info *mod = NULL;

    mod = g_modules_map[(string) module_name];
    if(mod == NULL)
        mod = g_controller_map[(string) module_name];

    return mod;
}

/**set the module fd*/
static void module_set_fd(char *module, int fd) {
    module_info *mi = NULL;

    if(module == NULL || (mi = get_module_by_name(module)) == NULL) {
        MMI_ALOGE("module_set_fd: %s", !module ? "Invalid parameter" : "Not find module");
        return;
    }

    string lib_name = mi->config_list[KEY_LIB_NAME];

    if(!lib_name.empty()) {
        /* set all module in the same lib_name to the same FD */
        unordered_map < string, module_info * >::iterator p;
        for(p = g_modules_map.begin(); p != g_modules_map.end(); p++) {
            module_info *mod = (module_info *) (p->second);
            if(mod != NULL && !mod->config_list[KEY_LIB_NAME].empty()
               && !mod->config_list[KEY_LIB_NAME].compare(lib_name)) {
                MMI_ALOGI("Set socket fd(%d) to module:[%s]\n", fd, mod->module);
                mod->socket_fd = fd;
                g_sock_client_map[lib_name] = fd;
            }
        }
    } else {
        /* if no lib_name just set the FD */
        MMI_ALOGI("Set socket fd(%d) to module:[%s]\n", fd, mi->module);
        mi->socket_fd = fd;
        g_sock_client_map[mi->module] = fd;
    }

    g_clients.push_back(mi);
    if(fd > g_max_fd)
        g_max_fd = fd;
}

static void *enter_shipmode(void *) {
    signal(SIGUSR1, signal_handler);
    int32_t color[] = { 0x007D7Dff, 0x7D0000ff };

    layout *lay = get_layout(LAYOUT_INDICATOR);

    if(lay == NULL) {
        MMI_ALOGE("Not find layout(%s) ", LAYOUT_INDICATOR);
        return NULL;
    }
    switch_cur_layout_locked(lay, get_main_module());
    button *btn_indicator = lay->find_button_by_name(KEY_INDICATOR);

    if(btn_indicator == NULL) {
        return NULL;
    }
    btn_indicator->set_text(get_string(KEY_UNPLUG_USB_NOTICE));
    invalidate();
    shipmode_status = 1;
    /**start to check usb cable*/
    int i = 0;

    while(is_usb_attached()) {
        MMI_ALOGE("usb cable atatched, please unplug it !");
        usleep(500 * 1000);
        btn_indicator->set_color(color[i++ % 2]);
        invalidate();
    }
    bool ret = enter_ship_mode();

    if(ret) {
        MMI_ALOGI("enter ship mode success, reboot!");
        btn_indicator->set_text(get_string(KEY_PASS));
    } else {
        MMI_ALOGI("enter ship mode fail!");
        btn_indicator->set_text(get_string(KEY_FAIL));
    }
    usleep(1000 * 1000);
    shipmode_status = 0;
    system("reboot -p");
    return NULL;
}

void enter_shipmode_thread() {
    pthread_t tid;

    if(shipmode_status == 1) {
        MMI_ALOGE("ongoing shipmode!");
        return;
    }
    int retval = pthread_create(&tid, NULL, enter_shipmode, NULL);

    if(retval < 0) {
        MMI_ALOGE("create thread fail, error=%s", strerror(errno));
        return;
    }
}

static void *server_accepting_thread(void *) {
    signal(SIGUSR1, signal_handler);

    int client_fd;
    struct sockaddr_un addr;
    socklen_t addr_size = sizeof(addr);
    int ret;

    ret = mkdir(MMI_SOCKET_DIR, 0644);
    if(ret) {
        MMI_ALOGW("Failed to create '%s', reason=%s\n", MMI_SOCKET_DIR, strerror(errno));
    }

    int sockfd = create_socket(get_value(KEY_MMI_SOCKET));

    if(sockfd < 0) {
        MMI_ALOGE("create socket failed\n");
        return NULL;
    }

    listen(sockfd, 8);
    sem_post(&g_sem_accept_ready);

    msg_t msg;

    MMI_ALOGI("thread(server_accepting_thread) started\n");
    while(1) {
        client_fd = -1;
        memset(&msg, 0, sizeof(msg));
        if((client_fd = accept(sockfd, (struct sockaddr *) &addr, &addr_size)) < 0) {
            MMI_ALOGW("accept fail, error=%s\n", strerror(errno));
            continue;
        }

        TEMP_FAILURE_RETRY(recv(client_fd, &msg, sizeof(msg), MSG_WAITALL));
        MMI_ALOGI("connect success for module=[%s], cmd=%s, subcmd=%s, client_fd=%d\n",
                  msg.module, MMI_CMD_NAME(msg.cmd), MMI_STR(msg.subcmd), client_fd);

        /*Set to module */
        module_set_fd(msg.module, client_fd);

    }
    return NULL;
}

static int msg_waiting() {

    fd_set fds;
    int retval;


    list < module_info * >fd_lists;
    list < module_info * >::iterator iter;
    msg_t *msg = NULL;

    struct timeval tv;

    /* Wait up to 3 seconds. */
    tv.tv_sec = 3;
    tv.tv_usec = 0;

    FD_ZERO(&fds);
    fd_lists.clear();
    if(!g_clients.empty()) {
        list < module_info * >::iterator iter;
        for(iter = g_clients.begin(); iter != g_clients.end(); iter++) {
            module_info *mod = (module_info *) (*iter);

            if(mod != NULL && mod->socket_fd > 0) {
                FD_SET(mod->socket_fd, &fds);
                fd_lists.push_back(mod);
            }
        }
    }

    if(fd_lists.empty()) {
        MMI_ALOGW("wait for client connection...\n");
        usleep(1000 * 1000);
        return -1;
    }
    retval = select(g_max_fd + 1, &fds, NULL, NULL, &tv);
    switch (retval) {
    case -1:
        MMI_ALOGE("select failed, error=%s\n", strerror(errno));
        break;
    case 0:
        MMI_ALOGI("select timeout: wait for receiving msg");
        break;
    default:
        int i = 0;

        for(iter = fd_lists.begin(); iter != fd_lists.end(); iter++) {
            module_info *mod = (module_info *) (*iter);

            if(mod == NULL)
                continue;

            int fd = mod->socket_fd;

            if(FD_ISSET(fd, &fds)) {
                msg = (msg_t *) zmalloc(sizeof(msg_t) + 1);
                if(msg == NULL) {
                    MMI_ALOGE("out of memory, abort the current request, error=%s\n", strerror(errno));
                    break;
                }

                int ret = TEMP_FAILURE_RETRY(recv(fd, msg, sizeof(msg_t), MSG_WAITALL));

                i++;
                if(ret <= 0) {
                    MMI_ALOGE("recv fail for module=[%s], error=%s\n", mod->module, strerror(errno));
                    close(mod->socket_fd);
                    mod->socket_fd = -1;
                } else {
                    MMI_ALOGI
                        ("mmi recv msg: moduld=[%s], cmd=%s, subcmd=%s, msg_id=%s, msg=%s, msg_size=%d, result=%s\n",
                         msg->module, MMI_CMD_NAME(msg->cmd), MMI_STR(msg->subcmd), MMI_PRI_TYPE(msg->msg_id),
                         MMI_STR(msg->msg), msg->length, MMI_RESULT(msg->result));
                    enqueue_msg(&g_msg_queue, msg);
                    sem_post(&g_msg_sem);
                }
            }
        }
        break;
    }
    return 0;
}

static void *msg_waiting_thread(void *) {
    signal(SIGUSR1, signal_handler);

    MMI_ALOGI("thread(msg_waiting_thread) started\n");
    while(1) {
        msg_waiting();
    }
    return NULL;
}

static void *msg_handle_thread(void *) {
    signal(SIGUSR1, signal_handler);

    msg_t *msg;
    module_info *mod;
    layout *curlay;

    MMI_ALOGI("thread(msg_handle_thread) started\n");
    while(1) {

        sem_wait(&g_msg_sem);
        dequeue_msg(&g_msg_queue, &msg);

        if(msg != NULL) {
            MMI_ALOGI("dequeue msg: moduld=[%s], cmd=%s, subcmd=%s, msg_id=%s, msg=%s, msg_size=%d, result=%s\n",
                      msg->module,
                      MMI_CMD_NAME(msg->cmd),
                      MMI_STR(msg->subcmd),
                      MMI_PRI_TYPE(msg->msg_id), MMI_STR(msg->msg), msg->length, MMI_RESULT(msg->result));

            mod = get_module_by_name(msg->module);
            if(mod == NULL) {
                MMI_ALOGW("Received invalid module, module=%s", "null");
                continue;
            }

            switch (msg->cmd) {
            case CMD_CTRL:
                handle_ctr_msg(msg, mod);
                break;
            case CMD_PRINT:
                handle_print(msg, mod);
                break;
            case CMD_QUERY:
                handle_query(msg, mod);
                break;
            case CMD_RUN:
                handle_run(msg, mod);
                break;
            default:
                MMI_ALOGW("msg(module=[%s], cmd=%s, subcmd=%s) no need to handle",
                          msg->module, MMI_CMD_NAME(msg->cmd), MMI_STR(msg->subcmd));
                break;
            }
        }
    }
    return NULL;
}
static char *trim_r(char *str) {
    char *p = str;

    if(*p == '\0') {
        return p;
    }

    while(*p == '\r') {
        p++;
    }
    if(*p == '\0') {
        return p;
    }

    char *end = p + strlen(p) - 1;

    while(*end == '\r') {
        *end = '\0';
        end--;
    }
    return p;
}

void _compile_date(char *buf){
	char year_month[12][4] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	char _date[128] = {'\0',};
	char _time[128] = {'\0',};
	char str_month[4] = {'\0',};
	int year = 0;
	int month = 0;
	int day = 0;
	int hour = 0;
	int minutes = 0;
	int seconds = 0;
//	LOGI("Get the Compile Date");
	if(buf == NULL){
//		LOGE("buf is illegal please check");
		return;
	}

	//snprintf(_date,sizeof(_date),"%s",__DATE__);
	//snprintf(_time,sizeof(_time),"%s",__TIME__);

	sscanf(_date, "%s %d %d", str_month, &day, &year);  
  	sscanf(_time, "%d:%d:%d", &hour, &minutes, &seconds);

	for(int i = 0; i < 12; ++i) {  
	    if(strncmp(str_month, year_month[i], 3) == 0) {  
	      month = i + 1;  
	      break;  
	    }  
	} 
	snprintf(buf,64,"%d-%d-%d %d:%d:%d\r\n",year,month,day,hour,minutes,seconds);
//	LOGI("Compile date is %s",buf);
	
}
//#ifdef ATE_TEST_OK
static void *ate_test_thread(void *) {
    signal(SIGUSR1, signal_handler);
    int fd;
    int nread,i;
    char buff[255];
    fd_set rd;
#if ANSEL_MMICPP
    char * uart_fb;
    int the_value;
    msg_t *uart_msg =  (msg_t *) zmalloc(sizeof(msg_t));
#endif 
    char _buf[] = "OK\n\r";
    void *ate_module = NULL;
    bool ate_test = false;
    button *btn = NULL;
    int uart_option = -1;
    uart_option = 1;  //for check HS0 or HS1 
    fd = ate_main(uart_option);//0 is HS0(debug),1 is HS1(main uart)
    if(fd < 0){
       MMI_ALOGE("[%1s]ate test thread error,maybe set uart port error,return value is %d",__FUNCTION__,fd);
       return NULL; 
    }
    MMI_ALOGI("[%s]open uart option[%d]...(0 is HSL0,1 is HSL1)",__FUNCTION__, uart_option);

    int j = 50;
    while (j--) {
        if (access("/dev/socket/mmi_camera", F_OK) == 0)  {
            break;
        }  else {
            usleep(1000 * 400);
            MMI_ALOGI("waiting mmi_camera remain %d times", j);
        }
    }

    uart_fd = fd;
    MMI_ALOGI("[%s]open uart fd is%d",__FUNCTION__,fd);
    write(fd, "QFCT:READY\r\n", strlen("QFCT:READY\r\n"));

    while(1){
      // MMI_ALOGI("waiting data...");
       FD_ZERO(&rd);
       FD_SET(fd,&rd);
       while(FD_ISSET(fd,&rd)){
           memset(buff,0,255);
           MMI_ALOGI("waiting data");
           if(select(fd+1,&rd,NULL,NULL,NULL) < 0){
               MMI_ALOGE("select error!");
           }else{
               while((nread = read(fd,buff,255))>0){
                   button *btn = NULL;
                   ate_test = false;
                   runnable_t *r = new runnable_t;
                   r->cb = NULL;
                   r->module = NULL;
                   /*step one:Get the main layout */
                   MMI_ALOGI("[%s]read data(%s,lenth is %d)", __FUNCTION__, buff, strlen(buff));
                   module_info *mod = get_main_module();
                   if(mod == NULL) {
                       MMI_ALOGE("%s Not main screen OR Null point",__FUNCTION__);
                       break;
                   } 
                   layout *lay = g_layout_map[mod->config_list[KEY_LAYOUT]];;
                   if(lay == NULL || lay->m_listview == NULL) {
                       MMI_ALOGE("%s No Main layout",__FUNCTION__);
                       break;
                   } 
                   
                   trim(buff);
                   trim_r(buff);
                   //buff[strlen(buff)-3] = '\0';
                   MMI_ALOGI("[%s]read data(%s,lenth is %d)", __FUNCTION__, buff, strlen(buff));
		   
                   if(!strcmp(buff,"QFCT")){
                       char t_buf[255] = {0,};
                       snprintf(t_buf, sizeof(t_buf), "QFCT:READY");
                       t_buf[strlen("QFCT:READY")] = '\r';
                       t_buf[strlen("QFCT:READY")+1] = '\n';
                       t_buf[strlen("QFCT:READY")+2] = '\0';
                        
                       nread=write(fd, t_buf, strlen(t_buf));
                       //nread=write(fd, "gpio initial\r\n", strlen("gpio initial\r\n"));
                       break;
                   } 

                   if(!strcmp(buff,"ATI")){
                       char t_buf[64] = {'\0',};
                       _compile_date(t_buf);
                       //snprintf(t_buf, sizeof(t_buf), "QFCT:V2018010501\r\n");
                       nread=write(fd, t_buf, strlen(t_buf));
                       break;
                   }
                   if(!strcmp(buff,"QFCT:START")){
                       char t_buf[255] = {0,};
                       int ret_cfg = reconfig("/mnt/vendor/persist/FTM_AP/mmi-auto.cfg");
                       if(ret_cfg <= 0){
                           is_factory_tab = false;
                           nread=write(fd, "QFCT:Swtich to Factory Config Failed\r\n", strlen("QFCT:Swtich to Factory Config Failed\r\n"));
                       }else{
                           is_factory_tab = true;
                           nread=write(fd, "QFCT:Swtich to Factory Config\r\n", strlen("QFCT:Swtich to Factory Config\r\n"));
                           module_info *mod_factory = get_main_module();
                           if(mod_factory == NULL) {
                               MMI_ALOGE("%s Not main screen OR Null point in switch factory config",__FUNCTION__);
                               break;
                           } 
                           layout *lay = g_layout_map[mod_factory->config_list[KEY_LAYOUT]];;
                          if(lay == NULL || lay->m_listview == NULL) {
                              MMI_ALOGE("%s No Main layout in switch factory config",__FUNCTION__);
                              break;
                          } 
                          if(lay != NULL && lay->m_listview != NULL) {
                              lay->m_listview->reset_result();
                              flush_result();
                          }
                      }
                      break;
                   }
                   //Internally triggered - by default no direct reply is processed
                   if(!strcmp(buff,"QFCT:START PWM")){
                       MMI_ALOGI("QFCT:START PWM");
                       nread=write(fd, "QFCT:Start to GPIO PWM\r\n", strlen("QFCT:Start to GPIO PWM\r\n"));
                       break;
                   }
                   if(!strcmp(buff,"QFCT:END PWM")){
                       MMI_ALOGI("QFCT:END PWM");
                       nread=write(fd, "QFCT:Close GPIO PWM Success\r\n", strlen("QFCT:Close GPIO PWM Success\r\n"));  
                       break;
                   }

                   if(!strcmp(buff,"QFCT:END")){
                       char t_buf[255] = {0,};
                       int ret_cfg = reconfig("/mnt/vendor/persist/FTM_AP/mmi.cfg");
                       if(ret_cfg <= 0){
                           is_factory_tab = true;
                           nread=write(fd, "QFCT:Swtich to Factory Config Failed\r\n", strlen("QFCT:Swtich to Factory Config Failed\r\n"));
                       }else{
                           is_factory_tab = false;
                           nread=write(fd, "QFCT:Swtich to Factory Config\r\n", strlen("QFCT:Swtich to Factory Config\r\n"));
                       }

                       layout *curlay = acquire_cur_layout();
                       button *btn_main_all = curlay->find_button_by_name(KEY_MAIN_ALL); 

                       if(btn_main_all != NULL){
                           btn = btn_main_all;
                           btn->set_disabled(true); 
                           r->cb = btn->get_cb();
                           r->module = curlay->module;
                       }
                       release_cur_layout();
                       r->cb(r->module);
                       if(btn != NULL && btn->get_disabled()){
                           MMI_ALOGI("[%s] Btn Main Set Enable", __FUNCTION__);
                           btn->set_disabled(false);
                           btn = NULL;
                       }
                       break;
                   }
#if ANSEL_MMICPP
                   if(!strcmp(buff,"ATV")){
                       char atv_buf[255] = {0,};
                       strcat(atv_buf,_ver);
                       nread=write(fd, atv_buf, strlen(atv_buf));
                       break;
                   }

#endif                

                   if((strstr(buff,"Camera_Front_End") != NULL) || (strstr(buff,"Camera_Rear_End") != NULL) || \
                       (strstr(buff,"Camera_Aux_End") != NULL) || (strstr(buff,"Vibrator_End") != NULL) || \
                       (strstr(buff,"Headset_L_End") != NULL) || (strstr(buff,"Headset_R_End") != NULL) || \
                       (strstr(buff,"Speaker_End") != NULL) || (strstr(buff,"Handset_End") != NULL)){

                       MMI_ALOGI("[%s] Receive Terminal Signal:%s(S)", __FUNCTION__, buff);
                       char *p = NULL; 
                       p = strtok(buff,":");
                       MMI_ALOGI("[%s] FCT Receive Confirm Result Split[0] is %s", __FUNCTION__, p);
                       if(p != NULL){
                           p = strtok(NULL,":");
                           MMI_ALOGI("[%s] FCT Receive Confirm Result Split[1] is %s", __FUNCTION__, p);
                           if(p == NULL){
                               MMI_ALOGE("[%s] FCT Receive Confirm Result 0 or 1 is NULL", __FUNCTION__);
                               break;
                           }
                       }
                       
                       layout *curlay = acquire_cur_layout();
                       button *btn_pass = curlay->find_button_by_name("pass"); 
                       button *btn_fail = curlay->find_button_by_name("fail"); 
                       if(btn_pass == NULL || btn_fail == NULL){
                           MMI_ALOGE("[%s] FCT Confirm Result Button is NULL", __FUNCTION__);
                           r->cb = NULL;
                           release_cur_layout();
                           break;
                       }else{
                           if(btn_pass != NULL && !strcmp(p,"1")){
                               MMI_ALOGI("[%s] FCT Confirm Result Button is PASS", __FUNCTION__);
                               btn = btn_pass;
                               btn->set_disabled(true); 
                               r->cb = btn->get_cb();
                           }
		           if(btn_fail != NULL && !strcmp(p,"0")){
                               MMI_ALOGI("[%s] FCT Confirm Result Button is FAIL", __FUNCTION__);
                               btn = btn_fail;
                               btn->set_disabled(true); 
                               r->cb = btn->get_cb();
                           }
                       }
		       r->module = curlay->module;
		       MMI_ALOGI("[%s]:[%s] Receive Terminal Signal:%s(E)", __FUNCTION__, curlay->module->module, buff);
                       release_cur_layout();
                       nread=write(fd, _buf, strlen(_buf));
		   }else if(!strcmp(buff,"QFCT_Auto_Start") || !strcmp(buff,"RESET_FCT")){
		       MMI_ALOGI("[%s] QFCT Auto Start", __FUNCTION__);
		       if(mod == NULL) {
                           MMI_ALOGE("[%s] Not main screen OR Null point", __FUNCTION__);
                           r->module = NULL;
		       }else{
		           r->module = mod;
                       }
                       if(!strcmp(buff,"QFCT_Auto_Start")){
                           btn = lay->find_button_by_name(KEY_MAIN_RUNALL);
                       }
                       if(!strcmp(buff,"RESET_FCT")){
                           btn = lay->find_button_by_name("main_reset");
                       }

                       if(btn == NULL){
                           MMI_ALOGE("[%s] [%s] No status button", __FUNCTION__, mod->module);
                           r->cb = NULL;
                       }else{
                           MMI_ALOGI("[%s] [%s] button is %s", __FUNCTION__, mod->module, btn->get_name());
                           if(btn->get_disabled()){
                               MMI_ALOGI("The previous RUN ALL action is not finished, please wait");
                               nread=write(fd, "Previous RUN ALL action is not finished, please wait\r\n", strlen("Previous RUN ALL action is not finished, please wait\r\n"));
                               break;
                           }else{
                               if(!strcmp(buff,"RESET_FCT")){
                                   btn->set_disabled(true);
                               }
                           }
                           // btn->set_disabled(true); 
                           r->cb = btn->get_cb();
                       }
                       nread=write(fd, _buf, strlen(_buf));
		   }else if(!strcmp(buff,"QFCTResult")){
		       MMI_ALOGI("[%s] QFCT Result", __FUNCTION__);
                       char res[SIZE_1K] = {0,};
                       char *p = res; 
                       char *result_pc = NULL;

                       snprintf(res, sizeof(res), "QFCTResult:");//send result to pc test,start begin with 'QFCTResult:'
                       p = p+strlen("QFCTResult:");

		       if(lay != NULL && lay->m_listview != NULL){

		           list < item_t * >*items = lay->m_listview->get_items();
		           list < item_t * >::iterator iter;
		           for(iter = items->begin(); iter != items->end(); iter++){
		       	       item_t *obj = (item_t *) (*iter);

                               if(obj != NULL && obj->mod != NULL){
                                   MMI_ALOGE("Not main screen OR Null point %s\n",obj->mod->module);
                                   if(!strcmp(obj->mod->module,"SIMCARD1") || !strcmp(obj->mod->module,"SIMCARD2") || \
                                       !strcmp(obj->mod->module,"WIFI") || !strcmp(obj->mod->module,"BLUETOOTH") || \
                                       !strcmp(obj->mod->module,"GPS") || !strcmp(obj->mod->module,"SDCARD") || \
                                       !strcmp(obj->mod->module,"EMMC") || !strcmp(obj->mod->module,"OTG")){

                                       if(obj->mod->result == FAILED){
                                           result_pc = "0";//0 test fail
                                       }
                                       if(obj->mod->result == SUCCESS){
                                           result_pc = "1";//1 test success
                                       }
                                       if(obj->mod->result == ERR_UNKNOW){
                                           result_pc = "2";//2 is not tested
                                       }
                                       
                                       snprintf(p, strlen(obj->mod->module)+strlen(result_pc)+3, "%s:%s;", obj->mod->module, result_pc);
                                       MMI_ALOGI("[%s] write result %s :%s ",res,obj->mod->module, result_pc);
                                       p = res + strlen(res);
                                   } 
                               }
                           }
		       }
                       int res_len = strlen(res); 
                       res[res_len] = '\r';
                       res[res_len+1] = '\n';
                       res[res_len+2] = '\0';
                       nread=write(fd, res, strlen(res));
		   }else if(!strcmp(buff,"do_ok") || !strcmp(buff,"do_cancel")){
                       MMI_ALOGI("[%s] Confirm Reset Signal:%s(S)", __FUNCTION__, buff);
                       layout *curlay = acquire_cur_layout();
                       button *btn_ok = curlay->find_button_by_name("ok");
                       button *btn_cancel = curlay->find_button_by_name("cancel");
                       if(btn_ok == NULL || btn_cancel == NULL){
                           MMI_ALOGE("[%s] FCT Confirm Result Button is NULL", __FUNCTION__);
                           r->cb = NULL;
                       }else{
                           if(btn_ok != NULL && !strcmp(buff,"do_ok")){
                               MMI_ALOGI("[%s] FCT Confirm Result Button is OK", __FUNCTION__);
                               btn = btn_ok;
                               btn->set_disabled(true);
                               r->cb = btn->get_cb();
                           }
                           if(btn_cancel != NULL && !strcmp(buff,"do_cancel")){
                               MMI_ALOGI("[%s] FCT Confirm Result Button is CANCEL", __FUNCTION__);
                               btn = btn_cancel;
                               btn->set_disabled(true);
                               r->cb = btn->get_cb();
                           }
                       }
                       r->module = curlay->module;
                       ALOGD("[%s]:[%s] Receive Terminal Signal:%s(E)", __FUNCTION__, curlay->module->module, buff);
                       release_cur_layout();
                   }else if(!strcmp(buff,"Headset_L_Start") || !strcmp(buff,"Headset_R_Start") || \
                       (strstr(buff,"Camera_Front_Start")!=NULL) || (strstr(buff,"Camera_Rear_Start")!=NULL)|| \
                       (strstr(buff,"Camera_Aux_Start")!=NULL) || !strcmp(buff,"Vibrator_Start") || \
                       !strcmp(buff,"Speaker_Start") || !strcmp(buff,"Handset_Start")){

                       MMI_ALOGI("[%s][%d] Receive ATE Test Command:%s", __FUNCTION__, __LINE__, buff);
                       /*step two:check modules running state */
                       if(lay != NULL && lay->m_listview != NULL){
                           list < item_t * >*items = lay->m_listview->get_items();
                           list < item_t * >::iterator iter;
                           for(iter = items->begin(); iter != items->end(); iter++){
                               item_t *item = (item_t *) (*iter);
                               module_info *tmod = item->mod;
                               //first check running state
                               if(tmod->running_state == MODULE_RUNNING){
                               //FCT has some module is in running,please waiting 
                                   MMI_ALOGI("[%s] FCT has some module is in running,please waiting", __FUNCTION__);
                                   ate_test = true;
                               }
                               if((!strcmp(tmod->module,"L HEADSET") && !strcmp(buff,"Headset_L_Start")) || \
                                   (!strcmp(tmod->module,"R HEADSET") && !strcmp(buff,"Headset_R_Start")) || \
                                   (!strcmp(tmod->module,"CAMERA_FRONT") && (strstr(buff,"Camera_Front_Start")!=NULL)) || \
                                   (!strcmp(tmod->module,"CAMERA_REAR") && (strstr(buff,"Camera_Rear_Start")!=NULL)) || \
                                   (!strcmp(tmod->module,"CAMERA_AUX") && (strstr(buff,"Camera_Aux_Start")!=NULL)) || \
                                   (!strcmp(tmod->module,"VIBRATOR") && !strcmp(buff,"Vibrator_Start")) || \
                                   (!strcmp(tmod->module,"LOUDSPEAKER") && !strcmp(buff,"Speaker_Start")) || \
                                   (!strcmp(tmod->module,"HANDSET") && !strcmp(buff,"Handset_Start"))){

                                   MMI_ALOGI("enter------2\n");       
#if ANSEL_MMICPP
                                   if(!strcmp(buff,"Camera_Front_Start")||!strcmp(buff,"Camera_Back_Main_Start")||!strcmp(buff,"Camera_Back_Aux_Start")){
                                       the_value = 0;
                                       MMI_ALOGI("Without the parameters, the exposure Settings will not be exposed!!!\n");
                                   }else if((strstr(buff,"Camera_Front_Start,")!=NULL)||(strstr(buff,"Camera_Rear_Start,")!=NULL)||(strstr(buff,"Camera_Aux_Start,")!=NULL)){
                                       /*get int value*/
                                       uart_fb = strtok(buff,",");
                                       uart_fb = strtok(NULL,",");
                                       if(uart_fb == NULL){
                                           goto Invalid_Arguments;
                                       }
                                       the_value = atoi(uart_fb);
                                       if ((the_value < 0) ||(the_value >=13) ){
                                           the_value = 0;
                                           memset(uart_msg,0,sizeof(msg_t));
                                           goto Invalid_Arguments;
                                       }
                                   }else{
                                       the_value = 0;
                                       MMI_ALOGI("Invalid parameter format,The parameter defaults to zero!!!\n");
                                   }
Invalid_Arguments:
                                   MMI_ALOGI("Invalid_Arguments,The parameter defaults to zero!!!\n");

                                   /*send value*/
                                   memset(uart_msg,0,sizeof(msg_t));
                                   char value[20]="";
                                   MMI_ALOGI("[%s] camera_fd=%d\n", __FUNCTION__,camera_fd);
                                   sprintf(value,"%d",the_value);
                                   strcat(uart_msg->msg,"exposure:");
                                   strcat(uart_msg->msg,value);
                                   strcat(uart_msg->msg,";");
                                   MMI_ALOGI("[%s] Without the parameters uart_msg->msg=%s\n", __FUNCTION__,uart_msg->msg);
                                   send_msg(camera_fd,uart_msg);
#endif
                                   MMI_ALOGI("[%s] Command:%s", __FUNCTION__ , buff);
                                   ate_module = tmod;
                                   r->module = ate_module;   
                                   r->cb = lay->m_listview->get_cb();
                                   usleep(1000 * 1000);
                                   nread=write(fd, _buf, strlen(_buf));
                               }
                           }
		           }
		       }else{
                           MMI_ALOGE("[%s] Invalid Command:%s Received", __FUNCTION__, buff);
                           char invalid_buf[255] = {0,};
                           snprintf(invalid_buf, sizeof(invalid_buf), "Invalid Command:%s Received", buff);
                           invalid_buf[strlen(invalid_buf)] = '\r';
                           invalid_buf[strlen(invalid_buf)+1] = '\n';
                           invalid_buf[strlen(invalid_buf)+2] = '\0';
                           ate_module = NULL;
                           r->module = NULL;   
                           r->cb = NULL;
                           nread=write(fd, invalid_buf, strlen(invalid_buf));
                       }
 
                      if(ate_test){//has module in testing,not handle this command,continue
                          MMI_ALOGE("[%s] Some Module in Testing,Please Waiting", __FUNCTION__);
                          ate_module = NULL;
                          r->module = NULL;   
                          r->cb = NULL;
                          nread=write(fd, "Some Module in Testing,Please Waiting\r\n", strlen("Some Module in Testing,Please Waiting\r\n"));
                          break; 
                      }
                      if((r != NULL) && (r->cb != NULL) && (r->module != NULL)) {
                          module_info *rmod = (module_info *)(r->module);
                          MMI_ALOGI("[%s] Callback Activated Module:%s Command:%s", __FUNCTION__, rmod->module, buff);

                        if (0 == strcmp(rmod->module, "L HEADSET")) {
                            if (0 == strcmp(buff, "Headset_L_Start")) {
                                MMI_ALOGI("[%s] [%s] set gpio", rmod->module, buff);
                                GpioOutput(AUDIO_TESTPIN_CTRL1, 0);
                                GpioOutput(AUDIO_TESTPIN_CTRL2, 0);
                                GpioOutput(AUDIO_TESTPIN_CTRL3, 0);
                                GpioOutput(AUDIO_TESTPIN_CTRL4, 0);
                            }
                            if (0 == strcmp(buff, "Headset_L_End")) {
                                MMI_ALOGI("[%s] [%s] release gpio", rmod->module, buff);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL1, 1);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL2, 1);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL3, 1);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL4, 1);
                            }
                        }

                        if (0 == strcmp(rmod->module, "R HEADSET")) {
                            if (0 == strcmp(buff, "Headset_R_Start")) {
                                MMI_ALOGI("[%s] [%s] set gpio", rmod->module, buff);
                                GpioOutput(AUDIO_TESTPIN_CTRL1, 0);
                                GpioOutput(AUDIO_TESTPIN_CTRL2, 1);
                                GpioOutput(AUDIO_TESTPIN_CTRL3, 0);
                                GpioOutput(AUDIO_TESTPIN_CTRL4, 0);
                            }
                            if (0 == strcmp(buff, "Headset_R_End")) {
                                MMI_ALOGI("[%s] [%s] release gpio", rmod->module, buff);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL1, 1);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL2, 0);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL3, 1);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL4, 1);
                            }
                        }

                        if (0 == strcmp(rmod->module, "LOUDSPEAKER")) {
                            if (0 == strcmp(buff, "Speaker_Start")) {
                                MMI_ALOGI("[%s] [%s] set gpio", rmod->module, buff);
                                GpioOutput(AUDIO_TESTPIN_CTRL1, 1);
                                GpioOutput(AUDIO_TESTPIN_CTRL2, 1);
                                GpioOutput(AUDIO_TESTPIN_CTRL3, 1);
                                GpioOutput(AUDIO_TESTPIN_CTRL4, 1);
                            }
                            if (0 == strcmp(buff, "Speaker_End")) {
                                MMI_ALOGI("[%s] [%s] release gpio", rmod->module, buff);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL1, 0);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL2, 0);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL3, 0);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL4, 0);
                            }
                        }

                        if (0 == strcmp(rmod->module, "HANDSET")) {
                            if (0 == strcmp(buff, "Handset_Start")) {
                                MMI_ALOGI("[%s] [%s] set gpio", rmod->module, buff);
                                GpioOutput(AUDIO_TESTPIN_CTRL1, 1);
                                GpioOutput(AUDIO_TESTPIN_CTRL2, 0);
                                GpioOutput(AUDIO_TESTPIN_CTRL3, 0);
                                GpioOutput(AUDIO_TESTPIN_CTRL4, 1);
                            }
                            if (0 == strcmp(buff, "Handset_End")) {
                                MMI_ALOGI("[%s] [%s] release gpio", rmod->module, buff);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL1, 0);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL2, 1);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL3, 1);
                                GpioReleaseTo(AUDIO_TESTPIN_CTRL4, 0);
                            }
                        }

                          if(!strcmp(buff,"QFCT_Auto_Start")){
                              btn->set_smart_fct(true);
                              MMI_ALOGI("[%s][%d] set smart fct true", __FUNCTION__, __LINE__);
                          }
                          r->cb(r->module);
                      }else{
                          MMI_ALOGE("[%s][%d] Nothing to do,Start Next Loop", __FUNCTION__, __LINE__);
                      }
                      if(btn != NULL && btn->get_disabled()){
                          MMI_ALOGI("[%s] Btn Set Enable", __FUNCTION__);
                          btn->set_disabled(false);
                          btn = NULL;
                      }
                      if(btn != NULL){
                          MMI_ALOGI("[%s] Btn Set NULL", __FUNCTION__);
                          btn = NULL;
                      
                   }
               }//while((nread = read(fd,buff,255))>0)
           }//
       }// if select(fd,..)
           break;
    }//while(1)

    MMI_ALOGI("nread=%d,%s\n",nread,buff);
    close(fd);
    return NULL;
}
//#endif
static void exit_handler(int num) {
    static int flag = 0;
    int status;

    if(flag == 0)
        usleep(10 * 1000);

    flag = 1;
    int pid = waitpid(-1, &status, WNOHANG);

    if(WIFEXITED(status)) {
        MMI_ALOGI("The child(pid=%d) exit with code %d\n", pid, WEXITSTATUS(status));
     /**Reset pid */
        unordered_map < string, module_info * >::iterator p;
        for(p = g_nodup_modules_map.begin(); p != g_nodup_modules_map.end(); ++p) {
            module_info *mod = (module_info *) (p->second);

            if(mod->pid == pid) {
                mod->pid = -1;
                break;
            }
        }
    }
    flag = 0;
}

static void copy_config_file(void) {
    int ret = FAILED;
    char target_file[PATH_MAX];

    snprintf(target_file, sizeof(target_file), "%s%s", get_value(KEY_FTM_AP_DIR), MMI_PCBA_SYS_CONFIG);
    MMI_ALOGI("copy '%s' to '%s'", MMI_PCBA_CONFIG, target_file);
    ret = copy_file(MMI_PCBA_CONFIG, target_file);
    if(ret != SUCCESS)
        MMI_ALOGE("fail to copy '%s' to '%s'", MMI_PCBA_CONFIG, target_file);
}

static void build_main_ui() {

    module_info *mod = g_modules_map[MAIN_MODULE];

    MMI_ALOGI("start build main ui");
    if(mod == NULL) {
        MMI_ALOGE("Invalid parameter");
        return;
    }

    /**Initial main module*/
    set_main_module(mod);

    /**Turn on backlight*/
    write_file(mod->config_list["sys_backlight"].c_str(), "120");

    layout *lay = get_layout(mod);

    if(lay == NULL) {
        MMI_ALOGE("get layout fail");
        return;
    }

    if(lay->m_listview != NULL && g_ordered_modules.size() > 0) {
        lay->m_listview->set_item_per_page(atoi(mod->config_list["item_display_per_page"].c_str()));
        lay->m_listview->set_items(&g_ordered_modules);
    }

    switch_cur_layout_locked(lay, mod);

    /**register the main screen*/
    register_home_screen(lay);

    string home_screens_str = mod->config_list[HOME_SCREENS];

    if(!home_screens_str.empty()) {
        list < string > screens;
        string_split(home_screens_str, ";", screens);

        list < string >::iterator iter;
        for(iter = screens.begin(); iter != screens.end(); iter++) {
            string layout_name = (string) (*iter);
            layout *layhome = get_layout(layout_name);

            if(layhome != NULL) {
                MMI_ALOGI("register home layout for: %s", layout_name.c_str());
                register_home_screen(layhome);
            }
        }
    }

    update_main_status();
    invalidate();
}

static void init_nodup_map(list < module_info * >*list_modules) {

    if(!list_modules->empty()) {
        list < module_info * >::reverse_iterator iter;

        MMI_ALOGD("total module count: %d\n", list_modules->size());
        for(iter = list_modules->rbegin(); iter != list_modules->rend(); iter++) {
            module_info *mod = (module_info *) (*iter);

            if(mod->config_list[KEY_LIB_NAME] != ""){
                g_nodup_modules_map[mod->config_list[KEY_LIB_NAME]] = mod;
            }
        }
    }
}

/**
 *   Start logcat daemon for logging
 *
 */
static void launch_log() {
    char log_file[PATH_MAX] = { 0 };
    snprintf(log_file, sizeof(log_file), "%s%s", get_value(KEY_FTM_AP_DIR), MMI_LOG_FILENAME);
    delete_file(log_file, 8 * SIZE_1M);

    char *log_daemon = (char *) get_value(KEY_MMI_LOG_DAEMON);

    MMI_ALOGI("launch log process: '%s', log file path: '%s'\n", log_daemon, log_file);

    if(log_daemon == NULL) {
        MMI_ALOGE("start log daemon failed\n");
        return;
    }

    int pid = fork();

    if(pid == 0) {
        char *args[6] = { log_daemon, "-v", "time", "-f", (char *) log_file, NULL };

        int res = execv(log_daemon, args);

        if(res < 0) {
            MMI_ALOGE("'%s' exec failed and exit, error=%s\n", log_daemon, strerror(errno));
            exit(1);
        }
    } else if(pid < 0) {
        MMI_ALOGE("fork failed, error=%s\n", strerror(errno));
    } else if(pid > 0) {
    }
}

/**
 *   Start diag daemon for handling diag command from PC tool
 *
 */
static void launch_controller() {

    module_info *mod = g_controller_map[CLIENT_DIAG_NAME];
    char *diag_exe = (char *) get_value(KEY_MMI_DIAG);

    if(mod == NULL || diag_exe == NULL) {
        MMI_ALOGE("Invalid parameter\n");
        return;
    }

    MMI_ALOGI("fock '%s' \n", diag_exe);
    int pid = fork();

    if(pid == 0) {
        char *args[2] = { diag_exe, NULL };

        int res = execv(diag_exe, args);

        if(res < 0) {
            MMI_ALOGE("'%s' exec failed and exit, error=%s\n", diag_exe, strerror(errno));
            exit(1);
        }
    } else if(pid < 0) {
        MMI_ALOGE("fork failed, error=%s\n", strerror(errno));
    } else if(pid > 0) {
        mod->pid = pid;
        MMI_ALOGD("diag process pid=%d\n", pid);
    }
}

static void launch_clients() {

    /* Launch clients */
    unordered_map < string, module_info * >::iterator p;
    for(p = g_nodup_modules_map.begin(); p != g_nodup_modules_map.end(); ++p) {
        module_info *mod = (module_info *) (p->second);
        if(mod != NULL) {
            fork_launch_module(mod);
        }
    }
}

static int start_threads() {
    int retval = -1;

    MMI_ALOGI("start create threads");
#ifdef ANDROID
    retval = pthread_create(&g_draw_tid, NULL, draw_thread, NULL);
    if(retval < 0) {
        MMI_ALOGE("create thread fail, error=%s", strerror(errno));
        return -1;
    }
    MMI_ALOGD("create draw thread(thread id=%lu) for draw screen\n", g_draw_tid);

    retval = create_input_threads();
    if(retval < 0) {
        MMI_ALOGE("create input threads fail.");
        return -1;
    }
#endif
    retval = pthread_create(&g_accept_tid, NULL, server_accepting_thread, NULL);
    if(retval < 0) {
        MMI_ALOGE("create thread fail, error=%s", strerror(errno));
        return -1;
    }
    MMI_ALOGD("create accepting thread(thread id=%lu) for accepting client connection\n", g_accept_tid);

    retval = pthread_create(&g_waiting_event_tid, NULL, msg_waiting_thread, NULL);
    if(retval < 0) {
        MMI_ALOGE("create thread fail, error=%s", strerror(errno));
        return -1;
    }
    MMI_ALOGD("create waiting thread(thread id=%lu) for waiting msg\n", g_waiting_event_tid);

    retval = pthread_create(&g_msg_handle_tid, NULL, msg_handle_thread, NULL);
    if(retval < 0) {
        MMI_ALOGE("create thread fail, error=%s", strerror(errno));
        return -1;
    }
    MMI_ALOGD("create handle thread(thread id=%lu) for handle msg\n", g_msg_handle_tid);
//#ifdef ATE_TEST_OK
    retval = pthread_create(&ate_test_tid, NULL, ate_test_thread, NULL);
    if(retval < 0){
		MMI_ALOGE("create thread ate test fail, error=%s", strerror(errno));
        return -1;
    }
//#endif
    sem_wait(&g_sem_accept_ready);
    return 0;
}


static void init_lang() {
    MMI_ALOGI("mmi start to loading lang");
    module_info *mod = get_module_by_name(MAIN_MODULE);

    if(mod == NULL) {
        MMI_ALOGE("Not find module by %s", MAIN_MODULE);
        return;
    }

    /*Initial language */
    if(!mod->config_list[KEY_STR_LANGUAGE].empty()) {
        load_lang(mod->config_list[KEY_STR_LANGUAGE].c_str());
    }

    /**Init font size*/
    if(!mod->config_list[KEY_FONTSIZE].empty()) {
        set_font_size(mod->config_list[KEY_FONTSIZE].c_str());
    }
}

static void init_layout(unordered_map < string, layout * >&layout_map) {

    struct dirent *de;
    DIR *dir;
    char layout_path[PATH_MAX] = { 0 };

    layout_map.clear();
    /*Initial layout */
    dir = opendir(MMI_LAYOUT_BASE_DIR);
    if(dir != 0) {
        while((de = readdir(dir))) {
            if(!strcmp(de->d_name, ".") || !strcmp(de->d_name, ".."))
                continue;

            snprintf(layout_path, sizeof(layout_path), "%s%s", MMI_LAYOUT_BASE_DIR, de->d_name);
            layout *lay = new layout(layout_path);

            if(load_layout(layout_path, lay) == 0) {
                layout_map[de->d_name] = lay;
                MMI_ALOGI("Loading layout file(%s) success", de->d_name);
            } else {
                MMI_ALOGE("Loading layout file(%s) fail", de->d_name);
                if(lay != NULL)
                    delete lay;
            }
        }
        closedir(dir);
    } else {
        MMI_ALOGE("dir(%s) open fail, error=%s", MMI_LAYOUT_BASE_DIR, strerror(errno));
    }
}

static void init_controller() {

    g_controller_map.clear();

     /**Init Diag module*/
    module_info *mod = new module_info((char *) CLIENT_DIAG_NAME);

    g_controller_map[CLIENT_DIAG_NAME] = mod;

     /**Init debug module*/
    mod = new module_info((char *) CLIENT_DEBUG_NAME);
    g_controller_map[CLIENT_DEBUG_NAME] = mod;
}

static void init_module_mode(list < module_info * >*list_modules) {

    uint32_t i = -1;
    char boot_mode[PROPERTY_VALUE_MAX] = { 0 };

    /**Get mode from config file*/
    module_info *mod = g_modules_map[MAIN_MODULE];

    if(mod == NULL || mod->config_list[KEY_TESTMODE] == "") {
        MMI_ALOGE("%s", !mod ? "Not find module" : "Not find test mode");
        return;
    }
    const char *testmode = mod->config_list[KEY_TESTMODE].c_str();

    for(i = 0; i < sizeof(test_mode_string) / sizeof(char *); i++) {
        if(!strcmp(test_mode_string[i], testmode)) {
            g_test_mode = (module_mode_t) i;
            break;
        }
    }
#ifdef ANDROID
   /**Get mode from misc boot mode,overwrite the config file mode*/
    property_get("ro.bootmode", boot_mode, "normal");
    for(i = 0; i < sizeof(boot_mode_string) / sizeof(char *); i++) {
        if(!strcmp(boot_mode_string[i], boot_mode)) {
            g_test_mode = (module_mode_t) i;
            break;
        }
    }
#endif
    if(!list_modules->empty()) {
        list < module_info * >::iterator iter;
        for(iter = list_modules->begin(); iter != list_modules->end(); iter++) {
            module_info *mod = (module_info *) (*iter);

            if(mod != NULL){
                mod->mode = g_test_mode;
            }
        }
    }
#ifdef ANDROID
    MMI_ALOGD("fastmmi testmode: %s", boot_mode_string[g_test_mode]);
#else
    MMI_ALOGD("fastmmi testmode: %s", test_mode_string[g_test_mode]);
#endif
}

static int init_config(const char *cfg, int _sim_num, int _lcd ) {

    int ret = -1;
    char cfg_tmp[128];
	int fp = -1;

    /*Initialize configuration */
    ret = load_config(cfg, &g_modules_map, &g_ordered_modules, _sim_num, _lcd);
    if(ret < 0) {
        MMI_ALOGE("Loading config(%s) fail", cfg);
        return -1;
    }

    /*Load lang */
    init_lang();

    /*Load more layout */
    init_layout(g_layout_map);

    /*Initial all module running mode */
    init_module_mode(&g_ordered_modules);

    /*Initial the duplicate module map */
    init_nodup_map(&g_ordered_modules);

    /*init the result file name */
    // Res filename should not contain the extension of the config file...
    strlcpy(cfg_tmp, cfg, sizeof(cfg_tmp));
    cfg_tmp[127] = NULL;
    char *p = strrchr(cfg_tmp, '/');
	char *pp = "/mnt/vendor/persist/FTM_AP/mmi-check";

    if(p != NULL) {
        string res_file_name = string(p);
        size_t last_index = res_file_name.find_last_of(".");

        if(last_index != string::npos) {
            res_file_name = res_file_name.substr(1, last_index - 1);
        }
        res_file_name = string(get_value(KEY_FTM_AP_DIR)) + res_file_name + ".res";
        strlcpy(g_res_file, res_file_name.c_str(), sizeof(g_res_file));
		if(check_file_exist(pp)){
			MMI_ALOGE("check file exist,clear path:%s",pp);
			clear_file(pp);
		}
		fp = open (pp, O_RDWR|O_CREAT|O_SYNC|O_TRUNC,0666);
		if(fp < 0){
			MMI_ALOGE("open path:%s failed[%s]",pp,strerror(errno));
		}else{
			write(fp,g_res_file,strlen(g_res_file));
			close(fp);
			MMI_ALOGI("write path [%s] to check file",g_res_file);
		}
		system("chmod 777 /mnt/vendor/persist/FTM_AP/mmi-check");

    }
    MMI_ALOGI("test result file: '%s'\n", g_res_file);
    return 0;
}

static void clean_resource() {

    /**Stop threads before clean module info*/
    kill_thread(g_waiting_event_tid);
    /**Clean Layout*/
    unordered_map < string, layout * >::iterator p2;
    for(p2 = g_layout_map.begin(); p2 != g_layout_map.end(); ++p2) {
        layout *lay = (layout *) (p2->second);

        MMI_ALOGI("Clean layout: %s\n", ((string) p2->first).c_str());
        if(lay != NULL) {
            lay->clear_locked();
            delete lay;

            lay = NULL;
        }
    }

     /**Clean Module info*/
    while(g_ordered_modules.begin() != g_ordered_modules.end()) {
        module_info *tmp = *g_ordered_modules.begin();

        if(tmp != NULL) {
            MMI_ALOGI("Clean module:[%s]\n", tmp->module);
            delete tmp;

            g_ordered_modules.erase(g_ordered_modules.begin());
        }
    }

   /**Clean static */
    g_nodup_modules_map.clear();
    g_ordered_modules.clear();
    g_modules_map.clear();
    g_layout_map.clear();
    g_clients.clear();
}

/**Check if need skip pass module **/
static bool is_skip_pass() {
    bool ret = false;

   /**check global configuration*/
    module_info *mod = get_main_module();

    if(mod != NULL && !mod->config_list["skip_if_autorun_passed"].empty()) {
        if(!mod->config_list["skip_if_autorun_passed"].compare("1"))
            ret = true;
    }

    MMI_ALOGI("mmi be configured %s", ret ? "skip pass" : "not skip pass");
    return ret;
}

/**autostart config **/
static bool is_autostart() {
    bool ret = false;
    char buf[64] = { 0 };
    char auto_config[PATH_MAX];

    /**check file .autostart*/
    snprintf(auto_config, sizeof(auto_config), "%s%s", get_value(KEY_FTM_AP_DIR), AUTOSTART_CONFIG);
    MMI_ALOGI("auto test config file: %s", auto_config);
    if(!read_file(auto_config, buf, sizeof(buf)) && !strcmp(buf, KEY_ASCII_TRUE)) {
        ret = true;
    }

   /**check global configuration*/
    if(!ret) {
        module_info *mod = get_main_module();

        if(mod != NULL && !mod->config_list["autorun_enable"].empty()) {
            if(!mod->config_list["autorun_enable"].compare("1"))
                ret = true;
        }
    }
    MMI_ALOGI("mmi be configured to %s test mode", ret ? "automatically" : "not automatically");
    return ret;
}

/*Start all test
**if automation ==1, then only start support automation test cases
**if automation == 0, start all test cases.
**/
void start_all(bool automation) {
    list < module_info * >::iterator iter;
    for(iter = g_ordered_modules.begin(); iter != g_ordered_modules.end(); iter++) {
        module_info *mod = (module_info *) (*iter);

        if(mod != NULL && (!automation || (automation && !mod->config_list[KEY_AUTOMATION].compare("1")))) {
            module_exec_pcba(mod);
        }
    }
}

/*Start all autorun module test
**/
void start_autorun(bool skip_pass) {
    list < module_info * >::iterator iter;
    for(iter = g_ordered_modules.begin(); iter != g_ordered_modules.end(); iter++) {
        module_info *mod = (module_info *) (*iter);

        if(mod != NULL && (!mod->config_list[KEY_AUTOMATION].compare("1"))) {
            if(skip_pass && mod->result == SUCCESS)
                continue;

            MMI_ALOGI("mmi start to autorun module:%s", mod->module);
            module_exec_pcba(mod);
        }
    }
}


static bool check_fd_exist(int fd) {
    list < module_info * >::iterator iter;
    for(iter = g_clients.begin(); iter != g_clients.end(); iter++) {
        module_info *tmod = (module_info *) (*iter);

        if(tmod->socket_fd == fd)
            return true;
    }

    return false;
}


static bool pre_config() {

    /** Initial path config from xml file,this should be done in the begin*/
    parse_strings(MMI_PATH_CONFIG);
    /**Common init*/
    write_file(WAKE_LOCK, "mmi");
    sem_init(&g_sem_exit, 0, 0);
    sem_init(&g_msg_sem, 0, 0);
    sem_init(&g_sem_accept_ready, 0, 0);
    sem_init(&g_data_print_sem, 0, 0);
    sem_init(&g_result_sem, 0, 0);
    pthread_mutex_init(&g_cur_layout_mutex, NULL);
#ifndef NO_UI
		/**Init draw*/
		if(!init_draw()) {
			MMI_ALOGE("init draw env failed!");
			return false;
		}
#endif
	create_func_map();
    return true;
}

static bool post_config() {
    MMI_ALOGI("start post config");
    /**Restore the latest result*/
    restore_result(g_res_file);
    return true;
}
static void reconfig_clients() {
    list < module_info * >::iterator iter;
    for(iter = g_ordered_modules.begin(); iter != g_ordered_modules.end(); iter++) {
        module_info *mod = (module_info *) (*iter);

        if(mod != NULL && !mod->config_list[KEY_LIB_NAME].empty()
           && g_sock_client_map[mod->config_list[KEY_LIB_NAME]] > 0) {
            mod->socket_fd = g_sock_client_map[mod->config_list[KEY_LIB_NAME]];
            if(!check_fd_exist(mod->socket_fd)) {
                g_clients.push_back(mod);
                MMI_ALOGI("socket fd=%d for module:[%s]", mod->socket_fd, mod->module);
            } else {
                MMI_ALOGW("socket fd:%d not exist", mod->socket_fd);
            }
        }
    }

    g_clients.push_back(g_controller_map[CLIENT_DIAG_NAME]);
    MMI_ALOGI("socket fd=%d for module:[%s]",
              g_controller_map[CLIENT_DIAG_NAME]->socket_fd, g_controller_map[CLIENT_DIAG_NAME]->module);
}

int reconfig(const char *cfg) {
    int ret = -1;

    if(cfg == NULL) {
        MMI_ALOGE("Invalid parameter");
        return -1;
    }

    clean_resource();

    /*Initialize configuration */
    ret = init_config(cfg,reset_sim_num,lcd_num);
    if(ret < 0) {
        MMI_ALOGE("init config failed");
        return -1;
    }

    if(!post_config()) {
        MMI_ALOGE("post config failed");
        return -1;
    }

    /*Initial the MMI screen */
    build_main_ui();

    /*Reconfig client */
    reconfig_clients();

    layout *lay = get_main_layout();

    if(lay == NULL || lay->m_listview == NULL) {
        MMI_ALOGE("%s", !lay ? "Not find layout" : "Not find listview in layout");
        return -1;
    }

    ret = pthread_create(&g_waiting_event_tid, NULL, msg_waiting_thread, NULL);
    if(ret < 0) {
        MMI_ALOGE("create thread fail, error=%s", strerror(errno));
        return -1;
    }
    MMI_ALOGD("create waiting thread(thread id=%lu) for waiting msg\n", g_waiting_event_tid);

    return lay->m_listview->get_items()->size();
}

int display_ffbm_string(void) {
    char layout_path[PATH_MAX] = { 0 };
    layout *lay = new layout(layout_path);
    int ret = FAILED;

    MMI_ALOGI("FFBM has been disabled, show FFBM mode only!!!!");
    /**logging to dmesg for debuging boot time*/
    MMI_KLOGE("FFBM has been disabled, show FFBM mode only!!!!");

    sem_init(&g_sem_exit, 0, 0);

    /**Init draw */
    if(!init_draw()) {
        MMI_ALOGE("init draw env failed!");
        return FAILED;
    }

    /*Load strings */
    load_lang("en");

    /*Load main UI layout file */
    snprintf(layout_path, sizeof(layout_path), "%s%s", MMI_LAYOUT_BASE_DIR, "main.xml");
    if(load_layout(layout_path, lay) == 0) {
        MMI_ALOGI("Loading main layout file success");
    } else {
        MMI_ALOGE("Loading main layout file fail");
        if(lay != NULL)
            delete lay;
        return FAILED;
    }

    /*Start draw thread to draw main UI */
    ret = pthread_create(&g_draw_tid, NULL, draw_thread, NULL);
    if(ret < 0) {
        MMI_ALOGE("create thread fail, error=%s", strerror(errno));
        return FAILED;
    }
    MMI_ALOGD("create draw thread(thread id=%lu) for draw screen\n", g_draw_tid);

    /**Turn on backlight*/
    write_file("/sys/class/leds/lcd-backlight/brightness", "120");

    /*Set current layout to main UI layout */
    g_cur_layout = lay;

    /*Refresh the screen to display UI */
    invalidate();

    return SUCCESS;
}

static void light_green_LED(void) {
    while(1) {
        write_file("/sys/class/leds/green/brightness", "255");
        usleep(500 * 1000);
        write_file("/sys/class/leds/green/brightness", "0");
        usleep(500 * 1000);
    }
}

int get_modem_version(char* result, int len) {
    if (result == NULL) {
        return -1;
    }
    FILE *fp;
    char *cmd = "cat /sys/devices/soc0/image_crm_version";
    if ((fp = popen(cmd, "r")) == NULL) {
        MMI_ALOGE("the command %s not exist\n", cmd);
        pclose(fp);
        return -1;
    }
    while (fgets(result, len - 1, fp) != NULL) {
        MMI_ALOGI("cmd_result: '%s' ", result);
    }
    pclose(fp);

    return 0;
}

static bool get_cmd_result(){
    char result[256] = {0};
    char* cmd = NULL;
    int try_times = 0;

    while (strlen(result) < 5 && try_times < 100) {
        if(system("echo 11 >/sys/devices/soc0/select_image")!=0){
            MMI_ALOGE("Check_modem_version_echo error");
        }

        get_modem_version(result, sizeof(result));
        if (strlen(result) > 5) {
            break;
        }
        MMI_ALOGE("Get empty modem version '%s', try again! ", result);
        try_times++;
        memset(result, 0, sizeof(result));
        usleep(1000 * 200);
    }

    MMI_ALOGI("END cmd result: '%s' ", result);
    if (strstr(result, "SC200EW") != NULL || strstr(result, "SC200EWF") != NULL) {
        MMI_ALOGI("the device is W version");
        return false;
    }
    return true;
}

void rewirte_qpdata(char * buf){
    int fd,i=0,j=0;
    char *ptr = NULL;
    char buff[255]={'\0'};
    fd = open("dev/block/bootdevice/by-name/qpdata1",O_RDWR);
    if(fd < 0){
        MMI_ALOGE("open qpdata error\n");
        close(fd);	
        return;
    }
    if(read(fd,buff,sizeof(buff)) > 0){
        MMI_ALOGE("qpdata already exist buf = %s\n",buff);
        if(strstr(buff,"SC600")!=NULL){
            close(fd);
            return;	
        }
    }
    close(fd);
    memset(buff,sizeof(buff),0);
    fd = open("dev/block/bootdevice/by-name/qpdata1",O_WRONLY);
    if(fd < 0){
        MMI_ALOGE("open qpdata error\n");
        close(fd);
        return;
    }
    while((*(buf+i)!=':')&&i<255){
        i++;
    }
    i = i-8;
    for(j=0;i<255;i++){
        if(*(buf+i)!=0x0a){
            strncpy(&buff[j],buf+i,1);
            j++;
        }else{
      	    break;
        }
    }

    MMI_ALOGE("rewirte qpdata success buf= %s\n",buff);
    if(write(fd,buff,sizeof(buff))!=sizeof(buff)){
        MMI_ALOGE("rewirte qpdata error\n");
    }else{
        MMI_ALOGE("rewirte qpdata success\n");
    }
    close(fd);
    fd = open("dev/block/bootdevice/by-name/qpdata1",O_RDWR);
    if(fd < 0){
        MMI_ALOGE("open qpdata error\n");
        close(fd);
        return;
    }
    if(read(fd,buff,sizeof(buff)) > 0)
        MMI_ALOGE("qpdata read again buf = %s\n",buff);

    close(fd);
    return;
}


int main(int argc, char **argv) {

    signal(SIGCHLD, exit_handler);
    int ret = -1;
    int _lcd_num = -1;
    int ret_parse_xml = -1;
    /** Disable FFBM function
     * Only display FFBM mode string on main UI
     */
    MMI_PROC_TYPE(MMI_PROC_TYPE_MMI);
    usleep(1000 * 500);

    MMI_ALOGI("mmi starting...., enjoy!!!!");
    /**logging to dmesg for debuging boot time*/
    MMI_KLOGI("mmi starting...., enjoy");

/*
    if(display_ffbm_string() != SUCCESS){
        MMI_ALOGE("Fail to display FFBM string on main ui");
    }
*/
    if(!pre_config()) {
        MMI_ALOGE("config failed");
        return -1;
    }
    lcd_num = _lcd_num;

    if(get_cmd_result() == false){
        w_ce_ds_ss=0;
    }else{
        w_ce_ds_ss=2;
    }
    
    reset_sim_num = w_ce_ds_ss;

    /*Initialize configuration */
    if(ret_parse_xml <= 0){
        LOGW("MMI Parse XML");
        if(true == is_create_mmi_cfg() && SUCCESS != create_mmi_cfg()) {
            LOGE("Parse mmi.xml file failed");
            return false;
        }
        ret = init_config(MMI_CONFIG,w_ce_ds_ss,_lcd_num);
    }else{
        ret = init_config("/cache/FTM_AP/mmi.cfg",w_ce_ds_ss,_lcd_num);
    }

    if(!post_config()) {
        MMI_ALOGE("post config failed");
        return -1;
    }

    init_controller();
    build_main_ui();
    set_boot_mode(BOOT_MODE_NORMAL);
    start_threads();
	
    MMI_KLOGI("starting modules");
    launch_controller();
    launch_clients();
//    fork_launch_module(0);
//    fork_launch_module(1);
    if(!check_file_exist(g_res_file)){
        flush_result();
    }

    sem_wait(&g_sem_exit);
 //   write_file(WAKE_UNLOCK, "mmi");
out:sem_close(&g_sem_exit);
    return 0;
}
