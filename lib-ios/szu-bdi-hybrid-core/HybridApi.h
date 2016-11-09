#import "HybridUi.h"

@interface HybridApi : NSObject

- (HybridHandler) getHandler;

@property (nonatomic, weak) id<HybridUi> currentUi;

@end
