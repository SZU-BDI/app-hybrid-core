//
//  HybridConf.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "HybridConf.h"

@interface HybridConf ()
@property (nonatomic, strong) NSMutableDictionary *dictUi;
@property (nonatomic, strong) NSMutableDictionary *dictApi;
@property (nonatomic, strong) NSMutableDictionary *dictConf;
@end

@implementation HybridConf


- (instancetype)init{
    if (self = [super init]){
        _dictUi = [[NSMutableDictionary alloc] initWithCapacity:0];
        _dictApi = [[NSMutableDictionary alloc] initWithCapacity:0];
        _dictConf = [[NSMutableDictionary alloc] initWithCapacity:0];
    }
    return self;
}

-(void)setHybridUiBase:(HybridUi *)hbui AndName:(NSString *)name{
    #warning TODO if dic==null throw exception('config is not found')
    [_dictUi setObject:hbui forKey:name];
}

-(HybridUi *)getHybridUiBase:(NSString *)name{
    return _dictUi[name];
}

- (void)setHybridApi:(HybridApi *)hbapi AndName:(NSString *)name{
    [_dictApi setObject:hbapi forKey:name];
}

- (HybridApi *)getHybridApi:(NSString *)name{
    return _dictApi[name];
}

- (void)setConfig:(id )conf AndName:(NSString *)name{
    [_dictConf setObject:conf forKey:name];
}

- (id )getConfig:(NSString *)name{
    return _dictConf[name];
}

- (NSMutableDictionary *)getApiDict{
    return _dictApi;
}

@end
