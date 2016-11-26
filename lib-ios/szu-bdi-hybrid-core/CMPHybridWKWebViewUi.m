#import "CMPHybridWKWebViewUi.h"

#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridWKWebViewUi

//WKWebViewConfiguration *webConfig;

//------------  UIViewController ------------


- (void) webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation
{
    
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    //injectDone=NO;
    //NSLog(@" notifyPollingInject from webViewDidStartLoad...");
    //[self notifyPollingInject :webView];
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
    [self spinnerOff];
}

//Invoked when an error occurs while starting to load data for the main frame.
//TODO
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error
{
    [self spinnerOff];
}

//Invoked when an error occurs during a committed main frame navigation.
//TODO
- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error
{
    [self spinnerOff];
}


//----------------   <WKUIDelegate>   -----------------
- (void)webView:(WKWebView *)webView runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(void))completionHandler
{
    [CMPHybridTools quickShowMsgMain:message callback:^{
        completionHandler();
    }];
}

//TODO need move to HybridTools to share with the UIWebView
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
        [CMPHybridTools callWKWebViewDoJs:self.myWebView :js_s];
    });
}


//NOTES: can be overrided
-(void) initUi
{
    
    [self registerHandlerApi];
    
    [self CustomTopBarBtn];
    
    /////////////
    // Create WKWebViewConfiguration instance
    WKWebViewConfiguration *
    webConfig = [[WKWebViewConfiguration alloc]init];
    
    // Setup WKUserContentController instance for injecting user script
    WKUserContentController* userController = [[WKUserContentController alloc]init];
    
    // Get script that's to be injected into the document
    NSString *js = [CMPHybridTools readAssetInStr:@"WebViewJavascriptBridge.js"];
    
    // Specify when and where and what user script needs to be injected into the web document
    WKUserScript* userScript = [[WKUserScript alloc] initWithSource:js
                                                      injectionTime:WKUserScriptInjectionTimeAtDocumentEnd
                                                   forMainFrameOnly:NO];
    
    // Add the user script to the WKUserContentController instance
    [userController addUserScript:userScript];
    
    // Configure the WKWebViewConfiguration instance with the WKUserContentController
    webConfig.userContentController= userController;
    
    [webConfig.userContentController addScriptMessageHandler:self name:@"nativejsb"];
    
    /////////////
    // initial the webView and add webview in window：
    //CGRect rect = [UIScreen mainScreen].bounds;
    
    //self.myWebView = [[WKWebView alloc]initWithFrame:rect];
    self.myWebView = [[WKWebView alloc] initWithFrame:CGRectZero configuration:webConfig];
    
    //self.myWebView.backgroundColor = [UIColor whiteColor];
    self.myWebView.backgroundColor = [UIColor blackColor];
    
    self.myWebView.navigationDelegate=self;
    self.myWebView.UIDelegate=self;//about alert/confirm/prompt
    
    // The page automatically zoom to fit the screen, default NO.
    //self.myWebView.scalesPageToFit = YES;
    
    // Edges prohibit sliding (default YES)
    self.myWebView.scrollView.bounces = NO;
    
    self.view = self.myWebView;
    
    NSString *address = [[self.uiData getChild:@"address"] toString];
    NSURL *address_url = [NSURL URLWithString:address];
    NSString *scheme_s=[address_url scheme];
    
    [self spinnerInit];
    
    if( [ CMPHybridTools isEmptyString:scheme_s ])
    {
        [self loadUrl:[@"file://" stringByAppendingString:[CMPHybridTools fullPathOfAsset:address]]];
    }else{
        [self loadUrl:[address_url absoluteString]];
    }
    
}
//NOTES: can be overrided
- (void) CustomTopBarBtn
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

- (void) spinnerOn
{
    [_myIndicatorView startAnimating];
}
- (void) spinnerOff
{
    [_myIndicatorView stopAnimating];
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

- (void) loadUrl:(NSString *)url{
    NSURL *requesturl = [NSURL URLWithString:url];
    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
    [self.myWebView loadRequest:request];
}


- (void) spinnerInit
{
    //INIT SPIN
    //UIActivityIndicatorView *
    _myIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    _myIndicatorView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    _myIndicatorView.color =[UIColor whiteColor];
    _myIndicatorView.layer.cornerRadius = 5;
    _myIndicatorView.layer.masksToBounds = TRUE;
    _myIndicatorView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleBottomMargin;
    
    _myIndicatorView.translatesAutoresizingMaskIntoConstraints = NO;
    [_myIndicatorView setHidesWhenStopped:YES];
    _myIndicatorView.center=self.view.center;
    [self.view addSubview:_myIndicatorView];
    
}


- (void)userContentController: (WKUserContentController *)userContentController
      didReceiveScriptMessage:(WKScriptMessage *)message{
    
    if(message.webView!=_myWebView){
        NSLog(@" userContentController: not the same webview?? ");
        return;
    }
    
    HybridUi caller=self;
    
    NSLog(@"message.body = %@", message.body);
    //    NSLog(@"message.name = %@", message.name);
    //    NSLog(@"message.frameInfo = %@", message.frameInfo);
    //    NSLog(@"message.WKWebView = %@", message.webView);
    JSO * msg=[JSO id2o:message.body];
    NSString * handlerName_s = [[msg getChild:@"handlerName"] toString];
    __block NSString * callBackId_s =[[msg getChild:@"callbackId"] toString];
    JSO * param =[msg getChild:@"data"];
    WKWebView *webView=message.webView;
    
    //^(JSValue *callBackId,JSValue *handlerName,JSValue *param){
    
    //to check the handlerName is auth by api_auth in config.json for current url !!
    
    JSO * api_auth = [CMPHybridTools getAppConfig:@"api_auth"];
    NSString * uiname = caller.uiName;
    JSO * api_auth_a = [api_auth getChild:uiname];
    if(nil==api_auth_a){
        NSLog(@" !!! find no api_auth for uiname %@", uiname);
        return;
    }
    //NSString * handlerName_s = [handlerName toString];
    if([CMPHybridTools isEmptyString:handlerName_s]){
        NSLog(@" empty handlerName?? %@", param);
        return;
    }
    BOOL flagFoundMatch=NO;
    NSMutableArray *found_a=[[NSMutableArray alloc] init];
    
    NSURL *url =[webView URL];
    NSString *scheme = [url scheme];
    NSString *currenturl =[url absoluteString];
    if( [@"file" isEqualToString:scheme]){
        currenturl=[url lastPathComponent];
    }
    for (NSString *kkk in [api_auth_a getChildKeys]) {
        if([currenturl isEqualToString:kkk]){
            flagFoundMatch=YES;
            //found_a= [api_auth_a getChild:kkk];
            //break;
            //[found_a basicMerge:[api_auth_a getChild:kkk]];
            JSO *jj =[api_auth_a getChild:kkk];
            id idjj = [jj toId];
            [found_a removeObjectsInArray:idjj];
            [found_a addObjectsFromArray:idjj];
        }
        NSArray * matches = [CMPHybridTools quickRegExpMatch :kkk :currenturl];
        if ([matches count] > 0){
            flagFoundMatch=YES;
            //found_a= [api_auth_a getChild:kkk];
            //break;
            //[found_a basicMerge:[api_auth_a getChild:kkk]];
            JSO *jj =[api_auth_a getChild:kkk];
            id idjj = [jj toId];
            [found_a removeObjectsInArray:idjj];
            [found_a addObjectsFromArray:idjj];
        }
    }
    if(flagFoundMatch!=YES){
        NSLog(@" !!! find no auth for api %@ for %@", handlerName_s, uiname);
        return;
    }
    
    BOOL flagInList=NO;
    NSArray * keys =[found_a copy];
    for (NSString *vvv in keys){
        if([handlerName_s isEqualToString:vvv]){
            flagInList=YES;
            break;
        }
    }
    
    if (flagInList!=YES){
        NSLog(@" !!! handler %@ is not in auth list %@", handlerName_s, keys);
        return;
    }
    if(nil==caller.uiApiHandlers) {
        NSLog(@" !!! caller.myApiHandlers is nil !!! %@", caller.uiData);
        return;
    }
    
    HybridHandler handler = caller.uiApiHandlers[handlerName_s];
    
    if (nil==handler) {
        NSLog(@" !!! found no handler for %@", handlerName_s);
        return;
    }
    
    //NSString *callBackId_s=[callBackId toString];
    HybridCallback callback=^(JSO *responseData){
        NSLog(@"HybridCallback responseData %@", [responseData toString]);
        NSString *rt_s=[JSO id2s:@{@"responseId":callBackId_s,@"responseData":[responseData toId]}];
        
        @try {
            NSString* javascriptCommand = [NSString stringWithFormat:@"setTimeout(function(){WebViewJavascriptBridge._app2js(%@);},1);", rt_s];
            dispatch_async(dispatch_get_main_queue(), ^{
                [CMPHybridTools callWKWebViewDoJs:webView :javascriptCommand];
            });
            //[caller evalJs:javascriptCommand];
        } @catch (NSException *exception) {
            NSLog(@" !!! error when callback to js %@",exception);
        } @finally {
        }
        
    };
    
    //async delay 0.01 second
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.01 * NSEC_PER_SEC)),
                   dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0),
                   ^{
                       NSString *param_s=[param toString];
                       @try {
                           handler([JSO s2o:param_s], callback);
                       } @catch (NSException *exception) {
                           callback([JSO id2o:@{@"STS":@"KO",@"errmsg":[exception reason]}]);
                       }
                   });
    
}


@end
