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

@interface HybridTools : NSObject

/**
 *  初始化缓存 AppConfig -> s2o()
 */
+ (void)initAppConfig;

/**
 *  启动或打开UI
 */
+ (void)startUi:(NSString *)strUiName strInitParam:(NSDictionary *)strInitParam objCaller:(id<HybridUi>)objCaller;

/**
 *  通过api映射出api对应的类
 */
+ (HybridApi *)getHybridApi:(NSString *)name;

/**
 *  取到整个AppConfig
 */
+ (id)wholeAppConfig;

//+ (HybridUi *) buildHybridUi:(NSString *)name;
//
//+ (HybridApi *) buildHybridApi:(NSString *)name;
//
//+ (NSDictionary *) fromAppConfigGetApi;

@end
