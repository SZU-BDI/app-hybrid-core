//
//  RootViewController.m
//  Hybrid-core
//
//  Created by 双虎 on 16/5/31.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "RootViewController.h"
#import "PushViewController.h"

@interface RootViewController ()<UIWebViewDelegate>
@property (nonatomic, strong) UIWebView *webView;
@property (nonatomic, copy) NSDictionary *inputParamData;
@property (nonatomic, copy) NSString *js_Model;
@property (nonatomic, copy) NSString *js_address;
@property (nonatomic, copy) NSString *js_topbar;
@end

@implementation RootViewController

- (instancetype)initWithNibName:(NSString *)nibNameOrNil
                         bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        _inputParamData = [[NSDictionary alloc] init];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self configWebview];
    // 绑定JavascriptBridge与WebView
    [self webViewJavascriptBridge_Binding:_webView];
    // 绑定会导致webview的代理失效，加上这句即可解决
    [self.bridge setWebViewDelegate:self];
    [self registerHandlerApi];
}

- (void)configWebview{
    
    CGRect bounds = [[UIScreen mainScreen] bounds];
    _webView = [[UIWebView alloc]initWithFrame:bounds];
    _webView.backgroundColor = [UIColor whiteColor];
    _webView.delegate = self;
    _webView.scalesPageToFit = YES;   // 自动对页面进行缩放以适应屏幕
    _webView.scrollView.bounces = NO; // 边缘禁止滑动 默认YES
    [self.view addSubview:_webView];
    // 加载本地html
    [self loadExamplePage:_webView];
}

- (void)registerHandlerApi{
    // *注册方法 _app_activity_open。
    [self.bridge registerHandler:@"_app_activity_open"
                         handler:^(id data, WVJBResponseCallback responseCallback) {
                             _inputParamData = (NSDictionary *)data;
                             _js_Model = _inputParamData[@"mode"];
                             _js_address = _inputParamData[@"address"];
                             _js_topbar = _inputParamData[@"topbar"];
                             
                             if ([_js_Model isEqualToString:@"WebView"]) {
                                 
                                 PushViewController *pushVC = [[PushViewController alloc] init];
                                 pushVC.jsCallback = responseCallback;
                                 // 判断有无yopbar
                                 pushVC.isTopBar = ([_js_topbar isEqualToString:@"Y"])? YES : NO;
                                 // 当 _js_Model 为WebView时，有url
                                 pushVC.address = _js_address;
                                 [self.navigationController pushViewController:pushVC animated:YES];
                             }else{
                                 NSLog(@"本机");
                             }
                         }];
}

- (void)loadExamplePage:(UIWebView*)webView {
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"root" ofType:@"htm"];
    NSString* appHtml = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:nil];
    NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
    [webView loadHTMLString:appHtml baseURL:baseURL];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UIWebViewDelegate
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    return YES;
}

- (void)webViewDidFinishLoad:(UIWebView *)webView;
{
    NSLog(@"Root - Load the success");
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    NSLog(@"Root - Load the fail");
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:NO];
    [[self navigationController] setNavigationBarHidden:YES animated:YES];
}

@end
