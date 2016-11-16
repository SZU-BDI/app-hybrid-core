#import "ApiUiClose.h"
#import "JSO.h"

@implementation ApiUiClose

- (HybridHandler) getHandler{
    return ^(JSO *ddd, HybridCallback responseCallback) {
        [self.currentUi closeUi];
        responseCallback([JSO id2o:@{@"STS":@"OK"}]);
    };
}

@end
