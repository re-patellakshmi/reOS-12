//
// Created by great.zheng on 2022/3/18.
// Editted for RE by john.king on 2023/6/12
package com.quectel.uartservice;

public class UART {
    static {
        System.loadLibrary("jni_uart");
    }
    public native  int uartOpen();
    public native void uartClose();
    public native char uartRead();
    public native  int uartWrite(char buf);

}
