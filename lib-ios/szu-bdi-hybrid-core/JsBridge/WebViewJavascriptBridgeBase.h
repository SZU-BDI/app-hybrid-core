//  WebViewJavascriptBridgeBase.h
#import "Hybrid.h"

#ifndef WebViewJavascriptBridgeBase_h

#define WebViewJavascriptBridgeBase_h

#import <Foundation/Foundation.h>

#import "JSO.h"

//NOTES: should be same as the one in WebViewJavascriptBridge.js
#define S_JSB_PROTOCOL @"jsb1"
#define S_JSB_Q_MSG      @"__QUEUE_MESSAGE__"

#warning TODO change as JSO
typedef NSDictionary WVJBMessage;

#endif


@protocol WebViewJavascriptBridgeBaseDelegate <NSObject>
- (NSString*) _evaluateJavascript:(NSString*)javascriptCommand;
@end

@interface WebViewJavascriptBridgeBase : NSObject

@property (assign) id <WebViewJavascriptBridgeBaseDelegate> delegate;

#warning TODO change to JSO
@property (strong, nonatomic) NSMutableArray* startupMessageQueue;
@property (strong, nonatomic) NSMutableDictionary* responseCallbacks;
@property (strong, nonatomic) NSMutableDictionary* messageHandlers;


@property (strong, nonatomic) HybridHandler messageHandler;

+ (void)enableLogging;

+ (void)setLogMaxLength:(int)length;

- (void)reset;

- (void)sendData:(id)data responseCallback:(HybridCallback)responseCallback handlerName:(NSString*)handlerName;

- (void)flushMessageQueue:(NSString *)messageQueueString;

- (void)injectJavascriptFile;

- (BOOL)isCorrectProcotocolScheme:(NSURL*)url;

- (BOOL)isQueueMessageURL:(NSURL*)urll;

- (BOOL)isBridgeLoadedURL:(NSURL*)urll;

//- (void)logUnkownMessage:(NSURL*)url;

- (NSString *)webViewJavascriptCheckCommand;
- (NSString *)webViewJavascriptFetchQueyCommand;

@end
