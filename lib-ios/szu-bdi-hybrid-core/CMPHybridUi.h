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
//#define HybridApi id<CMPHybridApi>

#endif /* Hybrid_h */


#ifndef CMPHybridUi_h
#define CMPHybridUi_h

//#import "JavaScriptCore/JavaScript.h"
@import JavaScriptCore;

@protocol CMPHybridUi <NSObject>

@property (strong) JSO *uiData;

@property (strong) NSString *uiName;

@property (strong, nonatomic) NSMutableDictionary* uiApiHandlers;

@property (strong, nonatomic) NSMutableDictionary* uiEventHandlers;

@required

@optional

-(void)initUi;
-(void) closeUi;

-(void) on:(NSString *)eventName :(HybridEventHandler) handler;
-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(JSO *)extraData;
-(void) trigger :(NSString *)eventName :(JSO *)extraData;


-(void) restoreTopBarStatus;
-(void) CustomTopBarBtn;
-(void) CustomTopBar :(NSString *)mode;
-(void) hideTopStatusBar;
-(void) showTopStatusBar;
-(void) hideTopBar;
-(void) showTopBar;

- (void) evalJs :(NSString *)js_s;

@end

#endif /* CMPHybridUi_h */
