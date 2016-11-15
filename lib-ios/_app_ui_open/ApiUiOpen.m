#import "ApiUiOpen.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@implementation ApiUiOpen

- (HybridHandler) getHandler{
    return ^(id data, HybridCallback responseCallback) {
        NSLog(@"ApiActivityOpen()");
        
        // JSON String -> JSO
        NSString *dataString = [JSO id2s:data];
        JSO *jso = [JSO s2o:dataString];
        
        // start UiContent
        CMPHybridUi *rt=[CMPHybridTools startUi:@"UiContent" initData:jso objCaller:self.currentUi
                         //callback:responseCallback
                         ];
        if(rt!=nil){
            if(nil!=responseCallback){
                [rt on:@"close" :^(NSString *eventName, id extraData){
                    responseCallback(extraData);
                }];
            }else{
                [rt on:@"close" :^(NSString *eventName, id extraData){
                    //responseCallback(extraData);
                    NSLog(@" ui trigger close but no responseCallback here...");
                }];
            }
        }
        
    };
}

@end
