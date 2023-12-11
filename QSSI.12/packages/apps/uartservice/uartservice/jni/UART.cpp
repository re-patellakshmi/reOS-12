//
// Created by great.zheng on 2022/3/18.
// Editted for RE by john.king on 2023/6/12
#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <termios.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <malloc.h>

#define UART_BAUDRATE B115200
#define UART_DEVICE "/dev/ttyHS1"

static int fd;

extern "C" JNIEXPORT int JNICALL Java_com_quectel_uartservice_UART_uartOpen(JNIEnv *env __unused, jobject cls __unused)
{
    static struct termios options;

    fd = open(UART_DEVICE, O_RDWR | O_NOCTTY | O_NDELAY);
    if (fd < 0) {
        printf("native uart open %s failed", UART_DEVICE);
        return -1;
    }

    if (tcflush(fd, TCIOFLUSH) < 0) {
        printf("native uart flush uart port error");
        return -1;
    }

    if (tcgetattr(fd, &options) < 0) {
        printf("native uart get port settings failed");
        return -1;
    }

    options.c_cc[VTIME]    = 0;
    options.c_cc[VMIN]     = 1;
    options.c_cflag &= ~CSIZE;
    options.c_cflag |= (CS8 | CLOCAL | CREAD);
    options.c_iflag = IGNPAR;
    options.c_oflag = 0;
    options.c_lflag = 0;
    cfsetospeed(&options, UART_BAUDRATE);
    cfsetispeed(&options, UART_BAUDRATE);

    if (tcsetattr(fd, TCSANOW, &options) < 0) {
        printf("native uart set port error");
        return -1;
    }

    fcntl(fd, F_SETFL, 0);

    return 0;
}

extern "C" JNIEXPORT void JNICALL Java_com_quectel_uartservice_UART_uartClose(JNIEnv *env __unused, jobject cls __unused)
{
    printf("native uart_close");
    close(fd);
}

extern "C" JNIEXPORT char JNICALL Java_com_quectel_uartservice_UART_uartRead(JNIEnv *env __unused, jobject cls __unused)
{
    int size;
    char buf;
    size = read(fd, &buf, 1);
    if (size < 0) {
        printf("native uart read error");
        return -1;
    }
    return buf;
}

extern "C" JNIEXPORT int JNICALL Java_com_quectel_uartservice_UART_uartWrite(JNIEnv *env __unused, jobject cls __unused, char buf)
{
    int size;
    size = write(fd, &buf, 1);
    if(size != 1) {
        printf("native uart_write failed");
        return -1;
    }
    return size;
}
