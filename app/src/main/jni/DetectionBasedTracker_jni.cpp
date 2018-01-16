#include <DetectionBasedTracker_jni.h>
#include <opencv2/core.hpp>
#include <opencv2/objdetect.hpp>

#include <string>
#include <vector>

#include <android/log.h>

#include "opencv/cv.h"
#include "opencv/highgui.h"
#include "opencv/cvwimage.h"
#include "opencv2/core/core.hpp"

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define RANGE     100 // 水平外凹或外凸的幅度
#define PI        3.1415926
using namespace std;
using namespace cv;

inline void vector_Rect_to_Mat(vector<Rect>& v_rect, Mat& mat)
{
    mat = Mat(v_rect, true);
}

class CascadeDetectorAdapter: public DetectionBasedTracker::IDetector
{
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector):
            IDetector(),
            Detector(detector)
    {
        LOGD("CascadeDetectorAdapter::Detect::Detect");
        CV_Assert(detector);
    }

    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects)
    {
        LOGD("CascadeDetectorAdapter::Detect: begin");
        LOGD("CascadeDetectorAdapter::Detect: scaleFactor=%.2f, minNeighbours=%d, minObjSize=(%dx%d), maxObjSize=(%dx%d)", scaleFactor, minNeighbours, minObjSize.width, minObjSize.height, maxObjSize.width, maxObjSize.height);
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);
        LOGD("CascadeDetectorAdapter::Detect: end");
    }

    virtual ~CascadeDetectorAdapter()
    {
        LOGD("CascadeDetectorAdapter::Detect::~Detect");
    }

private:
    CascadeDetectorAdapter();
    cv::Ptr<cv::CascadeClassifier> Detector;
};

struct DetectorAgregator
{
    cv::Ptr<CascadeDetectorAdapter> mainDetector;
    cv::Ptr<CascadeDetectorAdapter> trackingDetector;

    cv::Ptr<DetectionBasedTracker> tracker;
    DetectorAgregator(cv::Ptr<CascadeDetectorAdapter>& _mainDetector, cv::Ptr<CascadeDetectorAdapter>& _trackingDetector):
            mainDetector(_mainDetector),
            trackingDetector(_trackingDetector)
    {
        CV_Assert(_mainDetector);
        CV_Assert(_trackingDetector);

        DetectionBasedTracker::Parameters DetectorParams;
        tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, DetectorParams);
    }
};

JNIEXPORT jlong JNICALL Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeCreateObject
(JNIEnv * jenv, jclass, jstring jFileName, jint faceSize)
{
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeCreateObject enter");
    const char* jnamestr = jenv->GetStringUTFChars(jFileName, NULL);
    string stdFileName(jnamestr);
    jlong result = 0;

    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeCreateObject");

    try
    {
        cv::Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(stdFileName));
        cv::Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(stdFileName));
        result = (jlong)new DetectorAgregator(mainDetector, trackingDetector);
        if (faceSize > 0)
        {
            mainDetector->setMinObjectSize(Size(faceSize, faceSize));
            //trackingDetector->setMinObjectSize(Size(faceSize, faceSize));
        }
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
        catch (...)
        {
        LOGD("nativeCreateObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeCreateObject()");
        return 0;
    }

    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeCreateObject exit");
    return result;
}

JNIEXPORT void JNICALL Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeDestroyObject
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeDestroyObject");

    try
    {
        if(thiz != 0)
        {
            ((DetectorAgregator*)thiz)->tracker->stop();
            delete (DetectorAgregator*)thiz;
        }
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeestroyObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDestroyObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeDestroyObject()");
    }
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeDestroyObject exit");
}

JNIEXPORT void JNICALL Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeStart
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeStart");

    try
    {
        ((DetectorAgregator*)thiz)->tracker->run();
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeStart caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeStart caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeStart()");
    }
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeStart exit");
}

JNIEXPORT void JNICALL Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeStop
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeStop");

    try
    {
        ((DetectorAgregator*)thiz)->tracker->stop();
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeStop caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeStop caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeStop()");
    }
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeStop exit");
}

JNIEXPORT void JNICALL Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeSetFaceSize
(JNIEnv * jenv, jclass, jlong thiz, jint faceSize)
{
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeSetFaceSize -- BEGIN");

    try
    {
        if (faceSize > 0)
        {
            ((DetectorAgregator*)thiz)->mainDetector->setMinObjectSize(Size(faceSize, faceSize));
            //((DetectorAgregator*)thiz)->trackingDetector->setMinObjectSize(Size(faceSize, faceSize));
        }
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeStop caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeSetFaceSize caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeSetFaceSize()");
    }
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeSetFaceSize -- END");
}


JNIEXPORT void JNICALL Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeDetect
(JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong faces)
{
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeDetect");

    try
    {
        vector<Rect> RectFaces;
        ((DetectorAgregator*)thiz)->tracker->process(*((Mat*)imageGray));
        ((DetectorAgregator*)thiz)->tracker->getObjects(RectFaces);
        *((Mat*)faces) = Mat(RectFaces, true);
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDetect caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code DetectionBasedTracker.nativeDetect()");
    }
    LOGD("Java_com_wanghui_livegesturedemo_DetectionBasedTracker_nativeDetect END");
}




void MaxFrame(IplImage* frame)
{
    uchar* old_data = (uchar*)frame->imageData;
    uchar* new_data = new uchar[frame->widthStep * frame->height];

    int center_X = frame->width / 2;
    int center_Y = frame->height / 2;
    int radius = 400;
    int newX = 0;
    int newY = 0;

    int real_radius = (int)(radius / 2.0);
    for (int i = 0; i < frame->width; i++)
    {
        for (int j = 0; j < frame->height; j++)
        {
            int tX = i - center_X;
            int tY = j - center_Y;

            int distance = (int)(tX * tX + tY * tY);
            if (distance < radius * radius)
            {
                newX = (int)((float)(tX) / 2.0);
                newY = (int)((float)(tY) / 2.0);

                newX = (int) (newX * (sqrt((double)distance) / real_radius));
                newX = (int) (newX * (sqrt((double)distance) / real_radius));

                newX = newX + center_X;
                newY = newY + center_Y;

                new_data[frame->widthStep * j + i * 4] =  old_data[frame->widthStep * j + i * 4];
                new_data[frame->widthStep * j + i * 4 + 1] = old_data[frame->widthStep * newY + newX * 4 + 1];
                new_data[frame->widthStep * j + i * 4 + 2] =old_data[frame->widthStep * newY + newX * 4 + 2];
                new_data[frame->widthStep * j + i * 4 + 3] =old_data[frame->widthStep * newY + newX * 4 + 3];
            }
            else
           {
                new_data[frame->widthStep * j + i * 4] =  old_data[frame->widthStep * j + i * 4];
                new_data[frame->widthStep * j + i * 4 + 1] =  old_data[frame->widthStep * j + i * 4 + 1];
                new_data[frame->widthStep * j + i * 4 + 2] =  old_data[frame->widthStep * j + i * 4 + 2];
                new_data[frame->widthStep * j + i * 4 + 3] =old_data[frame->widthStep * j + i * 4 + 3];
            }
        }
    }
    memcpy(old_data, new_data, sizeof(uchar) * frame->widthStep * frame->height);
    delete[] new_data;
}


void MinFrame(IplImage* frame)
{
    uchar* old_data = (uchar*)frame->imageData;
    uchar* new_data = new uchar[frame->widthStep * frame->height];

    int center_X = frame->width / 2;
    int center_Y = frame->height / 2;

    int radius = 0;
    double theta = 0;
    int newX = 0;
    int newY = 0;

    for (int i = 0; i < frame->width; i++)
    {
        for (int j = 0; j < frame->height; j++)
        {
            int tX = i - center_X;
            int tY = j - center_Y;

            theta = atan2((double)tY, (double)tX);
            radius = (int)sqrt((double)(tX * tX) + (double) (tY * tY));
            int newR = (int)(sqrt((double)radius) * 12);
            newX = center_X + (int)(newR * cos(theta));
            newY = center_Y + (int)(newR * sin(theta));

            if (!(newX > 0 && newX < frame->width))
            {
                newX = 0;
            }
            if (!(newY > 0 && newY < frame->height))
            {
                newY = 0;
            }

            new_data[frame->widthStep * j + i * 4] = old_data[frame->widthStep * j + i * 4];
            new_data[frame->widthStep * j + i * 4 + 1] = old_data[frame->widthStep * newY + newX * 4 + 1];
            new_data[frame->widthStep * j + i * 4 + 2] =old_data[frame->widthStep * newY + newX * 4 + 2];
            new_data[frame->widthStep * j + i * 4 + 3] =old_data[frame->widthStep * newY + newX * 4 + 3];
        }
    }
    memcpy(old_data, new_data, sizeof(uchar) * frame->widthStep * frame->height);
    delete[] new_data;
}
//com.example.facedec

JNIEXPORT jintArray JNICALL Java_com_wanghui_livegesturedemo_DetectionBasedTracker_maxFrame
  (JNIEnv* env, jclass obj, jintArray buf, jint w, jint h)
{
 jint *cbuf;
     cbuf = env->GetIntArrayElements(buf, false);
     if(cbuf == NULL){
         return 0;
     }
     int size=w * h;
     Mat imgData(h, w, CV_8UC4, (unsigned char*)cbuf);
     IplImage temp = IplImage(imgData);
     IplImage *image=&temp;

     MaxFrame(image);

     jintArray result = env->NewIntArray(size);
  env->SetIntArrayRegion(result, 0, size, cbuf);
  env->ReleaseIntArrayElements(buf, cbuf, 0);
  return result;
}

JNIEXPORT jintArray JNICALL Java_com_wanghui_livegesturedemo_DetectionBasedTracker_minFrame
  (JNIEnv* env, jclass obj, jintArray buf, jint w, jint h)
{
 jint *cbuf;
     cbuf = env->GetIntArrayElements(buf, false);
     if(cbuf == NULL){
         return 0;
     }

     Mat imgData(h, w, CV_8UC4, (unsigned char*)cbuf);
     IplImage temp = IplImage(imgData);
     IplImage *src= &temp;
     MinFrame(src);

     int size=w * h;
     jintArray result = env->NewIntArray(size);
     env->SetIntArrayRegion(result, 0, size, cbuf);
     env->ReleaseIntArrayElements(buf, cbuf, 0);
     return result;
}

