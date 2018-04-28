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
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
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

    private int CALL_STATE_IDLE = 0;
    private int CALL_STATE_RINGING = 1;
    private int CALL_STATE_OFFHOOK = 2;

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
        if (telephonyManager == null || telephonyPhoneStateListener == null) {
            return;
        }
        telephonyManager.listen(telephonyPhoneStateListener,
                PhoneStateListener.LISTEN_NONE);
        telephonyManager = null;
        telephonyPhoneStateListener = null;
    }

    @ReactMethod
    public void isNetworkRoaming(Callback successCallback) {
        successCallback.invoke(telephonyManager.isNetworkRoaming());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @ReactMethod
    public void getCellInfo(Callback successCallback) {

        WritableArray mapArray = Arguments.createArray();

        List<CellInfo> cellInfo = telephonyManager.getAllCellInfo();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || cellInfo == null) {
            successCallback.invoke(mapArray);
            return;
        }

        int i = 0;

        for (CellInfo info : cellInfo) {
            WritableMap mapCellIdentity = Arguments.createMap();
            WritableMap mapCellSignalStrength = Arguments.createMap();
            WritableMap map = Arguments.createMap();

            map.putInt("key", i);

            if (info instanceof CellInfoGsm) {
                CellIdentityGsm cellIdentity = ((CellInfoGsm) info).getCellIdentity();
                map.putString("connectionType", "GSM");

                mapCellIdentity.putInt("cid", cellIdentity.getCid());
                mapCellIdentity.putInt("lac", cellIdentity.getLac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("psc", cellIdentity.getPsc());

                CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) info).getCellSignalStrength();
                mapCellSignalStrength.putInt("asuLevel", cellSignalStrengthGsm.getAsuLevel());
                mapCellSignalStrength.putInt("dBm", cellSignalStrengthGsm.getDbm());
                mapCellSignalStrength.putInt("level", cellSignalStrengthGsm.getLevel());
            } else if (info instanceof CellInfoWcdma && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                CellIdentityWcdma cellIdentity = ((CellInfoWcdma) info).getCellIdentity();
                map.putString("connectionType", "WCDMA");

                mapCellIdentity.putInt("cid", cellIdentity.getCid());
                mapCellIdentity.putInt("lac", cellIdentity.getLac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("psc", cellIdentity.getPsc());

                CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) info).getCellSignalStrength();

                mapCellSignalStrength.putInt("asuLevel", cellSignalStrengthWcdma.getAsuLevel());
                mapCellSignalStrength.putInt("dBm", cellSignalStrengthWcdma.getDbm());
                mapCellSignalStrength.putInt("level", cellSignalStrengthWcdma.getLevel());
            } else if (info instanceof CellInfoLte) {
                if(info.isRegistered()) {
                    mapCellIdentity.putBoolean("servingCellFlag", info.isRegistered());
                } else {
                    mapCellIdentity.putBoolean("servingCellFlag", info.isRegistered());
                }
                CellIdentityLte cellIdentity = ((CellInfoLte) info).getCellIdentity();
                map.putString("connectionType", "LTE");

                mapCellIdentity.putInt("cid", cellIdentity.getCi());
                mapCellIdentity.putInt("tac", cellIdentity.getTac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("pci", cellIdentity.getPci());
                getEarfcn(mapCellIdentity, cellIdentity);

                String cellIdHex = decToHex(cellIdentity.getCi());
                boolean cellIdHexIsEmpty = cellIdHex == null || cellIdHex.isEmpty() || cellIdHex.length() < 2;
                String eNodeBHex = cellIdHexIsEmpty ? "0" : cellIdHex.substring(0, cellIdHex.length() - 2);
                mapCellIdentity.putInt("eNodeB", hexToDec(eNodeBHex));
                String localCellIdHex = cellIdHexIsEmpty ? "0" : cellIdHex.substring(cellIdHex.length() - 2, cellIdHex.length());
                mapCellIdentity.putInt("localCellId", hexToDec(localCellIdHex));

                CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) info).getCellSignalStrength();

                mapCellSignalStrength.putInt("asuLevel", cellSignalStrengthLte.getAsuLevel());
                mapCellSignalStrength.putInt("dBm", cellSignalStrengthLte.getDbm());
                mapCellSignalStrength.putInt("level", cellSignalStrengthLte.getLevel());
                mapCellSignalStrength.putInt("timingAdvance", cellSignalStrengthLte.getTimingAdvance());

            } else if (info instanceof CellInfoCdma) {
                CellIdentityCdma cellIdentity = ((CellInfoCdma) info).getCellIdentity();
                map.putString("connectionType", "CDMA");

                mapCellIdentity.putInt("basestationId", cellIdentity.getBasestationId());
                mapCellIdentity.putInt("latitude", cellIdentity.getLatitude());
                mapCellIdentity.putInt("longitude", cellIdentity.getLongitude());
                mapCellIdentity.putInt("networkId", cellIdentity.getNetworkId());
                mapCellIdentity.putInt("systemId", cellIdentity.getSystemId());

                CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) info).getCellSignalStrength();

                mapCellSignalStrength.putInt("asuLevel", cellSignalStrengthCdma.getAsuLevel());
                mapCellSignalStrength.putInt("cmdaDbm", cellSignalStrengthCdma.getCdmaDbm());
                mapCellSignalStrength.putInt("cmdaEcio", cellSignalStrengthCdma.getCdmaEcio());
                mapCellSignalStrength.putInt("cmdaLevl", cellSignalStrengthCdma.getCdmaLevel());
                mapCellSignalStrength.putInt("dBm", cellSignalStrengthCdma.getDbm());
                mapCellSignalStrength.putInt("evdoDbm", cellSignalStrengthCdma.getEvdoDbm());
                mapCellSignalStrength.putInt("evdoEcio", cellSignalStrengthCdma.getEvdoEcio());
                mapCellSignalStrength.putInt("evdoLevel", cellSignalStrengthCdma.getEvdoLevel());
                mapCellSignalStrength.putInt("evdoSnr", cellSignalStrengthCdma.getEvdoSnr());
                mapCellSignalStrength.putInt("level", cellSignalStrengthCdma.getLevel());
            }

            map.putMap("cellIdentity", mapCellIdentity);
            map.putMap("cellSignalStrength", mapCellSignalStrength);

            mapArray.pushMap(map);
            i++;
        }

        successCallback.invoke(mapArray);
    }

    @TargetApi(24)
    public void getEarfcn(WritableMap mapCellIdentity, CellIdentityLte cellIdentity) {
        if (android.os.Build.VERSION.SDK_INT >= 24){
            // Do something for nougat and above versions
            mapCellIdentity.putInt("earfcn", cellIdentity.getEarfcn());
        } else{
            // do something for phones running an SDK before lollipop
            mapCellIdentity.putInt("earfcn", 0);
        }
    }

    @ReactMethod
    public void getPhoneInfo(Callback successCallBack) {
        WritableMap mapPhoneInfo = Arguments.createMap();

        mapPhoneInfo.putString("imsi", telephonyManager.getSubscriberId().toString());
        mapPhoneInfo.putString("imei", telephonyManager.getDeviceId());
        mapPhoneInfo.putString("mdn", telephonyManager.getLine1Number());
        mapPhoneInfo.putString("model", Build.MANUFACTURER
            + " " + Build.MODEL + " " + Build.VERSION.RELEASE
            + " " + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName());
        successCallBack.invoke(mapPhoneInfo);
    }

    @ReactMethod
    public void getNetworkClass(Callback successCallback) {
        int networkType = telephonyManager.getNetworkType();
        String network;

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                network = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                network = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                network = "4G";
                break;
            default:
                network = "Unknown";
                break;
        }

        successCallback.invoke(network);
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

        constants.put("DATA_ACTIVITY_NONE", DATA_ACTIVITY_NONE);
        constants.put("DATA_ACTIVITY_IN", DATA_ACTIVITY_IN);
        constants.put("DATA_ACTIVITY_OUT", DATA_ACTIVITY_OUT);
        constants.put("DATA_ACTIVITY_INOUT", DATA_ACTIVITY_INOUT);
        constants.put("DATA_ACTIVITY_DORMANT", DATA_ACTIVITY_DORMANT);

        constants.put("DATA_DISCONNECTED", DATA_DISCONNECTED);
        constants.put("DATA_CONNECTING", DATA_CONNECTING);
        constants.put("DATA_CONNECTED", DATA_CONNECTED);
        constants.put("DATA_SUSPENDED", DATA_SUSPENDED);

        constants.put("CALL_STATE_IDLE", CALL_STATE_IDLE);
        constants.put("CALL_STATE_RINGING", CALL_STATE_RINGING);
        constants.put("CALL_STATE_OFFHOOK", CALL_STATE_OFFHOOK);

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

        sendEvent(PHONE_STATE_LISTENER, result);
    }

    @Override
    public void phoneCallForwardingIndicatorUpdated(boolean cfi) {
        WritableMap map = Arguments.createMap();
        map.putBoolean("cfi", cfi);

        WritableMap result = Arguments.createMap();
        result.putString("type", "LISTEN_CALL_FORWARDING_INDICATOR");
        result.putMap("data", map);

        sendEvent(PHONE_STATE_LISTENER, result);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void phoneCellInfoUpdated(List<CellInfo> cellInfo) {
        WritableArray mapArray = Arguments.createArray();

        if (cellInfo == null) { return; }

        int i = 0;

        for (CellInfo info : cellInfo) {
            WritableMap mapCellIdentity = Arguments.createMap();
            WritableMap mapCellSignalStrength = Arguments.createMap();
            WritableMap map = Arguments.createMap();

            map.putInt("key", i);

            if (info instanceof CellInfoGsm) {
                CellIdentityGsm cellIdentity = ((CellInfoGsm) info).getCellIdentity();
                map.putString("connectionType", "GSM");

                mapCellIdentity.putInt("cid", cellIdentity.getCid());
                mapCellIdentity.putInt("lac", cellIdentity.getLac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("psc", cellIdentity.getPsc());

                CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) info).getCellSignalStrength();
                mapCellSignalStrength.putInt("asuLevel", cellSignalStrengthGsm.getAsuLevel());
                mapCellSignalStrength.putInt("dBm", cellSignalStrengthGsm.getDbm());
                mapCellSignalStrength.putInt("level", cellSignalStrengthGsm.getLevel());
            } else if (info instanceof CellInfoWcdma && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                CellIdentityWcdma cellIdentity = ((CellInfoWcdma) info).getCellIdentity();
                map.putString("connectionType", "WCDMA");

                mapCellIdentity.putInt("cid", cellIdentity.getCid());
                mapCellIdentity.putInt("lac", cellIdentity.getLac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("psc", cellIdentity.getPsc());

                CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) info).getCellSignalStrength();

                mapCellSignalStrength.putInt("asuLevel", cellSignalStrengthWcdma.getAsuLevel());
                mapCellSignalStrength.putInt("dBm", cellSignalStrengthWcdma.getDbm());
                mapCellSignalStrength.putInt("level", cellSignalStrengthWcdma.getLevel());
            } else if (info instanceof CellInfoLte) {
                CellIdentityLte cellIdentity = ((CellInfoLte) info).getCellIdentity();
                map.putString("connectionType", "LTE");

                mapCellIdentity.putInt("ci", cellIdentity.getCi());
                mapCellIdentity.putInt("tac", cellIdentity.getTac());
                mapCellIdentity.putInt("mcc", cellIdentity.getMcc());
                mapCellIdentity.putInt("mnc", cellIdentity.getMnc());
                mapCellIdentity.putInt("pci", cellIdentity.getPci());

                CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) info).getCellSignalStrength();

                mapCellSignalStrength.putInt("asuLevel", cellSignalStrengthLte.getAsuLevel());
                mapCellSignalStrength.putInt("dBm", cellSignalStrengthLte.getDbm());
                mapCellSignalStrength.putInt("level", cellSignalStrengthLte.getLevel());
                mapCellSignalStrength.putInt("timingAdvance", cellSignalStrengthLte.getTimingAdvance());

            } else if (info instanceof CellInfoCdma) {
                CellIdentityCdma cellIdentity = ((CellInfoCdma) info).getCellIdentity();
                map.putString("connectionType", "CDMA");

                mapCellIdentity.putInt("basestationId", cellIdentity.getBasestationId());
                mapCellIdentity.putInt("latitude", cellIdentity.getLatitude());
                mapCellIdentity.putInt("longitude", cellIdentity.getLongitude());
                mapCellIdentity.putInt("networkId", cellIdentity.getNetworkId());
                mapCellIdentity.putInt("systemId", cellIdentity.getSystemId());

                CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) info).getCellSignalStrength();

                mapCellSignalStrength.putInt("asuLevel", cellSignalStrengthCdma.getAsuLevel());
                mapCellSignalStrength.putInt("cmdaDbm", cellSignalStrengthCdma.getCdmaDbm());
                mapCellSignalStrength.putInt("cmdaEcio", cellSignalStrengthCdma.getCdmaEcio());
                mapCellSignalStrength.putInt("cmdaLevl", cellSignalStrengthCdma.getCdmaLevel());
                mapCellSignalStrength.putInt("dBm", cellSignalStrengthCdma.getDbm());
                mapCellSignalStrength.putInt("evdoDbm", cellSignalStrengthCdma.getEvdoDbm());
                mapCellSignalStrength.putInt("evdoEcio", cellSignalStrengthCdma.getEvdoEcio());
                mapCellSignalStrength.putInt("evdoLevel", cellSignalStrengthCdma.getEvdoLevel());
                mapCellSignalStrength.putInt("evdoSnr", cellSignalStrengthCdma.getEvdoSnr());
                mapCellSignalStrength.putInt("level", cellSignalStrengthCdma.getLevel());
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

        sendEvent(PHONE_STATE_LISTENER, result);
    }

    @Override
    public void phoneDataConnectionStateUpdated(int direction) {
        WritableMap map = Arguments.createMap();
        map.putInt("direction", direction);

        WritableMap result = Arguments.createMap();
        result.putString("type", "LISTEN_DATA_CONNECTION_STATE");
        result.putMap("data", map);

        sendEvent(PHONE_STATE_LISTENER, result);
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

        sendEvent(PHONE_STATE_LISTENER, result);
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
        stopListener();
    }

    public String decToHex(int dec) {
        return String.format("%x", dec);
    }

    public int hexToDec(String hex) {
        return Integer.parseInt(hex, 16);
    }
}
