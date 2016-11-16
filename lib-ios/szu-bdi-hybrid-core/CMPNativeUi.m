#import "CMPNativeUi.h"
#import "CMPHybridTools.h"

@implementation CMPNativeUi

//@overrided
- (void) CustomTopBarBtn
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

- (JSValue *) evalJs :(NSString *)js_s
{
    return nil;
}
@end
