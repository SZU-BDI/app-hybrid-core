#ifndef CMPHybridTools_h
#define CMPHybridTools_h

#import <Foundation/Foundation.h>
#import "JSO.h"
#import "CMPHybridUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
@import JavaScriptCore;

@interface CMPHybridTools : NSObject

//inner data store, hold until program exit.
@property (strong) JSO *jso;

//Singleton Pattern:
+ (CMPHybridTools *)shareInstance;

+ (void)checkAppConfig;

+ (CMPHybridApi *)getHybridApi:(NSString *)name;

+ (void)startUi:(NSString *)strUiName
   strInitParam:(JSO *)strInitParam
      objCaller:(CMPHybridUi *)objCaller
       callback:(HybridCallback)callback;

+ (JSO *)wholeAppConfig;

+ (UIViewController *) findTopRootView;

+ (NSString *) fullPathOfAsset :(NSString *)filename;

+ (void)quickShowMsgMain:(NSString *)message;

//- (void)someMethodThatTakesABlock:(returnType (^nullability)(parameterTypes))blockName;
+ (void)quickAlertMsg :(NSString *)msg callback:(void (^)())callback;

+ (void)quickConfirmMsgMain:(NSString *)msg
                 handlerYes:(HybridDialogCallback) handlerYes
                  handlerNo:(HybridDialogCallback) handlerNo
;

+ (void) suspendApp;
+ (void) quitGracefully;

+ (JSValue *) callWebViewDoJs :(UIWebView *)_webview :(NSString *)js_s;
+ (JSContext *) getWebViewJsCtx :(UIWebView *) _webview;


@end


#endif /* CMPHybridTools_h */
