//
//  HybridService.h
//  Hybrid-v2
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HybridUiBase.h"
#import "HybridApi.h"

@interface HybridService : NSObject

//- (HybridUiBase *) getHybridUiBase:(NSString *)name;
//
//- (HybridApi *) getHybridApi:(NSString *)name;

+ (HybridUiBase *) buildHybridUiBase:(NSString *)name;

+ (HybridApi *) buildHybridApi:(NSString *)name;

+ (NSDictionary *) getAppConfig:(NSString *)name;

@end
