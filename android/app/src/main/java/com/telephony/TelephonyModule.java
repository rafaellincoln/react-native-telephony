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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rafael on 03/07/17.
 */

public class TelephonyModule extends ReactContextBaseJavaModule
        implements TelephonyListener.PhoneCallStateUpdate, LifecycleEventListener {

    private ReactApplicationContext mReactContext;
    private TelephonyManager telephonyManager;
    private TelephonyListener telephonyPhoneStateListener;
    private String PHONE_STATE_LISTENER = "Telephony-PhoneStateListener";

    private String LISTEN_CALL_FORWARDING_INDICATOR = "LISTEN_CALL_FORWARDING_INDICATOR";
    private String LISTEN_CALL_STATE = "LISTEN_CALL_STATE";
    private String LISTEN_CELL_INFO = "LISTEN_CELL_INFO";
    private String LISTEN_CELL_LOCATION = "LISTEN_CELL_LOCATION";
    private String LISTEN_DATA_ACTIVITY = "LISTEN_DATA_ACTIVITY";
    private String LISTEN_DATA_CONNECTION_STATE = "LISTEN_DATA_CONNECTION_STATE";
    private String LISTEN_MESSAGE_WAITING_INDICATOR = "LISTEN_MESSAGE_WAITING_INDICATOR";
    private String LISTEN_SERVICE_STATE = "LISTEN_SERVICE_STATE";
    private String LISTEN_SIGNAL_STRENGTHS = "LISTEN_SIGNAL_STRENGTHS";

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
    public void startListener(int events) {
        telephonyManager = (TelephonyManager) mReactContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        telephonyPhoneStateListener = new TelephonyListener(this);
        telephonyManager.listen(telephonyPhoneStateListener, events);

    }

    @ReactMethod
    public void stopListener() {
        telephonyManager.listen(telephonyPhoneStateListener,
                PhoneStateListener.LISTEN_NONE);
        telephonyManager = null;
        telephonyPhoneStateListener = null;
    }

    @Override
    public String getName() {
        return "Telephony";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(LISTEN_CALL_FORWARDING_INDICATOR, PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
        constants.put(LISTEN_CALL_STATE, PhoneStateListener.LISTEN_CALL_STATE);
        constants.put(LISTEN_CELL_INFO, PhoneStateListener.LISTEN_CELL_INFO);
        constants.put(LISTEN_CELL_LOCATION, PhoneStateListener.LISTEN_CELL_LOCATION);
        constants.put(LISTEN_DATA_ACTIVITY, PhoneStateListener.LISTEN_DATA_ACTIVITY);
        constants.put(LISTEN_DATA_CONNECTION_STATE, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        constants.put(LISTEN_MESSAGE_WAITING_INDICATOR, PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);
        constants.put(LISTEN_SERVICE_STATE, PhoneStateListener.LISTEN_SERVICE_STATE);
        constants.put(LISTEN_SIGNAL_STRENGTHS, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        return constants;
    }

    @Override
    public void phoneCallStateUpdated(int state, String incomingNumber) {
        WritableMap map = Arguments.createMap();
        map.putInt("state", state);
        map.putString("incomingNumber", incomingNumber);
        sendEvent(PHONE_STATE_LISTENER, map);
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
