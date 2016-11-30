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

#warning TODO (1) TODO the height is not good...
- (void) spinnerInit
{
    //INIT SPIN
    //UIActivitymyIndicatorView *
    _myIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    _myIndicatorView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    _myIndicatorView.color =[UIColor whiteColor];
    _myIndicatorView.layer.cornerRadius = 5;
    _myIndicatorView.layer.masksToBounds = TRUE;
    _myIndicatorView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleBottomMargin;
    
    _myIndicatorView.translatesAutoresizingMaskIntoConstraints = NO;
    [_myIndicatorView setHidesWhenStopped:YES];
    _myIndicatorView.center=self.view.center;
    [self.view addSubview:_myIndicatorView];
}


- (void) spinnerOn
{
    [_myIndicatorView startAnimating];
}
- (void) spinnerOff
{
    [_myIndicatorView stopAnimating];
}

@end
