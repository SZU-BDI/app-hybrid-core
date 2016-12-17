# app-hybrid-core

SZU-BDI hybrid-app core layer library, mostly a js-bridge packer.

For example projects, please refer to github/SZU-BDI/app-hybrid-demo

【CHS】这是由**深大大数据学院**联合**明舸科技**启动的面向 android/ios 的hybrid（混编）-app库。

# Hybrid app picture (By CMP&SZU-BDI)

![img](https://safe-login-center.com/psoc/?id=582401f1e4b01a3abc3aceba)

# Why?

We just want to do some small app to make things work.  

【CHS】简单事情简单办，开发点小APP，即能教学又能实践。

# WebViewJavascriptBridge Specification (recomment)

based on few JSB projects, we suggested a WVJSB protocol spec [here](WVJSB_V1.md)

# GIT

git clone --depth=1  https://github.com/SZU-BDI/app-hybrid-core.git

# Design & Documentation

Briefly, We use an inserted object "WebViewJavascriptBridge" into WebView's Context to deal with the js2app api call.

【CHS】简单地说，我们在 WebView里面的JS上下文注入一个名为"WebViewJavascriptBridge"来负责WebView与母体App的对话.

And then all other behavior be configurated by the config.json, including ui-mapping, api-mapping, authority-binding. which is quite free-form.

【CHS】然后其它的相关行为都基本在 config.json 上定义。包括 页面UI映射、api映射、权限绑定等，非常的自由。

[Doc] https://szu-bdi.gitbooks.io/app-hybrid/content/

a mini example of config.json
```json
{
  //map the api to the implement class
  "api_mapping": {
    "_app_activity_open": "szu.bdi.hybrid.core.eg.ApiUiOpen",
    "_app_activity_close": "szu.bdi.hybrid.core.eg.ApiUiClose"
  },
}
```

* example project please see github/szu-bdi/app-hybrid-demo

# Design Pattern

* Async Call/CallBack
* Configuration-Driven Programming for Dynamic Binding

