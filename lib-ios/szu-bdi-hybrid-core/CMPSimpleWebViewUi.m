//#import <objc/message.h>


#import "CMPSimpleWebViewUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@interface CMPSimpleWebViewUi()

//private prop

#warning dont do like this... too ugly.. use logic like android one
@property (nonatomic) BOOL haveTopBar;

#warning dont do like this... too ugly.. use logic like android one
@property (nonatomic, copy) NSString *accessAddress; // 接口链接

@property (nonatomic, strong) HybridCallback jsCallback;

@end



@implementation CMPSimpleWebViewUi


//------------  UIViewController ------------

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if (self.haveTopBar)  [[self navigationController] setNavigationBarHidden:NO animated:YES];
    if (!self.haveTopBar) [[self navigationController] setNavigationBarHidden:YES animated:YES];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (instancetype)initWithNibName:(NSString *)nibNameOrNil
                         bundle:(NSBundle *)nibBundleOrNil{
    NSLog(@"TODO initWithNibName %@ bundle", nibNameOrNil);
    
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [self CustomLeftBarButtonItem];
    }
    return self;
}

- (void)loadView{
    
    // initial the webView and add webview in window：
    CGRect rect = [UIScreen mainScreen].bounds;
    self.webView = [[UIWebView alloc]initWithFrame:rect];
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
    
    //    if (_bridge) {
    //        return;
    //    }
    // initial the webView and add webview in window：
    // [self configWebview];
    
    // Enable logging：
    //    [WebViewJavascriptBridge enableLogging];
    
    // initial the WebViewJavascriptBridge，With the webview binding：
    //    _bridge = [WebViewJavascriptBridge bridgeForWebView:_webView];
    
    // WebViewJavascriptBridge 的绑定，会导致webview原有的代理失效，加上这句即可解决。
    //    [_bridge setWebViewDelegate:self];
    
    // Registered WebViewJavascriptBridge handleApi：
    [self registerHandlerApi];
}




//------------  prototol UIWebViewDelegate ------------

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    NSURL *url = [request URL];
    NSLog(@" TODO shouldStartLoadWithRequest= %@ ",url);
    
    //
    if (webView != _webView) { return YES; }
    
    //    __strong NSObject<UIWebViewDelegate> * strongDelegate = _webViewDelegate;
    //    if ([_base isCorrectProcotocolScheme:url]) {
    //        //        if ([_base isBridgeLoadedURL:url]) {
    //        //            NSLog(@" skip injecting js %@ ",url);
    //        //            //[_base injectJavascriptFile];
    //        //        } else
    //        if ([_base isQueueMessageURL:url]) {
    //            NSString *messageQueueString = [self _evaluateJavascript:[_base webViewJavascriptFetchQueyCommand]];
    //            [_base flushMessageQueue:messageQueueString];
    //        } else {
    //            //NSLog(@" logUnkownMessage %@ ",url);
    //            //            [_base logUnkownMessage:url];
    //            NSLog(@"WebViewJavascriptBridge: WARNING: Received unknown WebViewJavascriptBridge command url=%@", url);
    //        }
    //        return NO;
    //    } else if (strongDelegate && [strongDelegate respondsToSelector:@selector(webView:shouldStartLoadWithRequest:navigationType:)]) {
    //        return [strongDelegate webView:webView shouldStartLoadWithRequest:request navigationType:navigationType];
    //    } else {
    //        return YES;
    //    }
    return YES;
}
- (void)webViewDidStartLoad:(UIWebView *)webView {
    NSLog(@" TODO webViewDidStartLoad ");
    //    if (webView != _webView) { return; }
    //
    //    __strong NSObject<UIWebViewDelegate> * strongDelegate = _webViewDelegate;
    //    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webViewDidStartLoad:)]) {
    //        [strongDelegate webViewDidStartLoad:webView];
    //    }
}

//- (void)webViewDidFinishLoad:(UIWebView *)webView;{
//
//    NSLog(@" TODO webViewDidFinishLoad ?");
//}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    NSLog(@" TODO webViewDidFinishLoad() ");
    //    if (webView != _webView) {
    //        NSLog(@" skip: not the same webview?? ");
    //        return;
    //    }
    //
    //    __strong NSObject<UIWebViewDelegate> * strongDelegate = _webViewDelegate;
    //    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webViewDidFinishLoad:)]) {
    //        [strongDelegate webViewDidFinishLoad:webView];
    //    }
    //    NSLog(@" injecting js");
    //    [_base injectJavascriptFile];
    //
    //    //NOTES: failed for the windowScriptObject is for macOS only...
    //    //TODO change to wkwebview later for better performance
    //    //    //[webView windowScriptObject];
    //    //    //[win setValue:littleBlackBook forKey:@"AddressBook"];
    //    //    UIWebDocumentView *documentView = (UIWebDocumentView *)_webView;
    //    //    WebScriptObject *wso = documentView.webView.windowScriptObject;
    //    //    [wso setValue:[WebScriptBridge getWebScriptBridge] forKey:@"nativejsb"];
}


- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    NSLog(@" didFailLoadWithError() %@",error);
    //    if (webView != _webView) {
    //        NSLog(@" skip: not the same webview?? ");
    //        return;
    //    }
    //
    //    __strong NSObject<UIWebViewDelegate> * strongDelegate = _webViewDelegate;
    //    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webView:didFailLoadWithError:)]) {
    //        [strongDelegate webView:webView didFailLoadWithError:error];
    //    }
}


//------------   <HybridUi> ------------

#warning TODO add evalJs(js_s, callback)
- (void)evalJs:(NSString *)js_s{
    //
}

- (void)setHaveTopBar:(BOOL)haveTopBar{
    _haveTopBar = haveTopBar;
}

- (void)setTopBarTitle:(NSString *)title{
    self.title = title;
}

- (void)setWebViewUiUrl:(NSString *)url{
    _accessAddress = url;
}

- (void)setCallback:(HybridCallback)callback{
    _jsCallback = callback;
}

- (void)closeActivity{
    [self leftBarItemAction];
}

//------------ self -----------------


// Custom topBar left back buttonItem
- (void)CustomLeftBarButtonItem{
    
    UIBarButtonItem *leftBar = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"] style:UIBarButtonItemStylePlain target:self action:@selector(leftBarItemAction)];
    leftBar.tintColor = [UIColor blackColor];
    self.navigationItem.leftBarButtonItem = leftBar;
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex != 0)  // 0 == the cancel button
    {
        SEL sltQuit = NSSelectorFromString(@"terminateWithSuccess");
       
        [self dismissViewControllerAnimated:YES completion:nil];
        
        //home button press programmatically
        UIApplication *app = [UIApplication sharedApplication];
        [app performSelector:@selector(suspend)];
        
        /* Do not provide buttons or options for exiting from your application.
         If you do so apple will reject your application.
         Also if you call exit(0) from your application at certain point, apple will take it as a crash.
         So never do it, your app will be rejected.
         */
        //exit app when app is in background
//#if TARGET_IPHONE_SIMULATOR
//        //[[UIApplication sharedApplication] sendAction:SIGKILL to:[UIApplication sharedApplication] from:self forEvent:nil];
//        //        [app sendAction:SIGKILL to:app from:app forEvent:nil];
//        //        [UIApplication performSelector:@selector(terminateWithSuccess)];
//        //        [[UIApplication sharedApplication] terminateWithSuccess];
//        exit(EXIT_SUCCESS);
//        //exit(0);
//#else
//        
////        Class class = NSClassFromString("UIViewController");
////        id viewController = [[class alloc] init];
//        [app performSelector:@selector(terminateWithSuccess)];
//#endif
//        objc_msgSend(app, NSSelectorFromString(@"terminateWithSuccess"));
//        [app performSelector:sltQuit];

        //((void (*)(id, SEL))[app methodForSelector:selector])(app, sltQuit);
        
        [app sendAction:sltQuit to:app from:app forEvent:nil];
    }
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
        //NOTES: modify the config.json, let the UiRoot with a "topbar":"Y" to test this case
#warning TODO 对话框新建一个delate，不要用 self...最好做到 tools类
        //show confirmation message to user
        UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"Confirmation"
                                                        message:@"Sure to Quit?"
                                                       delegate:self
                                              cancelButtonTitle:@"Cancel"
                                              otherButtonTitles:@"OK", nil];
        [alert show];

    }
    
    if (self.jsCallback) {
        
        //        JSO *jsoValue = [JSO s2o:self.accessAddress];
        //        [jsoValue setChild:@"address" JSO:jsoValue];
        NSString *address = [NSString stringWithFormat:@"%@", self.accessAddress];
        self.jsCallback(@{@"address":address});
    }
}

- (void)registerHandlerApi{
    
    // get the appConfig:
    JSO *jsonO = [CMPHybridTools wholeAppConfig];
    
    // 获取 Api 映射数据
    JSO *jso_api_mapping = [jsonO getChild:@"api_mapping"];
    NSString *jso_string_value = [JSO o2s:jso_api_mapping];
    
    JSO *jso = [JSO s2o:jso_string_value];
    NSLog(@"TODO registerHandlerApi %@", [jso getChildKeys]);
    
    for (NSString *key in [jso getChildKeys]) {
        
        // Get the value through the key:
        
        NSString *apiname = [[jso getChild:key] toString] ;
        CMPHybridApi *api = [CMPHybridTools getHybridApi:apiname];

        api.currentUi = self;

    }

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
//- (void)dealloc{
//    NSLog(@"WebViewUi dealloc");
//}

@end
