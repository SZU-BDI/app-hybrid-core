#import "ApiActivityClose.h"
#import "CMPHybridUi.h"

@interface ApiActivityClose ()

@end

@implementation ApiActivityClose

- (HybridHandler) getHandler{
    return ^(id data, HybridCallback responseCallback) {
        NSLog(@"ApiActivityClose()");
#warning todo
//        HybridUi *hybridUi = [[HybridUi alloc] init];
//        hybridUi.HybridUiDelegate = self.currentUi;
//        [hybridUi activityClose];
        //[self.currentUi closeActivity];
    };
}

@end
