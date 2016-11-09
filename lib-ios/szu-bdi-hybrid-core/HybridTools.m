#import <UIKit/UIKit.h>

#import "HybridTools.h"

//github/szu-bdi/lib-ios-jso
#import "JSO.h"

@implementation HybridTools

+ (id)getSingleton{
    
    static HybridTools *_sharedHybridTools = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _sharedHybridTools = [[self alloc] init];
    });
    return _sharedHybridTools;
}

+ (void)checkAppConfig{
    
    HybridTools *hybridManager = [self getSingleton];
    if(nil==hybridManager.jso){
        
        // readFileFromAsset()
        NSString *jsonString = [self readFileFromAsset:@"config" ofType:@"json"];
        
        JSO *jsonJso = [JSO s2o:jsonString];
        hybridManager.jso = jsonJso;
    }
}

+ (void)startUi:(NSString *)strUiName strInitParam:(JSO *)strInitParam objCaller:(id<HybridUi>)objCaller callback:(WVJBResponseCallback)callback{
    [self checkAppConfig];
    
    // 获取 UI 映射数据
    JSO *jso_uiMapping = [self getAppConfig:@"ui_mapping"];
    
    // 获取 UI 的配置文件
    JSO *jso_uiConfig = [jso_uiMapping getChild:strUiName];
    
    // 动态获取 UI 类:
    JSO *jso_className = [jso_uiConfig getChild:@"class"];
    NSString *className = [JSO o2s:jso_className];
    
    // 实例化动态获取的 UI 类:
    Class uiClass = NSClassFromString(className);
    id <HybridUi> initUiClass = [[uiClass alloc] init];
    
    // 判断是否存在
    if (!initUiClass) {
        [self showAlertMessage:[NSString stringWithFormat:@"%@ is not found", strUiName]];
        return;
    }
    
    /*---- 若存在则执行以下步骤 -----------------
     1、设置获取的 UI 类， 遵循 HybridUi 协议。*/
    HybridUi *hyBridUi = [[HybridUi alloc] init];
    hyBridUi.HybridUiDelegate = initUiClass;
    //#warning 上面这个 delegate 有歧义，为什么要这样弄？
#warning 此处的 delegate 就是让要打开的ui 去 遵循hyBridUi的协议，然后下面才能执行协议中的方法
    
    // 2、获取 UI 的类型  *覆盖参数有type* 则覆盖附带的type
    NSString *uiMode = [self fastO2S:jso_uiConfig forKey:@"type"];
    NSString *paramUiMode = [self fastO2S:strInitParam forKey:@"type"];
    if (![paramUiMode isEqualToString:@""]) {
        uiMode = paramUiMode;
    }
    
    // 3、获取 UI 的url  *覆盖参数有url* 则覆盖附带的url
    NSString *webUrl = [self fastO2S:jso_uiConfig forKey:@"url"];
    NSString *paramWebUrl = [self fastO2S:strInitParam forKey:@"address"];
    if (![paramWebUrl isEqualToString:@""]) {
        webUrl = paramWebUrl;
    }
    
    // 4、获取 UI 有无topBar *覆盖参数有topBar* 则覆盖附带的topBar
    NSString *topBarStatus = [self fastO2S:jso_uiConfig forKey:@"topbar"];
    BOOL haveTopBar = ([topBarStatus isEqualToString:@"Y"])? YES : NO;
    NSString *paramTopBarStatus = [self fastO2S:strInitParam forKey:@"topbar"];
    if (![paramTopBarStatus isEqualToString:@""]) {
        haveTopBar = ([paramTopBarStatus isEqualToString:@"Y"])? YES : NO;
    }
    
    // 5、获取 UI topBar 的标题  *覆盖参数有title* 则覆盖附带的title
    NSString *title = [self fastO2S:jso_uiConfig forKey:@"title"];
    NSString *paramTitle = [self fastO2S:strInitParam forKey:@"title"];
    if (![paramTitle isEqualToString:@""]) {
        title = paramTitle;
    }
    
    // 6、判断是否有回调函数
    if (callback) {
        // 7、设置回调
        [hyBridUi setCallback:callback];
    }
    
    /*---------------- 开始设置 -----------------
     若为 WebView 类型，则通过HybridUi协议设置 ui 的 url*/
    if ([uiMode isEqualToString:@"WebView"]) {
        [hyBridUi setWebViewUiUrl:webUrl];
    }
    
    // 设置 topBar 的显示状态
    [hyBridUi setHaveTopBar:haveTopBar];
    
    // 若 topBar 为显示状态，则通过HybridUi协议设置 ui 的 topBar title
    if (haveTopBar) {
        // 设置 topBar 的标题
        [hyBridUi setTopBarTitle:title];
    }
    
    /*---- 开始执行 ----*/
    // 调用者为nil 则表示是启动
    if (objCaller == nil) {
        
        id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
        
        if ([initUiClass isKindOfClass:[UITabBarController class]]) {
            // 若为 UI 为 UITabBarController类型 则直接作为根视图
            ddd.window.rootViewController = (UIViewController *)initUiClass;
        }
        else{
            // 否则，添加导航栏后，作为根视图
            UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:(UIViewController *)initUiClass];
            ddd.window.rootViewController = nav;
        }
    }
    else{
        
        if (((UIViewController *)objCaller).navigationController != nil) {
            // push
            [((UIViewController *)objCaller).navigationController pushViewController:(UIViewController *)initUiClass animated:YES];
        }
        else{
            // moda
            [(UIViewController *)objCaller presentViewController:(UIViewController *)initUiClass animated:YES completion:nil];
        }
    }
}

+ (HybridApi *)getHybridApi:(NSString *)name{
    
    Class myApiClass = NSClassFromString(name);
    
    id myApiClassInstance = [[myApiClass alloc] init];
    
    if (myApiClassInstance) {
        // NSLog(@"返回api的是：(%@)", myApiClassInstance);
        return myApiClassInstance;
    }
    else{
        [self showAlertMessage:[NSString stringWithFormat:@"Api: %@ not found", name]];
    }
    
    return nil;
}

+ (JSO *)wholeAppConfig{
    
    HybridTools *hybridManager = [self getSingleton];
    return hybridManager.jso;
}

+ (JSO *)getAppConfig:(NSString *)key{
    
    JSO *jso_value;
    
    JSO *jsonJso = [self wholeAppConfig];
    if (jsonJso) {
        
        jso_value = [jsonJso getChild:key];
    }
    else{
        [self showAlertMessage:[NSString stringWithFormat:@"appConfig (%@) not found", key]];
        jso_value = nil;
    }
    
    return jso_value;
}

+ (NSString *)fastO2S:(JSO *)jso forKey:(NSString *)key{
    
    JSO *jsoValue = [jso getChild:key];
    NSString *jsonString = [JSO o2s:jsoValue];
    
    if ([jsonString isEqualToString:@"null"]){
        return @"";
    }
    
    return jsonString;
}

+ (NSString *)readFileFromAsset:(NSString *)asset ofType:(NSString *)type{
    
    // get path of asset (config.json)
    NSString *configFilePath = [[NSBundle mainBundle] pathForResource:asset ofType:type];
    
    // get the content of the config.json
    NSData *jsonData = [[NSData alloc] initWithContentsOfFile:configFilePath];
    
    // decoded as string of utf-8
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    return jsonString == nil ? @"":jsonString;
}

+ (void)showAlertMessage:(NSString *)message{
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message:message delegate:nil cancelButtonTitle:nil otherButtonTitles:@"确定", nil];
    [alert show];
}

/******************************备用*********************************/
+ (void)saveAppConfig{
    
    HybridTools *hybridManager = [self getSingleton];
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
