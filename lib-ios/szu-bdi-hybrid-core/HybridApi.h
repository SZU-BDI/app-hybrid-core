//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.

//#import <UIKit/UIKit.h>

#import "HybridUi.h"

@interface HybridApi : NSObject

//get callback handler
- (WVJBHandler) getHandler;

@property (nonatomic, weak) id<HybridUi> currentUi;
//@property (nonatomic, strong) id<HybridUi> currentUi;

@end
