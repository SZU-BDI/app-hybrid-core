//
//  ApiActivityClose.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/3.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "ApiActivityClose.h"
#import "HybridService.h"
#import "HybridUi.h"

@interface ApiActivityClose ()
@property (nonatomic, copy) NSMutableDictionary *callbackData;
@end
@implementation ApiActivityClose

- (instancetype)init{
    if (self = [super init]){
        _callbackData = [[NSMutableDictionary alloc] initWithCapacity:0];
    }
    return self;
}

// 覆盖父类的getHandler方法，并实现
- (WVJBHandler) getHandler{
    return ^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ApiActivityClose -->  _app_actibity_close");
        
        HybridUi *ui = (HybridUi *)self.hybridUi;
        [_callbackData setValue:ui.address forKey:@"address"];
        if (ui.jsCallback) {
            ui.jsCallback(_callbackData);
        }
        NSLog(@"colse %@", self.hybridUi);
        [self.hybridUi.navigationController popViewControllerAnimated:YES];
        
    };
}

@end
