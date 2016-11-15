#import "CMPNativeUi.h"
#import "CMPHybridTools.h"

//@interface CMPNativeUi()
//@property (nonatomic) BOOL haveTopBar;
//@property (nonatomic, strong) HybridCallback jsCallback;
//@end

@implementation CMPNativeUi

//- (instancetype)initWithNibName:(NSString *)nibNameOrNil
//                         bundle:(NSBundle *)nibBundleOrNil{
//    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
//    if (self) {
//        [self CustomLeftBarButtonItem];
//    }
//    return self;
//}

// viewWillAppear() is called before it's display.  some effect can be configurated here
- (void)viewWillAppear:(BOOL)animated{
    //self.view.backgroundColor = [UIColor grayColor];

    [self CustomTopBarBtn];
 
    NSString *mode = [[self.uiData getChild:@"topbar"] toString];
    [self CustomTopBar:mode];
    
    [super viewWillAppear:animated];
}
- (void)viewDidLoad {
    [super viewDidLoad];
}

// Custom topBar left back buttonItem
//- (void)CustomLeftBarButtonItem{
//    
//    UIBarButtonItem *leftBar = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"] style:UIBarButtonItemStylePlain target:self action:@selector(leftBarItemAction)];
//    leftBar.tintColor = [UIColor blackColor];
//    self.navigationItem.leftBarButtonItem = leftBar;
//}

//- (void)leftBarItemAction{
//    
//    // 判断是被push还是被modal出来的;
//    NSArray *viewcontrollers = self.navigationController.viewControllers;
//    
//    if (viewcontrollers.count > 1) {
//        
//        if ([viewcontrollers objectAtIndex:viewcontrollers.count-1] == self){
//            //push方式
//            [self.navigationController popViewControllerAnimated:YES];
//        }
//    }
//    else{
//        
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
//    
//    if (self.jsCallback) {
////TODO
//        //        JSO *jsoValue = [JSO s2o:self.accessAddress];
//        //        [jsoValue setChild:@"address" JSO:jsoValue];
//        //NSString *address = [NSString stringWithFormat:@"%@", self.accessAddress];
//        //self.jsCallback(@{@"address":address});
//    }
//}

//- (void)viewWillAppear:(BOOL)animated{
//    [super viewWillAppear:animated];
//    
//    if (self.haveTopBar)  [[self navigationController] setNavigationBarHidden:NO animated:YES];
//    if (!self.haveTopBar) [[self navigationController] setNavigationBarHidden:YES animated:YES];
//}
//
//#pragma mark - HybridUiDelegate
//- (void)getHaveTopBar:(BOOL)haveTopBar{
//    _haveTopBar = haveTopBar;
//}
//
//- (void)getTopBarTitle:(NSString *)title{
//    self.title = title;
//}
//
//- (void)getCallback:(HybridCallback)callback{
//    _jsCallback = callback;
//}
//
//- (void)dealloc{
//    NSLog(@"NativeUi dealloc");
//}

@end
