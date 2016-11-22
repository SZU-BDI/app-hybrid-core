//
//  CMPHybridWKWebViewUi.h
//  iosace
//
//  Created by wanjochan on 21/11/16.
//  Copyright © 2016 szu.bdi+cmptech+megatech. All rights reserved.
//

#ifndef CMPHybridWKWebViewUi_h
#define CMPHybridWKWebViewUi_h

#import "CMPHybridUi.h"

//#import <WebKit/WebKit.h>

@import JavaScriptCore;

@interface CMPHybridWKWebViewUi : CMPHybridUi <WKNavigationDelegate,WKUIDelegate,WKScriptMessageHandler>

@property (nonatomic, strong) WKWebView * myWebView;

-(void) loadUrl :(NSString *)url;

@end

#endif /* CMPHybridWKWebViewUi_h */
