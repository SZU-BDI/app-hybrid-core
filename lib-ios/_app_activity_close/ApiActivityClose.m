//
//  ApiActivityClose.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/3.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "ApiActivityClose.h"
#import "HybridUi.h"

@interface ApiActivityClose ()
@end

@implementation ApiActivityClose

- (WVJBHandler) getHandler{
    return ^(id data, HybridCallback responseCallback) {
        NSLog(@"ApiActivityClose()");
        
        HybridUi *hybridUi = [[HybridUi alloc] init];
        hybridUi.HybridUiDelegate = self.currentUi;
        [hybridUi activityClose];
    };
}

@end
