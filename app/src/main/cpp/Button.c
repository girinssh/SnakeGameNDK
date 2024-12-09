//
// Created by tngus on 2024-12-07.
//
#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <android/log.h>

#define MULTI_SELECT_EXCEPTION 10
#define NON_FUNCTIONAL_BUTTON_EXCEPTION 11

#define PATH "/dev/Button"

JNIEXPORT
jchar JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_getInputFromHW(JNIEnv* env, jobject thiz) {
    jchar button_state = 0;

    int button_fd, ret, i, cnt;
    char button_raw[9] = {0};

    button_fd = open(PATH, O_RDONLY);

    if(button_fd < 0){
        __android_log_print(ANDROID_LOG_ERROR, "BUTTON_HW", "Device open error : %s", PATH);
    }
    else {
        ret = read(button_fd, button_raw, 9);
        if(ret != 9){

            __android_log_print(ANDROID_LOG_ERROR, "BUTTON_HW", "FAILED TO READ BUTTON STATES");
            } else {
                cnt = 0;
                button_state = 0;
                for(i = 0;i < 9; i++){
                    if(button_raw[i] != 0){
                        cnt++;
                        if(cnt > 1){
                            __android_log_print(ANDROID_LOG_WARN, "BUTTON_HW", "MULTI_SELECT_EXCEPTION");
                            close(button_fd);
                            return MULTI_SELECT_EXCEPTION;
                        }
                        if(i % 2 != 1){
                            __android_log_print(ANDROID_LOG_WARN, "BUTTON_HW", "NON_FUNCTIONAL_BUTTON_EXCEPTION");
                            close(button_fd);
                            return NON_FUNCTIONAL_BUTTON_EXCEPTION;
                        }

                        if(button_state == 0){
                            button_state = i + 1;
                        }
                }
            }
        }
        __android_log_print(ANDROID_LOG_INFO, "BUTTON_HW", "Button PUSH ( %d )", button_state);
    }
    close(button_fd);
    return button_state;
}