#import <UIKit/UIKit.h>
#import "CMPHybridUi.h"

@interface CMPHybridUi_UIViewController : UIViewController <CMPHybridUi>
{
@protected UIActivityIndicatorView * _myIndicatorView;
}

@property (strong, nonatomic) JSO *uiData;
@property (strong, nonatomic) NSString *uiName;

@property (strong, nonatomic) JSO *responseData;

@property (strong, nonatomic) NSMutableDictionary* uiApiHandlers;
@property (strong, nonatomic) NSMutableDictionary* uiEventHandlers;

- (void) spinnerInit;
- (void) spinnerOn;
- (void) spinnerOff;

@end
