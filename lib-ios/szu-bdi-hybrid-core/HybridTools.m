//
//  HybridTools.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "HybridTools.h"
#import "AppDelegate.h"
#import "JSO.h"

@implementation HybridTools

+ (void)initAppConfig{
#warning 现在是每次启动都重新缓存

#warning TODO 参考 android ，增加 readFileFromAsset()
    
    //get path of asset (config.json)
    NSString *configFilePath = [[NSBundle mainBundle] pathForResource:@"config"ofType:@"json"];
    
    //get the content of the config.json
    NSData *jsonData = [[NSData alloc] initWithContentsOfFile:configFilePath];
    
    //decoded as string of utf-8
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    
    
    //push the ? into user user-default
#warning wholeAppConfig 奇怪，为什么不是s2o后的，而是放str???
    [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:@"config"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
//    // 把json数据转换成oc
//    NSError *error;
//    NSDictionary *jsonContent = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&error];
//    // 持久化获取的配置，以config为key
//    [[NSUserDefaults standardUserDefaults] setObject:jsonContent forKey:@"config"];
//    [[NSUserDefaults standardUserDefaults] synchronize];
}

+ (void)startUi:(NSString *)strUiName strInitParam:(NSDictionary *)strInitParam objCaller:(id<HybridUi>)objCaller{
    
    // 获取 UI 映射数据
    // NSDictionary *uiMapping = [self getAppConfig:@"ui_mapping"];
    JSO *jso_uiMapping = [self getAppConfig:@"ui_mapping"];
    
    // 获取 UI 的配置文件
    // NSDictionary *uiConfig = uiMapping[strUiName];
    JSO *jso_uiConfig = [jso_uiMapping getChild:strUiName];

    // 动态获取 UI 类:
    // Class uiClass = NSClassFromString((NSString *)uiConfig[@"class"]);
    JSO *jso_className = [jso_uiConfig getChild:@"class"];
    NSString *className = [JSO o2s:jso_className];
    Class uiClass = NSClassFromString(className);

    // 实例化动态获取的 UI 类:
    id <HybridUi> initUiClass = [[uiClass alloc] init];
    
    // 判断是否存在
    if (!initUiClass) {
#warning TODO  改为调用 HybridTools::quickAlertMsg(str)
        NSLog(@"%@ is not found", strUiName);
        return;
    }
    
    /*---- 若存在则执行以下步骤 ----*/
    // 1、设置获取的 UI 类， 遵循 HybridUi 协议。
    HybridUi *hyBridUi = [[HybridUi alloc] init];
    hyBridUi.delegate = initUiClass;
#warning 上面这个 delegate 有歧义，为什么要这样弄？
    
    // 2、获取 UI 的类型  *覆盖参数有type* 则覆盖附带的type
    // NSString *uiMode = (NSString *)uiConfig[@"type"];
    JSO *jso_uiMode = [jso_uiConfig getChild:@"type"];
    NSString *uiMode = [JSO o2s:jso_uiMode];
    if (strInitParam[@"type"] != nil) {
        uiMode = (NSString *)strInitParam[@"type"];
    }
    
    // 3、获取 UI 的url  *覆盖参数有url* 则覆盖附带的url
    // NSString *webUrl = (NSString *)uiConfig[@"url"];
    JSO *jso_webUrl = [jso_uiConfig getChild:@"url"];
    NSString *webUrl = [JSO o2s:jso_webUrl];
    if (strInitParam[@"url"] != nil) {
        webUrl = (NSString *)strInitParam[@"url"];
    }
    
    // 4、获取 UI 有无topBar *覆盖参数有topBar* 则覆盖附带的topBar
    // BOOL haveTopBar = ([uiConfig[@"topbar"] isEqualToString:@"Y"])? YES : NO;
    JSO *jso_haveTopBar = [jso_uiConfig getChild:@"topbar"];
    BOOL haveTopBar = ([[JSO o2s:jso_haveTopBar] isEqualToString:@"Y"])? YES : NO;
    if (strInitParam[@"topbar"] != nil) {
        haveTopBar = ([strInitParam[@"topbar"] isEqualToString:@"Y"])? YES : NO;
    }
    
    // 5、获取 UI topBar 的标题  *覆盖参数有title* 则覆盖附带的title
    // NSString *title = (NSString *)uiConfig[@"title"];
    JSO *jso_title = [jso_uiConfig getChild:@"title"];
    NSString *title = [JSO o2s:jso_title];
    if (strInitParam[@"title"] != nil) {
        title = (NSString *)strInitParam[@"title"];
    }
    
    // 6、获取覆盖参数中的回调函数
    if (strInitParam[@"callback"] != nil) {
        NSLog(@"设置回调函数");
        // 7、设置回调
        [hyBridUi setCallback:(WVJBResponseCallback)strInitParam[@"callback"]];
    }
    
    /*---- 开始设置 ----*/
    // 若为 WebView 类型，则通过HybridUi协议设置 ui 的 url
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
        
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        if ([initUiClass isKindOfClass:[UITabBarController class]]) {
            // 若为 UI 为 UITabBarController类型 则直接作为根视图
            delegate.window.rootViewController = (UIViewController *)initUiClass;
        }
        else{
            // 否则，添加导航栏后，作为根视图
            UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:(UIViewController *)initUiClass];
            delegate.window.rootViewController = nav;
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
    }else{
        NSLog(@"Api: %@ not found", name);
    }
    
    return nil;
}

+ (NSString *)wholeAppConfig{
    
//    NSDictionary *appConfig = [NSDictionary dictionaryWithDictionary:[[NSUserDefaults standardUserDefaults] objectForKey:@"config"]];
    
    NSString *jso_string = (NSString *)[[NSUserDefaults standardUserDefaults] objectForKey:@"config"];
    
    if (!jso_string){
        NSLog(@"appConfig not found");
        return nil;
    }
    return jso_string;
}



+ (JSO *)getAppConfig:(NSString *)key{
    
    NSString *jso_string = [self wholeAppConfig];
    
    JSO *jso_value;
    if (jso_string) {
       
        JSO *jso = [JSO s2o:jso_string];
        JSO *jso_key = [jso getChild:key];
        NSString *jso_string_value = [JSO o2s:jso_key];
        
        jso_value = [JSO s2o:jso_string_value];
    }
    else{
        NSLog(@"appConfig (%@) not found", key);
        jso_value = nil;
    }
    
    return jso_value;
    
//    if (![self wholeAppConfig][key]){
//        NSLog(@"appConfig (%@) not found", [self wholeAppConfig][key]);
//        return nil;
//    }
//    return [self wholeAppConfig][key];
}





//+ (HybridUi *) buildHybridUi:(NSString *)name{
//
//    // 利用类的名字，动态获得一个类:
//    Class myUiClass = NSClassFromString(name);
//    
//    // 实例化动态获取的这个类:
//    id myUiClassInstance = [[myUiClass alloc] init];
//
//    // 如果实例化成功，则返回该类的实例
//    if (myUiClassInstance) {
//        NSLog(@"返回ui的是：：：%@", myUiClassInstance);
//        return myUiClassInstance;
//    }else{
//        NSLog(@"Ui: %@ not found", name);
//    }
//    
//    return nil;
//}
//
//+ (HybridApi *) buildHybridApi:(NSString *)name{
//    
//    Class myApiClass = NSClassFromString(name);
//    
//    id myApiClassInstance = [[myApiClass alloc] init];
//    
//    if (myApiClassInstance) {
//        NSLog(@"返回api的是：：：%@", myApiClassInstance);
//        return myApiClassInstance;
//    }else{
//        NSLog(@"Api: %@ not found", name);
//    }
//    
//    return nil;
//}
//
//+ (NSDictionary *) fromAppConfigGetApi{
//    // 读取配置
//    NSDictionary *appConfig = [[NSUserDefaults standardUserDefaults] objectForKey:@"config"];
//    
//    if (appConfig[@"api_mapping"]) {
//        NSLog(@"==拿到的api映射 --> %@", appConfig[@"api_mapping"]);
//        return appConfig[@"api_mapping"];
//    }else{
//        NSLog(@"api not found");
//    }
//    return nil;
//}

@end
