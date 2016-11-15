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

@property (strong) HybridEventHandler tmpHandler;

//- (void) close;//the close is taken
-(void) closeUi;

-(void) restoreTopBarStatus;
-(void) CustomTopBarBtn;
-(void) CustomTopBar :(NSString *)mode;
-(void) hideTopStatusBar;
-(void) showTopStatusBar;
-(void) hideTopBar;
-(void) showTopBar;

-(void) on:(NSString *)eventName :(HybridEventHandler) handler;
-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(id)extraData;

-(void) trigger :(NSString *)eventName :(id)extraData;

@end




#endif /* CMPHybridUi_h */
