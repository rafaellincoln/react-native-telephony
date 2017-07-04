package com.telephony;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.List;
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

    private int DATA_ACTIVITY_NONE = 0;
    private int DATA_ACTIVITY_IN = 1;
    private int DATA_ACTIVITY_OUT = 2;
    private int DATA_ACTIVITY_INOUT = 3;
    private int DATA_ACTIVITY_DORMANT = 4;

    private int DATA_DISCONNECTED = 0;
    private int DATA_CONNECTING = 1;
    private int DATA_CONNECTED = 2;
    private int DATA_SUSPENDED = 3;

    public TelephonyModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;

        mReactContext.addLifecycleEventListener(this);

        telephonyManager = (TelephonyManager) mReactContext.getSystemService(
                Context.TELEPHONY_SERVICE);
    }

    private void sendEvent(String eventName, Object params) {
        mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void startListener(int events) {
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @ReactMethod
    public void getSignalStrength(Callback successCallback) {
        int dBm = 0;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CellInfoGsm cellinfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
            CellSignalStrengthGsm cellSignalStrengthGsm;
            cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
            dBm = cellSignalStrengthGsm.getDbm();
        }

        successCallback.invoke(dBm);
    }

    @ReactMethod
    private String getNetworkClass() {
        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
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

        final Map<String, Object> directions = new HashMap<>();
        directions.put("DATA_ACTIVITY_NONE", DATA_ACTIVITY_NONE);
        directions.put("DATA_ACTIVITY_IN", DATA_ACTIVITY_IN);
        directions.put("DATA_ACTIVITY_OUT", DATA_ACTIVITY_OUT);
        directions.put("DATA_ACTIVITY_INOUT", DATA_ACTIVITY_INOUT);
        directions.put("DATA_ACTIVITY_DORMANT", DATA_ACTIVITY_DORMANT);

        constants.put("DATA_ACTIVITY", directions);

        final Map<String, Object> dataActivity = new HashMap<>();
        directions.put("DATA_DISCONNECTED", DATA_DISCONNECTED);
        directions.put("DATA_CONNECTING", DATA_CONNECTING);
        directions.put("DATA_CONNECTED", DATA_CONNECTED);
        directions.put("DATA_SUSPENDED", DATA_SUSPENDED);

        constants.put("DATA_CONNECTION", dataActivity);

        return constants;
    }

    @Override
    public void phoneCallStateUpdated(int state, String incomingNumber) {
        WritableMap map = Arguments.createMap();
        map.putInt("state", state);
        map.putString("incomingNumber", incomingNumber);

        WritableMap result = Arguments.createMap();
        result.putString("type", "LISTEN_CALL_STATE");
        result.putMap("data", map);

        sendEvent(PHONE_STATE_LISTENER, map);
    }

    @Override
    public void phoneCallForwardingIndicatorUpdated(boolean cfi) {
        WritableMap map = Arguments.createMap();
        map.putBoolean("cfi", cfi);

        WritableMap result = Arguments.createMap();
        result.putString("type", "LISTEN_CALL_FORWARDING_INDICATOR");
        result.putMap("data", map);

        sendEvent(PHONE_STATE_LISTENER, map);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void phoneCellInfoUpdated(List<CellInfo> cellInfo) {
        WritableArray mapArray = Arguments.createArray();

        int i = 0;

        for (CellInfo info : cellInfo) {
            WritableMap mapCellIdentity = Arguments.createMap();
            WritableMap mapCellSignalStrength = Arguments.createMap();
            WritableMap map = Arguments.createMap();

            map.putInt("key", i);

            if (info instanceof CellInfoGsm) {
                CellIdentityGsm cellIdentity = ((CellInfoGsm) info).getCellIdentity();
                mapCellIdentity.putString("connectionType", "GSM");
                mapCellIdentity.putInt("cid", cellIdentity.getCid());
                mapCellIdentity.putInt("lac", cellIdentity.getLac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("psc", cellIdentity.getPsc());

                CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) info).getCellSignalStrength();
                mapCellIdentity.putInt("asuLevel", cellSignalStrengthGsm.getAsuLevel());
                mapCellIdentity.putInt("dBm", cellSignalStrengthGsm.getDbm());
                mapCellIdentity.putInt("level", cellSignalStrengthGsm.getLevel());
            } else if (info instanceof CellInfoWcdma && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                CellIdentityWcdma cellIdentity = ((CellInfoWcdma) info).getCellIdentity();
                mapCellIdentity.putString("connectionType", "WCDMA");
                mapCellIdentity.putInt("cid", cellIdentity.getCid());
                mapCellIdentity.putInt("lac", cellIdentity.getLac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("psc", cellIdentity.getPsc());
            } else if (info instanceof CellInfoLte) {
                CellIdentityLte cellIdentity = ((CellInfoLte) info).getCellIdentity();
                mapCellIdentity.putString("connectionType", "LTE");
                mapCellIdentity.putInt("ci", cellIdentity.getCi());
                mapCellIdentity.putInt("tac", cellIdentity.getTac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("pci", cellIdentity.getPci());
            } else if (info instanceof CellInfoCdma) {
                CellIdentityCdma cellIdentity = ((CellInfoCdma) info).getCellIdentity();
                mapCellIdentity.putString("connectionType", "CDMA");
                mapCellIdentity.putInt("basestationId", cellIdentity.getBasestationId());
                mapCellIdentity.putInt("latitude", cellIdentity.getLatitude());
                mapCellIdentity.putInt("longitude", cellIdentity.getLongitude());
                mapCellIdentity.putInt("networkId", cellIdentity.getNetworkId());
                mapCellIdentity.putInt("systemId", cellIdentity.getSystemId());
            }

            map.putMap("cellIdentity", mapCellIdentity);
            map.putMap("cellSignalStrength", mapCellSignalStrength);

            mapArray.pushMap(map);
            i++;
        }

        WritableMap result = Arguments.createMap();
        result.putString("type", "LISTEN_CELL_INFO");
        result.putArray("data", mapArray);

        sendEvent(PHONE_STATE_LISTENER, result);
    }

    @Override
    public void phoneDataActivityUpdated(int direction) {
        WritableMap map = Arguments.createMap();
        map.putInt("direction", direction);

        WritableMap result = Arguments.createMap();
        result.putString("type", "LISTEN_DATA_ACTIVITY");
        result.putMap("data", map);

        sendEvent(PHONE_STATE_LISTENER, map);
    }

    @Override
    public void phoneDataConnectionStateUpdated(int direction) {
        WritableMap map = Arguments.createMap();
        map.putInt("direction", direction);

        WritableMap result = Arguments.createMap();
        result.putString("type", "LISTEN_DATA_CONNECTION_STATE");
        result.putMap("data", map);

        sendEvent(PHONE_STATE_LISTENER, map);
    }

    @Override
    public void phoneSignalStrengthsUpdated(SignalStrength signalStrength) {
        WritableMap map = Arguments.createMap();
        map.putInt("cdmaDbm", signalStrength.getCdmaDbm());
        map.putInt("cdmaEcio()", signalStrength.getCdmaEcio());
        map.putInt("evdoDbm", signalStrength.getEvdoDbm());
        map.putInt("evdoEcio", signalStrength.getEvdoEcio());
        map.putInt("evdoSnr", signalStrength.getEvdoSnr());
        map.putInt("gsmBitErrorRate", signalStrength.getGsmBitErrorRate());
        map.putInt("gsmSignalStrength", signalStrength.getGsmSignalStrength());
        map.putBoolean("gsm", signalStrength.isGsm());

        WritableMap result = Arguments.createMap();
        result.putString("type", "LISTEN_SIGNAL_STRENGTHS");
        result.putMap("data", map);

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
