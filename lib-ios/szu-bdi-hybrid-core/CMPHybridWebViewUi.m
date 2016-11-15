#import <Foundation/Foundation.h>
#import "CMPHybridWebViewUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@import JavaScriptCore;

@implementation CMPHybridWebViewUi


//------------  UIViewController ------------


//NOTES:
//viewDidLoad: Whatever processing you have that needs to be done once.
//viewWilLAppear: Whatever processing that needs to change every time the page is loaded.


//http://stackoverflow.com/questions/18979837/how-to-hide-ios-status-bar
- (BOOL)prefersStatusBarHidden {
    return YES;
}

-(void)viewDidUnload
{
    NSLog(@"TODO viewDidUnload() 要不要在这里呼叫callback....");
    [super viewDidLoad];
}
- (void)viewDidLoad {
    
    [super viewDidLoad];
    
    [self initUi];
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
    
    NSString *js = [CMPHybridTools readAssetInStr:@"WebViewJavascriptBridge.js"];
    
    JSContext *ctx = [webView valueForKeyPath:(@"documentView" @".webView" @".mainFrame" @".javaScriptContext")];
    [ctx evaluateScript:@"nativejsb={};"];
    ctx[@"nativejsb"][@"js2app"]=^(JSValue *callBackId,JSValue *handlerName,JSValue *param_s){
        //,JSValue *handlerName,JSStringRef param_s
        NSLog(@"JavaScript %@ callBackId: %@ handlerName %@ param_s %@", [JSContext currentContext], callBackId, handlerName, param_s);
#warning 暂时用 _app_activity_close 测试，稍后要接回对应的api
        if( [@"_app_activity_close" isEqualToString:[handlerName toString]] ){
            [self closeUi];
        }
        if( [@"_app_activity_open" isEqualToString:[handlerName toString]] ){
            JSO *param =[JSO s2o:[param_s toString]];
            JSO *name=[param getChild:@"name"];
            NSString *name_s= [name toString];
            if([CMPHybridTools isEmptyString:name_s]){
                name_s=@"UiRoot";//TMP !!! need UiError...
            }
            CMPHybridUi *ui=[CMPHybridTools startUi:name_s strInitParam:nil objCaller:self];
            if(ui!=nil){
                [ui on:@"close" :^(NSString *eventName, id extraData){
                    //responseCallback(extraData);
                    NSLog(@" TODO !!! 转回给 API...");
                }];
                //[self toggleFullscreen:nil withDuration:0.3];
                
                //
                //
                
            }
        }
        if( [@"app_set_topbar" isEqualToString:[handlerName toString]] ){
            JSO *param =[JSO s2o:[param_s toString]];
            JSO *topbarmode=[param getChild:@"mode"];
            if([@"F" isEqualToString:topbarmode]){
                
            }
        }
        
        //                HybridHandler handler = self.messageHandlers[message[@"handlerName"]];
        //
        //                if (!handler) {
        //                    NSLog(@"WVJBNoHandlerException, No handler for message from JS: %@", message);
        //                    continue;
        //                }
        //
        //                handler(message[@"data"], responseCallback);
        
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
        
        
        
        return @"OK";
    };
    
    //STUB
    //    [ctx setExceptionHandler:^(JSContext *context, JSValue *value) {
    //        NSLog(@"%@", value);
    //    }];
    
    [ctx evaluateScript:js];
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

//------------ self -----------------


//TODO
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
        // NSLog(@"注册方法 %@" , key);
    }
    
    
}

//- (void)LoadLocalhtmlName:(NSString *)loadLocalhtml{
//    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:loadLocalhtml ofType:@"htm"];
//    NSString* appHtml = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:nil];
//    NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
//    [self.myWebView loadHTMLString:appHtml baseURL:baseURL];
//}

- (void) loadUrl:(NSString *)url{
    //    //TODO 要根据 地址的协议判断是否本地的，如果是本地的在前面pack上路径，然后统一 load....
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

-(void) initUi
{
    
    // initial the webView and add webview in window：
    CGRect rect = [UIScreen mainScreen].bounds;
    //NSLog(@"rect %@", rect);
    //    CGRect rect = [UIScreen mainScreen].nativeBounds;
    
    self.myWebView = [[UIWebView alloc]initWithFrame:rect];
    
    //UIWebView *_webView = self.myWebView;
    //_webView.frame=CGRectMake(-100,0,_webView.frame.size.width,_webView.frame.size.height+100);
    
    self.myWebView.backgroundColor = [UIColor whiteColor];
    self.myWebView.delegate = self;// NOTES: UIWebViewDelegate, using "self" as the responder...
    
    // The page automatically zoom to fit the screen, default NO.
    self.myWebView.scalesPageToFit = YES;
    
    // Edges prohibit sliding (default YES)
    self.myWebView.scrollView.bounces = NO;
    
    self.view = self.myWebView;
    
    //    _webView.frame = CGRectMake(0,0,_webView.frame.size.width,_webView.frame.size.height+topFrame.size.height+bottomFrame.size.height);
    
    
    //It works for iOS 5 and iOS 6 , but not in iOS 7.
    //[UIApplication sharedApplication].statusBarHidden = YES;//NOTES: Info.plist need add:
    //    <key>UIStatusBarHidden</key>
    //    <true/>
    //
    //    <key>UIViewControllerBasedStatusBarAppearance</key>
    //    <false/>
    
    //    [[UIApplication sharedApplication] setStatusBarHidden:YES
    //                                            withAnimation:UIStatusBarAnimationFade];
    //    [[UIApplication sharedApplication] setStatusBarHidden:NO
    //                                            withAnimation:UIStatusBarAnimationFade];
    [[UIApplication sharedApplication] setStatusBarHidden:NO
     //                                            withAnimation:UIStatusBarAnimationFade
                                            withAnimation:UIStatusBarAnimationNone
     ];
    [self CustomTopBar];
    
    [self loadUrl:[@"file://" stringByAppendingString:[CMPHybridTools fullPathOfAsset:@"root.htm"]]];
    
}

@end
