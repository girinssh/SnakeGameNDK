#include <jni.h>
#include <asm-generic/fcntl.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <android/log.h>
//
// Created by tngus on 2024-12-07.
//

#define PATH "/dev/effect"
#define DOTM_MAGIC		0x02
#define DOTM_START		_IOW(DOTM_MAGIC, 0, int)
#define DOTM_EAT		_IOW(DOTM_MAGIC, 1, int)
#define DOTM_END		_IOW(DOTM_MAGIC, 2, int)

int getDeviceFile(){
    int fd = open(PATH, O_WRONLY);
    if(fd < 0){
        __android_log_print(ANDROID_LOG_ERROR, "EFFECT_HW", "Device open error : %s", PATH);
    }
    return fd;
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_effectGameStart(JNIEnv *env, jobject thiz) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    ioctl(fd, DOTM_START, NULL);
    close(fd);
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_effectGameOver(JNIEnv *env, jobject thiz) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    ioctl(fd, DOTM_END, NULL);
    close(fd);
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_effectEatFood(JNIEnv *env, jobject thiz) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    ioctl(fd, DOTM_EAT, NULL);
    close(fd);
}