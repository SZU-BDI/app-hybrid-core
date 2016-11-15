#import "ApiUiClose.h"

@implementation ApiUiClose

- (HybridHandler) getHandler{
    return ^(id data, HybridCallback responseCallback) {
        [self.currentUi closeUi];
    };
}

@end
