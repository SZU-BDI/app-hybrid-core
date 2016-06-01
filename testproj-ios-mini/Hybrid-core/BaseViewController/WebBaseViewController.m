//
//  WebBaseViewController.m
//  Hybrid-core
//
//  Created by 双虎 on 16/5/31.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "WebBaseViewController.h"

@interface WebBaseViewController ()
@end

@implementation WebBaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)webViewJavascriptBridge_Binding:(UIWebView *)webView{
    if (self.bridge) {
        return;
    }
    // 配置logging 启用日志记录
    [WebViewJavascriptBridge enableLogging];
    // 创建WebViewJavascriptBridge对象并与UIWebView对象绑定
    self.bridge = [WebViewJavascriptBridge bridgeForWebView:webView];
}

@end
