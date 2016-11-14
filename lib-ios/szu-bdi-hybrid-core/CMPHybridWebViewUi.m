#import <Foundation/Foundation.h>
#import "CMPHybridWebViewUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@import JavaScriptCore;

//@interface CMPHybridWebViewUi()
//
////private prop
//
////@property (nonatomic) BOOL haveTopBar;
//
////my ui data
////@property JSO *myUiData;
//
////@property (nonatomic, copy) NSString *accessAddress; // 接口链接
////
////@property (nonatomic, strong) HybridCallback jsCallback;
//
//@end




@implementation CMPHybridWebViewUi


//------------  UIViewController ------------


//NOTES:
//viewDidLoad: Whatever processing you have that needs to be done once.
//viewWilLAppear: Whatever processing that needs to change every time the page is loaded.
/* TODO incase u need
 - (void)viewDidLoad
 {
 [self appWillEnterForeGround]; //register For Application Will enterForeground
 }
 
 
 - (id)appWillEnterForeGround{ //Application will enter foreground.
 
 [[NSNotificationCenter defaultCenter] addObserver:self
 selector:@selector(allFunctions)
 name:UIApplicationWillEnterForegroundNotification
 object:nil];
 return self;
 }
 
 
 -(void) allFunctions{ //call any functions that need to be run when application will enter foreground
 NSLog(@"calling all functions...application just came back from foreground");
 
 
 }
 */
// viewWillAppear() is called before it's display.  some effect can be configurated here
//- (void)viewWillAppear:(BOOL)animated{
//    [super viewWillAppear:animated];
//}


//- (void)didReceiveMemoryWarning {
//    [super didReceiveMemoryWarning];
//    // Dispose of any resources that can be recreated.
//}

//- (instancetype)initWithNibName:(NSString *)nibNameOrNil
//                         bundle:(NSBundle *)nibBundleOrNil{
//    NSLog(@"TODO initWithNibName %@ bundle", nibNameOrNil);
//
//    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
//    if (self) {
//        [self CustomLeftBarButtonItem];
//    }
//    return self;
//}

//NOTES: loadView() is called when the VC is loaded and display...
- (void)loadView{
    
    // initial the webView and add webview in window：
    CGRect rect = [UIScreen mainScreen].bounds;
    self.myWebView = [[UIWebView alloc]initWithFrame:rect];
    
    self.myWebView.backgroundColor = [UIColor whiteColor];
    self.myWebView.delegate = self;// NOTES: UIWebViewDelegate, using "self" as the responder...
    
    // The page automatically zoom to fit the screen, default NO.
    self.myWebView.scalesPageToFit = YES;
    
    // Edges prohibit sliding (default YES)
    self.myWebView.scrollView.bounces = NO;
    self.view = self.myWebView;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
#warning TODO read the myUiData and do init job..
    //initUi()
    /*---------------- 开始设置 -----------------
     若为 WebView 类型，则通过HybridUi协议设置 ui 的 url*/
    //    if ([uiMode isEqualToString:@"WebView"]) {
    //#warning 整个参数丢过去啊。。。唉
    //        //[theHybridUi setWebViewUiUrl:webUrl];
    //    }
    
    //[theHybridUi setTopBar:flagTopBar];
    
    //    // 若 topBar 为显示状态，则通过HybridUi协议设置 ui 的 topBar title
    //    if (flagTopBar && topBarTitle) {
    //        //[theHybridUi setTopBarTitle:topBarTitle];
    //    }
    
    //    // 2、获取 UI 的类型  *覆盖参数有type* 则覆盖附带的type
    //    NSString *uiMode = [self fastO2S:jso_uiConfig forKey:@"type"];
    //    NSString *paramUiMode = [self fastO2S:strInitParam forKey:@"type"];
    //    if (![paramUiMode isEqualToString:@""]) {
    //        uiMode = paramUiMode;
    //    }
    //
    //    // 3、获取 UI 的url  *覆盖参数有url* 则覆盖附带的url
    //    NSString *webUrl = [self fastO2S:jso_uiConfig forKey:@"url"];
    //    NSString *paramWebUrl = [self fastO2S:strInitParam forKey:@"address"];
    //    if (![paramWebUrl isEqualToString:@""]) {
    //        webUrl = paramWebUrl;
    //    }
    //
    //    // 4、获取 UI 有无topBar *覆盖参数有topBar* 则覆盖附带的topBar
    //    NSString *topBarStatus = [self fastO2S:jso_uiConfig forKey:@"topbar"];
    //    BOOL flagTopBar = ([topBarStatus isEqualToString:@"Y"])? YES : NO;
    //    NSString *paramTopBarStatus = [self fastO2S:strInitParam forKey:@"topbar"];
    //    if (![paramTopBarStatus isEqualToString:@""]) {
    //        flagTopBar = ([paramTopBarStatus isEqualToString:@"Y"])? YES : NO;
    //    }
    //
    //    // 5、获取 UI topBar 的标题  *覆盖参数有title* 则覆盖附带的title
    //    NSString *topBarTitle = [self fastO2S:jso_uiConfig forKey:@"title"];
    //    NSString *paramTitle = [self fastO2S:strInitParam forKey:@"title"];
    //    if (![paramTitle isEqualToString:@""]) {
    //        topBarTitle = paramTitle;
    //    }
    
    [self CustomLeftBarButtonItem ];
    
    //    if (self.haveTopBar) {
    //        [[self navigationController] setNavigationBarHidden:NO animated:YES];
    //    }else{
    //        [[self navigationController] setNavigationBarHidden:YES animated:YES];
    //    }
    
    // Do any additional setup after loading the view.
    
    //[self loadAccessAddress];
    
    //    if (_bridge) {
    //        return;
    //    }
    // initial the webView and add webview in window：
    // [self configWebview];
    
    // Registered WebViewJavascriptBridge handleApi：
    [self registerHandlerApi];
}


//------------  prototol UIWebViewDelegate ------------


-(BOOL)isCorrectProcotocolScheme:(NSURL*)url {
    if([[url scheme] isEqualToString:@"jsb1"]){
        return YES;
    } else {
        return NO;
    }
}

//- (void)_dispatchMessage:(NSDictionary*)message {
//    NSString *json_string = [self _serializeMessage:message pretty:NO];
//
//    //TODO if "" or null change to "null"...
//
//    NSString* javascriptCommand = [NSString stringWithFormat:@"WebViewJavascriptBridge._app2js(%@);", json_string];
//
//    //if current is main thread then run, otherwise dispatch to main queue to run on the main thread:
//    if ([[NSThread currentThread] isMainThread]) {
//        [self evalJs:javascriptCommand];
//
//    } else {
//        dispatch_sync(dispatch_get_main_queue(), ^{
//            [self evalJs:javascriptCommand];
//        });
//    }
//}



- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    NSURL *url = [request URL];
    NSLog(@" shouldStartLoadWithRequest() %@ ",url);
    
    //have to ??
    if (webView != self.myWebView) {
        NSLog(@" TODO why the requested webview is not the one private ??? ");
        return YES;
    }
    
    if ([self isCorrectProcotocolScheme:url]) {
        NSLog(@" ignore the old jsb1 scheme....no need any more");
        return NO;
    } else {
        return YES;
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    
    NSLog(@" TODO webViewDidFinishLoad() ");
    if (webView != self.myWebView) {
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
    
    //NSLog(@"get %@ by run js: %@",debug,js);
    
    
    //    JSContext *ctx = [webView valueForKeyPath:@"documentView.webView.mainFrame.javaScriptContext"];
    ////    ctx[@"nativejsb"][@"callHandler"]=^(JSValue *handlerName){
    ////        NSLog(@"JavaScript %@ handlerName: %@", [JSContext currentContext], handlerName);
    ////    };
    //    [ctx evaluateScript:@"setTime(function(){alert(888);},2000);"];
    
    JSContext *ctx = [webView valueForKeyPath:(@"documentView" @".webView" @".mainFrame" @".javaScriptContext")];
    [ctx evaluateScript:@"nativejsb={};"];
    ctx[@"nativejsb"][@"js2app"]=^(JSValue *callBackId,JSValue *handlerName,JSValue *param_s){
        //,JSValue *handlerName,JSStringRef param_s
        NSLog(@"JavaScript %@ callBackId: %@ handlerName %@ param_s %@", [JSContext currentContext], callBackId, handlerName, param_s);
        
        //find the api and call...
        //                responseCallback = ^(id responseData) {
        //                    if (responseData == nil) {
        //                        responseData = [NSNull null];
        //                    }
        //
        //                    WVJBMessage* msg = @{ @"responseId":callbackId, @"responseData":responseData };
        //                    [self _queueMessage:msg];
        //                };
        //        HybridHandler handler = self.messageHandlers[message[@"handlerName"]];
        //
        //        if (!handler) {
        //            NSLog(@"WVJBNoHandlerException, No handler for message from JS: %@", message);
        //            continue;
        //        }
        //
        //        handler(message[@"data"], responseCallback);
        
#warning important for callback : run on ui thread
        //        if ([[NSThread currentThread] isMainThread]) {
        //            [self _evaluateJavascript:javascriptCommand];
        //
        //        } else {
        //            dispatch_sync(dispatch_get_main_queue(), ^{
        //                [self _evaluateJavascript:javascriptCommand];
        //            });
        //        }
        
        return @"OK";
    };
    //        ctx[@"nativejsb"]=^(JSValue *handlerName){
    //            NSLog(@"JavaScript %@ handlerName: %@", [JSContext currentContext], handlerName);
    //        };
    //
    //    [ctx setExceptionHandler:^(JSContext *context, JSValue *value) {
    //        NSLog(@"%@", value);
    //    }];
    //ctx[@"document"][@"body"][@"style"][@"background"] = @"steelblue";
    //[ctx evaluateScript:@"setTimeout(function(){alert(888);},2000);"];
    //[ctx evaluateScript:@"setTimeout(function(){alert(typeof nativejsb.js2app);},2000);"];
    
    NSString *debug=[webView stringByEvaluatingJavaScriptFromString:js];
    
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    NSLog(@" didFailLoadWithError() %@",error);
    //TODO design a error handler in config.json with handlerClassName...
}

//------------   <HybridUi> ------------

- (void)callWebViewDoJs:(UIWebView *)webview :(NSString *)js_s
{
    //if current is main thread then run, otherwise dispatch to main queue to run on the main thread:
    if ([[NSThread currentThread] isMainThread]) {
        [CMPHybridTools callWebViewDoJs:webview :js_s];
    } else {
        dispatch_sync(dispatch_get_main_queue(), ^{
            [self callWebViewDoJs:webview :js_s];
        });
    }
}

- (JSValue *) evalJs:(NSString *)js_s
{
    return [CMPHybridTools callWebViewDoJs:self.myWebView :js_s];
}

//- (void)setTopBar:(BOOL)haveTopBar{
//    _haveTopBar = haveTopBar;
//}

//- (void)setTopBarTitle:(NSString *)title{
//    self.title = title;
//}

//- (void)setWebViewUiUrl:(NSString *)url{
//    _accessAddress = url;
//}

//- (void)setCallback:(HybridCallback)callback{
////    _jsCallback = callback;
//    self.callback=callback;
//}

//- (void)closeActivity{
//    [self leftBarItemAction];
//}

//------------ self -----------------

- (void) initUi{
    
}

// Custom topBar left back buttonItem
- (void)CustomLeftBarButtonItem{
    
    UIBarButtonItem *leftBar
    = [[UIBarButtonItem alloc]
       initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"]
       style:UIBarButtonItemStylePlain
       target:self
       action:@selector(close) //on('click')=>close()
       ];
    leftBar.tintColor = [UIColor blackColor];
    self.navigationItem.leftBarButtonItem = leftBar;
}

//- (void)leftBarItemAction{
//
//    // 判断是被push还是被modal出来的;
//    NSArray *viewcontrollers = self.navigationController.viewControllers;
//
//    if (viewcontrollers.count > 1) {
//
//        if ([viewcontrollers objectAtIndex:viewcontrollers.count-1] == self){
//            //push方式
//            [self.navigationController popViewControllerAnimated:YES];
//        }
//    }
//    else{
//
//        //quit app if prompted yes
//        [CMPHybridTools
//         quickConfirmMsgMain:@"Sure to Quit?"
//         //         handlerYes:^(UIAlertAction *action)
//         handlerYes:^(UIAlertAction *action){
//             [self dismissViewControllerAnimated:YES completion:nil];
//
//             //home button press programmatically
//             UIApplication *app = [UIApplication sharedApplication];
//             NSLog(@"Hide...");
//             [app performSelector:@selector(suspend)];
//             sleep(1);
//             NSLog(@"Really Quit...");
//             exit(EXIT_SUCCESS);
//         }
//         handlerNo:nil];
//    }
//
//#warning TODO here
////    if (self.callback) {
////
////        //        JSO *jsoValue = [JSO s2o:self.accessAddress];
////        //        [jsoValue setChild:@"address" JSO:jsoValue];
////        NSString *address = [NSString stringWithFormat:@"%@", self.accessAddress];
////        self.callback(@{@"address":address});
////    }
//}

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
#warning TODO 这里非常重要啊，要重新做 registerHandler 到当前webview
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
//- (void)loadAccessAddress{
//    NSLog(@"WebViewUi.loadAccessAddress() %@",self.accessAddress);
//
//    //TODO 要根据 地址的协议判断是否本地的，如果是本地的在前面pack上路径，然后统一 load....
//
//    if ([self.accessAddress isEqualToString:@"root.htm"])  {
//        [self LoadLocalhtmlName:@"root"];
//    }
//    else if (self.accessAddress != nil){
//        [self LoadTheUrl:self.accessAddress];
//    }
//}

//TODO 下面的代码的通用性好差。。。
- (void)LoadLocalhtmlName:(NSString *)loadLocalhtml{
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:loadLocalhtml ofType:@"htm"];
    NSString* appHtml = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:nil];
    NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
    [self.myWebView loadHTMLString:appHtml baseURL:baseURL];
}

- (void) loadUrl:(NSString *)url{
    NSURL *requesturl = [NSURL URLWithString:url];
    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
    [self.myWebView loadRequest:request];
}

-(id)init {
    self = [super init];
    NSLog(@"TODO HybridWebViewUi.init()");
    return(self);
}

- (void)dealloc {
    NSLog(@"TODO HybridWebViewUi.dealloc()");
}

@end
