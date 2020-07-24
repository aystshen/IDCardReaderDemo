package com.ayst.idcardreaderdemo.idcard;

import com.zkteco.android.IDReader.CardInfo;

public interface IReadCardCallBack {
    void onSuccess(CardInfo idCardInfo);
    void onRequestDevicePermission();
    void onFail(String error);
    void onNoCards();
}
