#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>
#import <sys/utsname.h>
#import "CMPHybridTools.h"

#import "CMPHybridTools.h"
@import JavaScriptCore;


/////////////////////////////////////////////////////////////

//ref
//https://github.com/jnjosh/PandoraBoy/
NSString *PBResourceHost = @".resource.";

@interface ResourceURLProtocol : NSURLProtocol {}

@end

@interface ResourceURL : NSURL {
}

+ (ResourceURL*) resourceURLWithPath:(NSString *)path;
@end

@implementation ResourceURLProtocol

+ (BOOL)canInitWithRequest:(NSURLRequest *)request
{
    BOOL flag1=[[[request URL] scheme] isEqualToString:@"local"];
    BOOL flag2=[[[request URL] host] isEqualToString:PBResourceHost];
    BOOL rt= (flag1 && flag2);
    return rt;
}

+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request
{
    return request;
}

-(void)startLoading
{
    NSBundle *thisBundle = [NSBundle bundleForClass:[self class]];
    NSString *notifierPath = [[thisBundle resourcePath] stringByAppendingPathComponent:[[[self request] URL] path]];
    NSError *err;
    NSData *data = [NSData dataWithContentsOfFile:notifierPath
                                          options:NSUncachedRead
                                            error:&err];
    if( data )
    {
        NSURLResponse *response = [[NSURLResponse alloc] initWithURL:[[self request] URL]
                                                            MIMEType:@"text/html"
                                               expectedContentLength:[data length]
                                                    textEncodingName:nil];
        
        [[self client] URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageAllowed];
        [[self client] URLProtocol:self didLoadData:data];
        [[self client] URLProtocolDidFinishLoading:self];
    }
    else
    {
        NSLog(@"BUG:Unable to load resource:%@:%@", notifierPath, [err description]);
        [[self client] URLProtocol:self didFailWithError:err];
    }
}

-(void)stopLoading
{
    return;
}

@end


@implementation ResourceURL

+ (ResourceURL *) resourceURLWithPath:(NSString *)path
{
    NSURL *rt= [[NSURL alloc] initWithScheme:@"local"
                                        host:PBResourceHost
                                        path:path];
    return (ResourceURL *)rt;
}

@end

@interface WKScriptMessageHandler : NSObject  <WKScriptMessageHandler>

@property (weak) HybridUi caller;

+(instancetype) initWithHybridUI:(HybridUi) ui;

@end

@implementation WKScriptMessageHandler
+(instancetype) initWithHybridUI:(HybridUi) ui
{
    WKScriptMessageHandler *idid = [[self alloc] init];
    idid.caller=ui;
    return idid;
}

//---- protocol <WKScriptMessageHandler> ----
- (void)userContentController: (WKUserContentController *)userContentController
      didReceiveScriptMessage:(WKScriptMessage *)message{
    
    //    if(message.webView!=_myWebView){
    //        NSLog(@" userContentController: not the same webview?? ");
    //        return;
    //    }
    //
    //HybridUi caller=self;
    HybridUi caller=_caller;
    
    //NSLog(@"message.body = %@", message.body);
    
    //    NSLog(@"message.name = %@", message.name);
    //    NSLog(@"message.frameInfo = %@", message.frameInfo);
    //    NSLog(@"message.WKWebView = %@", message.webView);
    
    JSO * msg=[JSO id2o:message.body];
    NSString * handlerName_s = [[msg getChild:@"handlerName"] toString];
    if([CMPHybridTools isEmptyString:handlerName_s]){
        NSLog(@"Unknow Message from WKWebview: %@", message.body);
        return;
    }
    __block NSString * callBackId_s =[[msg getChild:@"callbackId"] toString];
    JSO * param =[msg getChild:@"data"];
    WKWebView *webView=message.webView;
    
    //to check the handlerName is auth by api_auth in config.json for current url
    
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
    NSString *fullurl =[url absoluteString];
    NSString *currenturl=fullurl;
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
        NSArray * matches = [CMPHybridTools quickRegExpMatch :kkk :fullurl];
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
        NSLog(@" !!! find no auth for handlerName(%@) uiname(%@) url(%@)", handlerName_s, uiname, currenturl);
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
        //NSLog(@"HybridCallback responseData %@", [responseData toString]);
        NSString *rt_s=[JSO id2s:@{@"responseId":callBackId_s,@"responseData":[responseData toId]}];
        
        @try {
            NSString* javascriptCommand = [NSString stringWithFormat:@"setTimeout(function(){WebViewJavascriptBridge._app2js(%@);},1);", rt_s];
            dispatch_async(dispatch_get_main_queue(), ^{
                [CMPHybridTools callWebViewDoJs:webView :javascriptCommand];
            });
            //[caller evalJs:javascriptCommand];
        } @catch (NSException *exception) {
            NSLog(@" !!! error when callback to js %@",exception);
        } @finally {
        }
    };
    
    //async delay 0.01 second
    dispatch_after
    (dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.01 * NSEC_PER_SEC)),
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


/////////////////////////////////////////////////////////////
//internal class(CmpUIAlertView) to handle the callback for quickAlertMsg()
@interface CmpUIAlertView : UIAlertView

//@property () void (^callback)();
@property (strong) void (^callback)();

-(instancetype) initWithMsg:(NSString *)msg  callback:(void (^)())callback;

@end

@implementation CmpUIAlertView

-(void) alertView :(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if(self.callback)
    self.callback();
}

-(instancetype) initWithMsg:(NSString *)msg  callback:(void (^)())callback
{
    self.callback=callback;
    return [self initWithTitle:msg
                       message:@""
                      delegate:self
             cancelButtonTitle:nil
             otherButtonTitles:@"OK",
            nil];
}
@end
/////////////////////////////////////////////////////////////

@implementation CMPHybridTools


SINGLETON_shareInstance(CMPHybridTools);


+ (void)checkAppConfig{
    
    CMPHybridTools *hybridManager = [self shareInstance];
    if(nil==hybridManager.jso){
        //TODO [CMPHybridTools stripComment:s]
        NSString *s =[self readAssetInStr:@"config.json" :YES];
        hybridManager.jso = [JSO s2o:s];
        hybridManager.i18n =[hybridManager.jso getChild:@"I18N"];
    }
}

//TODO using a callback to hook back the HybridUi
//+ (void) startUi :(NSString *)strUiName
//                  initData:(JSO *) initData
//                 objCaller:(HybridUi )objCaller
//                  callback:...

+ (void) startUi :(NSString *)strUiName
         initData:(JSO *) initData
        objCaller:(HybridUi )objCaller
         callback:(void (^)(HybridUi ui))callback
{
    HybridUi  ui = [self startUi:strUiName initData:initData objCaller:objCaller];
    if(nil!=callback){
        callback(ui);
    }
}

+ (HybridUi ) startUi :(NSString *)strUiName
              initData:(JSO *) initData
             objCaller:(HybridUi)objCaller
{
    [self checkAppConfig];
    
    JSO *jso_uiMapping = [self getAppConfig:@"ui_mapping"];
    
    JSO *uiConfig = [[jso_uiMapping getChild:strUiName] copy];//important to copy one otherwise the real one will be poluted
    
    NSString *mode = [JSO o2s:[uiConfig getChild:@"mode"]];
    NSString *className = [JSO o2s:[uiConfig getChild:@"class"]];
    
    if ( [self isEmptyString :className]) {
        if( [@"WebView" isEqualToString:mode]){
            //TODO if < ios8 using UIWebView
            if([CMPHybridTools os_compare:8.0]>=0)
            {
                className=@"CMPHybridWKWebViewUi";//default to this now.
            }else{
                className=@"CMPHybridWebViewUi";//for <iOS8, using UIWebView
            }
        }else{
            return nil;
        }
    }
    
    Class uiClass = NSClassFromString(className);
    HybridUi theHybridUi = [[uiClass alloc] init];
    
    if (nil==theHybridUi) {
        [self quickShowMsgMain:[NSString stringWithFormat:@"Failed to start %@ %@", strUiName, className]];
        return nil;
    }
    
    [uiConfig basicMerge:initData];
    theHybridUi.uiName=strUiName;
    theHybridUi.uiData=uiConfig;
    
    //theHybridUi.responseData=[JSO id2o:@{}];
    
    /////////////////////////////////////// Display It {
    id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
    if (ddd.window.rootViewController==nil){
        
        if ([theHybridUi isKindOfClass:[UITabBarController class]]) {
            ddd.window.rootViewController = (UIViewController *)theHybridUi;
        }
        else{
            UINavigationController *nav
            = [[UINavigationController alloc] initWithRootViewController:(UIViewController *)theHybridUi];
            ddd.window.rootViewController = nav;
        }
    }
    
    UIViewController *ui = (UIViewController *)theHybridUi;
    
    //preload, which will call viewDidLoad first.
    [ui.view layoutSubviews];
    
    if (objCaller == nil) {
    }
    else{
        if (((UIViewController *)objCaller).navigationController != nil) {
            //for test only...((UIViewController *)theHybridUi).view.backgroundColor=[UIColor brownColor];
            [((UIViewController *)objCaller).navigationController pushViewController:ui animated:YES];
        }
        else{
            // modal
            [(UIViewController *)objCaller presentViewController:ui animated:YES completion:nil];
        }
    }
    /////////////////////////////////////// Display It }
    return theHybridUi;
}

+ (CMPHybridApi *)getHybridApi:(NSString *)name{
    
    Class myApiClass = NSClassFromString(name);
    
    id myApiClassInstance = [[myApiClass alloc] init];
    
    if (myApiClassInstance) {
        // NSLog(@"返回api的是：(%@)", myApiClassInstance);
        return myApiClassInstance;
    }
    else{
        [self quickShowMsgMain:[NSString stringWithFormat:@"Api: %@ not found", name]];
    }
    
    return nil;
}

+ (JSO *)wholeAppConfig{
    
    CMPHybridTools *hybridManager = [self shareInstance];
    return hybridManager.jso;
}

+ (JSO *) getAppConfig :(NSString *)key
{
    return [[self wholeAppConfig] getChild:key];
}

//IOS 8+
+ (void)quickShowMsgMain:(NSString *)msg{
    
    //    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:msg message:@"" preferredStyle:UIAlertControllerStyleAlert];
    //
    //    UIAlertAction* ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil];
    //    [alertController addAction:ok];
    //
    //    //modal
    //    [[self findTopRootView] presentViewController:alertController animated:NO completion:^(){
    //        NSLog(@" completion after quickShowMsgMain()");
    //    }];
    [self quickShowMsgMain:msg callback:^(){
        NSLog(@" completion after quickShowMsgMain()");
    }];
}

+ (void)quickShowMsgMain:(NSString *)msg callback:(void (^)())callback
{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:msg message:@"" preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:callback];
    [alertController addAction:ok];
    
    //modal
    [[self findTopRootView] presentViewController:alertController animated:NO completion:^(){
        //
    }];
}

//NOTES: quickAlertMsg()
//Mostly use in main.m to alert iOS version if too low (because quickShowMsgMain is not working <iOS8 )
+ (void)quickAlertMsgForOldiOS :(NSString *)msg callback:(void (^)())callback;
{
    [[[CmpUIAlertView alloc] initWithMsg:msg callback:callback] show];
}

+ (UIViewController *) findTopRootView
{
    UIViewController *topRootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    while (topRootViewController.presentedViewController)
    {
        topRootViewController = topRootViewController.presentedViewController;
    }
    
    return topRootViewController;
}

//IOS 8 +
+ (void)quickConfirmMsgMain:(NSString *)msg
//                 handlerYes:(void (^)(UIAlertAction *action))handlerYes
//                  handlerNo:(void (^)(UIAlertAction *action))handlerNo
                 handlerYes:(HybridDialogCallback) handlerYes
                  handlerNo:(HybridDialogCallback) handlerNo
{
    
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:msg message:@"" preferredStyle:UIAlertControllerStyleAlert];
    
    [alertController addAction:[UIAlertAction actionWithTitle:@"Yes" style:UIAlertActionStyleDefault handler:handlerYes]];
    [alertController addAction:[UIAlertAction actionWithTitle:@"No" style:UIAlertActionStyleDefault handler:handlerNo]];
    
    [[self findTopRootView] presentViewController:alertController animated:NO completion:nil];
}

+ (void) suspendApp
{
    //home button press programmatically
    UIApplication *app = [UIApplication sharedApplication];
    NSLog(@"Hide...");
    //[app performSelector:@selector(suspend)];
    [self call_o_do_m:app :@"suspend"];
}

+ (void) quitGracefully
{
    //close top
    //[[self findTopRootView] dismissViewControllerAnimated:YES completion:nil];
    
    [self suspendApp];
    
    sleep(1);
    
    NSLog(@"Really Quit...");
    
    exit(EXIT_SUCCESS);
}

+ (JSContext *) getWebViewJsCtx:(UIWebView *) _webview
{
    //prv api
    NSString *s1 =[self atob:@"ZG9jdW1lbnRWaWV3LndlYlZpZXc="];
    NSString *s2 =[self atob:@"Lm1haW5GcmFtZS5qYXZhU2NyaXB0Q29udGV4dA=="];
    return [_webview valueForKeyPath:[s1 stringByAppendingString:s2]];
}

+ (void) call_o_do_m :(id)ooo :(NSString *)mmm
{
    SEL sel = NSSelectorFromString(mmm);
    if ([(id)ooo respondsToSelector:sel]) {
        ((void (*)(id, SEL))[ooo methodForSelector:sel])(ooo, sel);
    }
}

+ (void) call_c_do_m :(NSString *)ccc :(NSString *)mmm
{
    Class cls = NSClassFromString(ccc);
    SEL sel = NSSelectorFromString(mmm);
    
    if ([(id)cls respondsToSelector:sel]) {
        ((void (*)(id, SEL))[cls methodForSelector:sel])(cls, sel);
        //((void (*)(id, SEL, NSString *))[cls methodForSelector:sss])(cls, sss, @"local");
    }
}
+ (void) call_c_do_m_1 :(NSString *)ccc :(NSString *)mmm :(NSString *) vvv
{
    Class cls = NSClassFromString(ccc);
    SEL sel = NSSelectorFromString(mmm);
    
    if ([(id)cls respondsToSelector:sel]) {
        ((void (*)(id, SEL, NSString *))[cls methodForSelector:sel])(cls, sel, vvv);
    }
}

+ (void) callWebViewLoadUrl:_webview :(NSString *)address
{
    if([CMPHybridTools isEmptyString:address]) return;
    
    if( [_webview isKindOfClass:[WKWebView class]] ){
        WKWebView *wv=_webview;
        @try {
            {
                NSURL *address_url = [NSURL URLWithString:address];
                NSString *scheme_s=[address_url scheme];
                
                //prv api
                [self call_c_do_m_1:[self atob:@"V0tCcm93c2luZ0NvbnRleHRDb250cm9sbGVy"]
                                   :[self atob:@"cmVnaXN0ZXJTY2hlbWVGb3JDdXN0b21Qcm90b2NvbDo="]
                                   :@"local"];
                
                [NSURLProtocol registerClass:[ResourceURLProtocol class]];
                
                
                if( [ CMPHybridTools isEmptyString:scheme_s ])
                {
                    ResourceURL *resource = [ResourceURL resourceURLWithPath:[@"/" stringByAppendingString:address]];
                    [wv loadRequest:[NSURLRequest requestWithURL:resource]];
                    
                }else{
                    //[self loadUrl:[address_url absoluteString]];
                    NSURL *requesturl = [NSURL URLWithString:[address_url absoluteString]];
                    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
                    [wv loadRequest:request];
                }
            }
            
        } @catch (NSException *exception) {
            NSLog(@"WKWebView callWebViewLoadUrl error %@", exception);
        } @finally {
            
        }
    }else if ([_webview isKindOfClass:[UIWebView class]]){
        UIWebView *wv=_webview;
        @try {
            
            NSURL *address_url = [NSURL URLWithString:address];
            NSString *scheme_s=[address_url scheme];
            
            if( [ CMPHybridTools isEmptyString:scheme_s ])
            {
                NSURL *requesturl = [NSURL URLWithString:[@"file://" stringByAppendingString:[CMPHybridTools fullPathOfAsset:address]]];
                NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
                [wv loadRequest:request];
                //[self loadUrl:[@"file://" stringByAppendingString:[CMPHybridTools fullPathOfAsset:address]]];
            }else{
                NSURL *requesturl = [NSURL URLWithString:[address_url absoluteString]];
                NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
                [wv loadRequest:request];
                //[self loadUrl:[address_url absoluteString]];
            }
        } @catch (NSException *exception) {
            NSLog(@"UIWebView callWebViewLoadUrl error %@", exception);
        } @finally {
            
        }
    }
}
+ (JSValue *) callWebViewDoJs:(id) _webview :(NSString *)js_s
{
    if( [_webview isKindOfClass:[UIWebView class]] ){
        @try {
            return [[self getWebViewJsCtx :_webview] evaluateScript:js_s];
        } @catch (NSException *exception) {
            NSLog(@"UIWebView JsCtx evaluateScript error %@", exception);
        } @finally {
            
        }
    }else if ([_webview isKindOfClass:[WKWebView class]]){
        @try {
            //return [[self getWKWebViewJsCtx :_webview] evaluateScript:js_s];
            [_webview evaluateJavaScript:js_s completionHandler:^(id _Nullable val, NSError * _Nullable error) {
                //code
            }];
        } @catch (NSException *exception) {
            NSLog(@"WKWebView evaluateJavaScript error %@", exception);
        } @finally {
            
        }
    }
    return nil;
}

+(NSString *) fullPathOfAsset :(NSString *) filename
{
    NSString *rt
    = [[NSBundle mainBundle]
       pathForResource:[filename stringByDeletingPathExtension]
       ofType:[filename pathExtension]];
    return rt;
}

+(NSString *)readAssetInStr :(NSString *)filename
{
    return [NSString
            stringWithContentsOfFile:[self fullPathOfAsset:filename]
            encoding:NSUTF8StringEncoding
            error:NULL];
}

+(NSString *)readAssetInStr :(NSString *)filename :(BOOL)removeComments
{
    NSString *rt=[self readAssetInStr:filename];
    rt=[self quickRegExpReplace :@"^[ \t]*//.*$" :rt :@""];
    return rt;
}

+(BOOL) isEmptyString :(NSString *)s
{
    return (nil==s || [@"" isEqualToString:s]);
}

+(NSArray *) quickRegExpMatch :(NSString *)regex_s :(NSString *)txt
{
    NSError *error = NULL;
    NSRange range = NSMakeRange(0, [txt length]);
    NSRegularExpression *regex =
    [NSRegularExpression regularExpressionWithPattern:regex_s
                                              options:0
                                                error:&error];
    if(nil!=error){
        NSLog(@"error when quickRegExpMatch %@",error);
    }
    return [regex matchesInString:txt options:0 range:range];
}

+(NSString *) quickRegExpReplace :(NSString *)regex_s :(NSString *)src :(NSString *)tgt
{
    NSError *error = NULL;
    NSRange range = NSMakeRange(0, [src length]);
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:regex_s options:NSRegularExpressionAnchorsMatchLines error:&error];
    if(nil!=error){
        NSLog(@"error when quickRegExpReplace %@",error);
    }
    return [regex stringByReplacingMatchesInString:src options:0 range:range withTemplate:tgt];
}

+ (void) countDown:(double)interval initTime:(double)initTime block:(BOOL (^)(NSTimer *tm))block
{
    if(initTime<=0){
        return;
    }
    __block double countTime=initTime;
    
    __block NSTimer * ttt=[NSTimer scheduledTimerWithTimeInterval:interval target:[NSBlockOperation blockOperationWithBlock:^(){
        countTime=countTime-interval;
        if(countTime<=0){
            [ttt invalidate];
            return;
        }
        BOOL rt = block(ttt);
        if(rt==YES){
            NSLog(@".");
            [ttt invalidate];
            countTime=0;
        }
    }] selector:@selector(main) userInfo:nil repeats:YES];
}

+ (void) injectJSB :(UIWebView *)webView :(HybridUi )caller
{
    //    NSString * uiname = caller.uiName;
    NSLog(@"injecting JSB to %@", caller.uiName);
    
    JSContext *ctx = [CMPHybridTools getWebViewJsCtx :webView];
    
    //inject nativejsb
    [ctx evaluateScript:@"nativejsb={};"];
    
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
        if(nil==caller.uiApiHandlers) {
            NSLog(@" !!! caller.myApiHandlers is nil !!! %@", caller.uiData);
            return;
        }
        
        HybridHandler handler = caller.uiApiHandlers[handlerName_s];
        
        if (nil==handler) {
            NSLog(@" !!! found no handler for %@", handlerName);
            return;
        }
        
        NSString *callBackId_s=[callBackId toString];
        HybridCallback callback=^(JSO *responseData){
            
            NSString *rt_s=[JSO id2s:@{@"responseId":callBackId_s,@"responseData":[responseData toId]}];
            
            @try {
                NSString* javascriptCommand = [NSString stringWithFormat:@"setTimeout(function(){WebViewJavascriptBridge._app2js(%@);},1);", rt_s];
                dispatch_async(dispatch_get_main_queue(), ^{
                    [CMPHybridTools callWebViewDoJs:webView :javascriptCommand];
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
    };//end nativejsb.js2app...
    
    ctx[@"nativejsb"][@"getVersion"] = ^() {
        return @"20161120";
    };
    
    //STUB
    //    [ctx setExceptionHandler:^(JSContext *context, JSValue *value) {
    //        NSLog(@"%@", value);
    //    }];
    
    NSString *js = [CMPHybridTools readAssetInStr:@"WebViewJavascriptBridge.js"];
    
    //TODO [CMPHybridTools stripComments:js];
    [ctx evaluateScript:js];
}
+ (NSString *) I18N:(NSString *)key
{
    //JSO *i18n=[self getAppConfig:@"I18N"];
    //    JSO *i18n = [self wholeAppConfig].i18n;
    CMPHybridTools *hybridManager = [self shareInstance];
    JSO *i18n =hybridManager.i18n;
    
    JSO *value_a=[i18n getChild:key];
    //NSString *cached_lang=[self loadAppConfig:@"lang"];
    NSString *lang=@"en";//TODO
    JSO *value=[value_a getChild:lang];
    if(nil==value || [value isNull]){
        //
    }else{
        return [value toString];
    }
    return key;
}
+ (void) setI18N:(NSString *)i18n
{
    return;
}
+ (NSInteger) os_compare:(Float32)tgt
{
    //TODO improve by cache the floatValue to val?
    float sysver=[[[UIDevice currentDevice] systemVersion] floatValue];
    if(sysver>tgt)return 1;
    if(sysver<tgt)return -1;
    return 0;
}
+ (BOOL) is_simulator
{
    struct utsname systemInfo;
    uname(&systemInfo);
    
    NSString * tgt= [NSString stringWithCString:systemInfo.machine encoding:NSUTF8StringEncoding];
    
    if([tgt isEqualToString:@"i386"]) return YES;
    if([tgt isEqualToString:@"x86_64"]) return YES;
    //    NSString *name = [[UIDevice currentDevice] name];
    //    if ([name hasSuffix:@"Simulator"]) {
    //        return YES;
    //    }
    return NO;
}
+ (NSString *) btoa:(NSString *)s
{
    // Create NSData object
    NSData *nsdata = [s dataUsingEncoding:NSUTF8StringEncoding];
    
    // Get NSString from NSData object in Base64
    NSString *base64Encoded = [nsdata base64EncodedStringWithOptions:0];
    
    // Print the Base64 encoded string
    NSLog(@"Encoded: %@", base64Encoded);
    return base64Encoded;
}

+ (NSString *) base64encode:(NSString *)s
{
    return [self btoa:s];
}

+ (NSString *) atob:(NSString *)s
{
    // NSData from the Base64 encoded str
    NSData *nsdataFromBase64String = [[NSData alloc]
                                      initWithBase64EncodedString:s options:0];
    
    // Decoded NSString from the NSData
    NSString *base64Decoded = [[NSString alloc]
                               initWithData:nsdataFromBase64String encoding:NSUTF8StringEncoding];
    //NSLog(@"Decoded: %@", base64Decoded);
    return base64Decoded;
}
+ (NSString *) base64decode:(NSString *)s
{
    return [self atob:s];
}


//+ (void)saveAppConfig
//{
//
//    CMPHybridTools *hybridManager = [self shareInstance];
//    NSString *jsonString = [JSO o2s:hybridManager.jso];
//
////    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
////    [userDefaults setObject:jsonString forKey:@"appConfig"];
////    [userDefaults synchronize];
//    [self saveUserConfig:@"appConfig" :jsonString :true];
//}
//

+ (JSO *)loadAppConfig
{
    
    //    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    //    NSString *jsonString =[userDefaults objectForKey:@"appConfig"];
    NSString *jsonString = (NSString *) [self loadUserConfig:@"appConfig"];
    
    JSO *jsonJso = [JSO s2o:jsonString];
    
    return jsonJso;
}

//TODO use in app_cache_save
+ (void)saveUserConfig :(NSString *)key :(NSString *)value_s :(BOOL)autosave
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:value_s forKey:key];
    if(autosave){
        [userDefaults synchronize];
    }
}

//TODO use in app_cache_load
+ (nullable id)loadUserConfig :(NSString *)key
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    return [userDefaults objectForKey:key];
}

+ (NSString *) getBuildType
{
    NSString * build_type = @"L";//default Live...
#ifdef DEBUG
    build_type = @"D";//DEBUG for "Run" in XCode
#else
    if ([[NSBundle mainBundle]
         pathForResource:@"embedded"
         ofType:@"mobileprovision"])
    {
        build_type = @"M";//DEMO for TestFlight
    } else {
        build_type = @"L";//LIVE for real app store
    }
#endif
    return build_type;
}

+ (void) notifyPause
{
    CMPHybridTools *hybridManager = [self shareInstance];
    if(nil!=hybridManager.uiRoot){
        NSLog(@"TODO notifyPause() !!!!!");
    }
}

+ (void) notifyResume
{
    CMPHybridTools *hybridManager = [self shareInstance];
    if(nil!=hybridManager.uiRoot){
        NSLog(@"TODO notifyResume() !!!!!");
    }
}

//+ (void) handlePause
//{
//    NSLog(@"handlePause() !!!!!");
//}
//+ (void) handleResume
//{
//    NSLog(@"handleResume() !!!!!");
//}

#pragma mark - 手势密码读写
+ (NSString *)loadGesturesPassword{
    
    NSString *pswd = [[NSUserDefaults standardUserDefaults] objectForKey:@"lock"];
    
    if (pswd != nil && ![pswd isEqualToString:@""] && ![pswd isEqualToString:@"(null)"]) {
        
        return pswd;
    }
    
    return nil;
}

+ (void)saveGesturesPassword:(NSString*)pswd{
    
    [[NSUserDefaults standardUserDefaults] setObject:pswd forKey:@"lock"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

+ (id) initHybridWebView :(Class)c :(HybridUi) caller
{
    if(c==[WKWebView class]){
        WKWebViewConfiguration *
        webConfig = [[WKWebViewConfiguration alloc]init];
        
        // Setup WKUserContentController instance for injecting user script
        WKUserContentController* userController = [[WKUserContentController alloc]init];
        
        // Get script that's to be injected into the document
        NSString *js = [CMPHybridTools readAssetInStr:@"WebViewJavascriptBridge.js" :YES];
        
        // Specify when and where and what user script needs to be injected into the web document
        WKUserScript* userScript
        = [[WKUserScript alloc] initWithSource:js
                                 injectionTime:WKUserScriptInjectionTimeAtDocumentEnd
                              forMainFrameOnly:NO];
        
        [userController addUserScript:userScript];
        
        webConfig.userContentController= userController;
        
        //[webConfig.userContentController addScriptMessageHandler:self name:@"nativejsb"];
        [webConfig.userContentController addScriptMessageHandler:[WKScriptMessageHandler initWithHybridUI:caller] name:@"nativejsb"];
        
        id rt = [[WKWebView alloc] initWithFrame:CGRectZero configuration:webConfig];
        return rt;
    }else if(c==[UIWebView class]){
        CGRect rect = [UIScreen mainScreen].bounds;
        UIWebView *wv = [[UIWebView alloc]initWithFrame:rect];
        wv.delegate = (id<UIWebViewDelegate>) caller;
        return wv;
    }else{
        NSLog(@"unknow class initHybridWebView %@",c);
    }
    return nil;
}

/****************************** STUB FOR LATER *********************************/

@end
