//TODO change to use JSO

#import "WebViewJavascriptBridge.h"

@interface WebScriptObject: NSObject
@end

@interface WebView
- (WebScriptObject *)windowScriptObject;
@end

@interface WebScriptBridge: NSObject
//- (void)someEvent: (uint64_t)foo :(NSString *)bar;
- (NSString *)js2app;
+ (BOOL)isKeyExcludedFromWebScript:(const char *)name;
+ (BOOL)isSelectorExcludedFromWebScript:(SEL)aSelector;
+ (WebScriptBridge*)getWebScriptBridge;
@end

static WebScriptBridge *gWebScriptBridge = nil;

@implementation WebScriptBridge

-(NSString *) js2app {
    NSLog(@"TODO js2app!!!!!");
    return @"TODO_js2app";
}

+ (BOOL)isKeyExcludedFromWebScript:(const char *)name;
{
    return NO;
}

+ (BOOL)isSelectorExcludedFromWebScript:(SEL)aSelector;
{
    return NO;
}

+ (NSString *)webScriptNameForSelector:(SEL)sel
{
    // Naming rules can be found at: https://developer.apple.com/library/mac/documentation/Cocoa/Reference/WebKit/Protocols/WebScripting_Protocol/Reference/Reference.html
    if (sel == @selector(js2app)) return @"js2app";
    //if (sel == @selector(someEvent::)) return @"someEvent";
    
    return nil;
}

//+ (BasicAddressBook *)addressBook;
//- (NSString *)js2app:(NSString *) callBackId handlerName:(NSString *)handlerName param_s:(NSString *)param_s;
+ (WebScriptBridge*)getWebScriptBridge {
    if (gWebScriptBridge == nil)
        gWebScriptBridge = [WebScriptBridge new];
    
    return gWebScriptBridge;
}
@end


@interface UIWebDocumentView: UIView
- (WebView *)webView;
@end

#if __has_feature(objc_arc_weak)
#define WVJB_WEAK __weak
#else
#define WVJB_WEAK __unsafe_unretained
#endif

@implementation WebViewJavascriptBridge {
    WVJB_WEAK WVJB_WEBVIEW_TYPE* _webView;
    WVJB_WEAK id _webViewDelegate;
    long _uniqueId;
    WebViewJavascriptBridgeBase *_base;
}

+ (void)enableLogging { [WebViewJavascriptBridgeBase enableLogging]; }
+ (void)setLogMaxLength:(int)length { [WebViewJavascriptBridgeBase setLogMaxLength:length]; }

+ (instancetype)bridgeForWebView:(WVJB_WEBVIEW_TYPE*)webView {
    WebViewJavascriptBridge* bridge = [[self alloc] init];
    [bridge _platformSpecificSetup:webView];
    return bridge;
}

- (void)setWebViewDelegate:(WVJB_WEBVIEW_DELEGATE_TYPE*)webViewDelegate {
    _webViewDelegate = webViewDelegate;
}

- (void)send:(id)data {
    [self send:data responseCallback:nil];
}

- (void)send:(id)data responseCallback:(HybridCallback)responseCallback {
    [_base sendData:data responseCallback:responseCallback handlerName:nil];
}

- (void)callHandler:(NSString *)handlerName {
    [self callHandler:handlerName data:nil responseCallback:nil];
}

- (void)callHandler:(NSString *)handlerName data:(id)data {
    [self callHandler:handlerName data:data responseCallback:nil];
}

- (void)callHandler:(NSString *)handlerName data:(id)data responseCallback:(HybridCallback)responseCallback {
    [_base sendData:data responseCallback:responseCallback handlerName:handlerName];
}

- (void)registerHandler:(NSString *)handlerName handler:(HybridHandler)handler {
    _base.messageHandlers[handlerName] = [handler copy];
}

/* Platform agnostic internals
 *****************************/

- (void)dealloc {
    [self _platformSpecificDealloc];
    _base = nil;
    _webView = nil;
    _webViewDelegate = nil;
}


//#if (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_8_0 || __MAC_OS_X_VERSION_MAX_ALLOWED >= __MAC_10_10)
//
//- (NSString *) _evaluateJavascript: (NSString *) javaScriptString {
//    //completionHandler: (void (^)(id, NSError *)) completionHandler
////    [_webView evaluateJavaScript:javaScriptString completionHandler:completionHandler];
//     [_webView evaluateJavaScript:javaScriptString completionHandler:nil];
//    return @"";
//}
//
//- (void) _platformSpecificSetup:(WVJB_WEBVIEW_TYPE*)webView {
//    _webView = webView;
//    //    _webView.delegate = self;
//    _webView.UIDelegate=self;
//    _base = [[WebViewJavascriptBridgeBase alloc] init];
//    _base.delegate = self;
//}
//
//- (void) _platformSpecificDealloc {
//    //    _webView.delegate = nil;
//    _webView.UIDelegate = nil;
//}
//
//- (void)webViewDidFinishLoad:(WKWebView *)webView {
//    NSLog(@" WebViewJavascriptBridge.webViewDidFinishLoad() ");
//    if (webView != _webView) {
//        NSLog(@" skip: not the same webview?? ");
//        return;
//    }
//    
//    __strong WVJB_WEBVIEW_DELEGATE_TYPE* strongDelegate = _webViewDelegate;
//    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webViewDidFinishLoad:)]) {
//        //        [strongDelegate webViewDidFinishLoad:webView];
//        //[strongDelegate webviewDidf];
//    }
//    NSLog(@" injecting js");
//    [_base injectJavascriptFile];
//}
//
//- (void)webView:(WKWebView *)webView didFailLoadWithError:(NSError *)error {
//    NSLog(@" didFailLoadWithError() %@",error);
//    if (webView != _webView) {
//        NSLog(@" skip: not the same webview?? ");
//        return;
//    }
//    
//    __strong WVJB_WEBVIEW_DELEGATE_TYPE* strongDelegate = _webViewDelegate;
//    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webView:didFailLoadWithError:)]) {
//        //[strongDelegate webView:webView didFailLoadWithError:error];
//        [strongDelegate webView:webView didFailNavigation:nil withError:error];
//    }
//}
//
//- (void)webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation{
//    //navigationAction.request.URL.host
//    NSLog(@"WKwebView ... didCommitNavigation ..");
//}
//
//
//- (void)webViewDidStartLoad:(WKWebView *)webView {
//    if (webView != _webView) { return; }
//    
//    __strong WVJB_WEBVIEW_DELEGATE_TYPE* strongDelegate = _webViewDelegate;
//    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webViewDidStartLoad:)]) {
//        NSLog(@"WKwebView ... webViewDidStartLoad ..");
//        //[strongDelegate webViewDidStartLoad:webView];
//    }
//}
//
//#elif defined __MAC_OS_X_VERSION_MAX_ALLOWED
//
//- (void) _platformSpecificSetup:(WVJB_WEBVIEW_TYPE*)webView {
//    _webView = webView;
//    
//    _webView.policyDelegate = self;
//    
//    _base = [[WebViewJavascriptBridgeBase alloc] init];
//    _base.delegate = self;
//}
//
//- (void) _platformSpecificDealloc {
//    _webView.policyDelegate = nil;
//}
//
//- (void)webView:(WebView *)webView decidePolicyForNavigationAction:(NSDictionary *)actionInformation request:(NSURLRequest *)request frame:(WebFrame *)frame decisionListener:(id<WebPolicyDecisionListener>)listener
//{
//    if (webView != _webView) { return; }
//    
//    NSURL *url = [request URL];
//    if ([_base isCorrectProcotocolScheme:url]) {
//        if ([_base isBridgeLoadedURL:url]) {
//            NSLog(@" (OSX) injecting js ");
//            [_base injectJavascriptFile];
//        } else if ([_base isQueueMessageURL:url]) {
//            NSString *messageQueueString = [self _evaluateJavascript:[_base webViewJavascriptFetchQueyCommand]];
//            [_base flushMessageQueue:messageQueueString];
//        } else {
//            [_base logUnkownMessage:url];
//        }
//        [listener ignore];
//    } else if (_webViewDelegate && [_webViewDelegate respondsToSelector:@selector(webView:decidePolicyForNavigationAction:request:frame:decisionListener:)]) {
//        [_webViewDelegate webView:webView decidePolicyForNavigationAction:actionInformation request:request frame:frame decisionListener:listener];
//    } else {
//        [listener use];
//    }
//}
//
//
//- (BOOL)webView:(WKWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
//    if (webView != _webView) { return YES; }
//    NSURL *url = [request URL];
//    NSLog(@" shouldStartLoadWithRequest= %@ ",url);
//    __strong WVJB_WEBVIEW_DELEGATE_TYPE* strongDelegate = _webViewDelegate;
//    if ([_base isCorrectProcotocolScheme:url]) {
//        //        if ([_base isBridgeLoadedURL:url]) {
//        //            NSLog(@" skip injecting js %@ ",url);
//        //            //[_base injectJavascriptFile];
//        //        } else
//        if ([_base isQueueMessageURL:url]) {
//            NSString *messageQueueString = [self _evaluateJavascript:[_base webViewJavascriptFetchQueyCommand]];
//            [_base flushMessageQueue:messageQueueString];
//        } else {
//            //NSLog(@" logUnkownMessage %@ ",url);
//            //            [_base logUnkownMessage:url];
//            NSLog(@"WebViewJavascriptBridge: WARNING: Received unknown WebViewJavascriptBridge command url=%@", url);
//        }
//        return NO;
//    } else if (strongDelegate && [strongDelegate respondsToSelector:@selector(webView:shouldStartLoadWithRequest:navigationType:)]) {
////        return [strongDelegate webView:webView shouldStartLoadWithRequest:request navigationType:navigationType];
////        return [strongDelegate webView:webView didCommitNavigation:navigationType]
//    } else {
//        return YES;
//    }
//}
//
//
//#elif defined __IPHONE_OS_VERSION_MAX_ALLOWED

#if defined __IPHONE_OS_VERSION_MAX_ALLOWED
- (NSString*) _evaluateJavascript:(NSString*)javascriptCommand
{
    return [_webView stringByEvaluatingJavaScriptFromString:javascriptCommand];
}
- (void) _platformSpecificSetup:(WVJB_WEBVIEW_TYPE*)webView {
    _webView = webView;
    _webView.delegate = self;
    _base = [[WebViewJavascriptBridgeBase alloc] init];
    _base.delegate = self;
}

- (void) _platformSpecificDealloc {
    _webView.delegate = nil;
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    NSLog(@" WebViewJavascriptBridge.webViewDidFinishLoad() ");
    if (webView != _webView) {
        NSLog(@" skip: not the same webview?? ");
        return;
    }
    
    __strong WVJB_WEBVIEW_DELEGATE_TYPE* strongDelegate = _webViewDelegate;
    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webViewDidFinishLoad:)]) {
        [strongDelegate webViewDidFinishLoad:webView];
    }
    NSLog(@" injecting js");
    [_base injectJavascriptFile];

    //NOTES: failed for the windowScriptObject is for macOS only...
    //TODO change to wkwebview later for better performance
//    //[webView windowScriptObject];
//    //[win setValue:littleBlackBook forKey:@"AddressBook"];
//    UIWebDocumentView *documentView = (UIWebDocumentView *)_webView;
//    WebScriptObject *wso = documentView.webView.windowScriptObject;
//    [wso setValue:[WebScriptBridge getWebScriptBridge] forKey:@"nativejsb"];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    NSLog(@" didFailLoadWithError() %@",error);
    if (webView != _webView) {
        NSLog(@" skip: not the same webview?? ");
        return;
    }
    
    __strong WVJB_WEBVIEW_DELEGATE_TYPE* strongDelegate = _webViewDelegate;
    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webView:didFailLoadWithError:)]) {
        [strongDelegate webView:webView didFailLoadWithError:error];
    }
}

// TODO wanjo
//https://developer.apple.com/library/content/documentation/AppleApplications/Conceptual/SafariJSProgTopics/ObjCFromJavaScript.html
//http://stackoverflow.com/questions/9473582/ios-javascript-bridge
//其实 也可以暴露一个 nativejsb 到 window.nativejsb 的！  注意回调时放回 UI线程

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (webView != _webView) { return YES; }
    NSURL *url = [request URL];
    NSLog(@" shouldStartLoadWithRequest= %@ ",url);
    __strong WVJB_WEBVIEW_DELEGATE_TYPE* strongDelegate = _webViewDelegate;
    if ([_base isCorrectProcotocolScheme:url]) {
//        if ([_base isBridgeLoadedURL:url]) {
//            NSLog(@" skip injecting js %@ ",url);
//            //[_base injectJavascriptFile];
//        } else
        if ([_base isQueueMessageURL:url]) {
            NSString *messageQueueString = [self _evaluateJavascript:[_base webViewJavascriptFetchQueyCommand]];
            [_base flushMessageQueue:messageQueueString];
        } else {
            //NSLog(@" logUnkownMessage %@ ",url);
//            [_base logUnkownMessage:url];
            NSLog(@"WebViewJavascriptBridge: WARNING: Received unknown WebViewJavascriptBridge command url=%@", url);
        }
        return NO;
    } else if (strongDelegate && [strongDelegate respondsToSelector:@selector(webView:shouldStartLoadWithRequest:navigationType:)]) {
        return [strongDelegate webView:webView shouldStartLoadWithRequest:request navigationType:navigationType];
    } else {
        return YES;
    }
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    if (webView != _webView) { return; }
    
    __strong WVJB_WEBVIEW_DELEGATE_TYPE* strongDelegate = _webViewDelegate;
    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webViewDidStartLoad:)]) {
        [strongDelegate webViewDidStartLoad:webView];
    }
}

#endif

@end
