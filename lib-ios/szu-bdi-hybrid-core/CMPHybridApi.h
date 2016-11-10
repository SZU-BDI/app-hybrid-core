//#ifndef CMPHybridUi_h
//#define CMPHybridUi_h


//#endif /* CMPHybridUi_h */


#import "CMPHybridUi.h"

@interface CMPHybridApi : NSObject

- (HybridHandler) getHandler;

@property (nonatomic, weak) CMPHybridUi *currentUi;

@end

