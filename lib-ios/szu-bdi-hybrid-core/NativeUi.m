//
//  NativeUi.m
//  testproj-ios-core
//
//  Created by 大数据 on 16/6/13.
//  Copyright © 2016年 szu.bdi. All rights reserved.
//

#import "NativeUi.h"

@interface NativeUi ()
@property (nonatomic) BOOL haveTopBar;
@property (nonatomic, strong) WVJBResponseCallback jsCallback;
@end

@implementation NativeUi

- (instancetype)initWithNibName:(NSString *)nibNameOrNil
                         bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [self CustomLeftBarButtonItem];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor grayColor];
}

// Custom topBar left back buttonItem
- (void)CustomLeftBarButtonItem{
    
    UIBarButtonItem *leftBar = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"] style:UIBarButtonItemStylePlain target:self action:@selector(leftBarItemAction)];
    leftBar.tintColor = [UIColor blackColor];
    self.navigationItem.leftBarButtonItem = leftBar;
}

- (void)leftBarItemAction{
    
    //    if (self.address) {
    //        _callbackData = [[NSDictionary alloc] initWithObjects:@[self.address] forKeys:@[@"address"]];
    //    }
    
    // 判断是被push还是被modal出来的;
    NSArray *viewcontrollers = self.navigationController.viewControllers;
    
    if (viewcontrollers.count > 1) {
        
        if ([viewcontrollers objectAtIndex:viewcontrollers.count-1] == self){
            
            //push方式
            [self.navigationController popViewControllerAnimated:YES];
        }
    }
    else{
        
        // present方式
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    
    if (self.jsCallback) {
        self.jsCallback(@"Native");
    }
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if (self.haveTopBar)  [[self navigationController] setNavigationBarHidden:NO animated:YES];
    if (!self.haveTopBar) [[self navigationController] setNavigationBarHidden:YES animated:YES];
}

#pragma mark - HybridUiDelegate
- (void)getHaveTopBar:(BOOL)haveTopBar{
    _haveTopBar = haveTopBar;
}

- (void)getTopBarTitle:(NSString *)title{
    self.title = title;
}

- (void)getCallback:(WVJBResponseCallback)callback{
    _jsCallback = callback;
}

- (void)dealloc{
    NSLog(@"NativeUi dealloc");
}

@end
