//
//  PushViewController.h
//  Hybrid-core
//
//  Created by 双虎 on 16/5/31.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "WebBaseViewController.h"
#import "WebViewJavascriptBridgeBase.h"

@interface PushViewController : WebBaseViewController

@property (nonatomic) BOOL isTopBar;
@property (nonatomic, copy) NSString *address;
@property (nonatomic, strong) WVJBResponseCallback jsCallback;

@end
