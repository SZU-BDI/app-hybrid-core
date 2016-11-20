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

#endif /* Hybrid_h */


#ifndef CMPHybridUi_h
#define CMPHybridUi_h

@import JavaScriptCore;

@interface CMPHybridUi : UIViewController

@property (strong) JSO *uiData;
@property (strong) NSString *uiName;

@property (strong, nonatomic) NSMutableDictionary* myApiHandlers;

@property (strong, nonatomic) NSMutableDictionary* myEventHandlers;

-(void) closeUi;//the "close" is taken...

-(void) restoreTopBarStatus;
-(void) CustomTopBarBtn;
-(void) CustomTopBar :(NSString *)mode;
-(void) hideTopStatusBar;
-(void) showTopStatusBar;
-(void) hideTopBar;
-(void) showTopBar;

-(void) on:(NSString *)eventName :(HybridEventHandler) handler;
-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(JSO *)extraData;

-(void) trigger :(NSString *)eventName :(JSO *)extraData;

- (void) evalJs :(NSString *)js_s;

@end




#endif /* CMPHybridUi_h */
