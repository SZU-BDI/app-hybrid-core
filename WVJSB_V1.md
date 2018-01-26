# WVJSB V1

### Declaration

Protocol Specification V1

```
.callHandler(handlerName, callMsg, callback(callbackMsg){
	//
});

.registerHandler(handlerName, handler(callMsg){
	//
});
```

### DataStructure (Base Suggestion)

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
