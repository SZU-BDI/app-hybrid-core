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

//jso.i18n
@property (strong) JSO *i18n;

@property (strong) NSString *lang;//en,zh-cn,zh-*,kh,vn,th

//Singleton Pattern:
+ (CMPHybridTools *) shareInstance;

+ (void) checkAppConfig;

+ (CMPHybridApi *) getHybridApi:(NSString *)name;

+ (void) startUi :(NSString *)strUiName
         initData:(JSO *) initData
        objCaller:(HybridUi )objCaller
         callback:(void (^)(HybridUi  ui))callback;

+ (HybridUi ) startUi :(NSString *)strUiName
                  initData:(JSO *)initData
                 objCaller:(HybridUi )objCaller;

+ (JSO *) wholeAppConfig;
+ (JSO *) getAppConfig :(NSString *)key;
+ (UIViewController *) findTopRootView;

+ (NSString *) fullPathOfAsset :(NSString *)filename;
+(NSString *)readAssetInStr :(NSString *)filename;
+(NSString *)readAssetInStr :(NSString *)filename :(BOOL)removeComments;

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

+ (JSValue *) callWebViewDoJs:(id) _webview :(NSString *)js_s;
//+ (JSValue *) callWebViewDoJs :(UIWebView *)_webview :(NSString *)js_s;
//+ (JSValue *) callWebViewDoJs :(WKWebView *) _webview js:(NSString *)js_s;

+ (void) callWebViewLoadUrl:_webview :(NSString *)address;

//+ (JSContext *) getWebViewJsCtx :(UIWebView *) _webview;
//+ (JSContext *) getWKWebViewJsCtx :(WKWebView *) _webview;

/**
 *
 * Return a array of "matches".
 * Usage
 *   if ([matches count] > 0) ...
 */
+(NSArray *) quickRegExpMatch :(NSString *)regex_s :(NSString *)txt;
+(NSString *) quickRegExpReplace :(NSString *)regex_s :(NSString *)src :(NSString *)tgt;

+ (void) countDown:(double)interval initTime:(double)initTime block:(BOOL (^)(NSTimer *tm))block;

+ (void) injectJSB :(UIWebView *)webView :(HybridUi )caller;
//+ (void) injectJSBWK :(WKWebView *)webView :(HybridUi )caller;


+ (NSInteger) os_compare:(Float32)tgt;
+ (BOOL) is_simulator;

+ (NSString *) btoa:(NSString *)s;
+ (NSString *) base64encode:(NSString *)s;

+ (NSString *) atob:(NSString *)s;
+ (NSString *) base64decode:(NSString *)s;

+ (NSString *) I18N:(NSString *)key;
+ (void) setI18N:(NSString *)i18n;

+ (void)saveUserConfig :(NSString *)key :(NSString *)value_s :(BOOL)autosave;
+ (id)loadUserConfig :(NSString *)key;

// 手势密码读写
+ (NSString *)loadGesturesPassword;

+ (void)saveGesturesPassword:(NSString*)pswd;

+ (id) initHybridWebView :(Class)c :(HybridUi) caller;

@end


#endif /* CMPHybridTools_h */
