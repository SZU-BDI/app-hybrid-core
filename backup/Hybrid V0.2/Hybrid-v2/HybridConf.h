//
//  HybridConf.h
//  Hybrid-v2
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HybridUiBase.h"
#import "HybridApi.h"

@interface HybridConf : NSObject

- (void)setHybridUiBase:(HybridUiBase *)hbui AndName:(NSString *)name;
- (HybridUiBase *)getHybridUiBase:(NSString *)name;

- (void)setHybridApi:(HybridApi *)hbapi AndName:(NSString *)name;
- (HybridApi *)getHybridApi:(NSString *)name;

- (void)setConfig:(id )conf AndName:(NSString *)name;
- (id )getConfig:(NSString *)name;

- (NSMutableDictionary *)getApiDict;

@end
