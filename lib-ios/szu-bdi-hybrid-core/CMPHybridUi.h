#import <Foundation/Foundation.h>
//#import <UIKit/UIKit.h>
@import UIKit;
@class JSO;

#ifndef Hybrid_h
#define Hybrid_h


typedef void (^HybridCallback)(id responseData);

typedef void (^HybridHandler)(id data, HybridCallback responseCallback);

typedef void (^HybridDialogCallback)(UIAlertAction *action);

typedef void (^HybridEventHandler)(NSString *eventName, id extraData);

#define SINGLETON_shareInstance(classname) \
+ (classname *)shareInstance\
{\
static classname *_sharedInstance = nil;\
static dispatch_once_t onceToken;\
dispatch_once(&onceToken, ^{\
_sharedInstance = [[self alloc] init];\
});\
return _sharedInstance;\
}

#endif /* Hybrid_h */


#ifndef CMPHybridUi_h
#define CMPHybridUi_h


@interface CMPHybridUi : UIViewController

@property (strong) JSO *uiData;

#warning HybridCallback is going to delete very soon.
//@property (strong) HybridCallback callback;

//- (void) close;//the close is taken
-(void) closeUi;

-(void) CustomTopBar;

-(void) on:(NSString *)eventName :(HybridEventHandler) handler;
-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(id)extraData;

-(void) trigger :(NSString *)eventName :(id)extraData;

@end




#endif /* CMPHybridUi_h */
