#import <Foundation/Foundation.h>

#import <WebKit/WebKit.h>
#import <UIKit/UIKit.h>

//@import WebKit;
//@import UIKit;

@class JSO;

#ifndef Hybrid_h
#define Hybrid_h

typedef void (^HybridCallback)(JSO* responseData);

typedef void (^HybridHandler)(JSO * jso, HybridCallback responseCallback);

typedef void (^HybridDialogCallback)(UIAlertAction* action);

typedef void (^HybridEventHandler)(NSString *eventName, JSO* extraData);

#define CMPHybridEventBeforeDisplay @"BeforeDisplay"
#define CMPHybridEventMemoryWarning @"MemoryWarning"

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

#define HybridUi id<CMPHybridUi>
//#define HybridUi CMPHybridUi *

//#define HybridApi id<CMPHybridApi>

#endif /* Hybrid_h */





#ifndef CMPHybridUi_h
#define CMPHybridUi_h

//#import "JavaScriptCore/JavaScript.h"
@import JavaScriptCore;

@protocol CMPHybridUi <NSObject>

@property (strong, nonatomic) JSO *uiData;

@property (strong, nonatomic) NSString *uiName;

@property (strong, nonatomic) JSO *responseData;

@property (strong, nonatomic) NSMutableDictionary* uiApiHandlers;

@property (strong, nonatomic) NSMutableDictionary* uiEventHandlers;

@required

@optional

-(void) initUi;
-(void) closeUi;

-(void) on:(NSString *)eventName :(HybridEventHandler) handler;
//for some case, some initData is sent and use when trigger
-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(JSO *)initData;
-(void) trigger :(NSString *)eventName :(JSO *)triggerData;
-(void) trigger :(NSString *)eventName;

-(void) resetTopBarBtn;
-(void) resetTopBar :(NSString *)mode;
-(void) resetTopBarStatus;
-(void) hideTopStatusBar;
-(void) showTopStatusBar;
-(void) hideTopBar;
-(void) showTopBar;
- (void)setTopBarTitle:(NSString *)title;

- (void) evalJs :(NSString *)js_s;

@end

#endif /* CMPHybridUi_h */
