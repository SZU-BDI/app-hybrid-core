//
//  ApiActivityOpen.m
//  Hybrid-v2
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "ApiActivityOpen.h"
#import "HybridService.h"
#import "HybridUi.h"

@interface ApiActivityOpen ()
@property (nonatomic, copy) NSDictionary *inputParamData;
@property (nonatomic, copy) NSString *js_Model;
@property (nonatomic, copy) NSString *js_address;
@property (nonatomic, copy) NSString *js_topbar;
@end

@implementation ApiActivityOpen

- (instancetype)init{
    if (self = [super init]){
        _inputParamData = [[NSDictionary alloc] init];
    }
    return self;
}

// 覆盖父类的getHandler方法，并实现
- (WVJBHandler) getHandler{
    return ^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ApiActivityOpen -->  _app_actibity_open");
        
        _inputParamData = (NSDictionary *)data;
        _js_Model = _inputParamData[@"mode"];
        _js_address = _inputParamData[@"address"];
        _js_topbar = _inputParamData[@"topbar"];
        
        if ([_js_Model isEqualToString:@"WebView"]) {
            
            HybridUi *ui = [HybridService buildHybridUiBase:@"UiOpen"];
            // 传递回调函数
            ui.jsCallback = responseCallback;
            // 判断有无yopbar
            ui.isTopBar = ([_js_topbar isEqualToString:@"Y"])? YES : NO;
            // 当 _js_Model 为WebView时，有url
            ui.address = _js_address;
            NSLog(@"open %@", self.hybridUi);
            [self.hybridUi.navigationController pushViewController:ui animated:YES];
            
        }
        
    };
}

@end
