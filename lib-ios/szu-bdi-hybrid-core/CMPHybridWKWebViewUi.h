#ifndef CMPHybridWKWebViewUi_h
#define CMPHybridWKWebViewUi_h

#import "CMPHybridUi.h"
#import "CMPHybridUi_UIViewController.h"

#import <WebKit/WebKit.h>

@interface CMPHybridWKWebViewUi :CMPHybridUi_UIViewController <WKNavigationDelegate, WKUIDelegate>
{
}
@property (nonatomic, strong) WKWebView * myWebView;


@end

#endif /* CMPHybridWKWebViewUi_h */
