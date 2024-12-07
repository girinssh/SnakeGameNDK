#include <jni.h>
#include <fcntl.h>
#include <android/log.h>
#include <unistd.h>

//
// Created by tngus on 2024-12-07.
//

#define PATH "/dev/7segment"

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_sendTime2HW(JNIEnv *env, jobject thiz, jint time) {
    int fd, num;
    unsigned char bytevalues[4];
    unsigned char ret;

    fd = open(PATH, O_WRONLY);

    if(fd < 0){
        __android_log_print(ANDROID_LOG_ERROR, "7SEGMENT_HW", "Device open error : %s", PATH);
    }
    num = time;
    if(time > 9999){
        num = 9999;
    }

    bytevalues[0] = num / 1000;
    num %= 1000;
    bytevalues[1] = num / 100;
    num %= 100;
    bytevalues[2] = num / 10;
    num %= 10;
    bytevalues[3] = num;

    ret = write(fd, bytevalues, 4);
    if(ret < 0){
        __android_log_print(ANDROID_LOG_ERROR, "7SEGMENT_HW", "Write Error");
        return;
    }
    close(fd);
}