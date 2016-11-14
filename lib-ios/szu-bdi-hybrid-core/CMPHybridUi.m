#import "CMPHybridUi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridUi


//NOTES: child can override the behavior...
- (void)close{
    [self dismissViewControllerAnimated:YES completion:^(){
        if (self.callback) {
            //NSString *address = [NSString stringWithFormat:@"%@", self.accessAddress];
            //self.callback(@{@"address":address});
            self.callback(@{@"TODO":@"YES"});
        }
    }];
    
//    NSArray *viewcontrollers = self.navigationController.viewControllers;
//    
//    if (viewcontrollers!=nil && viewcontrollers.count > 1) {
//        if ([viewcontrollers objectAtIndex:viewcontrollers.count-1] == self){
//            [self.navigationController popViewControllerAnimated:YES];
//            if (self.callback) {
//                //NSString *address = [NSString stringWithFormat:@"%@", self.accessAddress];
//                //self.callback(@{@"address":address});
//                self.callback(@{@"TODO":@"YES"});
//            }
//        }
//    }
//    else{
//        //quit app if prompted yes
//        [CMPHybridTools
//         quickConfirmMsgMain:@"Sure to Quit?"
//         //         handlerYes:^(UIAlertAction *action)
//         handlerYes:^(UIAlertAction *action){
//             [self dismissViewControllerAnimated:YES completion:nil];
//             
//             //home button press programmatically
//             UIApplication *app = [UIApplication sharedApplication];
//             NSLog(@"Hide...");
//             [app performSelector:@selector(suspend)];
//             sleep(1);
//             NSLog(@"Really Quit...");
//             exit(EXIT_SUCCESS);
//         }
//         handlerNo:nil];
//    }

}
@end
