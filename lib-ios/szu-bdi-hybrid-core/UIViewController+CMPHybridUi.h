/*
 * Extend UIViewController with
 * uiData/uiName/uiApiHandlers/uiEventHandlers
 */
#import <UIKit/UIKit.h>
#import "CMPHybridUi.h"

@interface UIViewController (CMPHybridUi)

@property (strong) JSO *uiData;
@property (strong) NSString *uiName;

@property (strong, nonatomic) NSMutableDictionary* uiApiHandlers;
@property (strong, nonatomic) NSMutableDictionary* uiEventHandlers;

@end
