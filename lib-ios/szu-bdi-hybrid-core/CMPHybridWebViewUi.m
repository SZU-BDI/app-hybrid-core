#import <Foundation/Foundation.h>
#import "CMPHybridWebViewUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@import JavaScriptCore;

@implementation CMPHybridWebViewUi

//NSMutableDictionary* myMessageHandlers;

//------------  UIViewController ------------

- (void) webViewDidStartLoad:(UIWebView *)webView
{
    NSLog(@"webViewDidStartLoad()");
    [self injectJSB :webView];
    NSLog(@"done injectJSB");
}

//------------  prototol UIWebViewDelegate ------------

- (void) webViewDidFinishLoad :(UIWebView *)webView {
    NSLog(@"webViewDidFinishLoad()");
    //[CMPHybridTools callWebViewDoJs:webView :@"alert("" + (typeof window) + (typeof nativejsb));"];
    //[self evalJs:@"setTimeout(function(){alert(typeof window +' '+(typeof nativejsb));},1);"];
    NSLog(@"done webViewDidFinishLoad");
    
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    NSLog(@" didFailLoadWithError() %@",error);
    [self showTopBar];
}

//------------   <HybridUi> ------------

- (void) evalJs:(NSString *)js_s
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [CMPHybridTools callWebViewDoJs:self.myWebView :js_s];
    });
}

//------------ self -----------------

- (void) injectJSB :(UIWebView *)webView
{
    
    CMPHybridUi *caller=self;
    //JSO *uiData = caller.uiData;
    NSString * uiname = caller.uiName;
    NSLog(@"injecting JSB to %@", uiname);
    
    if (webView != self.myWebView) {
        NSLog(@" skip: not the same webview?? ");
        return;
    }
    
    JSContext *ctx = [CMPHybridTools getWebViewJsCtx :webView];
    
    //inject nativejsb
    [ctx evaluateScript:@"nativejsb={version:20161116};"];
    ctx[@"nativejsb"][@"getVersion"] = ^() {
        return @"20161116";
    };

    //inject nativejsb.js2app()
    ctx[@"nativejsb"][@"js2app"]=^(JSValue *callBackId,JSValue *handlerName,JSValue *param){
        
        //to check the handlerName is auth by api_auth in config.json for current url !!
        
        JSO * api_auth = [CMPHybridTools getAppConfig:@"api_auth"];
        NSString * uiname = caller.uiName;
        JSO * api_auth_a = [api_auth getChild:uiname];
        if(nil==api_auth_a){
            NSLog(@" !!! find no api_auth for uiname %@", uiname);
            return;
        }
        NSString * handlerName_s = [handlerName toString];
        if([CMPHybridTools isEmptyString:handlerName_s]){
            NSLog(@" empty handlerName?? %@", param);
            return;
        }
        BOOL flagFoundMatch=NO;
        //JSO *found_a=[[JSO alloc]init];//failed...
        NSMutableArray *found_a=[[NSMutableArray alloc] init];
        
        NSURL *url =[[webView request] URL];
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
        if(nil==self.myApiHandlers) {
            NSLog(@" !!! handler at all for %@", self.uiData);
            return;
        }
        
        HybridHandler handler = self.myApiHandlers[handlerName_s];
        
        if (nil==handler) {
            NSLog(@" !!! found no handler for %@", handlerName);
            return;
        }
        
        NSString *callBackId_s=[callBackId toString];
        HybridCallback callback=^(JSO *responseData){
            
            NSString *rt_s=[JSO id2s:@{@"responseId":callBackId_s,@"responseData":[responseData toId]}];
            
            @try {
                NSString* javascriptCommand = [NSString stringWithFormat:@"setTimeout(){WebViewJavascriptBridge._app2js(%@);},1);", rt_s];
                [caller evalJs:javascriptCommand];
            } @catch (NSException *exception) {
                NSLog(@" !!! error when callback to js %@",exception);
            } @finally {
            }
            
        };
        
        //do the callback a little later
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        double delay = 0.01;//0.01 second
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC)), queue, ^{
            NSString *param_s=[param toString];
            @try {
                handler([JSO s2o:param_s], callback);
            } @catch (NSException *exception) {
                callback([JSO id2o:@{@"STS":@"KO",@"errmsg":[exception reason]}]);
            }
        });
    };
    
    //STUB
    //    [ctx setExceptionHandler:^(JSContext *context, JSValue *value) {
    //        NSLog(@"%@", value);
    //    }];
    
    NSString *js = [CMPHybridTools readAssetInStr:@"WebViewJavascriptBridge.js"];
    
    [ctx evaluateScript:js];
}
- (void)registerHandlerApi{
    
    self.myApiHandlers = [NSMutableDictionary dictionary];
    
    // get the appConfig:
    JSO *appConfig = [CMPHybridTools wholeAppConfig];
    
    JSO *api_mapping = [appConfig getChild:@"api_mapping"];
    
    for (NSString *kkk in [api_mapping getChildKeys]) {
        
        NSString *apiname = [[api_mapping getChild:kkk] toString] ;
        CMPHybridApi *api = [CMPHybridTools getHybridApi:apiname];
        api.currentUi = self;
        self.myApiHandlers[kkk] = [[api getHandler] copy];
    }
}

- (void) loadUrl:(NSString *)url{
    NSURL *requesturl = [NSURL URLWithString:url];
    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
    [self.myWebView loadRequest:request];
}

-(void) initUi
{
    [self registerHandlerApi];
    
    [self CustomTopBarBtn];
    
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
    
    NSString *address = [[self.uiData getChild:@"address"] toString];
    NSURL *address_url = [NSURL URLWithString:address];
    NSString *scheme_s=[address_url scheme];
    
    if( [ CMPHybridTools isEmptyString:scheme_s ])
    {
        [self loadUrl:[@"file://" stringByAppendingString:[CMPHybridTools fullPathOfAsset:address]]];
    }else{
        [self loadUrl:[address_url absoluteString]];
    }
}

//@overrided
- (void) CustomTopBarBtn
{
    //    UIBarButtonItem *leftBar
    //    = [[UIBarButtonItem alloc]
    //       initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"]//see Images.xcassets
    //       style:UIBarButtonItemStylePlain
    //       target:self
    //       action:@selector(closeUi) //on('click')=>close()
    //       ];
    //    leftBar.tintColor = [UIColor blueColor];
    
    self.navigationItem.leftBarButtonItem
    = [[UIBarButtonItem alloc]
       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
       target:self
       action:@selector(closeUi)];
    //
    //    UIBarButtonItem *rightBtn
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:nil];
    //    self.navigationItem.rightBarButtonItem = rightBtn;
}

@end
