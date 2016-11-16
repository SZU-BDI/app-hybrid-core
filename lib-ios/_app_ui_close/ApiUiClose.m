#import "ApiUiClose.h"
#import "JSO.h"

@implementation ApiUiClose

- (HybridHandler) getHandler{
    return ^(JSO *ddd, HybridCallback responseCallback) {
        dispatch_async(dispatch_get_main_queue(), ^{
            
            [self.currentUi closeUi];
            responseCallback([JSO id2o:@{@"STS":@"OK"}]);
        });
    };
}

@end
