//NOTES: the comments will be removed when eval()
//and this file share with iOS/Android JSB
//and this function is called by the "app" to inject a WebViewJavascriptBridge
(function(win,doc,PROTOCOL_SCHEME){
	if (win.WebViewJavascriptBridge) {
		return;
	}
    function s2o(s){try{var myjson=null;return(new Function('return '+s))();}catch(ex){}};function o2s(o){return JSON.stringify(o);};

	var msgIfrm;
	var send_Q = [];
	var msgHandlerSet = {};

	var responseCallbacks = {};
	var msgId = 1;

	//NOTES: can't remove yet, some old bridge codes might use it... will remove in further future...
	function init() {console.log('deprecated WebViewJavascriptBridge.init() is called.');}

    //send msg{handlerName:$n,data:$jsonObj} to app
	function _js2app(msg, cb){
		if (cb) {
			msgId=(msgId + 1) % 1000000;
			var callTime=new Date().getTime();
			var callbackId = 'cb_' + msgId + '_' + callTime;
			responseCallbacks[callbackId] = cb;
			msg.callbackId = callbackId;

			//TODO for gc in future
			msg.time=callTime;
		}
		if("undefined"!=typeof nativejsb){
			nativejsb.js2app(msg.callbackId,msg.handlerName,o2s(msg.data));
		}else{
		    //mostly for iOS: notify to fetch msg and handle inside app
			send_Q.push(msg);

			msgIfrm.src = PROTOCOL_SCHEME + '://__QUEUE_MESSAGE__/';
			//the __QUEUE_MESSAGE__ is just a what-ever word, @ref to shouldOverrideUrlLoading
		}
	}

	function _fetchQueue(flgReturn) {
		var messageQueueString = o2s(send_Q);
		send_Q = [];

		if(flgReturn){
		    //for iOS
			return messageQueueString;
		}else{
			//for android...
			//TODO to improve the mechanism, try using hacking prompt() as phonegap/cordova in future
			msgIfrm.src = PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
		}
	}

    //handle msg from app
	function _app2js(msg) {
		setTimeout(function(){
			var callback=null;
			if (msg.responseId) {
			    //this msg is a "Reply", so find the original callback
				callback = responseCallbacks[msg.responseId];
				if (!callback) { return; }
				callback(msg.responseData);
				delete responseCallbacks[msg.responseId];
//			} else {
//				var handler = null;
//				if (msg.handlerName) {
//					//find the handler
//					handler = msgHandlerSet[msg.handlerName];
//					if(handler==null){
//						console.log("WebViewJavascriptBridge: not found handler name="+msg.handlerName);
//					}else{
//                        try {
//                            if (msg.callbackId) {
//                                var callbackResponseId = msg.callbackId;
//                                callback = function(responseData) {
//                                    _js2app({
//                                        responseId: callbackResponseId,
//                                        responseData: responseData
//                                    });
//                                };
//                            }
//                            handler(msg.data, callback);
//                        } catch (exception) {
//                            console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
//                        }
//					}
//				}
			}
		},1);
	}

////we closed door for app to call js directly
//	function registerHandler(handlerName, handler) {
//		msgHandlerSet[handlerName] = handler;
//	}

	function callHandler(handlerName, data, cb) {
		_js2app({
			handlerName: handlerName,
			data: data
		}, cb);
	}

	win.WebViewJavascriptBridge = {
		_fetchQueue: _fetchQueue,

		//for js send app
		_js2app: _js2app,

		//for app send js:
		_app2js: _app2js,

		init: init,

		//registerHandler: registerHandler,
		callHandler: callHandler
	};

	//init msg iframe (TODO remove all ifrm hack later)
	msgIfrm = doc.createElement('iframe');
	msgIfrm.style.display = 'none';
	doc.documentElement.appendChild(msgIfrm);

})(window,document,'jsb1');
