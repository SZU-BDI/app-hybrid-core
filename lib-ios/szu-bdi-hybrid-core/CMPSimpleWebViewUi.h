#ifndef CMPSimpleWebViewUi_h
#define CMPSimpleWebViewUi_h


#endif /* SimpleWebViewUi_h */

#import <UIKit/UIKit.h>
#import "CMPHybridUi.h"

@interface CMPSimpleWebViewUi : CMPHybridUi <UIWebViewDelegate>

//public:
@property (nonatomic, strong) UIWebView *webView;

@end
