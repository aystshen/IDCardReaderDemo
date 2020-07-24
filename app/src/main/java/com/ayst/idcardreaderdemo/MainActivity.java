package com.ayst.idcardreaderdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ayst.idcardreaderdemo.idcard.CardReader;
import com.ayst.idcardreaderdemo.idcard.IReadCardCallBack;
import com.zkteco.android.IDReader.CardInfo;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * 身份证识别模块vid
     */
    public static final int IDCARD_READER_VID = 0x0400;

    /**
     * 身份证识别模块pid
     */
    public static final int IDCARD_READER_PID = 0xc35a;

    private static final String ACTION_USB_PERMISSION = "com.ayst.idcardreaderdemo.USB_PERMISSION";

    private TextView mInfoTv;

    // IdCard
    private IDCardReader mIdCardReader;
    private CountDownLatch mCountdownLatch = new CountDownLatch(1);
    private UsbManager mUsbManager = null;
    private BroadcastReceiver mUsbReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoTv = findViewById(R.id.tv_info);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        mUsbReceiver = new UsbBroadCastReceiver();
        registerReceiver(mUsbReceiver, filter);

        requestIdCardUsbPermission();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    /**
     * 读身份证
     */
    private void readIdCard() {
        mInfoTv.setText("请刷卡...");

        Map<String, Object> params = new HashMap<>();
        params.put(ParameterHelper.PARAM_KEY_VID, IDCARD_READER_VID);
        params.put(ParameterHelper.PARAM_KEY_PID, IDCARD_READER_PID);
        mIdCardReader = IDCardReaderFactory.createIDCardReader(getApplicationContext(), TransportType.USB, params);

        if (null != mIdCardReader) {
            CardReader reader = new CardReader();
            reader.read(mIdCardReader, mCountdownLatch, new IReadCardCallBack() {

                @Override
                public void onSuccess(CardInfo idCardInfo) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("姓名：").append(idCardInfo.getName())
                            .append("身份证：").append(idCardInfo.getId())
                            .append("性别：").append(idCardInfo.getSex())
                            .append("生日：").append(idCardInfo.getBirth())
                            .append("住址：").append(idCardInfo.getAddress())
                            .append("有效期：").append(idCardInfo.getValidityTime());
                    mInfoTv.setText(sb.toString());
                }

                @Override
                public void onRequestDevicePermission() {

                }

                @Override
                public void onFail(String error) {

                }

                @Override
                public void onNoCards() {

                }
            });
        }
    }

    /**
     * 请求usb权限
     */
    @SuppressLint("CheckResult")
    private void requestIdCardUsbPermission() {
        mUsbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        boolean hasIdCardReader = false;
        for (UsbDevice device : mUsbManager.getDeviceList().values()) {
            if (device.getVendorId() == IDCARD_READER_VID
                    && device.getProductId() == IDCARD_READER_PID) {
                hasIdCardReader = true;
                if (mUsbManager.hasPermission(device)) {
                    readIdCard();
                } else {
                    Intent intent = new Intent(ACTION_USB_PERMISSION);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this, 0, intent, 0);
                    mUsbManager.requestPermission(device, pendingIntent);
                }
            }
        }

        if (!hasIdCardReader) {
            Log.e(TAG, "requestIdCardUsbPermission, No IdCard reader");
            mInfoTv.setText("未发现身份证模块！");
        }
    }

    /**
     * usb权限广播
     */
    private class UsbBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        readIdCard();
                    } else {
                        Log.e(TAG, "UsbBroadCastReceiver, IdCard permission failed.");
                        mInfoTv.setText("申请USB权限失败！");
                    }
                }
            }
        }
    }
}