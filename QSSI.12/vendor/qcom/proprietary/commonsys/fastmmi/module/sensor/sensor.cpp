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
#include <hardware/sensors.h>
//#include "sensor_interface.h"
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/stat.h>    
#include <fcntl.h>

static pthread_t get_sensor_data_tid;
static int g_sock = -1;
static int sensor_type = 5;
static char cur_module_name[64];
typedef struct{
    long type;
    float data[4];
    char msg[1024];
}sensor_msg;

static void *get_sensor_data_thread(void *mod)
{
    sensor_msg msg;
    mmi_module_t *module = (mmi_module_t *) mod;
    int recv_size = -1;
    int ret= -1;
    signal(SIGUSR1, signal_handler);
    char print_buf[256]={0};
    g_sock = connect_server("/dev/socket/mmi_sensor");
    if(g_sock < 0) {
        ALOGE("client:sensor connect to server(/dev/socket/mmi_sensor) fail");
        return NULL;
    }
    memset(&msg, 0, sizeof(sensor_msg));
    strlcpy(msg.msg, "sensor start", sizeof(msg.msg));
    //msg.type=sensor_type;
    ret = write(g_sock, &msg, sizeof(sensor_msg));
    if(ret < 0) 
    {
		ALOGE("client:sensor write msg:sensor start fail, error=%s", strerror(errno));
        return NULL;
	} 
    ALOGI("client:sensor write msg:sensor start ok");
   
    while(1)
    {
        memset(&msg, 0, sizeof(sensor_msg));
        if((recv_size = recv(g_sock, &msg, sizeof(sensor_msg), MSG_WAITALL)) < 0) 
        {
            ALOGE("client recv msg fail, error=%s", strerror(errno));
            return NULL;
        }
        else
        {
            if(sensor_type == 5)
        	    snprintf(print_buf, sizeof(print_buf), "Lux\nvalue = %5.5f\n", msg.data[0]);
           // else
            //    snprintf(print_buf, sizeof(print_buf), "x = %5.5f\ny = %5.5f\nz = %5.5f\n", msg.data[0],msg.data[1],msg.data[2]);
            module->cb_print(cur_module_name, SUBCMD_MMI, print_buf, strlen(print_buf), PRINT_DATA);
		}
    }
}

static int32_t module_run_mmi(const mmi_module_t * module, unordered_map < string, string > &params) {
    ALOGI("%s start ", __FUNCTION__);
    int ret = FAILED;
    strlcpy(cur_module_name, params[KEY_MODULE_NAME].c_str(), sizeof(cur_module_name));
    char cmd[200] = {'\0'};
    sprintf(cmd,"/vendor/bin/get_sensors_data -s %d -d 65536 &",sensor_type);
    ALOGI("%s:%s", __FUNCTION__,cmd);
    system(cmd);
    usleep(1000*300);
    ret = pthread_create(&get_sensor_data_tid, NULL, get_sensor_data_thread, (void *) module);
    if(ret < 0)
    {
        ALOGE("can not create get_key_data_thread");
        return ret;
    }
    pthread_join(get_sensor_data_tid,NULL);
    return SUCCESS;
}

static int32_t module_run_pcba(const mmi_module_t * module, unordered_map < string, string > &params) {
    ALOGI("%s start", __FUNCTION__);

    int ret = FAILED, tried = 3;
   /*  sensors_event_t event;

    cur_sensor_type = get_sensor_type(params["type"].c_str());
    strlcpy(cur_module_name, params[KEY_MODULE_NAME].c_str(), sizeof(cur_module_name));

   
    ret = sensor_enable(cur_sensor_type, atoi(params["delay"].c_str()), true);
    if(ret != SUCCESS) {
        ALOGE("FFBM SENSOR : fail to initialize");
        return FAILED;
    }

   
    while(tried-- > 0) {
        get_sensor_data(cur_sensor_type, &event);
        ret = test_event(&event, params);
        if(ret == SUCCESS)
            break;
    }

    
    sensor_enable(cur_sensor_type, 0, false);
    ALOGI("%s  finished", __FUNCTION__);
    print_event(&event, module, true); */

    return ret;
}

static int32_t module_init(const mmi_module_t * module, unordered_map < string, string > &params) {
    ALOGI("%s start sensor", __FUNCTION__);

    if(module == NULL) {
        ALOGE("%s NULL point  received ", __FUNCTION__);
        return FAILED;
    }

    return SUCCESS;
}

static int32_t module_deinit(const mmi_module_t * module) {
    ALOGI("%s start.", __FUNCTION__);
    if(module == NULL) {
        ALOGE("%s NULL point  received ", __FUNCTION__);
        return FAILED;
    }

    return SUCCESS;
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
    pid=getProcessPidByName("get_sensors_data");
    kill_proc(pid);
}

static int32_t module_stop(const mmi_module_t * module) {
    ALOGI("%s start.", __FUNCTION__);
    if(module == NULL) {
        ALOGE("%s NULL point  received ", __FUNCTION__);
        return FAILED;
    }
    pthread_kill(get_sensor_data_tid, SIGUSR1);
    stop_get_data();
    return SUCCESS;
}

/**
* Before call Run function, caller should call module_init first to initialize the module.
* the "cmd" passd in MUST be defined in cmd_list ,mmi_agent will validate the cmd before run.
*
*/
static int32_t module_run(const mmi_module_t * module, const char *cmd, unordered_map < string, string > &params) {
    int ret = FAILED;

    if(!module || !cmd) {
        ALOGE("%s NULL point received ", __FUNCTION__);
        return FAILED;
    }
    ALOGI("%s start.command : %s", __FUNCTION__, cmd);

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
    .name = "Sensor",
    .author = "Qualcomm Technologies, Inc.",
    .methods = &module_methods,
    .module_handle = NULL,
    .supported_cmd_list = NULL,
    .supported_cmd_list_size = 0,
    .cb_print = NULL, /**it is initialized by mmi agent*/
    .run_pid = -1,
};
