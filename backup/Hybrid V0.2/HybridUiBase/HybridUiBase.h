//
//  HybridViewController.h
//  Hybrid-v2
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WebViewJavascriptBridge.h"

@interface HybridUiBase : UIViewController

@property (nonatomic, strong) WebViewJavascriptBridge *bridge;
@property (nonatomic, strong) UINavigationController *topBar;
@property (nonatomic, strong) UIWebView *webView;

- (void)LoadLocalhtmlName:(NSString *)loadLocalhtml;

@end
