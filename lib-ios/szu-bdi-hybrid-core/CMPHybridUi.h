#ifndef CMPHybridUi_h
#define CMPHybridUi_h


#endif /* CMPHybridUi_h */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "CMPHybrid.h"

@protocol HybridUiProtocol
//@optional
//
//- (void)setHaveTopBar:(BOOL)haveTopBar;
//
//- (void)setTopBarTitle:(NSString *)title;
//
////TODO
//- (void)setWebViewUiUrl:(NSString *)url;
//
//- (void)setCallback:(HybridCallback)callback;
//
//- (void)closeActivity;

- (void)setHaveTopBar:(BOOL)haveTopBar;

- (void)setTopBarTitle:(NSString *)title;

- (void)setWebViewUiUrl:(NSString *)url;

- (void)setCallback:(HybridCallback)callback;

- (void)activityClose;

@end

@interface CMPHybridUi : UIViewController<HybridUiProtocol>



@end

//@protocol HybridUi <NSObject>
//
//@optional
//
//- (void)setHaveTopBar:(BOOL)haveTopBar;
//
//- (void)setTopBarTitle:(NSString *)title;
//
////TODO
//- (void)setWebViewUiUrl:(NSString *)url;
//
//- (void)setCallback:(HybridCallback)callback;
//
//- (void)closeActivity;
//
//@end
//
//
//
//@interface HybridUiBase : NSObject <HybridUi>
//
////
////@property (nonatomic, weak) id<HybridUi> HybridUiDelegate;
////
////- (void)setHaveTopBar:(BOOL)haveTopBar;
////
////- (void)setTopBarTitle:(NSString *)title;
////
////- (void)setWebViewUiUrl:(NSString *)url;
////
////- (void)setCallback:(HybridCallback)callback;
////
////- (void)activityClose;
//
//@end
