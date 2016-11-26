#import "CMPNativeUi.h"
#import "CMPHybridTools.h"

@implementation CMPNativeUi

//------------   <HybridUi> ------------

- (void) resetTopBarBtn
{
    UIBarButtonItem *leftBar
    = [[UIBarButtonItem alloc]
       initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"]//see Images.xcassets
       style:UIBarButtonItemStylePlain
       target:self
       action:@selector(closeUi) //on('click')=>close()
       ];
    leftBar.tintColor = [UIColor blueColor];
    
    //    self.navigationItem.leftBarButtonItem
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
    //       target:self
    //       action:@selector(closeUi)];
    //
    UIBarButtonItem *rightBtn
    = [[UIBarButtonItem alloc]
       initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:nil];
    self.navigationItem.rightBarButtonItem = rightBtn;
}

- (void) evalJs :(NSString *)js_s
{
    NSLog(@"NativeUi: TODO evalJs()");
}

//NOTES: can be overrided
- (void)initUi
{
    [self on:CMPHybridEventBeforeDisplay :^(NSString *eventName, JSO *extraData) {
        
        NSLog(@"initUi() on eventName %@ ", eventName);
        [self resetTopBarStatus];
        [self resetTopBarBtn];
        [self setNeedsStatusBarAppearanceUpdate];
    } :nil];
    
    //[self resetTopBarBtn];
    self.view.backgroundColor=[UIColor blackColor];
    //self.navigationController.navigationBar.translucent=NO;
    self.navigationController.navigationBar.backgroundColor=[UIColor blackColor];
    self.navigationController.navigationBar.tintColor = [UIColor brownColor];
}

@end
