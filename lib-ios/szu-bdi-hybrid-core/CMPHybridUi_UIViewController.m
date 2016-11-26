#import "CMPHybridUi_UIViewController.h"
//#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridUi_UIViewController

//---------------- UIViewController: ----------

-(void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //[self setNeedsStatusBarAppearanceUpdate];
    
    [self restoreTopBarStatus];
}

-(void) viewDidLoad
{
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
