package com.telephony;

import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import java.util.List;

/**
 * Created by Rafael on 03/07/17.
 */

public class TelephonyListener extends PhoneStateListener {

    private PhoneCallStateUpdate callStatCallBack;

    public TelephonyListener(PhoneCallStateUpdate callStatCallBack) {
        super();
        this.callStatCallBack = callStatCallBack;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        this.callStatCallBack.phoneCallStateUpdated(state, incomingNumber);
    }

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        this.callStatCallBack.phoneCallForwardingIndicatorUpdated(cfi);
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        this.callStatCallBack.phoneCellInfoUpdated(cellInfo);
    }

//    @Override
//    public void onCellLocationChanged(CellLocation location) {
//        this.callStatCallBack.phoneCellLocationUpdated(location);
//    }

    @Override
    public void onDataActivity(int direction) {
        this.callStatCallBack.phoneDataActivityUpdated(direction);
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        this.callStatCallBack.phoneDataConnectionStateUpdated(state);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        this.callStatCallBack.phoneSignalStrengthsUpdated(signalStrength);
    }

    public interface PhoneCallStateUpdate {
        void phoneCallStateUpdated(int state, String incomingNumber);
        void phoneCallForwardingIndicatorUpdated(boolean cfi);
        void phoneCellInfoUpdated(List<CellInfo> cellInfo);
        // void phoneCellLocationUpdated(CellLocation location);
        void phoneDataActivityUpdated(int direction);
        void phoneDataConnectionStateUpdated(int state);
        void phoneSignalStrengthsUpdated(SignalStrength signalStrength);
    }
}
