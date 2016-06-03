//
//  UiOpen.m
//  Hybrid-v2
//
//  Created by 双虎 on 16/6/3.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "UiOpen.h"

@interface UiOpen ()
@property (nonatomic, copy) NSMutableDictionary *callbackData;
@end

@implementation UiOpen

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    _callbackData = [[NSMutableDictionary alloc] initWithCapacity:0];
    [self CustomLeftBarButtonItem];
    [self LoadTheUrl:self.address];
}

//自定义BarbuttonItem（导航栏左边的-返回按钮）
-(void)CustomLeftBarButtonItem
{
    UIBarButtonItem *leftBar = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"] style:UIBarButtonItemStylePlain target:self action:@selector(leftBarItemAction)];
    leftBar.tintColor = [UIColor blackColor];
    self.navigationItem.leftBarButtonItem = leftBar;
}
-(void)leftBarItemAction{
    [_callbackData setValue:self.address forKey:@"address"];
    if (self.jsCallback) {
        self.jsCallback(_callbackData);
    }
    [self.navigationController popViewControllerAnimated:YES];
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
