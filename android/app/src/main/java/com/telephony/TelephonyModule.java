package com.telephony;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by Rafael on 03/07/17.
 */

public class TelephonyModule extends ReactContextBaseJavaModule
        implements TelephonyListener.PhoneCallStateUpdate, LifecycleEventListener {

    private ReactApplicationContext mReactContext;
    private TelephonyManager telephonyManager;
    private TelephonyListener telephonyPhoneStateListener;

    public TelephonyModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;

        mReactContext.addLifecycleEventListener(this);
    }

    private void sendEvent(String eventName, Object params) {
        mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void startListener() {
        telephonyManager = (TelephonyManager) mReactContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        telephonyPhoneStateListener = new TelephonyListener(this);
        telephonyManager.listen(telephonyPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

    }

    @ReactMethod
    public void stopListener() {
        telephonyManager.listen(telephonyPhoneStateListener,
                PhoneStateListener.LISTEN_NONE);
        telephonyManager = null;
        telephonyPhoneStateListener = null;
    }

    @ReactMethod
    public void teste(Callback successCallback) {
        successCallback.invoke(1 | 2);
    }

    @Override
    public String getName() {
        return "Telephony";
    }

    @Override
    public void phoneCallStateUpdated(int state, String incomingNumber) {
        WritableMap map = Arguments.createMap();
        map.putInt("state", state);
        map.putString("incomingNumber", incomingNumber);
        sendEvent("Telephony-phoneCallStateUpdated", map);
    }

    @Override
    public void onHostResume() {
        // Activity `onResume`
    }

    @Override
    public void onHostPause() {
        // Activity `onPause`
    }

    @Override
    public void onHostDestroy() {
        // Activity `onDestroy`
    }
}
