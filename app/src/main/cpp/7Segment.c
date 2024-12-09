#include <jni.h>
#include <fcntl.h>
#include <unistd.h>
#include <android/log.h>

//
// Created by tngus on 2024-12-07.
//

#define PATH "/dev/Timer"


JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_sendTime2HW(JNIEnv *env, jobject thiz, jint time) {
    int fd;
    short time_value;
    unsigned char ret;

    fd = open(PATH, O_WRONLY);

    if(fd < 0){
        __android_log_print(ANDROID_LOG_ERROR, "7SEGMENT_HW", "Device open error : %s", PATH);
    }
    time_value = (short)time;
    if(time > 9999){
        time_value = 9999;
    }

    ret = write(fd, &time_value, 2);
    if(ret < 0){
        __android_log_print(ANDROID_LOG_ERROR, "7SEGMENT_HW", "Write Error");
        return;
    }
    close(fd);
}