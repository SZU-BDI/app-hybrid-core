//
//  ApiActivitySetTitle.m
//  AsiaWeiLuy
//
//  Created by 双虎 on 16/8/29.
//  Copyright © 2016年 Megadata. All rights reserved.
//

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

//        [_callbackData setValue:titlename forKey:@"title"];
//        responseCallback(_callbackData);
    };
}

@end
