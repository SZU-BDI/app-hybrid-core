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
        NSString *s =[self readAssetInStr:@"config.json"];
        hybridManager.jso = [JSO s2o:s];
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
    
    NSString *className = [JSO o2s:[uiConfig getChild:@"class"]];
    
#warning TODO(1) if WKWebview Not avail (e.g. < ios8)
    //NSString *oldclassName = [JSO o2s:[uiConfig getChild:@"oldclass"]];
    
    if ( [self isEmptyString :className]) {
        [self quickShowMsgMain:[NSString stringWithFormat:@"class is not found for %@",strUiName]];
        return nil;
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
            
            UINavigationController *nav = [[UINavigationController alloc]
                                           initWithRootViewController:(UIViewController *)theHybridUi];
            
            ddd.window.rootViewController = nav;
        }
    }
    if (objCaller == nil) {
    }
    else{
        if (((UIViewController *)objCaller).navigationController != nil) {
            //for test only...((UIViewController *)theHybridUi).view.backgroundColor=[UIColor brownColor];
            [((UIViewController *)objCaller).navigationController pushViewController:(UIViewController *)theHybridUi animated:YES];
        }
        else{
            // modal
            [(UIViewController *)objCaller presentViewController:(UIViewController *)theHybridUi animated:YES completion:nil];
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
    //    NSLog(@"!!!!  %@", [self btoa:@"document"
    //                        @"View"
    //                        @".web"
    //                        @"View"]);
    //    NSLog(@"!!!!  %@", [self btoa:@".main"
    //                        @"Frame"
    //                        @".javaScript"
    //                        @"Context"]);
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
                
                //                NSLog(@"!!!!  %@", [self btoa:@"WKB"
                //                                    @"row"
                //                                    @"sing"
                //                                    @"Context"
                //                                    @"Controller"]);
                //                NSLog(@"!!!!  %@", [self btoa:@"register"
                //                                    @"Scheme"
                //                                    @"For"
                //                                    @"Custom"
                //                                    @"Protocol"
                //                                    @":"]);
                //prv api
                [self call_c_do_m_1:[self atob:@"V0tCcm93c2luZ0NvbnRleHRDb250cm9sbGVy"]
                                   :[self atob:@"cmVnaXN0ZXJTY2hlbWVGb3JDdXN0b21Qcm90b2NvbDo="]
                                   :@"local"];
                
                //                Class cls = NSClassFromString(@"WKB"
                //                                              @"row"
                //                                              @"sing"
                //                                              @"Context"
                //                                              @"Controller");
                //                SEL sss = NSSelectorFromString(@"register"
                //                                               @"Scheme"
                //                                               @"For"
                //                                               @"Custom"
                //                                               @"Protocol"
                //                                               @":");
                //
                //                if ([(id)cls respondsToSelector:sss]) {
                //                    // 把 http 和 https 请求交给 NSURLProtocol 处理
                //                    //[(id)cls performSelector:sel withObject:@"http"];
                //                    //[(id)cls performSelector:sel withObject:@"https"];
                ////                    SuppressPerformSelectorLeakWarning(
                ////                    [(id)cls performSelector:sel withObject:@"local"];
                ////                                                       );
                //                    ((void (*)(id, SEL, NSString *))[cls methodForSelector:sss])(cls, sss, @"local");
                //                }
                
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


//+ (JSValue *) callWebViewDoJs:(WKWebView *) _webview js:(NSString *)js_s
//{
//    @try {
//        //return [[self getWKWebViewJsCtx :_webview] evaluateScript:js_s];
//        [_webview evaluateJavaScript:js_s completionHandler:^(id _Nullable val, NSError * _Nullable error) {
//            //code
//        }];
//    } @catch (NSException *exception) {
//        NSLog(@"callWebViewDoJs error %@", exception);
//    } @finally {
//
//    }
//    return nil;
//}

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

+(BOOL) isEmptyString :(NSString *)s
{
    return (nil==s || [@"" isEqualToString:s]);
}

+(NSArray *) quickRegExpMatch :(NSString *)regex_s :(NSString *)txt
{
    NSError *error = NULL;
    NSRange range = NSMakeRange(0, [txt length]);
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:regex_s options:0 error:&error];
    if(nil!=error){
        NSLog(@"error when quickRegExpMatch %@",error);
    }
    return [regex matchesInString:txt options:0 range:range];
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
    NSString * uiname = caller.uiName;
    NSLog(@"injecting JSB to %@", uiname);
    
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
+ (NSString *) getLang:(NSString *)key
{
#warning TODO(9) getLang()
    return key;
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
    NSLog(@"Decoded: %@", base64Decoded);
    return base64Decoded;
}
+ (NSString *) base64decode:(NSString *)s
{
    return [self atob:s];
}

/****************************** STUB FOR LATER *********************************/
+ (void)saveAppConfig{
    
    CMPHybridTools *hybridManager = [self shareInstance];
    NSString *jsonString = [JSO o2s:hybridManager.jso];
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:jsonString forKey:@"appConfig"];
    [userDefaults synchronize];
}

+ (JSO *)loadAppConfig{
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *jsonString =[userDefaults objectForKey:@"appConfig"];
    JSO *jsonJso = [JSO s2o:jsonString];
    
    return jsonJso;
}

+ (void)saveUserConfig{
    
}

+ (void)loadUserConfig{
    
}
/****************************** STUB FOR LATER *********************************/

@end
