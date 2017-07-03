/*
* @providesModule react-native-telephony
*/
import { NativeModules } from 'react-native'
const BatchedBridge = require('react-native/Libraries/BatchedBridge/BatchedBridge')

// const NativeCallDetector = NativeModules.CallDetectionManager
const PhoneState = NativeModules.PhoneState

const TelephonyActionModule = require('./TelephonyActionModule')
BatchedBridge.registerCallableModule('TelephonyActionModule', TelephonyActionModule)

const PHONE_STATE = 'phoneState'

export default class Telephony {
    addEventListener(event, callback) {
      switch (event) {
        case PHONE_STATE:
            PhoneState && PhoneState.startListener()
            TelephonyActionModule.callback = callback 
          break;
      
        default:
          break;
      }
    }
}
