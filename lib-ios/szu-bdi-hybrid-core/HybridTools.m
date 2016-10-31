//
//  HybridTools.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "HybridTools.h"

@implementation HybridTools

+ (HybridUi *) buildHybridUi:(NSString *)name{

    // 利用类的名字，动态获得一个类:
    Class myUiClass = NSClassFromString(name);
    
    // 实例化动态获取的这个类:
    id myUiClassInstance = [[myUiClass alloc] init];

    // 如果实例化成功，则返回该类的实例
    if (myUiClassInstance) {
        NSLog(@"返回ui的是：：：%@", myUiClassInstance);
        return myUiClassInstance;
    }else{
        NSLog(@"Ui: %@ not found", name);
    }
    
    return nil;
}

+ (HybridApi *) buildHybridApi:(NSString *)name{
    
    Class myApiClass = NSClassFromString(name);
    
    id myApiClassInstance = [[myApiClass alloc] init];
    
    if (myApiClassInstance) {
        NSLog(@"返回api的是：：：%@", myApiClassInstance);
        return myApiClassInstance;
    }else{
        NSLog(@"Api: %@ not found", name);
    }
    
    return nil;
}

+ (NSDictionary *) fromAppConfigGetApi{
    // 读取配置
    NSDictionary *appConfig = [[NSUserDefaults standardUserDefaults] objectForKey:@"config"];
    
    if (appConfig[@"api_mapping"]) {
        NSLog(@"==拿到的api映射 --> %@", appConfig[@"api_mapping"]);
        return appConfig[@"api_mapping"];
    }else{
        NSLog(@"api not found");
    }
    return nil;
}

@end
