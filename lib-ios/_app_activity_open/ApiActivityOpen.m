//
//  ApiActivityOpen.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "ApiActivityOpen.h"
#import "HybridTools.h"
#import "JSO.h"

@interface ApiActivityOpen ()

@end

@implementation ApiActivityOpen

- (WVJBHandler) getHandler{
    return ^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ApiActivityOpen()");
        
        // JSON String -> JSO
        NSString *dataString = [JSO id2s:data];
        JSO *jso = [JSO s2o:dataString];
        
        // start UiContent
        [HybridTools startUi:@"UiContent" strInitParam:jso objCaller:self.currentUi callback:responseCallback];
    };
}

@end
