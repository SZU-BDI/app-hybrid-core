#import "UIViewController+CMPHybridUi.h"
#import <UIKit/UIKit.h>
#import "CMPHybridUi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation UIViewController (CMHybridUi)

/* remember to call initUi at viewDidLoad
 -(void) viewDidLoad
 {
 [super viewDidLoad];
 [self initUi];
 }
 */
- (void)initUi
{
//    [CMPHybridTools quickAlertMsgForOldiOS:@"Forget to implement initUi() ?!" callback:^{
//        NSLog(@"callback after alert");
//        [CMPHybridTools quitGracefully];
//    }];
}

- (void) closeUi
{
    BOOL flagIsLast=YES;
    id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
    UINavigationController *nnn=self.navigationController;
    if (nnn!=nil){
        NSArray *vvv = nnn.viewControllers;
        if(vvv!=nil){
            if(vvv.count>1){
                [self.navigationController popViewControllerAnimated:YES];
                flagIsLast=NO;
            }
        }
        if(flagIsLast==YES){
            NSLog(@" flagIsLast==YES");
        }
    }else{
        UIViewController *rootUi  =ddd.window.rootViewController;
        if (rootUi == self){
            NSLog(@" root = self");
            flagIsLast=YES;
        }else{
            [self dismissViewControllerAnimated:YES completion:nil];
        }
    }
    
    if(flagIsLast==YES){
        //quit app if prompted yes
        [CMPHybridTools
         quickConfirmMsgMain:@"Sure to Quit?"
         //handlerYes:^(UIAlertAction *action)
         handlerYes:(HybridDialogCallback) ^{
             [self dismissViewControllerAnimated:YES completion:nil];
             [CMPHybridTools quitGracefully];
         }
         handlerNo:nil];
    }
    [self trigger:@"close" :nil];
}

-(void) on:(NSString *)eventName :(HybridEventHandler) handler
{
    [self on:eventName :handler :nil];
}

-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(JSO *)extraData
{
    if(nil==self.uiEventHandlers){
        self.uiEventHandlers=[NSMutableDictionary dictionary];
    }
    self.uiEventHandlers[eventName]=handler;
}

-(void) trigger :(NSString *)eventName :(JSO *)extraData
{
    NSLog(@"trigger(%@) is called.", eventName);
    HybridEventHandler hdl=self.uiEventHandlers[eventName];
    if(nil!=hdl){
        if(nil==extraData) extraData=[JSO id2o:@{}];
        hdl(eventName, extraData);
    }
}

-(void) trigger :(NSString *)eventName
{
    [self trigger:eventName :nil];
}

//- (void) evalJs :(NSString *)js_s
//{
//    NSLog(@"CMPHybridUi: evalJs() should be overrided by descendants");
//}

/* About FullScreen (hide top status bar)
 // Plan A, It works for iOS 5 and iOS 6 , but not in iOS 7.
 // [UIApplication sharedApplication].statusBarHidden = YES;
 // Info.plist need add:
 //    <key>UIStatusBarHidden</key>
 //    <true/>
 // Plan B: Info.plist
 //    <key>UIViewControllerBasedStatusBarAppearance</key>
 //    <false/>
 //    [[UIApplication sharedApplication] setStatusBarHidden:YES
 //                                            withAnimation:UIStatusBarAnimationFade];
 
 //@ref http://stackoverflow.com/questions/18979837/how-to-hide-ios-status-bar
 */

-(void) hideTopStatusBar
{
    NSLog(@"UIViewController+CMHybridUi hideTopStatusBar()");
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationNone];
}
-(void) showTopStatusBar
{
    NSLog(@"UIViewController+CMHybridUi showTopStatusBar()");
    [[UIApplication sharedApplication] setStatusBarHidden:NO withAnimation:UIStatusBarAnimationNone];
}
-(void) hideTopBar
{
    NSLog(@"UIViewController+CMHybridUi hideTopBar()");
    [[self navigationController] setNavigationBarHidden:YES animated:NO];
}
-(void) showTopBar
{
    NSLog(@"UIViewController+CMHybridUi showTopBar()");
    [[self navigationController] setNavigationBarHidden:NO animated:NO];
}

-(void) resetTopBarStatus
{
    JSO *param =self.uiData;
    JSO *topbarmode=[param getChild:@"topbar"];
    NSString *topbarmode_s=[JSO o2s:topbarmode];
    [self resetTopBar :topbarmode_s];
    
    //self.wantsFullScreenLayout = YES;
    //[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleBlackTranslucent];
    
    //    @property(nonatomic,assign) UIRectEdge edgesForExtendedLayout NS_AVAILABLE_IOS(7_0); // Defaults to UIRectEdgeAll
    //    @property(nonatomic,assign) BOOL extendedLayoutIncludesOpaqueBars NS_AVAILABLE_IOS(7_0); // Defaults to NO, but bars are translucent by default on 7_0.
    //    @property(nonatomic,assign) BOOL automaticallyAdjustsScrollViewInsets NS_AVAILABLE_IOS(7_0); // Defaults to YES
    
    NSString *topbar_color=[JSO o2s:[param getChild:@"topbar_color"]];
    if([@"B" isEqualToString:topbar_color]){
        NSLog(@"UIViewController+CMHybridUi restoreTopBarStatus setStatusBarStyle:UIStatusBarStyleDefault");
        [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleDefault];
    }else{
        NSLog(@"UIViewController+CMHybridUi restoreTopBarStatus setStatusBarStyle:UIStatusBarStyleLightContent");
        [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    }
}

-(void) resetTopBar :(NSString *)mode
{
    if ([CMPHybridTools isEmptyString:mode])
        mode=@"Y";
    
    if([@"F" isEqualToString:mode]){
        [self hideTopStatusBar];
        [self hideTopBar];
    }
    if([@"M" isEqualToString:mode]){
        [self hideTopStatusBar];
        [self showTopBar];
    }
    if([@"Y" isEqualToString:mode]){
        [self showTopStatusBar];
        [self showTopBar];
    }
    if([@"N" isEqualToString:mode]){
        [self showTopStatusBar];
        [self hideTopBar];
    }
}

- (void) resetTopBarBtn
{
    self.navigationItem.leftBarButtonItem
    = [[UIBarButtonItem alloc]
       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
       target:self
       action:@selector(closeUi)];
}


//for <iOS9 ?
- (BOOL)prefersStatusBarHidden {
    NSLog(@"UIViewController+CMHybridUi returns NO");
    return NO;
}

//for <iOS9
-(UIStatusBarStyle)preferredStatusBarStyle{
    //return UIStatusBarStyleLightContent;
    NSLog(@"UIViewController+CMHybridUi preferredStatusBarStyle returns UIStatusBarStyleDefault");
    return UIStatusBarStyleDefault;
}
@end
