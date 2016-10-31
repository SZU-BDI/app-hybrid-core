//
//  WebViewUi.h
//  testproj-ios-core
//
//  Created by 大数据 on 16/6/13.
//  Copyright © 2016年 szu.bdi. All rights reserved.
//

#import "HybridUi.h"
#import "WebViewJavascriptBridge.h"

@interface WebViewUi : HybridUi

@property (nonatomic, strong) WebViewJavascriptBridge *bridge;

- (void)LoadLocalhtmlName:(NSString *)loadLocalhtml;
- (void)LoadTheUrl:(NSString *)url;

@end
