
# WKWebView problem

WKWebView cannot load files using file:// URL protocol since iOS 8 beta 4 and 5
http://www.openradar.me/18039024

Fixed since iOS 9

# Cordova WKWebView Engine


This plugin makes Cordova use the WKWebView component instead of the default UIWebView component, and is installable only on a system with the iOS 9.0 SDK.

https://github.com/apache/cordova-plugin-wkwebview-engine

综上， 就是说要用得上 WKWebView的高级性能，需要至少 iOS 9。
否则的话，打不开file://文件的话，就没办法加载cordova的文件（用它所说的local webserver就太恶心了）


# react-native (RN)

假设ACE.V3/MKC.V2



# sth about block in objc

How Do I Declare A Block in Objective-C?

As a local variable:

returnType (^blockName)(parameterTypes) = ^returnType(parameters) {...};
As a property:

@property (nonatomic, copy, nullability) returnType (^blockName)(parameterTypes);
As a method parameter:

- (void)someMethodThatTakesABlock:(returnType (^nullability)(parameterTypes))blockName;
As an argument to a method call:

[someObject someMethodThatTakesABlock:^returnType (parameters) {...}];
As a typedef:

typedef returnType (^TypeName)(parameterTypes);
TypeName blockName = ^returnType(parameters) {...};
This site is not intended to be an exhaustive list of all possible uses of blocks.
If you find yourself needing syntax not listed here, it is likely that a typedef would make your code more readable.

Unable to access this site due to the profanity in the URL? http://goshdarnblocksyntax.com is a more work-friendly mirror.
By Mike Lazer-Walker, who has a very bad memory for this sort of thing.
