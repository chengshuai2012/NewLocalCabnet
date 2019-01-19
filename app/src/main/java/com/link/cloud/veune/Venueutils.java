package com.link.cloud.veune;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.link.cloud.R;
import com.link.cloud.bean.AllUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import md.com.sdk.MicroFingerVein;

/**
 * Created by 49488 on 2018/10/15.
 */

public class Venueutils {
    String Tag = "Venueutils";
    public MdUsbService.MyBinder mdDeviceBinder;
    private byte[] img;
    Context context;
    VenueCallBack callBack;
    private boolean bOpen = false;//设备是否打开
    private int[] pos = new int[1];
    private float[] score = new float[1];
    private boolean ret;
    public ModelImgMng modelImgMng = new ModelImgMng();
    private int[] tipTimes = {0, 0};//后两次次建模时用了不同手指或提取特征识别时，最多重复提醒限制3次
    private int lastTouchState = 0;//记录上一次的触摸状态
    private int modOkProgress = 0;
    private final static float IDENTIFY_SCORE_THRESHOLD = 0.63f;
    private final static float MODEL_SCORE_THRESHOLD = 0.4f;

    public interface VenueCallBack {
        void modelMsg(int state, String msg, Bitmap bitmap);
    }
    public  void initVenue(Context context, MdUsbService.MyBinder mdDeviceBinder,  Boolean bOpen,VenueCallBack callBack){
        this.bOpen=bOpen;
        this.context=context;
        this.mdDeviceBinder =mdDeviceBinder;
        this.callBack = callBack;
    }


    public int getState() {
        if (!bOpen) {
            modOkProgress = 0;
            modelImgMng.reset();
            bOpen = mdDeviceBinder.openDevice(0);//开启指定索引的设备
            if (bOpen) {

            } else {

            }
        }
        int state = mdDeviceBinder.getDeviceTouchState(0);
        if (state != 3) {
            if (lastTouchState != 0) {
                mdDeviceBinder.setDeviceLed(0, MdUsbService.getFvColorRED(), true);
            }
            lastTouchState = 0;
        }
        if (state == 3) {
            //返回值state=3表检测到了双Touch触摸,返回1表示仅指腹触碰，返回2表示仅指尖触碰，返回0表示未检测到触碰
            if (lastTouchState == 3) {
                return 4;
            }
            lastTouchState = 3;
            mdDeviceBinder.setDeviceLed(0, MdUsbService.getFvColorGREEN(), false);
            img = mdDeviceBinder.tryGetBestImg(5);
            if (img == null) {
                Log.e(Tag, "get img failed,please try again");
                callBack.modelMsg(1, context.getString(R.string.image_fail), null);
            }
        }

        return state;
    }

    public void workModel() {
        float[] quaScore = {0f, 0f, 0f, 0f};
        int quaRtn = MdUsbService.qualityImgEx(img, quaScore);
        String oneResult = ("quality return=" + quaRtn) + ",result=" + quaScore[0] + ",score=" + quaScore[1] + ",fLeakRatio=" + quaScore[2] + ",fPress=" + quaScore[3];
        Log.e("workModel: ", oneResult);
        Bitmap bitmap = MdUsbService.chg2VisibleBmp(img);
        int quality = (int) quaScore[0];
        if (quality != 0) {
            callBack.modelMsg(1, context.getString(R.string.image_fail), bitmap);
            return;
        }
        byte[] feature = MdUsbService.extractImgModel(img, null, null);
        if (feature == null) {
            callBack.modelMsg(1, context.getString(R.string.image_fail), bitmap);
        } else {
            modOkProgress++;
            if (modOkProgress == 1) {//first model
                tipTimes[0] = 0;
                tipTimes[1] = 0;
                modelImgMng.setImg1(img);
                modelImgMng.setFeature1(feature);
                callBack.modelMsg(1, context.getString(R.string.again_finger), bitmap);
            } else if (modOkProgress == 2) {//second model
                ret = MdUsbService.fvSearchFeature(modelImgMng.getFeature1(), 1, img, pos, score);
                if (ret && score[0] > MODEL_SCORE_THRESHOLD) {
                    feature = MdUsbService.extractImgModel(img, null, null);//无须传入第一张图片，第三次混合特征值时才同时传入3张图；
                    if (feature != null) {
                        tipTimes[0] = 0;
                        tipTimes[1] = 0;
                        modelImgMng.setImg2(img);
                        modelImgMng.setFeature2(feature);
                        callBack.modelMsg(1, context.getString(R.string.again_finger), bitmap);
                    } else {//第二次建模从图片中取特征值无效
                        modOkProgress = 1;
                        if (++tipTimes[0] <= 3) {
                            callBack.modelMsg(2, context.getString(R.string.same_finger), bitmap);

                        } else {//连续超过3次放了不同手指则忽略此次建模重来
                            modOkProgress = 0;
                            modelImgMng.reset();
                            callBack.modelMsg(2, context.getString(R.string.same_finger), bitmap);
                        }
                    }
                } else {
                    modOkProgress = 1;
                    if (++tipTimes[0] <= 3) {
                        callBack.modelMsg(2, context.getString(R.string.same_finger), bitmap);
                    } else {//连续超过3次放了不同手指则忽略此次建模重来
                        modOkProgress = 0;
                        modelImgMng.reset();
                        callBack.modelMsg(2, context.getString(R.string.same_finger), bitmap);
                    }
                }
            } else if (modOkProgress == 3) {//third model
                ret = MdUsbService.fvSearchFeature(modelImgMng.getFeature2(), 1, img, pos, score);
                if (ret && score[0] > MODEL_SCORE_THRESHOLD) {
                    feature = MdUsbService.extractImgModel(modelImgMng.getImg1(), modelImgMng.getImg2(), img);
                    if (feature != null) {//成功生成一个3次建模并融合的融合特征数组
                        tipTimes[0] = 0;
                        tipTimes[1] = 0;
                        modelImgMng.setImg3(img);
                        callBack.modelMsg(3, HexUtil.bytesToHexString(feature), bitmap);
                        modelImgMng.setFeature3(feature);
                        modelImgMng.reset();
                        //mdDeviceBinder.closeDevice(0);
                        //bOpen = false;
                    } else {//第三次建模从图片中取特征值无效
                        modOkProgress = 2;
                        if (++tipTimes[1] <= 3) {
                            callBack.modelMsg(2, context.getString(R.string.same_finger), bitmap);
                        }
                    }
                } else {
                    modOkProgress = 2;
                    if (++tipTimes[1] <= 3) {
                        callBack.modelMsg(2, context.getString(R.string.same_finger), bitmap);
                    } else {//连续超过3次放了不同手指则忽略此次建模重来
                        modOkProgress = 0;
                        modelImgMng.reset();
                        callBack.modelMsg(2, context.getString(R.string.same_finger), bitmap);
                    }
                }
            } else {
                modOkProgress = 0;
                modelImgMng.reset();
            }
        }

    }

    public String identifyNewImg(final List<AllUser> peoples) {
        final int nThreads = peoples.size() / 1000 + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<String>> futures = new ArrayList();
        for (int i = 0; i < nThreads; i++) {
            List<AllUser> subListPeople = new ArrayList<>();
            if (i == nThreads - 1) {
                subListPeople = peoples.subList(1000 * i, peoples.size());
            } else {
                subListPeople = peoples.subList(1000 * i, 1000 * (i + 1));
            }

            final List<AllUser> finalSubListPeople = subListPeople;
            Callable<String> task = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    int[] pos = new int[1];
                    float[] score = new float[1];
                    StringBuffer sb = new StringBuffer();
                    String[] uids = new String[1000];
                    int position = 0;
                    for (AllUser userBean : finalSubListPeople) {
                        sb.append(userBean.getFingerprint());
                        uids[position] = userBean.getPhone();
                        position++;

                    }
                    byte[] allFeaturesBytes = HexUtil.hexStringToByte(sb.toString());
                    boolean identifyResult = MicroFingerVein.fv_index(allFeaturesBytes, allFeaturesBytes.length / 3352, img, pos, score);
                    identifyResult = identifyResult && score[0] > IDENTIFY_SCORE_THRESHOLD;//得分是否达标
                    if (identifyResult) {//比对通过且得分达标时打印此手指绑定的用户名
                        String uid = uids[pos[0]];
                        return uid;
                    } else {
                        return null;

                    }
                }
            };

            futures.add(executorService.submit(task));

        }
        for (Future<String> future : futures) {
            try {
                Log.d("future=", future.get() + "");
                if (!TextUtils.isEmpty(future.get())) {
                    return future.get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        return null;
    }


    private List<MdDevice> mdDevicesList = new ArrayList<MdDevice>();
    public static MdDevice mdDevice;
    private final int MSG_REFRESH_LIST = 0;
    private Handler listManageH = new Handler(new Handler.Callback() {

        @Override

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LIST: {
                    mdDevicesList.clear();
                    mdDevicesList = getDevList();
                    if (mdDevicesList.size() > 0) {
                        mdDevice = mdDevicesList.get(0);
                    } else {
                        listManageH.sendEmptyMessageDelayed(MSG_REFRESH_LIST, 1500L);

                    }
                    break;
                }

            }
            return false;

        }

    });

    private List<MdDevice> getDevList() {
        List<MdDevice> mdDevList = new ArrayList<MdDevice>();
        if (mdDeviceBinder != null) {
            int deviceCount = MicroFingerVein.fvdev_get_count();
            for (int i = 0; i < deviceCount; i++) {
                MdDevice mdDevice = new MdDevice();
                mdDevice.setNo(i);
                mdDevice.setIndex(mdDeviceBinder.getDeviceNo(i));
                mdDevList.add(mdDevice);
            }
        } else {
            Log.e(Tag, "microFingerVein not initialized by MdUsbService yet,wait a moment...");
        }
        return mdDevList;

    }

    private ServiceConnection mdSrvConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mdDeviceBinder = (MdUsbService.MyBinder) service;
            if (mdDeviceBinder != null) {
                mdDeviceBinder.setOnUsbMsgCallback(mdUsbMsgCallback);
                listManageH.sendEmptyMessage(MSG_REFRESH_LIST);
                Log.e(Tag, "bind MdUsbService success.");
            } else {
                Log.e(Tag, "bind MdUsbService failed.");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(Tag, "disconnect MdUsbService.");
        }
    };

    private MdUsbService.UsbMsgCallback mdUsbMsgCallback = new MdUsbService.UsbMsgCallback() {
        @Override
        public void onUsbConnSuccess(String usbManufacturerName, String usbDeviceName) {
            String newUsbInfo = "USB厂商：" + usbManufacturerName + "  \nUSB节点：" + usbDeviceName;
            Log.e(Tag, newUsbInfo);
        }

        @Override
        public void onUsbDisconnect() {
            Log.e(Tag, "USB连接已断开");
        }

    };

    public void unBindService() {
        context.unbindService(mdSrvConn);
    }

}
