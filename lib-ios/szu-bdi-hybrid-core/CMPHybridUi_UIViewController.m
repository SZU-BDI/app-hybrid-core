#import "CMPHybridUi_UIViewController.h"

@implementation CMPHybridUi_UIViewController

//---------------- UIViewController: ----------

-(void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self trigger:CMPHybridEventBeforeDisplay];
}

-(void) viewDidLoad
{
    [super viewDidLoad];
    [self initUi];
}

- (BOOL)prefersStatusBarHidden {
    NSLog(@"CMPHybridUi_UIViewController prefersStatusBarHidden returns NO");
    return NO;
}

-(UIStatusBarStyle)preferredStatusBarStyle{
    //return UIStatusBarStyleLightContent;
    NSLog(@"CMPHybridUi_UIViewController preferredStatusBarStyle returns UIStatusBarStyleDefault");
    return UIStatusBarStyleDefault;
}

//---------------- Special: ----------

@end
