//
//  UiRoot.m
//  Hybrid-v2
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
    NSLog(@"viewDidLoad2");
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:NO];
    [[self navigationController] setNavigationBarHidden:YES animated:YES];
}

@end
