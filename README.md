# app-hybrid-core

SZU-BDI hybrid-app core layer library, mostly a js-bridge packer.

Please refer to github/SZU-BDI/app-hybrid-demo

# Target

Combine with app-hybrid-demo, we need to complete these tasks:

1. auto check update and sync app-hybrid-core and copy to $my_project/../build/
2. build and pack target apk/ipa with console cmd base on $my_project/config.json
//3. sign and deploy binary to app store. [user/pass/cert will be prompted]

# Folders

* lib-ios/
* lib-ios/szu-bdi-hybrid-core/
* testproj-android/
		the test project for android core
* lib-android/
* lib-android/szu.bdi.hybrid.core/
* testproj-ios/
		the test project for ios core
