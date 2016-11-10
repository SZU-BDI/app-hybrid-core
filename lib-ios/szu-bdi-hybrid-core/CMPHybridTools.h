#ifndef CMPHybridTools_h
#define CMPHybridTools_h

#endif

#import <Foundation/Foundation.h>
#import "CMPHybridUi.h"
#import "CMPHybridApi.h"
#import "JSO.h"

@interface CMPHybridTools : NSObject

//inner data store
@property (nonatomic, strong) JSO *jso;

+ (void)checkAppConfig;

+ (CMPHybridApi *)getHybridApi:(NSString *)name;

+ (void)startUi:(NSString *)strUiName strInitParam:(JSO *)strInitParam objCaller:(CMPHybridUi *)objCaller callback:(HybridCallback)callback;

+ (JSO *)wholeAppConfig;

@end
