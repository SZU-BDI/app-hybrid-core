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
#import "WebViewJavascriptBridgeBase.h"
@class JSO;

@interface HybridTools : NSObject

@property (nonatomic, strong) JSO *jso;

/**
 *  单例，保证再多线程只实例化一次
 */
+ (id)sharedManager;

/**
 *  初始化AppConfig
 */
+ (void)initAppConfig;

/**
 *  启动或打开UI
 */
+ (void)startUi:(NSString *)strUiName strInitParam:(JSO *)strInitParam objCaller:(id<HybridUi>)objCaller callback:(WVJBResponseCallback)callback;

/**
 *  通过api映射出api对应的类
 */
+ (HybridApi *)getHybridApi:(NSString *)name;

/**
 *  取到整个AppConfig
 */
+ (JSO *)wholeAppConfig;

@end
