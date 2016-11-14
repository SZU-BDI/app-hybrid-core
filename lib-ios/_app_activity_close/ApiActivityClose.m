#import "ApiActivityClose.h"

@interface ApiActivityClose ()

@end

@implementation ApiActivityClose

- (HybridHandler) getHandler{
    return ^(id data, HybridCallback responseCallback) {
        [self.currentUi close];
    };
}

@end
