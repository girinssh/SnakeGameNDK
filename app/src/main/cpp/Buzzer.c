#include <jni.h>
#include <fcntl.h>
#include <android/log.h>
#include <unistd.h>

#define PATH "/dev/Buzzer"

//
// Created by tngus on 2024-12-09.
//
int getDeviceFile(){
    int fd = open(PATH, O_WRONLY);
    if(fd < 0){
        __android_log_print(ANDROID_LOG_ERROR, "BUZZER_HW", "Device open error : %s", PATH);
    }
    return fd;
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_soundOn(JNIEnv *env, jobject thiz) {
    int fd = getDeviceFile();
    int i = 0;
    char v = 50; // deci second

    if(fd < 0){
        return;
    }

    write(fd, &v, 1);

    close(fd);
}