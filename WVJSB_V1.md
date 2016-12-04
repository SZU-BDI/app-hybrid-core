# WVJSB V1

## from WebView Js => Wrapper(MobileApp/DesktopApp/etc)

### Declaration

WebViewJavascript Protocol Specification V1

```
.callHandler(handlerName, data, callback(responseData){
	//
});

registerHandler(handlerName, handler(data){
	//
});
```

### Implementation DataStructure (Suggestion)

```
callMsg:{
	callbackId //generated
	callTime //for housekeeping and benchmark
	data // JSON Object/Array/null, suggest not primitive
}

callbackMsg:{
	responseId //link to callbackId
	responseData // JSON Object/Array/null.  suggest not primitive
}
```
