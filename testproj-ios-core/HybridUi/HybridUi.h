//
//  HybridUi
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WebViewJavascriptBridge.h"

@interface HybridUi : UIViewController

@property (nonatomic, strong) WebViewJavascriptBridge *bridge;
@property (nonatomic, strong) UINavigationController *topBar;
@property (nonatomic, strong) UIWebView *webView;

- (void)LoadLocalhtmlName:(NSString *)loadLocalhtml;

- (void)LoadTheUrl:(NSString *)url;

@property (nonatomic, copy) NSString *address;
@property (nonatomic, strong) WVJBResponseCallback jsCallback;
@property (nonatomic) BOOL isTopBar;
@end
