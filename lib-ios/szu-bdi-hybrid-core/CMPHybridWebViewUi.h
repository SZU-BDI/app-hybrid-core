#ifndef CMPHybridWebViewUi_h
#define CMPHybridWebViewUi_h

@import JavaScriptCore;

#import "CMPHybridUi.h"

@interface CMPHybridWebViewUi : CMPHybridUi <UIWebViewDelegate>

@property (nonatomic, strong) UIWebView * myWebView;

-(void) loadUrl :(NSString *)url;

@end


#endif /* CMPHybridWebViewUi_h */
