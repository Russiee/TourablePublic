# iOS README

This is the readme for the iOS  Tourable client app, built as part of a software engineering project at King's College London. The folowing information details the key features of the app, along with information on how to open and run it in Xcode. The app supports the viewing of tours created on the Tourable CMS system. For more information see tourable.org

**Main features**
- Online and offline viewing of tour content
- The ability for admins to push updates to previously downloaded tours
- Support for unlimited text, image and video content
- Interactive quizes that can be intergrated directly into the tour content

**Security features**

Tours may contain sensitive information so we have included a range of tools to control access rights:
- HTTPS throughout
- Tour access is controlled by a key which can have an expiry or can be revoked at any time.
- Tours with out of date or revoked keys are deleted from the user's device automatically.

This assumes you are running Xcode 7.2, the latest available at the time of publication.

# Installation

The iOS App for tourable is written entirely in native swift 2.0. The source code is provided unsigned, so the project we need rebuilding under a new AppleID in order to be compiled onto a runable device/simulator. The app requires the AVKit.framework from Apple to be linked in order to play video. This can be imported into the project as follows:

- In the project editor, select the Tourable target.
- Click Build Phases at the top of the project editor.
- Open the Link Binary With Libraries section.
- Click the Add button (+) to add a library or framework.
- Select a AVKit.framework from the list and click Add.

If these steps are followed the app should be runnable on a compatible device or simulator simply by pressing the "run" button in Xcode. 

# Testing

The Tourable iOS app comes with a full suite of Unit, Performance and UI Tests, which test all main features of the app and its logic. Over 80 tests are included, with code coverage for Unit and UI Tests at 83%.

These tests are included in the Xcode project and can be run directly from there after instalation. To run the full test suite simply long press on the "run" button in Xcode and choose "test".

**The test suite makes some assumptions that should be considered when running:**

- The suite assumes it is being run on a fresh install of Tourable on a simulator running iOS 9.2 or above. With this in mind, please remember to re-install the app before and after running the tests to ensure accuracy. 

- Becuase Xcode runs the tests in an arbitrary order we have found it is important to ensure that UI Tests run *before* the Unit tests if running the entire set at once. To ensure that UI tests run first follow the these steps:
   - In Xcode, in the navigation bar: Click Product > scheme > Edit Scheme...
   - In the drop down that appears, select "Test" on the left hand side
   - In the "Tests" table on this page, if TourableUITests apears below TourableTests, drag it up one level to be above TourableTests. 
   - Click close, clean the project with shift-command-k and then run the tests. 
   - The UI tests will now be executed before the Unit tests.
 
- Some of the tests rely on Aysnchonus methods to check API connectivity is working correctly. If  the internet connection on the test machine is very slow (<1.5Mbps) They may time out or fail due to the delay in connecting. We recommend testing on an internet connection with at least 3G speeds, so 3.5Mbps+  

# Licence 

MIT

# Version

1.0.1
