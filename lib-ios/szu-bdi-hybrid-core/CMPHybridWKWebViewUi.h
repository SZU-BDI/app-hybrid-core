#ifndef CMPHybridWKWebViewUi_h
#define CMPHybridWKWebViewUi_h

#import "CMPHybridUi.h"

@interface CMPHybridWKWebViewUi : CMPHybridUi <WKNavigationDelegate, WKUIDelegate, WKScriptMessageHandler>
{
@protected UIActivityIndicatorView * _myIndicatorView;
    
}

@property (nonatomic, strong) WKWebView * myWebView;

-(void) loadUrl :(NSString *)url;

@end

#endif /* CMPHybridWKWebViewUi_h */
