//
//  ApiActivityOpen.m
//  Hybrid-v2
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "ApiActivityOpen.h"

@implementation ApiActivityOpen

// 覆盖父类的getHandler方法，并实现
- (WVJBHandler) getHandler{
    return ^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ApiActivityOpen -->  _app_actibity_open");
    };
}

@end
