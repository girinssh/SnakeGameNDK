//
// Created by tngus on 2024-12-07.
//

#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <android/log.h>

JNIEXPORT jcharArray JNICALL
Java_kr_ac_cau_embedded_snakegame_MainActivity_getInputFromHW(JNIEnv
* env,
jobject thiz
) {
// TODO: implement getInputFromHW()

    int button_fd, ret, i;
    char button_raw[9] = {0};
    unsigned short button_state[9] = {0};

    button_fd = open("/dev/button", O_RDONLY);

    if(button_fd < 0){
        printf("Device open error : /dev/button\n");
    }
    else {
        ret = read(button_fd, button_raw, 9);
        if(ret != 9){
            fprintf(stderr, "FAILED TO READ BUTTON STATES\n");
        } else {
            for(i = 0; i < 9; i++){
                button_state[i] = button_raw[i];
            }
        }
    }

    return button_state;
}
