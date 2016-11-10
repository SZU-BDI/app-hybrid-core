//#ifndef HybridUi_h
//#define HybridUi_h
//
//
//
//
//#endif /* HybridUi_h */


#import "HybridUi.h"

@interface HybridApi : NSObject

- (HybridHandler) getHandler;

//@property (nonatomic, weak) id<HybridUi> currentUi;

@property (nonatomic, weak) HybridUi *currentUi;

@end

