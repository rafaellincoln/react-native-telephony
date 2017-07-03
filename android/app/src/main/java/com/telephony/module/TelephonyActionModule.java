package com.telephony.module;

import com.facebook.react.bridge.JavaScriptModule;

/**
 * Created by Rafael on 03/07/17.
 */

public interface TelephonyActionModule extends JavaScriptModule {
    void callStateUpdated(String state);
}
