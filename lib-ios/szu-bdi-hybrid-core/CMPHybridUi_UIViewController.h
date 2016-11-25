#import <UIKit/UIKit.h>
#import "CMPHybridUi.h"

@interface CMPHybridUi_UIViewController : UIViewController <CMPHybridUi>


@property (strong) JSO *uiData;
@property (strong) NSString *uiName;

@property (strong, nonatomic) NSMutableDictionary* myApiHandlers;
@property (strong, nonatomic) NSMutableDictionary* myEventHandlers;


@end
