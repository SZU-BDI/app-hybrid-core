#import <UIKit/UIKit.h>
#import "CMPHybridUi.h"

@interface CMPHybridUi_UIViewController : UIViewController <CMPHybridUi>
{
    
}

@property (strong, nonatomic) JSO *uiData;
@property (strong, nonatomic) NSString *uiName;

@property (strong, nonatomic) NSMutableDictionary* uiApiHandlers;
@property (strong, nonatomic) NSMutableDictionary* uiEventHandlers;

@end
