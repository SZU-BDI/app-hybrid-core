#import <UIKit/UIKit.h>

#import "CMPHybridTools.h"

#import "CMPHybridTools.h"
@import JavaScriptCore;

/////////////////////////////////////////////////////////////
//internal class(CmpUIAlertView) to handle the callback
//quickAlertMsg()
@interface CmpUIAlertView : UIAlertView

@property () void (^callback)();

-(instancetype) initWithMsg:(NSString *)msg  callback:(void (^)())callback;

@end

@implementation CmpUIAlertView

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
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
        hybridManager.jso = [JSO s2o:[self readAssetInStr:@"config.json"]];
    }
}

+ (CMPHybridUi *) startUi :(NSString *)strUiName
              initData:(JSO *) initData
                 objCaller:(CMPHybridUi *)objCaller
{
    
    [self checkAppConfig];
    
    JSO *jso_uiMapping = [self getAppConfig:@"ui_mapping"];
    
    JSO *uiConfig = [[jso_uiMapping getChild:strUiName] copy];//important to copy one!!
    
    JSO *jso_className = [uiConfig getChild:@"class"];
    NSString *className = [JSO o2s:jso_className];
    
    if ( [self isEmptyString :className]) {
        [self quickShowMsgMain:[NSString stringWithFormat:@"class is not found for %@",strUiName]];
        return nil;
    }
    
    Class uiClass = NSClassFromString(className);
    CMPHybridUi * theHybridUi = [[uiClass alloc] init];
    
    if (!theHybridUi) {
        [self quickShowMsgMain:[NSString stringWithFormat:@"%@ is unable to init", strUiName]];
        return nil;
    }
    [uiConfig basicMerge:initData];
    theHybridUi.uiData=uiConfig;
//    [theHybridUi.uiData basicMerge:initData];

    
//    id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
//    UINavigationController *nav = [[UINavigationController alloc]
//                                   initWithRootViewController:(UIViewController *)theHybridUi];
//    ddd.window.rootViewController = nav;
//    [(UIViewController *)objCaller presentViewController:(UIViewController *)theHybridUi animated:YES completion:nil];

//    //TODO need to release the name_s??
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
            // push
            theHybridUi.view.backgroundColor = [UIColor whiteColor];//important for speed
            //http://stackoverflow.com/questions/19917928/uinavigationcontroller-pushviewcontroller-pauses-freezes-midway-through/19919081#19919081
            [((UIViewController *)objCaller).navigationController pushViewController:(UIViewController *)theHybridUi animated:YES];
        }
        else{
            // modal
            [(UIViewController *)objCaller presentViewController:(UIViewController *)theHybridUi animated:YES completion:nil];
        }
    }
    //    });
    //            [NSTimer scheduledTimerWithTimeInterval:3.0 target:self selector:@selector(run:) userInfo:@"abc" repeats:NO];

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

+ (JSO *)getAppConfig:(NSString *)key{
    
    JSO *jso_value;
    
    JSO *jsonJso = [self wholeAppConfig];
    if (jsonJso) {
        
        jso_value = [jsonJso getChild:key];
    }
    else{
        [self quickShowMsgMain:[NSString stringWithFormat:@"appConfig (%@) not found", key]];
        jso_value = nil;
    }
    
    return jso_value;
}

//+ (NSString *)fastO2S:(JSO *)jso forKey:(NSString *)key{
//    
//    JSO *jsoValue = [jso getChild:key];
//    NSString *jsonString = [JSO o2s:jsoValue];
//    
//    if ([jsonString isEqualToString:@"null"]){
//        return @"";
//    }
//    
//    return jsonString;
//}

//deprecated, see quickShowMsgMain()
//+ (void)showAlertMessage:(NSString *)message{
//
//    //deprecated UIAlertView and UIAlertViewDelegate
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message:message delegate:nil cancelButtonTitle:nil otherButtonTitles:@"确定", nil];
//        [alert show];
//}

//IOS 8+
+ (void)quickShowMsgMain:(NSString *)msg{
    
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:msg message:@"" preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil];
    [alertController addAction:ok];
    
    //[self presentViewController:alertController animated:YES completion:nil];
    
    //    UIViewController *topRootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    //    while (topRootViewController.presentedViewController)
    //    {
    //        topRootViewController = topRootViewController.presentedViewController;
    //    }
    //
    //    [topRootViewController presentViewController:alertController animated:NO completion:nil];
    
    [[self findTopRootView] presentViewController:alertController animated:NO completion:nil];
    
    //NOTES: the alert above will not block the whole application,
    //that's why need to find the top view to show?
    NSLog(@"After show alert %@",msg);
}

+ (void)quickAlertMsg :(NSString *)msg callback:(void (^)())callback;
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

+ (JSValue *) callWebViewDoJs:(UIWebView *) _webview :(NSString *)js_s
{
    @try {
        return [[self getWebViewJsCtx:_webview] evaluateScript:js_s];
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

+(void) promptUserQuit
{
    [CMPHybridTools
     quickConfirmMsgMain:@"Sure to Quit?"
     //         handlerYes:^(UIAlertAction *action)
     handlerYes:^(UIAlertAction *action){
         //         [self dismissViewControllerAnimated:YES completion:nil];
         
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


/******************************备用*********************************/
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
/******************************备用*********************************/

@end
