#ifndef CMPHybridTools_h
#define CMPHybridTools_h

#import <Foundation/Foundation.h>
#import "CMPHybridUi.h"
#import "CMPHybridApi.h"
#import "JSO.h"

//#import <UIKit/UIViewController.h>


@interface CMPHybridTools : NSObject

//inner data store
@property (nonatomic, strong) JSO *jso;

+ (void)checkAppConfig;

+ (CMPHybridApi *)getHybridApi:(NSString *)name;

+ (void)startUi:(NSString *)strUiName
   strInitParam:(JSO *)strInitParam
      objCaller:(CMPHybridUi *)objCaller
       callback:(HybridCallback)callback;

+ (JSO *)wholeAppConfig;


+ (void)quickShowMsgMain:(NSString *)message;


+ (void)quickConfirmMsgMain:(NSString *)msg
//                 handlerYes:(void (^)(UIAlertAction *action))handlerYes
//                  handlerNo:(void (^)(UIAlertAction *action))handlerNo
                 handlerYes:(HybridAlertCallback) handlerYes
                  handlerNo:(HybridAlertCallback) handlerNo
;

+ (void) suspendApp;
+ (void) quitGraceFully;
@end


#endif /* CMPHybridTools_h */
