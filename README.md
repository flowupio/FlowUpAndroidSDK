![FlowUp Logo][flowuplogo] FlowUp [![Build Status](https://travis-ci.com/Karumi/FlowUpAndroidSDK.svg?token=Kb2RqPaWxFZ8XPxpqvqz&branch=master)](https://travis-ci.com/Karumi/FlowUpAndroidSDK) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.flowup/android-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.flowup/android-sdk)
==============================

FlowUp, mobile real time applications performance monitoring solution!

FlowUp helps you to radically improve your mobile applications performance with actionable insight into real-time key reports including frame time, frames per second, bandwidth, memory consumption, CPU/GPU performance, disk usage and much more. Available for Android and iOS soon.

Getting started
---------------

Add the FlowUp library to your ``build.gradle`:

```groovy
 dependencies {
   compile 'io.flowup:android-sdk:<LAST_VERSION_RELEASED>'
   testCompile 'io.flowup:android-sdk-no-op:<LAST_VERSION_RELEASED>'
 }
```

Initialize FlowUp in your ``Application`` class:

```java
@Override public void onCreate() {
	super.onCreate();
   FlowUp.Builder.with(this)
        .apiKey("<YOUR_FLOW_UP_API_KEY>")
        .forceReports(BuildConfig.DEBUG)
        .logEnabled(BuildConfig.DEBUG)
        .start();
}
```

**Start a build of your app using DEBUG as build type and you're good to go!** FlowUp will automatically send performance metrics to our servers and you'll be able to see information about your app in our platform.

If for some reason you don't want to distribute FlowUp's artifact in your release builds you can use this dependencies configuration instead of the previous one:

```groovy
 dependencies {
   releaseCompile 'io.flowup:android-sdk-no-op:<LAST_VERSION_RELEASED>'
   debugCompile 'io.flowup:android-sdk:<LAST_VERSION_RELEASED>'
   testCompile 'io.flowup:android-sdk-no-op:<LAST_VERSION_RELEASED>'
 }
```

This configuration will use a no operational version of the library in your release builds. This **no operational** version won't include FlowUp or any related dependency in your release APK.


How to build this project
-------------------------

This repository contains an Android project built on top of ``gradlew``, a binary file you can use to build this project without any additional build tool. To build this repository you should use the Gradle Wrapper following the following steps:

###From command line:

* Clone this repository executing: ``git clone git@github.com:Karumi/FlowUpAndroidSDK.git``
* Download and update your ``Android SDK`` configuration.
* Build your project using the different commands available:
	* Check if the checkstyle passes the project: ``./gradlew checkstyle``
	* Perform a regular build: ``./gradlew build``
	* Execute unit tests: ``./gradlew check``.
	* Execute Android instrumentation tests using the current connected device: ``./gradlew connectedCheck``
	* Install the app: ``./gradlew installDebug``. Remember that this command will not start the app automatically, you need to tap on the application icon by yourself.

###From Android Studio/IntelliJ:

* Clone this repository executing: ``git clone git@github.com:Karumi/FlowUpAndroidSDK.git``
* Download ``Android Studio`` or ``Intelli J``.
* Download and update your ``Android SDK`` configuration.
* Import this Android project as an gradle based project. Wait for indexing.
* Build your project using the different actions available:
	* Check if the checkstyle passes pressing the ``checstyle`` task in the Gradle tab you have on the right. You can also install the official IntelliJ checkstyle plugin.
	* Perform a regular build pressing the ``build`` task in the Gradle tab you have on the right.
	* Execute unit tests: Press the ``check`` task in the Gradle tab you have on the right or press the secondary mouse button over a test file or test method choosing the ``run test`` button to execute the unit tests.
	* Execute Android instrumentation: Press the ``connectedCheck`` task in the Gradle tab you have on the right or press the secondary mouse button over a instrumentation test file or test method choosing the ``run test`` button to execute the unit tests.
	* Install and run the app: Press the ``play button`` you have in the top project bar, close to the ``debug button``.

By default the project is ready to execute a sample app reporting reports to our service. If you start the sample app some reports obtained from your application instalation will be sent to our server.**

If you are using IntelliJ, we strongly recommend you to install [AutoValue](https://plugins.jetbrains.com/plugin/8091) and [SQLDelight](https://plugins.jetbrains.com/plugin/8191)

How to review the persisted data
--------------------------------

This library has been developed using SQLite to persist the metrics data. FlowUp will store metrics reports until the sync process be activated. All this information is persisted inside a database you can check what's inside following the next steps:

* Get a copy of the Realm database executing: ``adb pull /data/data/<APPLICATION_PACKAGE_WHERE_FLOWUP_IS_BEING_USED>/databases .``
* Intall the [SQLite Browser](http://sqlitebrowser.org/) app.
* Open the file named ``flowup.db`` using the SQLite Browser.

Inside this database you can find all the information persisted by FlowUp which is pending to be synced with our servers. Once this information is synced, it will be removed from the database. **Remember that to be able to inspect the databse the device used has to be an emulator or be rooted**.

How to get info about the sync process
--------------------------------------

This library uses part of the Google Play Services API to implement the reports sync mechanism. This API is named [GcmTaskService][https://developers.google.com/cloud-messaging/network-manager] and it's being used to schedule a call to our ``WiFiSyncService`` every hour if the device is connected to an unmetered wifi network. You can review the scheduler configuration in the class ``WiFiSyncServiceScheduler``.

The service will be invoked once per hour in the best case and to be able to know what's going on we've added some log traces. If you want to review the service tasks execution historic you can execute the following commands:

* ``adb shell dumpsys activity service GcmService --endpoints``: Shows information about the tasks scheduled for every app in the connected device.
* ``adb shell dumpsys activity service GcmService --endpoints <APP-PACKAGE-NAME>``: Shows information about the history of tasks executed for the application and the total time the service has been running in seconds since the application was installed.

Copyright 2016 Karumi.

[flowuplogo]: ./art/FlowUpLogo.png

