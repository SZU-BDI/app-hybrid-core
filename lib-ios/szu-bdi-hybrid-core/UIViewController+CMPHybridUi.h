/*
 * Extend UIViewController with
 * uiData/uiName/uiApiHandlers/uiEventHandlers
 */
//#import <UIKit/UIKit.h>
#import "CMPHybridUi.h"
#import "JSO.h"

@interface UIViewController (CMPHybridUi)

@property (strong) JSO *uiData;
@property (strong) NSString *uiName;

@property (strong) JSO *responseData;//data for close

@property (strong, nonatomic) NSMutableDictionary* uiApiHandlers;
@property (strong, nonatomic) NSMutableDictionary* uiEventHandlers;

@end
