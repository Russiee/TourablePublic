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

This app requires Android 5.0 (API 21) as a minimum.

# Installation

To run this app, open it in Android Studio (```touring/android``` will appear as an Android Studio project). Then run it on your device or emulator as normal.

# Testing

The Tourable Android app comes with a full suite of Unit and UI Tests, which test all main features of the app and its logic.


To run tests, expand:
   - The android folder
   - App
   - Src
   - AndroidTest
   - Right click on Java and click on "Run 'All tests'"
   - The UI tests will now be executed before the Unit tests.

# Dependencies

Android libs:
- [Design](https://developer.android.com/tools/support-library/features.html#design) 23+
- [v7 appcompat](https://developer.android.com/tools/support-library/features.html#v7-appcompat) 23+
- [v7 cardview](https://developer.android.com/tools/support-library/features.html#v7-cardview) 23+
- [Testing support library](https://developer.android.com/tools/testing-support-library/index.html) 0.4
- [Espresso-core](https://developer.android.com/tools/testing-support-library/index.html#Espresso) 2.2.1

External libs (testing):
- [JUnit](http://junit.org/junit4/) 4.12
- [Hamcrest](https://github.com/hamcrest/JavaHamcrest) 1.3

Other libs:
- [ExoPlayer](https://github.com/google/ExoPlayer) 1.5.6 - for playing video files
- [AndroidVideoCache](https://github.com/danikula/AndroidVideoCache) 2.3.4 - for caching video URL streams

# Licence 

MIT

# Version

1.0.1
