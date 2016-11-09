#import <Foundation/Foundation.h>
#import "Hybrid.h"

// 协议定义
@protocol HybridUi <NSObject>

@optional

- (void)setHaveTopBar:(BOOL)haveTopBar;

- (void)setTopBarTitle:(NSString *)title;

//TODO
- (void)setWebViewUiUrl:(NSString *)url;

- (void)setCallback:(HybridCallback)callback;

- (void)closeActivity;

@end

@interface HybridUi : NSObject

// 遵循协议的一个代理变量定义
@property (nonatomic, weak) id<HybridUi> HybridUiDelegate;

- (void)setHaveTopBar:(BOOL)haveTopBar;

- (void)setTopBarTitle:(NSString *)title;

- (void)setWebViewUiUrl:(NSString *)url;

- (void)setCallback:(HybridCallback)callback;

- (void)activityClose;

@end
