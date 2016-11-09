#import <UIKit/UIKit.h>
#import "HybridUi.h"

@interface SimpleWebViewUi : UIViewController<HybridUi,UIWebViewDelegate>

@property (nonatomic, strong) UIWebView *webView;



@end

//UIWebViewDelegate
//@optional
//- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType;
//- (void)webViewDidStartLoad:(UIWebView *)webView;
//- (void)webViewDidFinishLoad:(UIWebView *)webView;
//- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error;
