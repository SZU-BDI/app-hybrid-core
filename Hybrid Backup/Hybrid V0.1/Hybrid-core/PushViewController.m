//
//  PushViewController.m
//  Hybrid-core
//
//  Created by 双虎 on 16/5/31.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "PushViewController.h"

@interface PushViewController ()<UIWebViewDelegate>
@property (nonatomic, strong) UIWebView *webView;
@property (nonatomic, copy) NSMutableDictionary *callbackData;
@end

@implementation PushViewController

- (instancetype)initWithNibName:(NSString *)nibNameOrNil
                         bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        _callbackData = [[NSMutableDictionary alloc] initWithCapacity:0];
    }
    return self;
}


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self CustomLeftBarButtonItem];
    [self configWebView];
    [self getAddress];
    // 绑定JavascriptBridge与WebView
    [self webViewJavascriptBridge_Binding:_webView];
    // 绑定会导致webview的代理失效，加上这句即可解决
    [self.bridge setWebViewDelegate:self];
    [self registerHandlerApi];
}

//自定义BarbuttonItem（导航栏左边的-返回按钮）
-(void)CustomLeftBarButtonItem
{
    UIBarButtonItem *leftBar = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"] style:UIBarButtonItemStylePlain target:self action:@selector(leftBarItemAction)];
    leftBar.tintColor = [UIColor blackColor];
    self.navigationItem.leftBarButtonItem = leftBar;
}
-(void)leftBarItemAction{
    if (self.jsCallback) {
        self.jsCallback(_callbackData);
        NSLog(@"回调 %@", _callbackData);
    }
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)configWebView{
    
    CGRect bounds = [[UIScreen mainScreen] bounds];
    _webView = [[UIWebView alloc]initWithFrame:bounds];
    _webView.backgroundColor = [UIColor whiteColor];
    _webView.delegate = self;
    _webView.scalesPageToFit = YES;   // 自动对页面进行缩放以适应屏幕
    _webView.scrollView.bounces = NO; // 边缘禁止滑动 默认YES
    [self.view addSubview:_webView];
    
    // 加载网页
    [self startLoading:self.address];
}

- (void)registerHandlerApi{
    // 注册方法 _app_activity_close 关闭当前界面.
    [self.bridge registerHandler:@"_app_activity_close"
                     handler:^(id data, WVJBResponseCallback responseCallback) {
                         NSLog(@"_app_activity_close");
                         if (self.jsCallback) {
                             self.jsCallback(_callbackData);
                             NSLog(@"关闭界面，回调 %@", _callbackData);
                         }
                         [self.navigationController popViewControllerAnimated:YES];
                     }];
}


-(void)startLoading:(NSString *)address{
    NSURL* url = [NSURL URLWithString:address];
    NSURLRequest* request = [NSURLRequest requestWithURL:url];
    [_webView loadRequest:request];
}

- (void)getAddress{
    
    [_callbackData setValue:self.address forKey:@"address"];
}

#pragma mark - UIWebViewDelegate
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{   //控制WebView能否加载 （返回yes可以 no则不可以）
    return YES;
}

- (void)webViewDidFinishLoad:(UIWebView *)webView;
{
    NSLog(@"2 - Load the success");
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    NSLog(@"2 - Load the fail");
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:NO];
    // 当 self.isTopBar = YES ,则显示topbar
    if (self.isTopBar == YES) {
        [[self navigationController] setNavigationBarHidden:NO animated:YES];
    }else{
        [[self navigationController] setNavigationBarHidden:YES animated:YES];
    }
}

@end
