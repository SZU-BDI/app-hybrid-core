#import "CMPHybridApi.h"

@implementation CMPHybridApi

//please overrided by decendence

- (HybridHandler) getHandler{
    return ^(JSO *ddd, HybridCallback responseCallback) {
        dispatch_async(dispatch_get_main_queue(), ^{
            //self.currentUi.responseData=ddd;
            //[self.currentUi closeUi];
            if(nil!=responseCallback)
                responseCallback(ddd);
        });
    };
}

@end
