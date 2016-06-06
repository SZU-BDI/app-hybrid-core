//
//  UiOpen.m
//  Hybrid-v2
//
//  Created by 双虎 on 16/6/3.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "UiOpen.h"

@interface UiOpen ()

@end

@implementation UiOpen

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self LoadTheUrl:self.address];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:NO];
    // 当 self.isTopBar = YES ,则显示topbar
    if (self.isTopBar == YES) {
        [[self navigationController] setNavigationBarHidden:NO animated:YES];
    }else{
        [[self navigationController] setNavigationBarHidden:YES animated:YES];
    }
}

@end
