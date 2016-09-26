#import <Foundation/Foundation.h>

@interface JSO : NSObject{
	id innerid;
}

//$id=JSO::s2id($s);
+ (id)s2id:(NSString *)s;

//$s=JSO::id2s($id);
+ (NSString *)id2s:(id)id;

//$o=JSO::s2o($s);
+ (JSO *)s2o:(NSString *)s;

//$s=JSO::o2s($o);
+ (NSString *)o2s:(JSO *)o;

//$s=$o->toString();
- (NSString *)toString;

//$o->fromString($s);//@o->innerid=JSO::s2id($s);
- (void)fromString:(NSString *)s;

//$sub_o=$o->getChild($k);
- (JSO *)getChild:(NSString *)key;

//$o->setChild($k,$o);
- (void)setChild:(JSO *)jso forKey:(NSString *)key;

@end
