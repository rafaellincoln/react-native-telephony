## React Native Telephony
[![npm version](https://badge.fury.io/js/react-native-telephony.svg)](https://badge.fury.io/js/react-native-telephony)

## Installation

Add the package to your react-native project in the following way

```shell
yarn add react-native-telephony

```

Link the current package to your react native project

```shell
react-native link react-native-telephony

```

## Usage
There are different hooks that you may get depending on the platform. So if

Its really easy to setup the package. Have a look at the following code snippet

``` javascript
import Telephony from 'react-native-telephony'

componentWillMount() {
  Telephony.addEventListener(Telephony.LISTEN_CALL_STATE | Telephony.LISTEN_DATA_ACTIVITY,
  (event) => {
    if (event.type === 'LISTEN_CALL_STATE') {
      console.log(event.data)
    } else {
      console.log(event.data)
    }
  })

  Telephony.isNetworkRoaming((roaming) => {
    if (roaming) {
      // ...
    }
  })

  Telephony.getNetworkClass((network) => {
    switch(network) {
      case "2G":
        // ...
      break;
      case "3G":
        // ...
      break;
      case "4G":
        // ...
      break;
      default:
        // ...
      break;
    }
  })

  Telephony.getCellInfo((cellInfos) => {
    celInfos.map((info) => {
      switch(info.connectionType) {
        case "CDMA":
          console.log(info.cellIdentity)
          console.log(info.cellSignalStrength)
        break;
        case "WCDMA":
          console.log(info.cellIdentity)
          console.log(info.cellSignalStrength)
        break;
        case "GSM":
          console.log(info.cellIdentity)
          console.log(info.cellSignalStrength)
        break;
        case "LTE":
          console.log(info.cellIdentity)
          console.log(info.cellSignalStrength)
        break;
        default:
          // ...
        break;
      }
    })
  })
}

```

### Events

``` javascript

Telephony.LISTEN_CALL_FORWARDING_INDICATOR
Telephony.LISTEN_CALL_STATE
Telephony.LISTEN_CELL_INFO
Telephony.LISTEN_CELL_LOCATION
Telephony.LISTEN_DATA_ACTIVITY
Telephony.LISTEN_DATA_CONNECTION_STATE
Telephony.LISTEN_MESSAGE_WAITING_INDICATOR
Telephony.LISTEN_SERVICE_STATE
Telephony.LISTEN_SIGNAL_STRENGTHS

```

### LISTEN_CALL_STATE states

``` javascript

Telephony.CALL_STATE_IDLE
Telephony.CALL_STATE_RINGING
Telephony.CALL_STATE_OFFHOOK

```

### LISTEN_DATA_ACTIVITY states

``` javascript

Telephony.DATA_ACTIVITY_NONE
Telephony.DATA_ACTIVITY_IN
Telephony.DATA_ACTIVITY_OUT
Telephony.DATA_ACTIVITY_INOUT
Telephony.DATA_ACTIVITY_DORMANT

```

### LISTEN_DATA_CONNECTION_STATE states

``` javascript

Telephony.DATA_DISCONNECTED
Telephony.DATA_CONNECTING
Telephony.DATA_CONNECTED
Telephony.DATA_SUSPENDED

```

For any problems and doubt raise an issue.
