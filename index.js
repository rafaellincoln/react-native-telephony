/*
* @providesModule react-native-telephony
*/
import { NativeModules, NativeAppEventEmitter } from 'react-native'
const BatchedBridge = require('react-native/Libraries/BatchedBridge/BatchedBridge')

// const NativeCallDetector = NativeModules.CallDetectionManager
const PhoneState = NativeModules.PhoneState

const TelephonyActionModule = require('./TelephonyActionModule')
BatchedBridge.registerCallableModule('TelephonyActionModule', TelephonyActionModule)

const PHONE_STATE = 'phoneState'

const LISTENER_CALL_STATE = 'Telephony-phoneCallStateUpdated'

export default class Telephony {
    static addEventListener(event, handler) {
      switch (event) {
        case PHONE_STATE:
            PhoneState && PhoneState.startListener()
            // TelephonyActionModule.callback = callback 
            NativeAppEventEmitter.addListener(
                LISTENER_CALL_STATE,
                (result) => {
                    handler(result);
                }
            );
          break;
      
        default:
          break;
      }
    }
}
