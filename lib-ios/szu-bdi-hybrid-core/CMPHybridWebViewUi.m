#import <Foundation/Foundation.h>
#import "CMPHybridWebViewUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@import JavaScriptCore;

@implementation CMPHybridWebViewUi

- (void)webViewDidStartLoad:(UIWebView *)webView {
    
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    //injectDone=NO;
    NSLog(@" notifyPollingInject from webViewDidStartLoad...");
    [self notifyPollingInject :webView];
    [self spinnerOn];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    NSLog(@" notifyPollingInject from webViewDidFinishLoad...");
    [self notifyPollingInject :webView];
    [self spinnerOff];
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    NSLog(@" didFailLoadWithError() %@",error);
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    [self showTopBar];
    NSLog(@" notifyPollingInject from didFailLoadWithError...");
    [self notifyPollingInject :webView];
    [self spinnerOff];
}

//----------------   <HybridUi>   -----------------

- (void) evalJs:(NSString *)js_s
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [CMPHybridTools callWebViewDoJs:self.myWebView :js_s];
    });
}

//NOTES: can be overrided
-(void) initUi
{
    [self initUi];
//    [self resetTopBarBtn];
//
//    NSString *title = [[self.uiData getChild:@"title"] toString];
//    if ([CMPHybridTools isEmptyString:title]){
//        title=@" - - - ";//TODO
//    }
//    [self on:CMPHybridEventBeforeDisplay :^(NSString *eventName, JSO *extraData) {
//        
//        NSLog(@"initUi() on eventName %@ ", eventName);
//        [self resetTopBarStatus];
//        [self setTopBarTitle:title];
//        [self setNeedsStatusBarAppearanceUpdate];
//    } :nil];
    
    [self registerHandlerApi];
    
    //[self CustomTopBarBtn];
    
    // initial the webView and add webview in window：
    //    CGRect rect = [UIScreen mainScreen].bounds;
    //
    //    self.myWebView = [[UIWebView alloc]initWithFrame:rect];
    self.myWebView=[CMPHybridTools initHybridWebView:[UIWebView class] :(HybridUi) self];
    
    //self.myWebView.backgroundColor = [UIColor whiteColor];
    //self.view.backgroundColor=[UIColor whiteColor];
    //self.myWebView.backgroundColor = [UIColor blackColor];
    
    //self.myWebView.delegate = self;// NOTES: UIWebViewDelegate, using "self" as the responder...
    
    // The page automatically zoom to fit the screen, default NO.
    self.myWebView.scalesPageToFit = YES;
    
    // Edges prohibit sliding (default YES)
    self.myWebView.scrollView.bounces = NO;
    
    self.view = self.myWebView;
    
    NSString *address = [[self.uiData getChild:@"address"] toString];
    
    [self spinnerInit];
    [self spinnerOn];
    
    [CMPHybridTools callWebViewLoadUrl:_myWebView :address];
    
}

//------------ self -----------------

- (void) notifyPollingInject :(UIWebView *)webView {
    
    [CMPHybridTools countDown:0.2 initTime:3 block:^BOOL(NSTimer *tm) {
        NSString *readyState = [webView stringByEvaluatingJavaScriptFromString:@"document.readyState"];
        
        //NSLog(@"polling ... %@", readyState);
        if (readyState != nil) {
            if (readyState.length > 0) {
                if ([readyState isEqualToString:@"loading"]) {
                }else{
                    NSString *typeof_nativejsb = [webView stringByEvaluatingJavaScriptFromString:@"(typeof nativejsb)"];
                    //NSLog(@"typeof_nativejsb=%@",typeof_nativejsb);
                    if([@"undefined" isEqualToString:typeof_nativejsb]){
                        [CMPHybridTools injectJSB :webView :self];
                        NSLog(@"done injectJSB");
                    }else{
                        return YES;//YES means stop the timer in advance
                    }
                }
            }
        }
        return NO;
    }];
}

- (void)registerHandlerApi{
    
    self.uiApiHandlers = [NSMutableDictionary dictionary];
    
    // get the appConfig:
    JSO *appConfig = [CMPHybridTools wholeAppConfig];
    
    JSO *api_mapping = [appConfig getChild:@"api_mapping"];
    
    for (NSString *kkk in [api_mapping getChildKeys]) {
        NSString *apiname = [[api_mapping getChild:kkk] toString] ;
        CMPHybridApi *api = [CMPHybridTools getHybridApi:apiname];
        api.currentUi = self;
        self.uiApiHandlers[kkk] = [api getHandler];//[[api getHandler] copy];
    }
}

//- (void) loadUrl:(NSString *)url{
//    NSURL *requesturl = [NSURL URLWithString:url];
//    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
//    [self.myWebView loadRequest:request];
//}



@end
