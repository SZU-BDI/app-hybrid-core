#import <Foundation/Foundation.h>
#import "CMPHybridWebViewUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@interface CMPHybridWebViewUi()

//private prop

#warning dont do like this... too ugly.. use logic like android one
@property (nonatomic) BOOL haveTopBar;

#warning dont do like this... too ugly.. use logic like android one
@property (nonatomic, copy) NSString *accessAddress; // 接口链接

@property (nonatomic, strong) HybridCallback jsCallback;

@end

@implementation CMPHybridWebViewUi


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

//- (void)injectJavascriptFile {
//    //TODO 要优化缓存（等下OK再弄)
//
//    //get path of asset (config.json)
//    NSString *filepath = [[NSBundle mainBundle] pathForResource:@"WebViewJavascriptBridge"ofType:@"js"];
//
//    //get the content of the config.json
//    NSData *filedata = [[NSData alloc] initWithContentsOfFile:filepath];
//
//    //decoded as string of utf-8
//    NSString *js = [[NSString alloc] initWithData:filedata encoding:NSUTF8StringEncoding];
//
//    [self _evaluateJavascript:js];
//    if (self.startupMessageQueue) {
//        NSArray* queue = self.startupMessageQueue;
//        self.startupMessageQueue = nil;
//        for (id queuedMessage in queue) {
//            [self _dispatchMessage:queuedMessage];
//        }
//    }
//}
-(BOOL)isCorrectProcotocolScheme:(NSURL*)url {
    if([[url scheme] isEqualToString:S_JSB_PROTOCOL]){
        return YES;
    } else {
        return NO;
    }
}
-(BOOL)isQueueMessageURL:(NSURL*)url {
    if([[url host] isEqualToString:S_JSB_Q_MSG]){
        return YES;
    } else {
        return NO;
    }
}
- (void)_dispatchMessage:(WVJBMessage*)message {
    NSString *json_string = [self _serializeMessage:message pretty:NO];
    
    //TODO if "" or null change to "null"...
    
    NSString* javascriptCommand = [NSString stringWithFormat:@"WebViewJavascriptBridge._app2js(%@);", json_string];
    
    //if current is main thread then run, otherwise dispatch to main queue to run on the main thread:
    if ([[NSThread currentThread] isMainThread]) {
        [self evalJs:javascriptCommand];
        
    } else {
        dispatch_sync(dispatch_get_main_queue(), ^{
            [self evalJs:javascriptCommand];
        });
    }
}

- (NSString *)_serializeMessage:(id)message pretty:(BOOL)pretty{
    return [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:message options:(NSJSONWritingOptions)(pretty ? NSJSONWritingPrettyPrinted : 0) error:nil] encoding:NSUTF8StringEncoding];
}

- (NSArray*)_deserializeMessageJSON:(NSString *)messageJSON {
    return [NSJSONSerialization JSONObjectWithData:[messageJSON dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:nil];
}

-(NSString *)webViewJavascriptFetchQueyCommand {
    return @"WebViewJavascriptBridge._fetchQueue(true);";
}
//- (void)flushMessageQueue:(NSString *)messageQueueString{
//    if (messageQueueString == nil || messageQueueString.length == 0) {
//        NSLog(@"WebViewJavascriptBridge: WARNING: ObjC got nil while fetching the message queue JSON from webview. This can happen if the WebViewJavascriptBridge JS is not currently present in the webview, e.g if the webview just loaded a new page.");
//        return;
//    }
//
//    id messages = [self _deserializeMessageJSON:messageQueueString];
//    for (WVJBMessage* message in messages) {
//        if (![message isKindOfClass:[WVJBMessage class]]) {
//            NSLog(@"WebViewJavascriptBridge: WARNING: Invalid %@ received: %@", [message class], message);
//            continue;
//        }
//        //[self _log:@"RCVD" json:message];
//
//        NSString* responseId = message[@"responseId"];
//        if (responseId) {
//            HybridCallback responseCallback = _responseCallbacks[responseId];
//            responseCallback(message[@"responseData"]);
//            [self.responseCallbacks removeObjectForKey:responseId];
//        } else {
//            HybridCallback responseCallback = NULL;
//            NSString* callbackId = message[@"callbackId"];
//            if (callbackId) {
//                responseCallback = ^(id responseData) {
//                    if (responseData == nil) {
//                        responseData = [NSNull null];
//                    }
//
//                    WVJBMessage* msg = @{ @"responseId":callbackId, @"responseData":responseData };
//                    [self _queueMessage:msg];
//                };
//            } else {
//                responseCallback = ^(id ignoreResponseData) {
//                    // Do nothing
//                };
//            }
//
//            HybridHandler handler = self.messageHandlers[message[@"handlerName"]];
//
//            if (!handler) {
//                NSLog(@"WVJBNoHandlerException, No handler for message from JS: %@", message);
//                continue;
//            }
//
//            handler(message[@"data"], responseCallback);
//        }
//    }
//}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    NSURL *url = [request URL];
    NSLog(@" TODO shouldStartLoadWithRequest= %@ ",url);
    
    //
    if (webView != _webView) { return YES; }
    
    if ([self isCorrectProcotocolScheme:url]) {
        //        if ([_base isBridgeLoadedURL:url]) {
        //            NSLog(@" skip injecting js %@ ",url);
        //            //[_base injectJavascriptFile];
        //        } else
        if ([self isQueueMessageURL:url]) {
            //                NSString *messageQueueString = [self _evaluateJavascript:[self webViewJavascriptFetchQueyCommand]];
            NSString *messageQueueString = [webView stringByEvaluatingJavaScriptFromString:[self webViewJavascriptFetchQueyCommand]];
            //                [self flushMessageQueue:messageQueueString];
        } else {
            //NSLog(@" logUnkownMessage %@ ",url);
            //            [_base logUnkownMessage:url];
            NSLog(@"WebViewJavascriptBridge: WARNING: Received unknown WebViewJavascriptBridge command url=%@", url);
        }
        return NO;
        //        } else if (strongDelegate && [strongDelegate respondsToSelector:@selector(webView:shouldStartLoadWithRequest:navigationType:)]) {
        //            return [strongDelegate webView:webView shouldStartLoadWithRequest:request navigationType:navigationType];
    } else {
        return YES;
    }
    
    //    if (self.startupMessageQueue) {
    //        NSArray* queue = self.startupMessageQueue;
    //        self.startupMessageQueue = nil;
    //        for (id queuedMessage in queue) {
    //            [self _dispatchMessage:queuedMessage];
    //        }
    //    }
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
    if (webView != _webView) {
        NSLog(@" skip: not the same webview?? ");
        return;
    }
    
    //TODO 好像有个函数读asset，等下换
    //get path of asset (config.json)
    NSString *filepath = [[NSBundle mainBundle] pathForResource:@"WebViewJavascriptBridge"ofType:@"js"];
    
    //get the content of the config.json
    NSData *filedata = [[NSData alloc] initWithContentsOfFile:filepath];
    
    //decoded as string of utf-8
    NSString *js = [[NSString alloc] initWithData:filedata encoding:NSUTF8StringEncoding];
    
    NSString *debug=[webView stringByEvaluatingJavaScriptFromString:js];
    NSLog(@"get %@ by run js: %@",debug,js);
    
    //    __strong NSObject<UIWebViewDelegate> * strongDelegate = _webViewDelegate;
    //    if (strongDelegate && [strongDelegate respondsToSelector:@selector(webViewDidFinishLoad:)]) {
    //        [strongDelegate webViewDidFinishLoad:webView];
    //    }
    //    NSLog(@" injecting js");
    //    [_base injectJavascriptFile];
    //
    
    //NOTES: failed for the windowScriptObject is for macOS only...
    //TODO change to wkwebview later for better performance
    //    //[webView windowScriptObject];
    //    //[win setValue:littleBlackBook forKey:@"AddressBook"];
    //    UIWebDocumentView *documentView = (UIWebDocumentView *)_webView;
    //    WebScriptObject *wso = documentView.webView.windowScriptObject;
    //    [wso setValue:[WebScriptBridge getWebScriptBridge] forKey:@"nativejsb"];
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
    [_webView stringByEvaluatingJavaScriptFromString:js_s];
}

- (void)setTopBar:(BOOL)haveTopBar{
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

-(id)init {
    self = [super init];
    //    self.messageHandlers = [NSMutableDictionary dictionary];
    //    self.startupMessageQueue = [NSMutableArray array];
    //    self.responseCallbacks = [NSMutableDictionary dictionary];
    //    _uniqueId = 0;
    return(self);
}

- (void)dealloc {
    //    self.startupMessageQueue = nil;
    //    self.responseCallbacks = nil;
    //    self.messageHandlers = nil;
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
        
        //quit app if prompted yes
        [CMPHybridTools
         quickConfirmMsgMain:@"Sure to Quit?"
         //         handlerYes:^(UIAlertAction *action)
         handlerYes:^(UIAlertAction *action){
             [self dismissViewControllerAnimated:YES completion:nil];
             
             //home button press programmatically
             UIApplication *app = [UIApplication sharedApplication];
             NSLog(@"Hide...");
             [app performSelector:@selector(suspend)];
             sleep(1);
             NSLog(@"Really Quit...");
             exit(EXIT_SUCCESS);
         }
         handlerNo:nil];
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
        
        // 把当前控制器（ui）赋值给 api的成员变量
        api.currentUi = self;
        
        // Registered name of key handler:
        //            [self.bridge registerHandler:key handler:[api getHandler]];
        
        // NSLog(@"注册方法 %@" , key);
    }
    
    //    NSData *jsonData = [jso_string_value dataUsingEncoding:NSUTF8StringEncoding];
    //    NSError *err;
    //    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
    //                                                        options:NSJSONReadingMutableContainers
    //                                                          error:&err];
    //    if (!err) {
    //
    //        // get the apiMapping all keys:
    //        NSArray *appConfigkeys = [dic allKeys];
    //
    //        // Iterate through all the value(The values in the appConfigkeys is key):
    //        for (NSString *key in appConfigkeys) {
    //
    //            // Get the value through the key:
    //            HybridApi *api = [HybridTools getHybridApi:dic[key]];
    //
    //            // 把当前控制器（ui）赋值给 api的成员变量
    //            api.currentUi = self;
    //
    //            // Registered name of key handler:
    //            //            [self.bridge registerHandler:key handler:[api getHandler]];
    //
    //            // NSLog(@"注册方法 %@" , key);
    //        }
    //    }
}


//TODO 改为 getUiData("address");然后操作
- (void)loadAccessAddress{
    NSLog(@"WebViewUi.loadAccessAddress() %@",self.accessAddress);
    
    //TODO 要根据 地址的协议判断是否本地的，如果是本地的在前面pack上路径，然后统一 load....
    
    if ([self.accessAddress isEqualToString:@"root.htm"])  {
        [self LoadLocalhtmlName:@"root"];
    }
    else if (self.accessAddress != nil){
        [self LoadTheUrl:self.accessAddress];
    }
}

//TODO 下面的代码的通用性好差。。。
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
