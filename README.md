# Purpose

This minimalist app demonstrates that single device Companion Device Manager association works on a particular Android phone as described here:  https://developer.android.com/develop/connectivity/bluetooth/companion-device-pairing
On some phones, particularly the Samsung Galaxy S21 with Android 14, this does not work as expected and the dialog described in Figure 2 of the above link never appears. This works fine on other Samsung models with Andorid 14.

# Prerequisites

1. A Bluetooth LE peripheral that can be made connectable while advertising a known name.
2. And Android phone to test
   
# How to test

1. Edit MainActivity.kt and type in the advertised name of your blueooth device used for testing here:
        `val deviceName =  // TODO: Put your expected advertised name of your device here, e.g. "MyDevice"`

2. Using Android Studio, compile and run on your test Android phone
3. Follow the app's prompts to start Companion Device Manager association on the phone.
4. If Companion Device Manager works as expected, you will see an allow dialog as part of this process.  Tap the allow button if you see this and you will get a success confirmation.
