//
//  HybridUi.m
//  testproj-ios-core
//
//  Created by 双虎 on 16/6/2.
//  Copyright © 2016年 Cmptech. All rights reserved.
//

#import "HybridUi.h"

@implementation HybridUi

- (void)setHaveTopBar:(BOOL)haveTopBar{
    
    [self.delegate getHaveTopBar:haveTopBar];
}

- (void)setTopBarTitle:(NSString *)title{
    
    [self.delegate getTopBarTitle:title];
}

- (void)setWebViewUiUrl:(NSString *)url{
    
    [self.delegate getWebViewUiUrl:url];
}

- (void)setCallback:(WVJBResponseCallback)callback{
    
    [self.delegate getCallback:callback];
}

- (void)activityClose{
    [self.delegate closeActivity];
}

@end

//@interface HybridUi ()
//
//@end
//
//@implementation HybridUi
//
//- (instancetype)initWithNibName:(NSString *)nibNameOrNil
//                         bundle:(NSBundle *)nibBundleOrNil{
//    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
//    if (self) {
//        self.topBar  = [[UINavigationController alloc] initWithRootViewController:self];
//    }
//    return self;
//}
//
//- (void)viewDidLoad {
//    [super viewDidLoad];
//    // Do any additional setup after loading the view.
//}
//
//- (void)didReceiveMemoryWarning {
//    [super didReceiveMemoryWarning];
//    // Dispose of any resources that can be recreated.
//}
//
//@end
