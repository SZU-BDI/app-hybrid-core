#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>

#import "CMPHybridTools.h"

#import "CMPHybridTools.h"
@import JavaScriptCore;

/////////////////////////////////////////////////////////////
//internal class(CmpUIAlertView) to handle the callback for quickAlertMsg()
@interface CmpUIAlertView : UIAlertView

@property () void (^callback)();

-(instancetype) initWithMsg:(NSString *)msg  callback:(void (^)())callback;

@end

@implementation CmpUIAlertView

-(void) alertView :(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if(self.callback) self.callback();
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
//                 objCaller:(CMPHybridUi *)objCaller
//                  callback:...

+ (void) startUi :(NSString *)strUiName
         initData:(JSO *) initData
        objCaller:(CMPHybridUi *)objCaller
         callback:(void (^)(CMPHybridUi * ui))callback
{
    CMPHybridUi * ui = [self startUi:strUiName initData:initData objCaller:objCaller];
    if(nil!=callback){
        callback(ui);
    }
}

+ (CMPHybridUi *) startUi :(NSString *)strUiName
                  initData:(JSO *) initData
                 objCaller:(CMPHybridUi *)objCaller
{
    [self checkAppConfig];
    
    JSO *jso_uiMapping = [self getAppConfig:@"ui_mapping"];
    
    JSO *uiConfig = [[jso_uiMapping getChild:strUiName] copy];//important to copy one otherwise the real one will be poluted
    
    NSString *className = [JSO o2s:[uiConfig getChild:@"class"]];
    
    if ( [self isEmptyString :className]) {
        [self quickShowMsgMain:[NSString stringWithFormat:@"class is not found for %@",strUiName]];
        return nil;
    }
    
    Class uiClass = NSClassFromString(className);
    CMPHybridUi * theHybridUi = [[uiClass alloc] init];
    
    if (nil==theHybridUi) {
        [self quickShowMsgMain:[NSString stringWithFormat:@"%@ is unable to init", strUiName]];
        return nil;
    }

    [uiConfig basicMerge:initData];
    theHybridUi.uiName=strUiName;
    theHybridUi.uiData=uiConfig;
    
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
    
    UIAlertAction* ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil];
    [alertController addAction:ok];
    
    //modal
    [[self findTopRootView] presentViewController:alertController animated:NO completion:callback];
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
    [app performSelector:@selector(suspend)];
    
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
    return [_webview
            valueForKeyPath:
            (@"documentView"
             @".webView"
             @".mainFrame"
             @".javaScriptContext")];
}

//warning! caller need to handler the thread like:

+ (JSValue *) callWebViewDoJs:(UIWebView *) _webview :(NSString *)js_s
{
    @try {
        return [[self getWebViewJsCtx :_webview] evaluateScript:js_s];
    } @catch (NSException *exception) {
        NSLog(@"callWebViewDoJs error %@", exception);
    } @finally {
        
    }
    return nil;
}


+ (JSValue *) callWKWebViewDoJs:(WKWebView *) _webview :(NSString *)js_s
{
    @try {
        //return [[self getWKWebViewJsCtx :_webview] evaluateScript:js_s];
        [_webview evaluateJavaScript:js_s completionHandler:^(id _Nullable val, NSError * _Nullable error) {
            //code
        }];
    } @catch (NSException *exception) {
        NSLog(@"callWebViewDoJs error %@", exception);
    } @finally {
        
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

+ (void) injectJSB :(UIWebView *)webView :(CMPHybridUi *)caller
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
        if(nil==caller.myApiHandlers) {
            NSLog(@" !!! caller.myApiHandlers is nil !!! %@", caller.uiData);
            return;
        }
        
        HybridHandler handler = caller.myApiHandlers[handlerName_s];
        
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
