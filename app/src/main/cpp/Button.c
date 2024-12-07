//
// Created by tngus on 2024-12-07.
//

#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <android/log.h>
#include <jni.h>

#define MULTI_SELECT_EXCEPTION 10
#define NON_FUNCTIONAL_BUTTON_EXCEPTION 11

JNIEXPORT
jchar JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_getInputFromHW(JNIEnv* env, jobject thiz) {
    jchar button_state;

    int button_fd, ret, i, cnt;
    char button_raw[9] = {0};

    button_fd = open("/dev/button", O_RDONLY);

    if(button_fd < 0){
        printf("Device open error : /dev/button\n");
    }
    else {
        ret = read(button_fd, button_raw, 9);
        if(ret != 9){
            fprintf(stderr, "FAILED TO READ BUTTON STATES\n");
            } else {
                cnt = 0;
                button_state = 0;
                for(i = 0;i < 9; i++){
                    if(button_raw[i] != 0){
                        cnt++;
                        if(cnt > 1){
                            return MULTI_SELECT_EXCEPTION;
                        }
                        if(i % 2 != 1){
                            return NON_FUNCTIONAL_BUTTON_EXCEPTION;
                        }

                        if(button_state == 0){
                            button_state = i + 1;
                        }
                }
            }
        }
    }
    close(button_fd);
    return button_state;
}