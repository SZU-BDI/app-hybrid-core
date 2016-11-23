#ifndef CMPHybridTools_h
#define CMPHybridTools_h

//#import <Foundation/Foundation.h>
@import Foundation;

#import "JSO.h"

#import "CMPHybridUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"

@import JavaScriptCore;

@interface CMPHybridTools : NSObject

//inner data store, hold until program exit.
@property (strong) JSO *jso;

//Singleton Pattern:
+ (CMPHybridTools *) shareInstance;

+ (void) checkAppConfig;

+ (CMPHybridApi *) getHybridApi:(NSString *)name;

+ (void) startUi :(NSString *)strUiName
         initData:(JSO *) initData
        objCaller:(CMPHybridUi *)objCaller
         callback:(void (^)(CMPHybridUi * ui))callback;

+ (CMPHybridUi *) startUi :(NSString *)strUiName
                  initData:(JSO *)initData
                 objCaller:(CMPHybridUi *)objCaller;

+ (JSO *) wholeAppConfig;
+ (JSO *) getAppConfig :(NSString *)key;
+ (UIViewController *) findTopRootView;

+ (NSString *) fullPathOfAsset :(NSString *)filename;
+(NSString *)readAssetInStr :(NSString *)filename;
+(BOOL) isEmptyString :(NSString *)s;

+ (void) quickShowMsgMain :(NSString *)msg;
+ (void) quickShowMsgMain :(NSString *)msg callback:(void (^)())callback;

+ (void) quickAlertMsgForOldiOS :(NSString *)msg callback:(void (^)())callback;

+ (void) quickConfirmMsgMain:(NSString *)msg
                  handlerYes:(HybridDialogCallback) handlerYes
                   handlerNo:(HybridDialogCallback) handlerNo
;

+ (void) suspendApp;
+ (void) quitGracefully;

+ (JSValue *) callWebViewDoJs :(UIWebView *)_webview :(NSString *)js_s;
+ (JSValue *) callWKWebViewDoJs:(WKWebView *) _webview :(NSString *)js_s;

+ (JSContext *) getWebViewJsCtx :(UIWebView *) _webview;
//+ (JSContext *) getWKWebViewJsCtx :(WKWebView *) _webview;

+(NSArray *) quickRegExpMatch :(NSString *)regex_s :(NSString *)txt;

+ (void) countDown:(double)interval initTime:(double)initTime block:(BOOL (^)(NSTimer *tm))block;

+ (void) injectJSB :(UIWebView *)webView :(CMPHybridUi *)caller;
//+ (void) injectJSBWK :(WKWebView *)webView :(CMPHybridUi *)caller;

@end


#endif /* CMPHybridTools_h */
