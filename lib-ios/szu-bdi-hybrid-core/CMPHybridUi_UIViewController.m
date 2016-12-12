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
    
#warning (7) TODO move to WKWebview/UIWebview override viewDidLoad()
#warning (7) TODO make multi on/trigger and send event into the webview for pause/resume event.
    //[self on:<#(NSString *)#> :<#^(NSString *eventName, JSO *extraData)handler#>];

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

- (void) spinnerInit
{
    //INIT SPIN
    //UIActivitymyIndicatorView *
    _myIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    _myIndicatorView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    _myIndicatorView.color =[UIColor whiteColor];
    _myIndicatorView.layer.cornerRadius = 5;
    _myIndicatorView.layer.masksToBounds = TRUE;
    
    _myIndicatorView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin
    //| UIViewAutoresizingFlexibleWidth
    | UIViewAutoresizingFlexibleRightMargin
    | UIViewAutoresizingFlexibleTopMargin
    //| UIViewAutoresizingFlexibleHeight
    | UIViewAutoresizingFlexibleBottomMargin
    ;
    
    
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
