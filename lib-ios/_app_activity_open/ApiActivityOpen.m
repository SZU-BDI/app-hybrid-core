#import "ApiActivityOpen.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@interface ApiActivityOpen ()

@end

@implementation ApiActivityOpen

- (HybridHandler) getHandler{
    return ^(id data, HybridCallback responseCallback) {
        NSLog(@"ApiActivityOpen()");
        
        // JSON String -> JSO
        NSString *dataString = [JSO id2s:data];
        JSO *jso = [JSO s2o:dataString];
        
        // start UiContent
        [CMPHybridTools startUi:@"UiContent" strInitParam:jso objCaller:self.currentUi callback:responseCallback];
    };
}

@end
