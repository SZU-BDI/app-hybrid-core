#import "CMPHybridWKWebViewUi.h"

#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridWKWebViewUi

BOOL isFirstLoad=YES;

- (void) webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation
{
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    
    [self spinnerOn];
}

- (void) webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation
{
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    isFirstLoad=NO;
    [self spinnerOff];
}

//Invoked when an error occurs while starting to load data for the main frame.
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error
{
    NSLog(@" webview didFailProvisionalNavigation for desc %@",[error description]);
    if(_myWebView==webView){
        
        if(isFirstLoad)
            [self closeUi];
        else
            [self spinnerOff];
    }
}

//Invoked when an error occurs during a committed main frame navigation.
- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error
{
    NSLog(@" webview didFailNavigation for desc %@",[error description]);
    if(_myWebView==webView)
        [self spinnerOff];
}

//----------------   <WKUIDelegate>   -----------------
- (void)webView:(WKWebView *)webView runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(void))completionHandler
{
    [CMPHybridTools quickShowMsgMain:message callback:^{
        completionHandler();
    }];
}

- (void)webView:(WKWebView *)webView runJavaScriptConfirmPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(BOOL))completionHandler
{
    [CMPHybridTools quickConfirmMsgMain:message handlerYes:^(UIAlertAction *action) {
        completionHandler(YES);
    } handlerNo:^(UIAlertAction *action) {
        completionHandler(NO);
    }];
}

- (void)webView:(WKWebView *)webView
runJavaScriptTextInputPanelWithPrompt:(NSString *)prompt
    defaultText:(NSString *)defaultText
initiatedByFrame:(WKFrameInfo *)frame
completionHandler:(void (^)(NSString * _Nullable))completionHandler
{
    //NSString *hostString = webView.URL.host;
    //NSString *sender = [NSString stringWithFormat:@"%@ からの表示", hostString];
    NSString *sender = @"";
    
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:prompt message:sender preferredStyle:UIAlertControllerStyleAlert];
    [alertController addTextFieldWithConfigurationHandler:^(UITextField *textField) {
        //textField.placeholder = defaultText;
        textField.text = defaultText;
    }];
    [alertController addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
        NSString *input = ((UITextField *)alertController.textFields.firstObject).text;
        completionHandler(input);
    }]];
    [alertController addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
        completionHandler(nil);
    }]];
    [self presentViewController:alertController animated:YES completion:^{}];
}

//----------------   <HybridUi>   -----------------


-(instancetype) trigger :(NSString *)eventName :(JSO *) extraData
{
    if ([CMPHybridEventAppResume isEqualToString:eventName]){
        NSLog(@" !!!! TODO _myWebView trigger resume to page ...");
    }else if([CMPHybridEventAppPause isEqualToString:eventName]){
        NSLog(@" !!!! TODO _myWebView trigger pause to page ...");
    }
    [super trigger:eventName :extraData];
    return self;
}

- (void) evalJs:(NSString *)js_s
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [CMPHybridTools callWebViewDoJs:self.myWebView :js_s];
    });
}

-(void) initUi
{
    [super initUi];
    
//    NSString *title = [[self.uiData getChild:@"title"] toString];
//    if ([CMPHybridTools isEmptyString:title]){
//        title=@" - - - ";//TODO
//    }
//    [self on:CMPHybridEventBeforeDisplay :^(NSString *eventName, JSO *extraData) {
//        
//        NSLog(@"initUi() on eventName %@ ", eventName);
//        [self resetTopBarStatus];
//        
//        [self setTopBarTitle:title];
//        [self setNeedsStatusBarAppearanceUpdate];
//    } :nil];
    
    [self registerHandlerApi];
    
    self.myWebView = [CMPHybridTools initHybridWebView :[WKWebView class] :self];
    
    //self.myWebView.backgroundColor = [UIColor whiteColor];
    
    self.myWebView.navigationDelegate=self;//for start/stop/fail etc.
    self.myWebView.UIDelegate=self;//for alert/confirm/prompt
    
    // Edges prohibit sliding (default YES)
    self.myWebView.scrollView.bounces = NO;
    
    self.extendedLayoutIncludesOpaqueBars=YES;
    self.automaticallyAdjustsScrollViewInsets=NO;
    
    self.view = self.myWebView;
    
    [self spinnerInit];
    [self spinnerOn];
    
    NSString *address = [[self.uiData getChild:@"address"] toString];
    
    if ( [CMPHybridTools isEmptyString:address] ){
        [CMPHybridTools quickShowMsgMain:@"no address?" callback:^{
            [self closeUi];
        }];
        return;
    }
    
    [CMPHybridTools callWebViewLoadUrl:_myWebView :address];
    
}

- (void) resetTopBarBtn
{
    NSLog(@"resetTopBarBtn in CMPHybridWKWebViewUi....");
    UIBarButtonItem *leftBar
    = [[UIBarButtonItem alloc]
       initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"]//see Images.xcassets
       style:UIBarButtonItemStylePlain
       target:self
       action:@selector(closeUi) //on('click')=>close()
       ];
    leftBar.tintColor = [UIColor blueColor];
    self.navigationItem.leftBarButtonItem=leftBar;
    
    //    self.navigationItem.leftBarButtonItem
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
    //       target:self
    //       action:@selector(closeUi)];
    
    //    UIBarButtonItem *rightBtn
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:nil];
    //    self.navigationItem.rightBarButtonItem = rightBtn;
}

//register will cache the handler inside the memory for speeding up.  so it's important
- (void)registerHandlerApi{
    
    self.uiApiHandlers = [NSMutableDictionary dictionary];
    
    // get the appConfig:
    JSO *appConfig = [CMPHybridTools wholeAppConfig];
    
    JSO *api_mapping = [appConfig getChild:@"api_mapping"];
    
    for (NSString *kkk in [api_mapping getChildKeys]) {
        NSString *apiname = [[api_mapping getChild:kkk] toString] ;
        CMPHybridApi *api = [CMPHybridTools getHybridApi:apiname];
        api.currentUi = self;
        self.uiApiHandlers[kkk] = [api getHandler];//[[api getHandler] copy];
    }
}

//- (void) loadUrl:(NSString *)url{
//    NSURL *requesturl = [NSURL URLWithString:url];
//    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
//    [self.myWebView loadRequest:request];
//}


- (BOOL)prefersStatusBarHidden {
    NSLog(@"prefersStatusBarHidden returns NO");
    return NO;
}

-(UIStatusBarStyle)preferredStatusBarStyle{
    //return UIStatusBarStyleLightContent;
    NSLog(@"preferredStatusBarStyle returns UIStatusBarStyleDefault");
    return UIStatusBarStyleDefault;
}
@end
