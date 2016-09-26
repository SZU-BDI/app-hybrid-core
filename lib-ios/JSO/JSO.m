#import "JSO.h"

@implementation JSO

+ (id)s2id:(NSString *)s
{
	NSData *data = [s dataUsingEncoding:NSUTF8StringEncoding];
	NSError *error = nil;

	//$idid =NSJSONSerialization::JSONObjectWithData($data, $optinos=NSJSONReadingAllowFragments, &$error);
	id idid = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&error];
	if (!idid) {
		NSLog(@"%@", error.description);
	}

	return idid;
}

//TODO 增加一个可选参数 flagThrowEx【是否抛出异常， 默认值是 false】
+ (NSString *)id2s:(id)idid
{
	NSError *error;

	if (idid==nil) return @"null";
	if( [idid isKindOfClass:[NSString class]]){
		return (NSString *)idid;
	}
	@try
	{
		//$result = NSJSONSerialization::dataWithJSONObject( $idid, $options=0, &$error);//don't use NSJSONWritingPrettyPrinted
		NSData *result = [NSJSONSerialization dataWithJSONObject:idid options:0 error:&error];
		if (!result) {
			NSLog(@"%@", error.description);
			//TODO if (flagThrowEx) { 抛出异常 }
		}

		//$rt= (new String())->initWithData($result, NSUTF8StringEncoding);
		NSString *rt= [[NSString alloc] initWithData:result encoding:NSUTF8StringEncoding];
		return rt;
	}
	@catch (NSException *theException)
	{
		//TODO 判断如果是 字符串就直接返回，如果不是再看看怎么处理，下面强制转换是临时占位的，应该不够正确。。。
		NSLog(@"Exception: %@", theException);
		return (NSString *)idid;
	}

}

+ (JSO *)s2o:(NSString *)s{

	//$idid=$this->s2id($s);
	id idid=[self s2id:s];

	//$o=new JSO;
	JSO *o=[[JSO alloc] init];

	//$o->setValue("innerid",$idid);
	[o setValue:idid forKey:@"innerid"];

	return o;
}

+ (NSString *)o2s:(JSO *)o{
		id idid=[o valueForKey:@"innerid"];

		//$s=$this->id2s($idid);
		NSString *s=[self id2s:idid];

		return s;
}

- (NSString *)toString{

	//return JSO::o2s($this);
	return [JSO o2s:self];
}

- (void)fromString:(NSString *)s{

	//$idid=JSO::s2id($s);
	id idid=[JSO s2id:s];

	//$this->setValue("innerid", $idid);
	[self setValue:idid forKey:@"innerid"];
}

//TODO 临时实现的，还要优化！！！
- (JSO *)getChild:(NSString *)key{
		id idid=[self valueForKey:@"innerid"];
		id subid=[idid valueForKey:key];

		if(subid!=nil){
			//$o=new JSO;
			JSO *o=[[JSO alloc] init];

			//$o->setValue("innerid",$idid);
			[o setValue:subid forKey:@"innerid"];
			return o;
		}else{
			return nil;
		}
}

//TODO
- (void)setChild:(JSO *)jso forKey:(NSString *)key{
	//TODO
}

@end
