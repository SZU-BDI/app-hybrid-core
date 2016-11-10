#ifndef HybridTools_h
#define HybridTools_h

#endif

#import <Foundation/Foundation.h>
#import "HybridUi.h"
#import "HybridApi.h"
#import "JSO.h"

@interface HybridTools : NSObject

//inner data store
@property (nonatomic, strong) JSO *jso;

+ (void)checkAppConfig;

+ (HybridApi *)getHybridApi:(NSString *)name;

+ (void)startUi:(NSString *)strUiName strInitParam:(JSO *)strInitParam objCaller:(HybridUi *)objCaller callback:(HybridCallback)callback;

+ (JSO *)wholeAppConfig;

@end
