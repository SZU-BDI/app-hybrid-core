//
//  UiContent.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/3.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "UiContent.h"

@interface UiContent ()

@end

@implementation UiContent

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
//    [self LoadTheUrl:self.address];
    NSLog(@"UiContent 初始化完毕");
    if (self.isTopBar == YES) {
        [self LoadLocalhtmlName:@"root"];
    }else{
        [self LoadTheUrl:self.address];
    }
}

- (void)didReceiveMemoryWarning {
    // Dispose of any resources that can be recreated.
    [super didReceiveMemoryWarning];
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:NO];
    //  if (self.isTopBar = NO) activity hidden topbar
    if (self.isTopBar == YES) {
        [[self navigationController] setNavigationBarHidden:NO animated:YES];
    }else{
        [[self navigationController] setNavigationBarHidden:YES animated:YES];
    }
}

- (void)dealloc{
    NSLog(@"UiRoot dealloc");
}

@end
