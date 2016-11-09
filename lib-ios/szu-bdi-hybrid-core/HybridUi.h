//
//  HybridUi
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "WebViewJavascriptBridgeBase.h"

// 协议定义
@protocol HybridUi <NSObject>

@optional

- (void)getHaveTopBar:(BOOL)haveTopBar;

- (void)getTopBarTitle:(NSString *)title;

- (void)getWebViewUiUrl:(NSString *)url;

- (void)getCallback:(HybridCallback)callback;

- (void)closeActivity;

@end

@interface HybridUi : NSObject

// 遵循协议的一个代理变量定义
@property (nonatomic, weak) id<HybridUi> HybridUiDelegate;

- (void)setHaveTopBar:(BOOL)haveTopBar;

- (void)setTopBarTitle:(NSString *)title;

- (void)setWebViewUiUrl:(NSString *)url;

- (void)setCallback:(HybridCallback)callback;

- (void)activityClose;

@end
