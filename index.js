/*
* @providesModule react-native-telephony
*/
import { NativeModules, NativeAppEventEmitter, Platform } from 'react-native'

const RNTelephony = Platform.OS === 'ios' ? {} : NativeModules.Telephony

const EVENT_PHONE_STATE = 'phoneState'

const PHONE_STATE_LISTENER = 'Telephony-PhoneStateListener'

const telephony = RNTelephony

if (Platform.OS === 'android') {
  telephony.addEventListener = (events, handler) => {
    RNTelephony && RNTelephony.startListener(events)
      NativeAppEventEmitter.addListener(
        PHONE_STATE_LISTENER,
        (result) => {
            handler(result);
        }
    );
  }
}

export default telephony