#import "CMPHybridUi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridUi

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
    //    [[self navigationController] setNavigationBarHidden:YES animated:NO];
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
    
    //    UIBarButtonItem *rightBtn
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:nil];
    //    self.navigationItem.rightBarButtonItem = rightBtn;
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
                //                [self trigger:@"close" :@{@"debug":@"popViewControllerAnimated"}];
                flagIsLast=NO;
            }
        }
        if(flagIsLast==YES){
            NSLog(@" TODO for flagIsLast==YES");
        }
    }else{
        UIViewController *rootUi  =ddd.window.rootViewController;
        if (rootUi == self){
            NSLog(@" TODO !!! root = self");
            flagIsLast=YES;
        }else{
            [self dismissViewControllerAnimated:YES completion:nil];
            //            [self trigger:@"close" :@{@"debug":@"dismissViewControllerAnimated"}];
        }
    }
    
    if(flagIsLast==YES){
        //quit app if prompted yes
        [CMPHybridTools
         quickConfirmMsgMain:@"Sure to Quit?"
         //         handlerYes:^(UIAlertAction *action)
         handlerYes:^(UIAlertAction *action){
             [self dismissViewControllerAnimated:YES completion:nil];
             
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
    [self trigger:@"close" :@{@"debug":@"closeUi"}];
}

-(void) on:(NSString *)eventName :(HybridEventHandler) handler
{
    [self on:eventName :handler :nil];
}
-(void) on:(NSString *)eventName :(HybridEventHandler) handler :(id)extraData
{
    NSLog(@"TODO on %@ callback ", eventName);
}
-(void) trigger :(NSString *)eventName :(id)extraData
{
    NSLog(@"TODO trigger event %@", eventName);
}
//    - (void)toggleFullscreen:(void(^)())complete withDuration:(NSTimeInterval)duration {
//        if (_fullscreenToggled) {
//            [self exitFullscreen:complete withDuration:duration];
//        } else {
//            [self enterFullscreen:complete withDuration:duration];
//        }
//    }
//- (void)enterFullscreen:(void(^)())complete withDuration:(NSTimeInterval)duration {
//    CGRect topFrame = _topToolbar.frame;
//    CGRect bottomFrame = _bottomToolbar.frame;
//
//    [UIView animateWithDuration:duration animations:^{
//        _topToolbar.frame = CGRectMake(topFrame.origin.x, topFrame.origin.y-topFrame.size.height, topFrame.size.width, topFrame.size.height);
//        _bottomToolbar.frame = CGRectMake(bottomFrame.origin.x, bottomFrame.origin.y+bottomFrame.size.height, bottomFrame.size.width, bottomFrame.size.height);
//
//        _webView.frame = CGRectMake(0,0,_webView.frame.size.width,_webView.frame.size.height+topFrame.size.height+bottomFrame.size.height);
//    } completion:^(BOOL finished) {
//        if (complete != nil) {
//            complete();
//        }
//    }];
//
//    _fullscreenToggled = YES;
//}
//
//- (void)exitFullscreen:(void(^)())complete withDuration:(NSTimeInterval)duration {
//    CGRect topFrame = _topToolbar.frame;
//    CGRect bottomFrame = _bottomToolbar.frame;
//
//    [UIView animateWithDuration:duration animations:^{
//        _topToolbar.frame = CGRectMake(topFrame.origin.x, topFrame.origin.y+topFrame.size.height, topFrame.size.width, topFrame.size.height);
//        _bottomToolbar.frame = CGRectMake(bottomFrame.origin.x, bottomFrame.origin.y-bottomFrame.size.height, bottomFrame.size.width, bottomFrame.size.height);
//
//        _webView.frame = CGRectMake(0,topFrame.size.height,_webView.frame.size.width,_webView.frame.size.height);
//    } completion:^(BOOL finished) {
//        // Clip off the extra bottom. It wasn't in the animation
//        // because the bottom portion of the web view would blink.
//        _webView.frame = CGRectMake(
//                                    _webView.frame.origin.x,
//                                    _webView.frame.origin.y,
//                                    _webView.frame.size.width,
//                                    _webView.frame.size.height-topFrame.size.height-bottomFrame.size.height
//                                    );
//        if (complete != nil) {
//            complete();
//        }
//    }];
//
//    _fullscreenToggled = NO;
//}

/* About FullScreen (hide top status bar)
 //It works for iOS 5 and iOS 6 , but not in iOS 7.
 //[UIApplication sharedApplication].statusBarHidden = YES;//NOTES: Info.plist need add:
 //    <key>UIStatusBarHidden</key>
 //    <true/>
 //
 //    <key>UIViewControllerBasedStatusBarAppearance</key>
 //    <false/>
 
 //    [[UIApplication sharedApplication] setStatusBarHidden:YES
 //                                            withAnimation:UIStatusBarAnimationFade];
 //    [[UIApplication sharedApplication] setStatusBarHidden:NO
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
@end
