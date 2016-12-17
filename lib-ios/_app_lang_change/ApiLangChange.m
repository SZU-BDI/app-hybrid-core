
#import "ApiLangChange.h"

#import "CMPHybridUi.h"
#import "CMPHybridTools.h"
#import "JSO.h"


@implementation ApiLangChange

- (HybridHandler) getHandler
{
    return ^(JSO * jso, HybridCallback responseCallback) {
        
        NSString *lang = [[jso getChild:@"lang"] toString];
        if(![CMPHybridTools isEmptyString:lang]){
            [CMPHybridTools shareInstance].lang=lang;
        }
        
    };
}

@end
