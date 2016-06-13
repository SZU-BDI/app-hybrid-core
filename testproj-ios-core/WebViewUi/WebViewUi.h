//
//  WebViewUi.h
//  testproj-ios-core
//
//  Created by 大数据 on 16/6/13.
//  Copyright © 2016年 szu.bdi. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HybridUi.h"
#import "WebViewJavascriptBridge.h"

@interface WebViewUi : HybridUi

@property (nonatomic, strong) WebViewJavascriptBridge *bridge;
@property (nonatomic, strong) UIWebView *webView;
@property (nonatomic, copy) NSString *address;
@property (nonatomic, strong) WVJBResponseCallback jsCallback;
@property (nonatomic) BOOL isTopBar;

- (void)LoadLocalhtmlName:(NSString *)loadLocalhtml;

- (void)LoadTheUrl:(NSString *)url;

@end
