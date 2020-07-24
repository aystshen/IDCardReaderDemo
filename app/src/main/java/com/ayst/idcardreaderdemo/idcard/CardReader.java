package com.ayst.idcardreaderdemo.idcard;

import com.zkteco.android.IDReader.CardInfo;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;
import com.zkteco.android.biometric.module.idcard.meta.IDPRPCardInfo;

import java.util.concurrent.CountDownLatch;

public class CardReader {
    private static final String TAG = "CardReader";

    private static final boolean DEBUG = false;

    private static final int ID_CARD = 1;
    private static final int IDPRP_CARD = 2;
    private static final int HONGKONG_MACAO_PASSPORT = 3;

    private boolean mActive = true;

    public void activate(boolean active) {
        this.mActive = active;
    }

    public void read(final IDCardReader idCardReader,
                     final CountDownLatch countdownLatch,
                     final IReadCardCallBack callBack) {

        if (null == idCardReader) {
            android.util.Log.e(TAG, "read, idCardReader is null.");
            return;
        }

        try {
            callBack.onRequestDevicePermission();
            idCardReader.open(0);
        } catch (Exception e) {
            android.util.Log.e(TAG, "read, Disconnect. " + e.getMessage());
            callBack.onFail("IdCard reader disconnect");
        }

        new Thread(new Runnable() {
            public void run() {
                android.util.Log.i(TAG, "read, Start...");
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (mActive) {
                        boolean status = false;
                        try {
                            status = idCardReader.getStatus(0);
                        } catch (Exception e) {
                            android.util.Log.e(TAG, "read, getStatus failed, error: "
                                    + e.getMessage());
                        }

                        if (!status) {
                            try {
                                idCardReader.reset(0);
                            } catch (Exception e) {
                                android.util.Log.e(TAG, "read, reset failed, error: "
                                        + e.getMessage());
                            }
                        }

                        try {
                            idCardReader.findCard(0);
                            idCardReader.selectCard(0);
                        } catch (Exception e) {
                            if (DEBUG) android.util.Log.e(TAG, "read, findCard/selectCard failed, error: "
                                    + e.getMessage());
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        int cardType = 0;
                        try {
                            cardType = idCardReader.readCardEx(0, 0);
                        } catch (Exception e) {
                            android.util.Log.e(TAG, "read, readCardEx failed, error: "
                                    + e.getMessage());
                        }

                        if (cardType == ID_CARD) {
                            IDCardInfo idCardInfo = idCardReader.getLastIDCardInfo();
                            callBack.onSuccess(new CardInfo(ID_CARD, idCardInfo));
                            android.util.Log.i(TAG, "read, Found ID_CARD card.");

                        } else if (cardType == IDPRP_CARD) {
                            IDPRPCardInfo idprpCardInfo = idCardReader.getLastPRPIDCardInfo();
                            callBack.onSuccess(new CardInfo(IDPRP_CARD, idprpCardInfo));
                            android.util.Log.i(TAG, "read, Found IDPRP_CARD card.");

                        } else if (cardType == HONGKONG_MACAO_PASSPORT) {
                            IDCardInfo idCardInfo = idCardReader.getLastIDCardInfo();
                            callBack.onSuccess(new CardInfo(HONGKONG_MACAO_PASSPORT, idCardInfo));
                            android.util.Log.i(TAG, "read, Found HONGKONG_MACAO_PASSPORT card.");

                        } else {
                            callBack.onNoCards();
                        }
                    }
                    countdownLatch.countDown();
                }
            }
        }).start();
    }
}
