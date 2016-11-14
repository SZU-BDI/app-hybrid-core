#import "CMPHybridUi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridUi


//NOTES: child can override the behavior...
- (void)closeUi{
    BOOL flagIsLast=YES;
    id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
    UINavigationController *nnn=self.navigationController;
    if (nnn!=nil){
        NSArray *vvv = nnn.viewControllers;
        if(vvv!=nil){
            //NSLog(@" total view left %@",vvv.count);
            if(vvv.count>1){
                [self.navigationController popViewControllerAnimated:YES];
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
            [self dismissViewControllerAnimated:YES completion:^(){
                if (self.callback) {
                    self.callback(@{@"TODO":@"YES001"});
                }
            }];
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
    
}
@end
