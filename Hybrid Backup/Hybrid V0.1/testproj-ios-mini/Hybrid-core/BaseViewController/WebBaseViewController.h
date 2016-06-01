//
//  WebBaseViewController.h
//  Hybrid-core
//
//  Created by 双虎 on 16/5/31.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WebViewJavascriptBridge.h"

@interface WebBaseViewController : UIViewController

@property (nonatomic, strong) WebViewJavascriptBridge *bridge;

- (void)webViewJavascriptBridge_Binding:(UIWebView *)webView;

@end
