#ifndef SimpleWebViewUi_h
#define SimpleWebViewUi_h


#endif /* SimpleWebViewUi_h */

#import <UIKit/UIKit.h>
#import "HybridUi.h"

@interface SimpleWebViewUi : HybridUi <UIWebViewDelegate>

//public:
@property (nonatomic, strong) UIWebView *webView;

@end
