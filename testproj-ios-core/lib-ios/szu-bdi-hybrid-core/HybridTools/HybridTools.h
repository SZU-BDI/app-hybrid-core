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

//- (HybridUiBase *) getHybridUiBase:(NSString *)name;
//
//- (HybridApi *) getHybridApi:(NSString *)name;

+ (HybridUi *) buildHybridUiBase:(NSString *)name;

+ (HybridApi *) buildHybridApi:(NSString *)name;

+ (NSDictionary *) getAppConfig:(NSString *)name;

@end
