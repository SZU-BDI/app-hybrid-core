#ifndef Hybrid_h
#define Hybrid_h

typedef void (^HybridCallback)(id responseData);

typedef void (^HybridHandler)(id data, HybridCallback responseCallback);

#endif /* Hybrid_h */

#ifndef CMPHybridUi_h
#define CMPHybridUi_h


#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@protocol HybridUiProtocol

@optional

- (void)setHaveTopBar:(BOOL)haveTopBar;

- (void)setTopBarTitle:(NSString *)title;

- (void)setWebViewUiUrl:(NSString *)url;

- (void)setCallback:(HybridCallback)callback;

- (void)activityClose;

@end

@interface CMPHybridUi : UIViewController<HybridUiProtocol>



@end



#endif /* CMPHybridUi_h */
