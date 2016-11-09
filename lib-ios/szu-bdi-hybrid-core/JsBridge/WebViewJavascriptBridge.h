//@ref https://developer.apple.com/reference/webkit/wkwebview?language=objc
//Starting in iOS 8.0 and OS X 10.10, use WKWebView to add web content to your app. Do not use UIWebView or WebView.

#import <Foundation/Foundation.h>

#import "WebViewJavascriptBridgeBase.h"

//#if (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_8_0 || __MAC_OS_X_VERSION_MAX_ALLOWED >= __MAC_10_10)
//
////move on to the new MKWebView
//	#import <WebKit/WebKit.h>
//	#define WVJB_WEBVIEW_TYPE WKWebView
//	#define WVJB_WEBVIEW_DELEGATE_TYPE NSObject<WKUIDelegate,WKNavigationDelegate>
//	#define WVJB_WEBVIEW_DELEGATE_INTERFACE NSObject<WKUIDelegate, WebViewJavascriptBridgeBaseDelegate>
//#define WEBVIEWUIINTERFACE ()<WKUIDelegate>
//
//#elif defined __MAC_OS_X_VERSION_MAX_ALLOWED
//
////WARNING OSX not tested, just put here for place holding...
////for osx...
//    #import <WebKit/WebKit.h>
//    #define WVJB_WEBVIEW_TYPE WebView
//    #define WVJB_WEBVIEW_DELEGATE_TYPE NSObject<WebViewJavascriptBridgeBaseDelegate>
//    #define WVJB_WEBVIEW_DELEGATE_INTERFACE NSObject<WebViewJavascriptBridgeBaseDelegate, WebPolicyDelegate>
//#define WEBVIEWUIINTERFACE ()<WKUIDelegate>
//
//#elif defined __IPHONE_OS_VERSION_MAX_ALLOWED
//
////for older ios
//	#import <UIKit/UIWebView.h>
//	#define WVJB_WEBVIEW_TYPE UIWebView
//	#define WVJB_WEBVIEW_DELEGATE_TYPE NSObject<UIWebViewDelegate>
//	#define WVJB_WEBVIEW_DELEGATE_INTERFACE NSObject<UIWebViewDelegate, WebViewJavascriptBridgeBaseDelegate>
//#define WEBVIEWUIINTERFACE ()<UIWebViewDelegate>
//
//#endif

#import <UIKit/UIWebView.h>
#define WVJB_WEBVIEW_TYPE UIWebView
#define WVJB_WEBVIEW_DELEGATE_TYPE NSObject<UIWebViewDelegate>
#define WVJB_WEBVIEW_DELEGATE_INTERFACE NSObject<UIWebViewDelegate, WebViewJavascriptBridgeBaseDelegate>
#define WEBVIEWUIINTERFACE ()<UIWebViewDelegate>

@interface WebViewJavascriptBridge : WVJB_WEBVIEW_DELEGATE_INTERFACE

+ (instancetype)bridgeForWebView:(WVJB_WEBVIEW_TYPE*)webView;
+ (void)enableLogging;
+ (void)setLogMaxLength:(int)length;

- (void)registerHandler:(NSString*)handlerName handler:(WVJBHandler)handler;
- (void)callHandler:(NSString*)handlerName;
- (void)callHandler:(NSString*)handlerName data:(id)data;
- (void)callHandler:(NSString*)handlerName data:(id)data responseCallback:(HybridCallback)responseCallback;
- (void)setWebViewDelegate:(WVJB_WEBVIEW_DELEGATE_TYPE*)webViewDelegate;
@end


