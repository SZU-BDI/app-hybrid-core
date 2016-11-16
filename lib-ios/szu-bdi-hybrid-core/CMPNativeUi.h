#ifndef CMPNativeUi_h
#define CMPNativeUi_h

@import JavaScriptCore;

#import "CMPHybridUi.h"

@interface CMPNativeUi : CMPHybridUi

//for future, evalJs will be supported ;)
- (JSValue *) evalJs :(NSString *)js_s;

@end

#endif /* CMPNativeUi_h */
