package com.telephony.module.PhoneState;

import android.telephony.PhoneStateListener;

/**
 * Created by Rafael on 03/07/17.
 */

public class TelephonyCallStateListener extends PhoneStateListener {

    private PhoneCallStateUpdate callStatCallBack;

    public TelephonyCallStateListener(PhoneCallStateUpdate callStatCallBack) {
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
