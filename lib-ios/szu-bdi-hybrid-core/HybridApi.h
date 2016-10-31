//
//  HybridApi.h
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

//#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "HybridUi.h"
//#import "WebViewJavascriptBridgeBase.h"

@interface HybridApi : NSObject

/**
 *   获取js注册方法的回调
 */
- (WVJBHandler) getHandler;

/**
 *   当前显示的Ui
 */
@property (nonatomic, weak) id<HybridUi> currentUi;

//@property (nonatomic, strong) UIViewController *currentUi;

@end
