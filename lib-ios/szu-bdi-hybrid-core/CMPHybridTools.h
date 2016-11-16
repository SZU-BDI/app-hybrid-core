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

+ (CMPHybridUi *) startUi :(NSString *)strUiName
                  initData:(JSO *)initData
                 objCaller:(CMPHybridUi *)objCaller;

+ (JSO *) wholeAppConfig;
+ (JSO *) getAppConfig :(NSString *)key;
+ (UIViewController *) findTopRootView;

+ (NSString *) fullPathOfAsset :(NSString *)filename;
+(NSString *)readAssetInStr :(NSString *)filename;
+(BOOL) isEmptyString :(NSString *)s;

+ (void) quickShowMsgMain:(NSString *)message;

//- (void) someMethodThatTakesABlock:(returnType (^nullability)(parameterTypes))blockName;
+ (void) quickAlertMsg :(NSString *)msg callback:(void (^)())callback;

+ (void) quickConfirmMsgMain:(NSString *)msg
                  handlerYes:(HybridDialogCallback) handlerYes
                   handlerNo:(HybridDialogCallback) handlerNo
;

+ (void) suspendApp;
+ (void) quitGracefully;

+ (JSValue *) callWebViewDoJs :(UIWebView *)_webview :(NSString *)js_s;
+ (JSContext *) getWebViewJsCtx :(UIWebView *) _webview;

+(NSArray *) quickRegExpMatch :(NSString *)regex_s :(NSString *)txt;

@end


#endif /* CMPHybridTools_h */
