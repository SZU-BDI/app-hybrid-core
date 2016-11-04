
# WKWebView problem

WKWebView cannot load files using file:// URL protocol since iOS 8 beta 4 and 5
http://www.openradar.me/18039024

Fixed since iOS 9

# Cordova WKWebView Engine


This plugin makes Cordova use the WKWebView component instead of the default UIWebView component, and is installable only on a system with the iOS 9.0 SDK.

https://github.com/apache/cordova-plugin-wkwebview-engine

综上， 就是说要用得上 WKWebView的高级性能，需要至少 iOS 9。
否则的话，打不开file://文件的话，就没办法加载cordova的文件（用它所说的local webserver就太恶心了）


# react-native (RN)

假设ACE.V3/MKC.V2
