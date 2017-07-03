package com.telephony.module.PhoneState;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.telephony.module.TelephonyActionModule;

/**
 * Created by Rafael on 03/07/17.
 */

public class TelephonyCallStateModule extends ReactContextBaseJavaModule
        implements Application.ActivityLifecycleCallbacks,
        TelephonyCallStateListener.PhoneCallStateUpdate {

    private ReactApplicationContext reactContext;
    private TelephonyManager telephonyManager;
    private TelephonyCallStateListener telephonyPhoneStateListener;
    private TelephonyActionModule jsModule = null;

    public TelephonyCallStateModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        jsModule = this.reactContext.getJSModule(TelephonyActionModule.class);
    }

    @ReactMethod
    public void startListener() {
        telephonyManager = (TelephonyManager) this.reactContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        telephonyPhoneStateListener = new TelephonyCallStateListener(this);
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

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public String getName() {
        return "PhoneState";
    }

    @Override
    public void phoneCallStateUpdated(int state, String incomingNumber) {
        if (jsModule == null) {
            jsModule = this.reactContext.getJSModule(TelephonyActionModule.class);
        }

        jsModule.callStateUpdated(incomingNumber);
    }
}
