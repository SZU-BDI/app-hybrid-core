(function(win,doc){
	if (win.WebViewJavascriptBridge) {
		return;
	}
	function s2o(s){try{return(new Function('return '+s))();}catch(ex){}};function o2s(o){return JSON.stringify(o);};
	var responseCallbacks = {};
	var msgHandlerSet={};
	var msgId = 1;

	//send msg{handlerName:$n,data:$jsonObj} to app
	function _js2app(msg, cb){
		if (cb) {
			msgId=(msgId + 1) % 1000000;
			var callTime=new Date().getTime();
			var callbackId = 'cb_' + msgId + '_' + callTime;
			cb.time=callTime;
			responseCallbacks[callbackId] = cb;
			msg.callbackId = callbackId;
		}
		if("undefined"!=typeof nativejsb){
			return nativejsb.js2app(msg.callbackId,msg.handlerName,o2s(msg.data));
		}else if("undefined"!=typeof window.webkit.messageHandlers.nativejsb){
			window.webkit.messageHandlers.nativejsb.postMessage(msg);
		}else{
			alert("ERROR nativejsb is missing");
		}
	}

	function _gc(){
		if(responseCallbacks){
			var nowTime=new Date().getTime();
			for(k in responseCallbacks){
				var cb=responseCallbacks[k];
				//alert("TODO "+(nowTime-cb.time));
				if( (nowTime-cb.time)>1800000 ){
					responseCallbacks[msg.responseId]=null;
					delete responseCallbacks[msg.responseId];
				}
			}
		}
	}

	//handle msg from app
	function _app2js(msg){
		var _this=this;
		setTimeout(function(){
			var callback=null;
			if (msg.responseId) {
				//this msg is a "Reply", so find the original callback
				callback = responseCallbacks[msg.responseId];
				if (!callback) { return; }
				callback(msg.responseData);
				try{
					responseCallbacks[msg.responseId]=null;
					delete responseCallbacks[msg.responseId];
					_gc();
				}catch(ex){console.log(ex);}
			} else {
				var handler = null;
				if (msg.handlerName) {
					handler = msgHandlerSet[msg.handlerName];
					if(handler==null){
						console.log("WebViewJavascriptBridge: not found handler name="+msg.handlerName);
					}else{
						try {
							if (msg.callbackId) {
								var callbackResponseId = msg.callbackId;
								callback = function(responseData) {
									_js2app({
										responseId: callbackResponseId,
										responseData: responseData
									});
								};
							}
							handler(msg.data, callback);
						} catch (exception) {
							console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
						}
					}
				}else{
					console.log("WebViewJavascriptBridge: ERROR: unsupported msg from app!!!",msg);
					alert("unsupported msg from app");
				}
			}
		},1);
		return {STS:"_app2js"};
	}

	function registerHandler(handlerName, handler) {
		msgHandlerSet[handlerName] = handler;
	}

	function callHandler(handlerName, data, cb) {
		return _js2app({
			handlerName: handlerName,
			data: data
		}, function( rt ){
			if(cb){
				if (typeof(rt)=='string'){
					try{ rt=s2o(rt); } catch(ex){};
				}
				cb(rt);
			}
		});
	}

	win.WebViewJavascriptBridge = {
		//for < API 17
		nativejsb:{
			js2app:function(callbackId,handlerName,data_s){
				setTimeout(function(){
					prompt('nativejsb:',JSON.stringify([callbackId,handlerName,data_s]));
				},1);
			}
		},
		//for js send app
		_js2app: _js2app,

		//for app send js:
		_app2js: _app2js,

		//for old system...
		init:function(){console.log('old init() is called.')},

		registerHandler: registerHandler,
		callHandler: callHandler
	};

})(window,document);
