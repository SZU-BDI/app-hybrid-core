//
//  UiRoot.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "UiRoot.h"

@interface UiRoot ()

@end

@implementation UiRoot

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self LoadLocalhtmlName:@"root"];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:NO];
    // Root activity hidden topbar
    [[self navigationController] setNavigationBarHidden:YES animated:YES];
}

@end
