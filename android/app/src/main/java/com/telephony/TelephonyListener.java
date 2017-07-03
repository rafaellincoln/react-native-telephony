package com.telephony;

import android.telephony.PhoneStateListener;

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

    interface PhoneCallStateUpdate {
        void phoneCallStateUpdated(int state, String incomingNumber);
    }
}
