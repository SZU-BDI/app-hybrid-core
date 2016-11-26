#import "CMPHybridUi_UIViewController.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation CMPHybridUi_UIViewController

//override:

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

//---------------- override ----------
- (BOOL)prefersStatusBarHidden {
    return NO;
}

-(UIStatusBarStyle)preferredStatusBarStyle{
    return UIStatusBarStyleLightContent;
    //return UIStatusBarStyleDefault;
}

//-(void) initUi
//{
//    NSLog(@"initUi() must be overrided!!!");
//}
//- (void)closeUi
//{
//    NSLog(@"closeUi() must be overrided!!!");
//}
@end
