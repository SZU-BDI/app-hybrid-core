//
//  ApiActivityOpen.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "ApiActivityOpen.h"
#import "HybridTools.h"
#import "WebViewUi.h"

@interface ApiActivityOpen ()
@property (nonatomic, copy) NSDictionary *js_param;
@property (nonatomic, copy) NSString *js_model;
@property (nonatomic, copy) NSString *js_address;
@property (nonatomic, copy) NSString *js_topbar;
@end

@implementation ApiActivityOpen

// 覆盖父类的getHandler方法，并实现.
- (WVJBHandler) getHandler{
    return ^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ApiActivityOpen -->  (_app_actibity_open)");
        
        _js_param = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)data];
        _js_model = _js_param[@"mode"];
        _js_address = _js_param[@"address"];
        _js_topbar = _js_param[@"topbar"];
        
        if ([_js_model isEqualToString:@"WebView"]) {
            
            WebViewUi *ui = (WebViewUi *)[HybridTools buildHybridUiBase:@"WebViewUi"];
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
