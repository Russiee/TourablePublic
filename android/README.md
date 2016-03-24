# Android App

This is the readme for the Android Tourable client app, built as part of a software engineering project at King's College London. The folowing information details the key features of the app, along with information on how to open and run it. The app supports the viewing of tours created on the Tourable CMS system. For more information see tourable.org

**Main features**
- Online and offline viewing of tour content
- The ability for admins to push updates to previously downloaded tours
- Support for unlimited text, image and video content
- Interactive quizes that intergrated directly into the tour content

**Security features**

Tours may contain sensitive information so we have included a range of tools to control access rights:
- HTTPS throughout
- Tour access is controlled by a key which can have an expiry or can be revoked at any time.
- Tours with out of date or revoked keys are deleted from the user's device automatically.

This assumes you are running API 21+

# Installation

To run this app, open it in Android Studio (```touring/android``` will appear as an Android Studio project). Then run it on your device or emulator as normal.

# Testing

The Tourable Android app comes with a full suite of Unit, Performance and UI Tests, which test all main features of the app and its logic. Over 100 tests are included.


To run tests, expand:
   - The android folder
   - App
   - Src
   - AndroidTest
   - Right click on Java and click on "Run 'All tests'"
   - The UI tests will now be executed before the Unit tests.
 
- Some of the tests rely on Aysnchonus methods to check API connectivity is working correctly. If  the internet connection on the test machine is very slow (<1.5Mbps) They may time out or fail due to the delay in connecting. We recommend testing on an internet connection with at least 3G speeds, so 3.5Mbps+  

# Licence 

MIT

# Version

1.0.1
