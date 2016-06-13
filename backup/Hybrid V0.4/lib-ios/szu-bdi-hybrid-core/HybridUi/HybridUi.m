//
//  HybridUi.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "HybridUi.h"
#import "HybridApi.h"
#import "HybridConf.h"
#import "HybridTools.h"

@interface HybridUi ()<UIWebViewDelegate>
@property (nonatomic, copy) NSMutableDictionary *callbackData;
@end

@implementation HybridUi

- (instancetype)initWithNibName:(NSString *)nibNameOrNil
                         bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.topBar  = [[UINavigationController alloc] initWithRootViewController:self];
        _callbackData = [[NSMutableDictionary alloc] initWithCapacity:0];
        [self CustomLeftBarButtonItem];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    if (_bridge) {
        return;
    }
    // initial the webView and add webview in window：
    [self configWebview];
    
    // Enable logging：
    [WebViewJavascriptBridge enableLogging];
    
    // initial the WebViewJavascriptBridge，With the webview binding：
    _bridge = [WebViewJavascriptBridge bridgeForWebView:_webView];
    
    // WebViewJavascriptBridge 的绑定，会导致webview原有的代理失效，加上这句即可解决。
    [_bridge setWebViewDelegate:self];
    
    // Registered WebViewJavascriptBridge handleApi：
    [self registerHandlerApi];
}

// Custom topBar left back buttonItem
-(void)CustomLeftBarButtonItem
{
    UIBarButtonItem *leftBar = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"] style:UIBarButtonItemStylePlain target:self action:@selector(leftBarItemAction)];
    leftBar.tintColor = [UIColor blackColor];
    self.navigationItem.leftBarButtonItem = leftBar;
}
-(void)leftBarItemAction{
    [_callbackData setValue:self.address forKey:@"address"];
    if (self.jsCallback) {
        self.jsCallback(_callbackData);
    }
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)configWebview{
    
    CGRect bounds = [[UIScreen mainScreen] bounds];
    self.webView = [[UIWebView alloc]initWithFrame:bounds];
    self.webView.backgroundColor = [UIColor whiteColor];
    self.webView.delegate = self;
    // The page automatically zoom to fit the screen, default NO.
    self.webView.scalesPageToFit = YES;
    // Edges prohibit sliding, default YES.
    self.webView.scrollView.bounces = NO;
    [self.view addSubview:self.webView];
}

- (void)LoadLocalhtmlName:(NSString *)loadLocalhtml{
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:loadLocalhtml ofType:@"htm"];
    NSString* appHtml = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:nil];
    NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
    [self.webView loadHTMLString:appHtml baseURL:baseURL];
}

- (void)LoadTheUrl:(NSString *)url{
    NSURL *requesturl = [NSURL URLWithString:url];
    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
    [self.webView loadRequest:request];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (void)registerHandlerApi{
    
    // get the appConfig:
    NSDictionary *appConfig = [HybridTools getAppConfig:@"ApiConf"];
    
    // get the appConfig all keys:
    NSArray *appConfigkeys = [appConfig allKeys];
    
    // Iterate through all the value(The values in the appConfigkeys is key):
    for (NSString *key in appConfigkeys) {
        
        // Get the value through the key:
        HybridApi *api = [HybridTools buildHybridApi:appConfig[key]];
        
        // 把当前控制器（ui）赋值给 api的成员变量
        api.hybridUi = self;
        
        // Registered name of key handler:
        [self.bridge registerHandler:key handler:[api getHandler]];
        NSLog(@"注册方法 %@" , key);
    }
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

@end
