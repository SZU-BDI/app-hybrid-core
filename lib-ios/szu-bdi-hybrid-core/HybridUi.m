//
//  HybridUi.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "HybridUi.h"

@implementation HybridUi

- (void)setHaveTopBar:(BOOL)haveTopBar{
    
    [self.HybridUiDelegate setHaveTopBar:haveTopBar];
}

- (void)setTopBarTitle:(NSString *)title{
    
    [self.HybridUiDelegate setTopBarTitle:title];
}

- (void)setWebViewUiUrl:(NSString *)url{
    
    [self.HybridUiDelegate setWebViewUiUrl:url];
}

- (void)setCallback:(HybridCallback)callback{
    
    [self.HybridUiDelegate setCallback:callback];
}

- (void)activityClose{
    [self.HybridUiDelegate closeActivity];
}

@end
