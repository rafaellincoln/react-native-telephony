/*
* @providesModule react-native-telephony
*/
import { NativeModules, NativeAppEventEmitter } from 'react-native'

const RNTelephony = NativeModules.Telephony

const EVENT_PHONE_STATE = 'phoneState'

const LISTENER_CALL_STATE = 'Telephony-phoneCallStateUpdated'

export default class Telephony {
    static addEventListener(event, handler) {
      switch (event) {
        case EVENT_PHONE_STATE:
            RNTelephony && RNTelephony.startListener()
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

    static teste() {
      RNTelephony.teste((result) => {
        console.log('Teste', result)
      })
    }
}
