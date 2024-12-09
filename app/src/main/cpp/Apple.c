#include <jni.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <android/log.h>
//
// Created by tngus on 2024-12-07.
//
#define PATH "/dev/Apple"
#define APPLE_MAGIC		0x01
#define APPLE_SMT_OFF	_IOW(APPLE_MAGIC, 0, int)
#define APPLE_RESET		_IOW(APPLE_MAGIC, 1, int)

#define APPLE_SOUND_ON  _IOW(APPLE_MAGIC, 2, int)
#define APPLE_SOUND_OFF  _IOW(APPLE_MAGIC, 3, int)

int getDeviceFile(){
    int fd = open(PATH, O_WRONLY);
    if(fd < 0){
        __android_log_print(ANDROID_LOG_ERROR, "APPLE_HW", "Device open error : %s", PATH);
    }
    return fd;
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_sendCombo2HW(JNIEnv *env, jobject thiz,
                                                             jint combo) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    char val = (char) (combo != 0 ? ((combo-1) % 8 + 1) : 0);

    write(fd, &val, 1);

    close(fd);
}


JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_stopMotor(JNIEnv *env, jobject thiz) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    ioctl(fd, APPLE_SMT_OFF, NULL);
    close(fd);
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_resetApple(JNIEnv *env, jobject thiz) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    ioctl(fd, APPLE_RESET, NULL);
    close(fd);
}
