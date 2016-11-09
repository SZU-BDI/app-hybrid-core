//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.

#import "HybridUi.h"

@interface HybridApi : NSObject

//get callback handler
- (HybridHandler) getHandler;

@property (nonatomic, weak) id<HybridUi> currentUi;

@end
