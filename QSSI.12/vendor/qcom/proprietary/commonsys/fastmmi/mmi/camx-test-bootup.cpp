#include <stdlib.h>
#include <string.h>
#include <utils/Log.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <dirent.h>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>

pid_t getProcessPidByName(const char *proc_name)
{
    FILE *fp;
    char buf[255];
    char cmd[200] = {'\0'};
    pid_t pid = -1;
    
    sprintf(cmd, "pidof %s", proc_name);
    if((fp = popen(cmd, "r")) != NULL) {
        if(fgets(buf, 255, fp) != NULL) {
            pid = atoi(buf);
        }
    }
    ALOGE("pid = %d; proc_name = %s\n", pid, proc_name);
    pclose(fp);
    return pid;
}

int main(int argc , char ** argv){
    int pid = -1;
    int i = 0;
    int j = 0;
    ALOGE("camx_test_bootup: to search %s \n", argv[1]);
    
    while(1) {
        if(i > 10) break;
        pid=getProcessPidByName("android.hardware.camera.provider@2.4-service_64");
        usleep(1000*500);
        if(pid > 0) break;
        i++;
    }
    system("stop vendor.camera-provider-2-4");

    while(1) {
        if(j > 10) break;
        pid=getProcessPidByName("cameraserver");
        usleep(1000*500);
        if(pid > 0) break;
        j++;
    }

    ALOGE("stop vendor.camera-provider finish and run camx-hal3-test\n");
    system("/vendor/bin/camx-hal3-test &");
    usleep(1000*100);
    while(1){
        if(getProcessPidByName("camx-hal3-test") > 0) {
            ALOGE("camx-hal3-test start success\n");
            break;
        } else {
            ALOGE("camx-hal3-test restart again\n");
            system("/vendor/bin/camx-hal3-test &");
            usleep(1000*100);
        }
    }
    sleep(1000*60*60*24);
    return 0;
}
