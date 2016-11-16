#ifndef CMPHybridWebViewUi_h
#define CMPHybridWebViewUi_h

@import JavaScriptCore;

#import "CMPHybridUi.h"

@interface CMPHybridWebViewUi : CMPHybridUi <UIWebViewDelegate>

@property (nonatomic, strong) UIWebView * myWebView;

-(void) loadUrl :(NSString *)url;

//using default myWebView to do js...
//- (void) evalJs :(NSString *)js_s;

@end


#endif /* CMPHybridWebViewUi_h */
