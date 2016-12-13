#import "ApiUiTitle.h"
#import "CMPHybridUi.h"
#import "JSO.h"

@interface ApiUiTitle ()

@end

@implementation ApiUiTitle

- (HybridHandler) getHandler
{
    return ^(JSO * jso, HybridCallback responseCallback) {
        
        NSString *titlename = [[jso getChild:@"title"] toString];
        
        HybridUi caller=self.currentUi;
        
        [caller setTopBarTitle:titlename];
    };
}

@end
