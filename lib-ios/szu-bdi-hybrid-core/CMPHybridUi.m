#import "CMPHybridUi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridUi

//------------  UIViewController ------------


-(void) viewWillAppear:(BOOL)animated
{
    [self restoreTopBarStatus];
    [super viewWillAppear:animated];
}
-(void) viewDidLoad
{
    [self initUi];
}

//------------   <HybridUi> ------------

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
- (BOOL)prefersStatusBarHidden {
    return YES;
}
-(void) hideTopStatusBar
{
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationNone];
}
-(void) showTopStatusBar
{
    [[UIApplication sharedApplication] setStatusBarHidden:NO withAnimation:UIStatusBarAnimationNone];
}
-(void) hideTopBar
{
    [[self navigationController] setNavigationBarHidden:YES animated:NO];
}
-(void) showTopBar
{
    [[self navigationController] setNavigationBarHidden:NO animated:NO];
}

-(void) restoreTopBarStatus
{
    JSO *param =self.uiData;
    JSO *topbarmode=[param getChild:@"topbar"];
    NSString *topbarmode_s=[JSO o2s:topbarmode];
    [self CustomTopBar :topbarmode_s];
}
-(void) CustomTopBar :(NSString *)mode
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

//NOTES: can be overrided!
- (void) CustomTopBarBtn
{
    self.navigationItem.leftBarButtonItem
    = [[UIBarButtonItem alloc]
       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
       target:self
       action:@selector(closeUi)];
}

//NOTES: child can override the behavior...
- (void)closeUi{
    
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

- (void) evalJs :(NSString *)js_s
{
    NSLog(@"CMPHybridUi: evalJs() should be overrided by descendants");
}
//////////////////////////////  on/trigger mechanism

//TODO to upgrade for supporting multi-listeners in future.

-(void) on:(NSString *)eventName :(HybridEventHandler) handler
{
    [self on:eventName :handler :nil];
}
-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(JSO *)extraData
{
    if(nil==self.myEventHandlers){
        self.myEventHandlers=[NSMutableDictionary dictionary];
    }
    self.myEventHandlers[eventName]=handler;
}
-(void) trigger :(NSString *)eventName :(JSO *)extraData
{
    HybridEventHandler hdl=self.myEventHandlers[eventName];
    if(nil!=hdl){
        hdl(eventName, extraData);
    }
}
//////////////////////////////

//------------ self -----------------

//NOTES: can be overrided
- (void)initUi
{
    [self CustomTopBarBtn];
}

@end
