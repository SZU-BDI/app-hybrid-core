

#import "WebViewUi.h"
#import "HybridApi.h"
#import "HybridTools.h"
#import "WebViewJavascriptBridge.h"
#import "JSO.h"


@interface WebViewUi WEBVIEWUIINTERFACE


@property (nonatomic, strong) WVJB_WEBVIEW_TYPE *webView;

//@property (nonatomic, copy) NSDictionary *callbackData;

@property (nonatomic) BOOL haveTopBar;
@property (nonatomic, copy) NSString *accessAddress; // 接口链接
@property (nonatomic, strong) WebViewJavascriptBridge *bridge;
@property (nonatomic, strong) WVJBResponseCallback jsCallback;

@end

@implementation WebViewUi

- (instancetype)initWithNibName:(NSString *)nibNameOrNil
                         bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [self CustomLeftBarButtonItem];
    }
    return self;
}

- (void)loadView{
    
    // initial the webView and add webview in window：
    CGRect rect = [UIScreen mainScreen].bounds;
    self.webView = [[WVJB_WEBVIEW_TYPE alloc]initWithFrame:rect];
    self.webView.backgroundColor = [UIColor whiteColor];
    self.webView.delegate = self;
    
    // The page automatically zoom to fit the screen, default NO.
    self.webView.scalesPageToFit = YES;
    
    // Edges prohibit sliding, default YES.
    self.webView.scrollView.bounces = NO;
    self.view = self.webView;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self loadAccessAddress];

    if (_bridge) {
        return;
    }
    // initial the webView and add webview in window：
    // [self configWebview];
    
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
- (void)CustomLeftBarButtonItem{
    
    UIBarButtonItem *leftBar = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"] style:UIBarButtonItemStylePlain target:self action:@selector(leftBarItemAction)];
    leftBar.tintColor = [UIColor blackColor];
    self.navigationItem.leftBarButtonItem = leftBar;
}

- (void)leftBarItemAction{
    
    // 判断是被push还是被modal出来的;
    NSArray *viewcontrollers = self.navigationController.viewControllers;
    
    if (viewcontrollers.count > 1) {
        
        if ([viewcontrollers objectAtIndex:viewcontrollers.count-1] == self){
            
            //push方式
            [self.navigationController popViewControllerAnimated:YES];
        }
    }
    else{
        
        // present方式
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    
    if (self.jsCallback) {
        
        self.jsCallback(@{@"address":@"test url"});
    }
//    if (self.address) {
//        _callbackData = [[NSDictionary alloc] initWithObjects:@[self.address] forKeys:@[@"address"]];
//    }
//    if (self.jsCallback) {
//        self.jsCallback(_callbackData);
//    }
//    if (self.navigationController.viewControllers.count > 1){
//        [self.navigationController popViewControllerAnimated:YES];
//    }
}

- (void)loadAccessAddress{
    NSLog(@"WebViewUi.loadAccessAddress() %@",self.accessAddress);
    if ([self.accessAddress isEqualToString:@"root.htm"])  {
        [self LoadLocalhtmlName:@"root"];
    }
    else if (self.accessAddress != nil){
        [self LoadTheUrl:self.accessAddress];
    }
}

//- (void)configWebview{
//    
//    CGRect bounds = [[UIScreen mainScreen] bounds];
//    self.webView = [[WVJB_WEBVIEW_TYPE alloc]initWithFrame:bounds];
//    self.webView.backgroundColor = [UIColor whiteColor];
//    self.webView.delegate = self;
//    // The page automatically zoom to fit the screen, default NO.
//    self.webView.scalesPageToFit = YES;
//    // Edges prohibit sliding, default YES.
//    self.webView.scrollView.bounces = NO;
//    [self.view addSubview:self.webView];
//    
//    if (self.address) {
//        [self LoadTheUrl:self.address];
//    }else{
//        [self LoadLocalhtmlName:@"root"];
//    }
//
//}

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
    // NSDictionary *appConfig = [NSDictionary dictionaryWithDictionary:(NSDictionary *)[HybridTools wholeAppConfig]];
    NSString *jso_string = [HybridTools wholeAppConfig];
    
    // 获取 Api 映射数据
    // NSDictionary *apiMapping = [NSDictionary dictionaryWithDictionary:(NSDictionary *)(appConfig[@"api_mapping"])];
    JSO *jso = [JSO s2o:jso_string];
    JSO *jso_api_mapping = [jso getChild:@"api_mapping"];
    NSString *jso_string_value = [JSO o2s:jso_api_mapping];
    
    
    NSData *jsonData = [jso_string_value dataUsingEncoding:NSUTF8StringEncoding];
    NSError *err;
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
                                                        options:NSJSONReadingMutableContainers
                                                          error:&err];
    
    if (!err) {
        
        // get the apiMapping all keys:
        NSArray *appConfigkeys = [dic allKeys];
        
        // Iterate through all the value(The values in the appConfigkeys is key):
        for (NSString *key in appConfigkeys) {
            
            // Get the value through the key:
            HybridApi *api = [HybridTools getHybridApi:dic[key]];
            
            // 把当前控制器（ui）赋值给 api的成员变量
            api.currentUi = self;
            
            // Registered name of key handler:
            [self.bridge registerHandler:key handler:[api getHandler]];
            
            // NSLog(@"注册方法 %@" , key);
        }
    }
    
//    NSLog(@"88 == %@", jso_string_value);
//    JSO *apiMapping = [JSO s2o:jso_string_value];
}

//- (void)registerHandlerApi{
//    
//    // get the appConfig:
//    NSDictionary *appConfig = [HybridTools fromAppConfigGetApi];
//    
//    // get the appConfig all keys:
//    NSArray *appConfigkeys = [appConfig allKeys];
//    
//    // Iterate through all the value(The values in the appConfigkeys is key):
//    for (NSString *key in appConfigkeys) {
//        
//        // Get the value through the key:
//        HybridApi *api = [HybridTools buildHybridApi:appConfig[key]];
//        
//        // 把当前控制器（ui）赋值给 api的成员变量
//        api.currentUi = self;
//        
//        // Registered name of key handler:
//        [self.bridge registerHandler:key handler:[api getHandler]];
//        NSLog(@"注册方法 %@" , key);
//    }
//}

#pragma mark - WVJB_WEBVIEW_DELEGATE_TYPE

//- (BOOL)webView:(WVJB_WEBVIEW_TYPE *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType{
//    
//    return YES;
//}

- (void)webViewDidFinishLoad:(WVJB_WEBVIEW_TYPE *)webView;{
    
    NSLog(@"webViewDidFinishLoad ?");
}

- (void)webView:(WVJB_WEBVIEW_TYPE *)webView didFailLoadWithError:(NSError *)error{
    
    NSLog(@"WebViewUi %@",error);
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if (self.haveTopBar)  [[self navigationController] setNavigationBarHidden:NO animated:YES];
    if (!self.haveTopBar) [[self navigationController] setNavigationBarHidden:YES animated:YES];
}

#pragma mark - HybridUiDelegate
- (void)getHaveTopBar:(BOOL)haveTopBar{
    _haveTopBar = haveTopBar;
}

- (void)getTopBarTitle:(NSString *)title{
    self.title = title;
}

- (void)getWebViewUiUrl:(NSString *)url{
    _accessAddress = url;
}

- (void)getCallback:(WVJBResponseCallback)callback{
    _jsCallback = callback;
}

- (void)closeActivity{
    [self leftBarItemAction];
}

- (void)dealloc{
    NSLog(@"WebViewUi dealloc");
}

@end
