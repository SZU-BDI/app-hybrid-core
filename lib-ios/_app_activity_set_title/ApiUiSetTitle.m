#import "ApiUiSetTitle.h"
#import "CMPHybridUi.h"
#import "JSO.h"

@interface ApiUiSetTitle ()

@end

@implementation ApiUiSetTitle

- (HybridHandler) getHandler
{
    return ^(JSO * jso, HybridCallback responseCallback) {
        
        NSLog(@"ApiUiSetTitle()");
        
        NSString *titlename = [[jso getChild:@"title"] toString];
        
        HybridUi caller=self.currentUi;
        
        [caller setTopBarTitle:titlename];
    };
}

@end
