#import <Foundation/Foundation.h>
#import "CMPHybridWebViewUi.h"
#import "CMPHybridApi.h"
#import "CMPHybridTools.h"
#import "JSO.h"

@import JavaScriptCore;

@implementation CMPHybridWebViewUi

UIActivityIndicatorView *IndicatorView;

//------------  UIViewController ------------


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
    [self registerHandlerApi];
    
    [self CustomTopBarBtn];
    
    // initial the webView and add webview in window：
    CGRect rect = [UIScreen mainScreen].bounds;
    
    self.myWebView = [[UIWebView alloc]initWithFrame:rect];
    
    //self.myWebView.backgroundColor = [UIColor whiteColor];
    self.myWebView.backgroundColor = [UIColor blackColor];
    
    self.myWebView.delegate = self;// NOTES: UIWebViewDelegate, using "self" as the responder...
    
    // The page automatically zoom to fit the screen, default NO.
    self.myWebView.scalesPageToFit = YES;
    
    // Edges prohibit sliding (default YES)
    self.myWebView.scrollView.bounces = NO;
    
    self.view = self.myWebView;
    
    NSString *address = [[self.uiData getChild:@"address"] toString];
    NSURL *address_url = [NSURL URLWithString:address];
    NSString *scheme_s=[address_url scheme];
    
    [self spinnerInit];
    
    if( [ CMPHybridTools isEmptyString:scheme_s ])
    {
        [self loadUrl:[@"file://" stringByAppendingString:[CMPHybridTools fullPathOfAsset:address]]];
    }else{
        [self loadUrl:[address_url absoluteString]];
    }
    
}
//NOTES: can be overrided
- (void) CustomTopBarBtn
{
    UIBarButtonItem *leftBar
    = [[UIBarButtonItem alloc]
       initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"]//see Images.xcassets
       style:UIBarButtonItemStylePlain
       target:self
       action:@selector(closeUi) //on('click')=>close()
       ];
    leftBar.tintColor = [UIColor blueColor];
    
    //    self.navigationItem.leftBarButtonItem
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
    //       target:self
    //       action:@selector(closeUi)];
    
    //    UIBarButtonItem *rightBtn
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:nil];
    //    self.navigationItem.rightBarButtonItem = rightBtn;
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

- (void) spinnerOn
{
    [IndicatorView startAnimating];
}
- (void) spinnerOff
{
    [IndicatorView stopAnimating];
}

- (void)registerHandlerApi{
    
    self.myApiHandlers = [NSMutableDictionary dictionary];
    
    // get the appConfig:
    JSO *appConfig = [CMPHybridTools wholeAppConfig];
    
    JSO *api_mapping = [appConfig getChild:@"api_mapping"];
    
    for (NSString *kkk in [api_mapping getChildKeys]) {
        NSString *apiname = [[api_mapping getChild:kkk] toString] ;
        CMPHybridApi *api = [CMPHybridTools getHybridApi:apiname];
        api.currentUi = self;
        self.myApiHandlers[kkk] = [api getHandler];//[[api getHandler] copy];
    }
}

- (void) loadUrl:(NSString *)url{
    NSURL *requesturl = [NSURL URLWithString:url];
    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
    [self.myWebView loadRequest:request];
}


- (void) spinnerInit
{
    //INIT SPIN
    //UIActivityIndicatorView *
    IndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    IndicatorView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    IndicatorView.color =[UIColor whiteColor];
    IndicatorView.layer.cornerRadius = 5;
    IndicatorView.layer.masksToBounds = TRUE;
    IndicatorView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleBottomMargin;
    
    IndicatorView.translatesAutoresizingMaskIntoConstraints = NO;
    [IndicatorView setHidesWhenStopped:YES];
    IndicatorView.center=self.view.center;
    [self.view addSubview:IndicatorView];
    
}


@end
