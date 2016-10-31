//
//  HybridTools.h
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import <Foundation/Foundation.h>
@class HybridUi;
@class HybridApi;

@interface HybridTools : NSObject

+ (HybridUi *) buildHybridUi:(NSString *)name;

+ (HybridApi *) buildHybridApi:(NSString *)name;

+ (NSDictionary *) fromAppConfigGetApi;

@end
