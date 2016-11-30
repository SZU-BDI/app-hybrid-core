#import "CMPHybridWKWebViewUi.h"

#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridWKWebViewUi

- (void) webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation
{
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    //injectDone=NO;
    //NSLog(@" notifyPollingInject from webViewDidStartLoad...");
    //[self notifyPollingInject :webView];
    
    NSLog(@"spinnerOn...");
    [self spinnerOn];
}

- (void) webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation
{
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    //NSLog(@" notifyPollingInject from webViewDidFinishLoad...");
    //[self notifyPollingInject :webView];
    NSLog(@"spinnerOff");
    [self spinnerOff];
}

//Invoked when an error occurs while starting to load data for the main frame.
#warning TODO (1) after alert error, page should auto close...
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error
{
    NSLog(@" webview didFailProvisionalNavigation for desc %@",[error description]);
    if(_myWebView==webView){
        [self spinnerOff];
#warning TODO(!!!) judge whether first time load
        [self closeUi];
    }
}

//Invoked when an error occurs during a committed main frame navigation.
#warning TODO (1) after alert error, page should auto close...
- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error
{
    NSLog(@" webview didFailNavigation for desc %@",[error description]);
    if(_myWebView==webView)
        [self spinnerOff];
    //[self closeUi];
}


//----------------   <WKUIDelegate>   -----------------
- (void)webView:(WKWebView *)webView runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(void))completionHandler
{
    [CMPHybridTools quickShowMsgMain:message callback:^{
        completionHandler();
    }];
}

#warning TODO (1) try move to HybridTools to share with the UIWebView?
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

- (void) evalJs:(NSString *)js_s
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [CMPHybridTools callWebViewDoJs:self.myWebView :js_s];
    });
}

-(void) initUi
{
    [self on:CMPHybridEventBeforeDisplay :^(NSString *eventName, JSO *extraData) {
        
        NSLog(@"initUi() on eventName %@ ", eventName);
        [self resetTopBarStatus];
        [self resetTopBarBtn];
        [self setNeedsStatusBarAppearanceUpdate];
    } :nil];
    
    [self registerHandlerApi];
    
    self.myWebView = [CMPHybridTools initHybridWebView :[WKWebView class] :self];
    
    //self.myWebView.backgroundColor = [UIColor whiteColor];
    
    self.myWebView.navigationDelegate=self;//about start/stop/fail etc.
    self.myWebView.UIDelegate=self;//about alert/confirm/prompt
    
    // Edges prohibit sliding (default YES)
    self.myWebView.scrollView.bounces = NO;
    
    //@property(nonatomic,assign) UIRectEdge edgesForExtendedLayout NS_AVAILABLE_IOS(7_0); // Defaults to UIRectEdgeAll
    //@property(nonatomic,assign) BOOL extendedLayoutIncludesOpaqueBars NS_AVAILABLE_IOS(7_0); // Defaults to NO, but bars are translucent by default on 7_0.
    //@property(nonatomic,assign) BOOL automaticallyAdjustsScrollViewInsets NS_AVAILABLE_IOS(7_0); // Defaults to YES
    
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
    UIBarButtonItem *leftBar
    = [[UIBarButtonItem alloc]
       initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"]//see Images.xcassets
       style:UIBarButtonItemStylePlain
       target:self
       action:@selector(closeUi) //on('click')=>close()
       ];
    leftBar.tintColor = [UIColor blueColor];
    
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
