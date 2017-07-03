/*
* @providesModule react-native-telephony
*/
import { NativeModules, NativeEventEmitter, Platform } from 'react-native'
const BatchedBridge = require('react-native/Libraries/BatchedBridge/BatchedBridge')

// const NativeCallDetector = NativeModules.CallDetectionManager
const PhoneState = NativeModules.PhoneState

var TelephonyActionModule = require('./TelephonyActionModule')
BatchedBridge.registerCallableModule('TelephonyActionModule', TelephonyActionModule)

class CallDetectorManager {

    subscription;
    callback
    constructor(callback) {
        this.callback = callback
        if (Platform.OS === 'ios') {
          return
            // NativeCallDetector && NativeCallDetector.startListener()
            // this.subscription = new NativeEventEmitter(NativeCallDetector)
            // this.subscription.addListener('PhoneCallStateUpdate', callback);
        }
        else {
            PhoneState && PhoneState.startListener()
            TelephonyActionModule.callback = callback
        }
    }

    dispose() {
    	NativeCallDetector && NativeCallDetector.stopListener()
    	NativeCallDetectorAndroid && NativeCallDetectorAndroid.stopListener()
        CallStateUpdateActionModule.callback = undefined
      if(this.subscription) {
          this.subscription.removeAllListeners('PhoneCallStateUpdate');
          this.subscription = undefined
      }
    }
}

export default module.exports = CallDetectorManager;