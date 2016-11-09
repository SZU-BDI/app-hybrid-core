//
//  HybridTools.h
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HybridUi.h"
#import "HybridApi.h"
//#import "WebViewJavascriptBridgeBase.h"
@class JSO;
#ifndef HybridTools_h
#define HybridTools_h

@interface HybridTools : NSObject

//inner data store
@property (nonatomic, strong) JSO *jso;

+ (void)checkAppConfig;

+ (HybridApi *)getHybridApi:(NSString *)name;

+ (void)startUi:(NSString *)strUiName strInitParam:(JSO *)strInitParam objCaller:(id)objCaller callback:(HybridCallback)callback;

+ (JSO *)wholeAppConfig;

@end

#endif
