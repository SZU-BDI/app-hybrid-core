//#ifndef CMPHybridUi_h
//#define CMPHybridUi_h



#import "CMPHybridUi.h"

@interface CMPHybridApi : NSObject

- (HybridHandler) getHandler;

@property (nonatomic, weak) id<CMPHybridUi> currentUi;

@end








//#endif /* CMPHybridUi_h */


