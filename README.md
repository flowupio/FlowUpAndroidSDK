![FlowUp Logo][flowuplogo] FlowUp [![Build Status](https://travis-ci.com/Karumi/FlowUpAndroidSDK.svg?token=Kb2RqPaWxFZ8XPxpqvqz&branch=master)](https://travis-ci.com/Karumi/FlowUpAndroidSDK)
==============================

FlowUp, mobile real time applications performance monitoring solution!

FlowUp helps you to radically improve your mobile applications performance with actionable insight into real-time key metrics including frame time, frames per second, bandwidth, memory consumption, CPU/GPU performance, disk usage and much more. Available for Android and iOS soon.

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

By default the project is ready to execute a sample app reporting metrics to our service. If you start the sample app some metrics obtained from your application instalation will be sent to our server.**

How to review the persisted data
--------------------------------

This library has been developed using [Realm][realm] as the persistence motor. FlowUp will store metrics reports until the sync process be activated. All this information is persisted inside a Realm data base you can check what's inside following the next steps:

* Get a copy of the Realm database executing: ``adb pull /data/data/<APPLICATION_PACKAGE_WHERE_FLOWUP_IS_BEING_USED>/files/FlowUp.realm .``
* Download the [Realm Browser][realmbrowser] app.
* Open the file named ``FlowUp.realm`` using the Realm Browser.

Inside this database you can find all the information persisted by FlowUp which is pending to be synced with our servers. Once this information be synced, it will be removed from the database.

[flowuplogo]: ./art/FlowUpLogo.png
[realm]: https://realm.io/es/docs/java/latest/
[realmbrowser]: https://itunes.apple.com/es/app/realm-browser/id1007457278?mt=12


