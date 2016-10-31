//
//  ApiActivityClose.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/3.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "ApiActivityClose.h"
#import "HybridUi.h"

@interface ApiActivityClose ()
@end

@implementation ApiActivityClose

- (WVJBHandler) getHandler{
    return ^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ApiActivityClose()");
        
        HybridUi *ui = [[HybridUi alloc] init];
        ui.delegate = self.currentUi;
        [ui activityClose];
        
//        HybridUi *ui = (HybridUi *)self.currentUi;
//        if (ui.address) {
//            _callbackData = [[NSDictionary alloc] initWithObjects:@[ui.address] forKeys:@[@"address"]];
//        }
//        if (ui.jsCallback) {
//            ui.jsCallback(_callbackData);
//        }
//        if (self.currentUi.navigationController.viewControllers.count > 1){
//            NSLog(@"colse %@", self.currentUi);
//            [self.currentUi.navigationController popViewControllerAnimated:YES];
//        }
    };
}

@end
