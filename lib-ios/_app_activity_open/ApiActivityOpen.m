//
//  ApiActivityOpen.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "ApiActivityOpen.h"
#import "HybridTools.h"
//#import "HybridUi.h"
#import "JSO.h"

@interface ApiActivityOpen ()
@property (nonatomic, copy) NSMutableDictionary *paramData;
@property (nonatomic, copy) NSString *js_model;
@property (nonatomic, copy) NSString *js_address;
@property (nonatomic, copy) NSString *js_topbar;
@end

@implementation ApiActivityOpen

//TODO startUI要搬去 Tools类
// 覆盖父类的getHandler方法，并实现.
- (WVJBHandler) getHandler{
    return ^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ApiActivityOpen()");
        
        // JSON String -> JSO -> NSString
        NSString *data_str = [JSO id2s:data];
        JSO *data_jso = [JSO s2o:data_str];
        
        // getChild for key
        self.js_model = [JSO o2s:[data_jso getChild:@"mode"]];
        self.js_address = [JSO o2s:[data_jso getChild:@"address"]];
        self.js_topbar = [JSO o2s:[data_jso getChild:@"topbar"]];
        
        // set param
        _paramData = [[NSMutableDictionary alloc] initWithCapacity:0];
        [self setParam:self.js_model forKey:@"mode"];
        [self setParam:self.js_address forKey:@"url"];
        [self setParam:self.js_topbar forKey:@"topbar"];
        [self setParam:responseCallback forKey:@"callback"];
        
        // start UiContent
        [HybridTools startUi:@"UiContent" strInitParam:(NSDictionary *)_paramData objCaller:self.currentUi];
        
//        _js_param = [[NSDictionary alloc] initWithDictionary:(NSDictionary *)data];
//        _js_model = _js_param[@"mode"];
//        _js_address = _js_param[@"address"];
//        _js_topbar = _js_param[@"topbar"];
// 
//        NSLog(@" _js_param %@",_js_param);
//        
////        if ([_js_model isEqualToString:@"WebView"]) {
//            // 读取配置，获取UiContent
//            NSDictionary *appConfig = [[NSUserDefaults standardUserDefaults] objectForKey:@"config"];
//            HybridUi *ui = [HybridTools buildHybridUi:(NSString *)(appConfig[@"ui_mapping"][@"UiContent"][@"class"])];
//            // 判断有无yopbar
//            ui.isTopBar = ([_js_topbar isEqualToString:@"Y"])? YES : NO;
//           // ([appConfig[@"ui_mapping"][@"UiContent"][@"topbar"] isEqualToString:@"Y"])? YES : NO;
//            // 传递回调函数
//            ui.jsCallback = responseCallback;
//            // 当 _js_Model 为WebView时，有url
//            ui.address = _js_address;
//            NSLog(@"open %@", self.currentUi);
//            [self.currentUi.navigationController pushViewController:ui animated:YES];
//
//        }
    };
}

// 设置覆盖参数，为niu 则不覆盖。
- (void)setParam:(id)param forKey:(NSString *)key{

#warning 这里抓取不到param为空，不知道为什么，打印出来的日志显示是 null ， 但是以下的判断都抓取不到。
    
    if (param == nil || [param isEqual:[NSNull class]] || param == NULL || [param isKindOfClass:[NSNull class]]) {
        return;
    }
    
    [_paramData setObject:param forKey:key];
}

@end
