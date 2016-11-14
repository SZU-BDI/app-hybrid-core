#ifndef CMPHybridWebViewUi_h
#define CMPHybridWebViewUi_h
@import JavaScriptCore;

#endif /* CMPHybridWebViewUi_h */

#import "CMPHybridUi.h"

@interface CMPHybridWebViewUi : CMPHybridUi <UIWebViewDelegate>

@property (nonatomic, strong) UIWebView * myWebView;

-(void) loadUrl :(NSString *)url;

//using default myWebView to do js...
- (JSValue *) evalJs :(NSString *)js_s;

@end
