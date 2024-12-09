#include <jni.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <android/log.h>

//
// Created by tngus on 2024-12-07.
//

#define PATH "/dev/Score"
#define SCORE_MAGIC	0x04
#define SCORE_RESET	_IOW(SCORE_MAGIC, 0, int)
#define SCORE_UPDATE	_IOW(SCORE_MAGIC, 1, int)
#define BEST_SCORE_UPDATE _IOW(SCORE_MAGIC, 2, int)

int getDeviceFile(){
    int fd = open(PATH, O_WRONLY);
    if(fd < 0){
        __android_log_print(ANDROID_LOG_ERROR, "TEXTLCD_HW", "Device open error : %s", PATH);
    }
    return fd;
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_sendScore2HW(JNIEnv *env, jobject thiz,
                                                             jint score) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    __android_log_print(ANDROID_LOG_INFO, "TEXTLCD_HW", "Score : %d", score);

    ioctl(fd, SCORE_UPDATE, &score, _IOC_SIZE(SCORE_UPDATE));
    close(fd);
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_resetLCD(JNIEnv *env, jobject thiz) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    ioctl(fd, SCORE_RESET, NULL);
    close(fd);
}

JNIEXPORT void JNICALL
Java_kr_ac_cau_1embedded_snakegame_MainActivity_sendBestScore2HW(JNIEnv *env, jobject thiz,
                                                                 jint best_score) {
    int fd = getDeviceFile();

    if(fd < 0){
        return;
    }

    __android_log_print(ANDROID_LOG_INFO, "TEXTLCD_HW", "Best : %d", best_score);

    ioctl(fd, BEST_SCORE_UPDATE, &best_score, _IOC_SIZE(SCORE_UPDATE));
    close(fd);
}