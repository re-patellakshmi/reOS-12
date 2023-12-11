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

#include "mmi_module.h"

#define VBUS_STATE_PATH    "/sys/kernel/debug/charger/force_vbus_state"

static char cur_module_name[64];

static char* get_cmd_result(char *cmd)
{
    FILE *fp;
    char result[256];
    memset (result, 0,sizeof(result));
    if((fp=popen(cmd,"r"))==NULL){
        ALOGE("the command %s not exist\n",cmd);
        pclose(fp);
        return 0;
    }
    while(fgets(result,sizeof(result)-1,fp)!=NULL){
        ALOGI("cmd_result: %s", result);
    }
    pclose(fp);
	ALOGI("cmd result: %s", result);
    return result;
}

static int32_t module_run_mmi(const mmi_module_t * module, unordered_map < string, string > &params) {
    ALOGI("%s start ", __FUNCTION__);
    int fd  = -1;
    int retry = 10;
    int ret = FAILED;
    int usb_id=-1;
    char result[256];
    const char *str_usb_id = params["type"].c_str();

    strlcpy(cur_module_name, params[KEY_MODULE_NAME].c_str(), sizeof(cur_module_name));
    if(str_usb_id != NULL) {
        usb_id = atoi(str_usb_id);
        usb_id = (usb_id < 0) ? 0 : (usb_id);
    }
    ALOGI("%s usb bus id =%d\n", __FUNCTION__,usb_id);

    if (!check_file_exist("/dev/block/sda")) {
        /* force reset vbus to trigger re-enumeration */
        fd = open(VBUS_STATE_PATH, O_WRONLY);
        if (fd < 0) {
            MMI_ALOGE("open failed, errno : %d", errno);
            goto OUT;
        }

        ret = write(fd, "0", strlen("0"));
        if (ret < 0) {
            close(fd);
            MMI_ALOGE("write failed, errno : %d", errno);
            goto OUT;
        }
        usleep(1000 * 100);

        ret = write(fd, "1", strlen("1"));
        if (ret < 0) {
            close(fd);
            MMI_ALOGE("write failed, errno : %d", errno);
            goto OUT;
        }
        close(fd);

        while (retry != 0) {
            retry--;
            usleep(1000 * 400);
            if (check_file_exist("/dev/block/sda")) {
                ret = SUCCESS;
                break;
            }
            ALOGI("OTG detect remain %d times", retry);
        }
    }
    else {
        ret = SUCCESS;
    }

OUT:
    if (ret == SUCCESS) {
        ALOGI("OTG detect success");
        strcpy(result, "USB DETECT SUCCESS !");
    }
    else {
        ALOGI("OTG not detected");
        strcpy(result, "USB NOT DETECTED !");
		ret = FAILED;
    }

    module->cb_print(cur_module_name, SUBCMD_MMI, result, strlen(result), PRINT_DATA);
    usleep(1000 * 500);

    return ret;
}

static int32_t module_run_pcba(const mmi_module_t * module, unordered_map < string, string > &params) {
    ALOGI("%s start", __FUNCTION__);

    int ret = FAILED, tried = 3;
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



static int32_t module_stop(const mmi_module_t * module) {
    ALOGI("%s start.", __FUNCTION__);
    if(module == NULL) {
        ALOGE("%s NULL point  received ", __FUNCTION__);
        return FAILED;
    }
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
        ret = module_run_mmi(module, params);
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
    .name = "Usb",
    .author = "Qualcomm Technologies, Inc.",
    .methods = &module_methods,
    .module_handle = NULL,
    .supported_cmd_list = NULL,
    .supported_cmd_list_size = 0,
    .cb_print = NULL, /**it is initialized by mmi agent*/
    .run_pid = -1,
};
