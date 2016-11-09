#import "ApiActivityClose.h"
#import "HybridUi.h"

@interface ApiActivityClose ()

@end

@implementation ApiActivityClose

- (HybridHandler) getHandler{
    return ^(id data, HybridCallback responseCallback) {
        NSLog(@"ApiActivityClose()");
        
        HybridUi *hybridUi = [[HybridUi alloc] init];
        hybridUi.HybridUiDelegate = self.currentUi;
        [hybridUi activityClose];
    };
}

@end
