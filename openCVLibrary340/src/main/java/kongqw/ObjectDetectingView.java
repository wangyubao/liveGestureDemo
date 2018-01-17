package kongqw;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import org.opencv.R;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32S;
import static org.opencv.core.CvType.CV_8U;

/**
 * Created by kqw on 2016/7/13.
 * RobotCameraView
 */
public class ObjectDetectingView extends BaseCameraView {

    private static final String TAG = "ObjectDetectingView";
    private ArrayList<ObjectDetector> mObjectDetects;
    private String fileSrcPath;
    private Mat iconMat;
    private Mat maskMat;

    private MatOfRect mObject;


    @Override
    public void onOpenCVLoadSuccess() {
        Log.i(TAG, "onOpenCVLoadSuccess: ");

        mObject = new MatOfRect();

        mObjectDetects = new ArrayList<>();
    }

    @Override
    public void onOpenCVLoadFail() {
        Log.i(TAG, "onOpenCVLoadFail: ");
    }

    public ObjectDetectingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // 子线程（非UI线程）
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        Mat tMat = null;

        tMat = mRgba.t();
        Core.flip(tMat, mRgba, mCameraFront ? -1 : 1);
        tMat.release();
        tMat = mGray.t();
        Core.flip(tMat, mGray, mCameraFront ? -1 : 1);
        tMat.release();


//        for (ObjectDetector detector : mObjectDetects) {
//            // 检测目标
//            Rect[] object = detector.detectObject(mGray, mObject);
//            for (Rect rect : object) {
//                Imgproc.rectangle(mRgba, rect.tl(), rect.br(), detector.getRectColor(), 3);
//            }
//        }

        for (int i = 0; i < mObjectDetects.size(); i++) {
            ObjectDetector detector = mObjectDetects.get(i);
            Rect[] objects = detector.detectObject(mGray, mObject);
            for (Rect rect : objects) {
//                Imgproc.rectangle(mRgba, rect.tl(), rect.br(), detector.getRectColor(), 3);
                if (i == 0) {
                    addFaceSticker(objects);
                }
            }
        }

        mGray.release();

        tMat = mRgba.t();
        Core.flip(tMat, mRgba, mCameraFront ? 1 : -1);
        tMat.release();

        return mRgba;
    }

    private void addFaceSticker(Rect[] facesArray) {
        for (int i = 0; i < facesArray.length; i++) {
            if (i ==0) {
                Rect rect = facesArray[i];
                int faceX = rect.x;
                int faceY = rect.y-50 < 0 ? 0 : rect.y-50;
                Size dsize =new Size(rect.width, rect.height);
                Mat newIconMat =new Mat(dsize,CV_32S);
                Mat newMaskMat = new Mat(dsize, CV_32S);
                Imgproc.resize(subIconMat, newIconMat, dsize);
                Imgproc.resize(subMask, newMaskMat, dsize);
                Mat imgRGBA = new Mat();
                Imgproc.cvtColor(newIconMat, imgRGBA, Imgproc.COLOR_BGR2RGBA);
                if (!subIconMat.empty()) {
                    if (faceX + imgRGBA.cols() <= mRgba.cols() && faceY + imgRGBA.rows() <= mRgba.rows()) {
                        Rect rec = new Rect(faceX , faceY, imgRGBA.cols(), imgRGBA.rows());
                        Mat submat = mRgba.submat(rec);
                        imgRGBA.copyTo(submat, newMaskMat);
//                        iconMat.copyTo(submat);
                    }
                }
            }
        }
    }

    /**
     * 添加检测器
     *
     * @param detector 检测器
     */
    public synchronized void addDetector(ObjectDetector detector) {
        if (!mObjectDetects.contains(detector)) {
            mObjectDetects.add(detector);
        }
    }

    /**
     * 移除检测器
     *
     * @param detector 检测器
     */
    public synchronized void removeDetector(ObjectDetector detector) {
        if (mObjectDetects.contains(detector)) {
            mObjectDetects.remove(detector);
        }
    }

    private Mat subIconMat;
    private Mat subMask;
    public void setFileSrcPath(String fileSrcPath) {
        this.fileSrcPath = fileSrcPath;
        iconMat = Imgcodecs.imread(fileSrcPath);
        maskMat = Imgcodecs.imread(fileSrcPath, 0);
        Rect rect = new Rect(60, 20, iconMat.width() - 120, iconMat.height() - 40);
        subIconMat = iconMat.submat(rect);
        subMask = maskMat.submat(rect);
    }
}
