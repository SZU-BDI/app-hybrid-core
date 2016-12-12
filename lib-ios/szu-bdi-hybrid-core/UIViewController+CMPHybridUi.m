#import "UIViewController+CMPHybridUi.h"

#import "CMPHybridTools.h"

@implementation UIViewController (CMHybridUi)


-(void) on:(NSString *)eventName :(HybridEventHandler) handler
{
    [self on:eventName :handler :nil];
}

-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(JSO *)initData
{
    if(nil==self.uiEventHandlers){
        self.uiEventHandlers=[NSMutableDictionary dictionary];
    }
    self.uiEventHandlers[eventName]=handler;
}

-(void) trigger :(NSString *)eventName :(JSO *)triggerData
{
    NSLog(@"trigger(%@) is called.", eventName);
    HybridEventHandler hdl=self.uiEventHandlers[eventName];
    if(nil!=hdl){
        if(nil==triggerData) triggerData=[JSO id2o:@{}];
        NSLog(@"with triggerData %@", [triggerData toString]);
        hdl(eventName, triggerData);
    }
}

-(void) trigger :(NSString *)eventName
{
    [self trigger:eventName :nil];
}

/** NOTES: remember to call initUi at viewDidLoad
 -(void) viewDidLoad
 {
 [super viewDidLoad];
 [self initUi];
 }
 */

- (void)initUi{}//STUB ONLY

//do close
- (void) closeUi
{
    NSLog(@"!!!!!!!!! closeUi() called at UIViewController+CMPHybridUI.m   !!!!!!!!!!!!!!");
    
    BOOL flagIsLast=YES;
    
    id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
    UINavigationController *nnn=self.navigationController;
    if (nil!=nnn){
        NSArray *vvv = nnn.viewControllers;
        if(nil!=vvv){
            if(vvv.count>1){
                [self.navigationController popViewControllerAnimated:YES];
                flagIsLast=NO;
            }
        }
        if(flagIsLast==YES){
            NSLog(@" flagIsLast==YES for navigationController");
        }
    }else{
        UIViewController *rootUi=ddd.window.rootViewController;
        if (rootUi == self){
            NSLog(@" flagIsLast==YES for rootViewController root = self");
            flagIsLast=YES;
        }else{
            [self dismissViewControllerAnimated:YES completion:^{
                NSLog(@"Current View dismissViewControllerAnimated");
            }];
            [self trigger:CMPHybridEventWhenClose :self.responseData];
            return;
        }
    }
    
    if(flagIsLast==YES){
        //quit app if prompted yes
        [CMPHybridTools
         quickConfirmMsgMain:@"Sure to Quit?"
         //handlerYes:^(UIAlertAction *action)
         handlerYes:(HybridDialogCallback) ^{
             [CMPHybridTools quitGracefully];
             [self dismissViewControllerAnimated:YES completion:^{
                 NSLog(@"Last View dismissViewControllerAnimated");
             }];
         }
         handlerNo:^(UIAlertAction *action) {
             NSLog(@"User said No to quit");
         }];
        return;
    }else{
        [self trigger:CMPHybridEventWhenClose :self.responseData];
    }
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
    
    NSString *topbar_color=[JSO o2s:[param getChild:@"topbar_color"]];
    if([@"B" isEqualToString:topbar_color]){
        NSLog(@"UIViewController+CMHybridUi restoreTopBarStatus setStatusBarStyle:UIStatusBarStyleDefault");
        [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleDefault];
    }else if([@"W" isEqualToString:topbar_color]){
        NSLog(@"UIViewController+CMHybridUi restoreTopBarStatus setStatusBarStyle:UIStatusBarStyleLightContent");
        [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    }else{
        //ignore...
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
    NSLog(@"resetTopBarBtn() %@",self.uiName);
          
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

- (void) setTopBarTitle :(NSString *)title
{
    self.title=title;
}

@end
