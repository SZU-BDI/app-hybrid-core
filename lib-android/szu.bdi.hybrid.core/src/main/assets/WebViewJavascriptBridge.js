(function() {
	if (window.WebViewJavascriptBridge) {
		return;
	}

	var messagingIframe;
	var sendMessageQueue = [];
	//var receiveMessageQueue = null;
	var messageHandlers = {};

	var CUSTOM_PROTOCOL_SCHEME = 'jsb1';

	var responseCallbacks = {};
	var uniqueId = 1;

	function o2s(o){
		if(null==o)return "null";
		f=arguments.callee;
		t=typeof o;
		if('object'==t){if(Array==o.constructor)t='array';else if(RegExp==o.constructor)t='regexp';}
		switch(t){
			case 'undefined':case 'unknown':return;
			case 'function':return !('prototype' in o)?"function(){}":(""+o);break;
			case 'boolean':case 'regexp':return o.toString(); break;
			case 'number':return isFinite(o)?o.toString():'null';break;
			case 'string':return '"'+o.replace(/(\\|\")/g,"\\$1").replace(/\n/g,"\\n").replace(/\r/g,"\\r")+'"';break;
			case 'object':var r=[];try{for(var p in o){v=f(o[p]);if(v!==undefined)r.push('"'+p+'":'+v);}}catch(e){};
				return '{'+r.join(',')+'}';break;
			case 'array':var r=[];
				if(o.length>=0){
					for(var i=0;i<o.length;i++){var v=f(o[i]);if (v!==undefined)r.push(v);};return '['+r.join(',')+']';
				}
				else{
					for(var k in o){var v=f(o[k]);if(v!==undefined)r.push('"'+k+'":'+v);};return '{'+r.join(',')+'}';
				}
		}
	};
	function s2o(s){ try{ return (new Function('return '+s))(); }catch(ex){} };

	function _createQueueReadyIframe(doc) {
		messagingIframe = doc.createElement('iframe');
		messagingIframe.style.display = 'none';
		doc.documentElement.appendChild(messagingIframe);
	}

	//can't remove yet, old cmp bridge might use it... will remove in future...
	function init() {
		console.log('deprecated WebViewJavascriptBridge.init() is called.');
	}

	//function send(data, responseCallback) {
	//	_js2java({
	//		data: data
	//	}, responseCallback);
	//}

	function _js2java(message, responseCallback) {
		if (responseCallback) {
			var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
			responseCallbacks[callbackId] = responseCallback;
			message.callbackId = callbackId;
		}

		sendMessageQueue.push(message);

		//just a notification to the java that "hey, you got message, come get them"
		messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://__QUEUE_MESSAGE__/';
		//so the __QUEUE_MESSAGE__ is just a what-ever word, @ref to shouldOverrideUrlLoading
	}

	function _fetchQueue() {
		//        var messageQueueString = JSON.stringify(sendMessageQueue);
		var messageQueueString = o2s(sendMessageQueue);
		sendMessageQueue = [];
		//reload iframe src to communicate with java
		messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
	}

	function _java2js(messageJSON) {
		setTimeout(function(){
			//            var message = JSON.parse(messageJSON);
			var message = s2o(messageJSON);
			var responseCallback;
			//java call finished, now need to call js callback function
			if (message.responseId) {
				responseCallback = responseCallbacks[message.responseId];
				if (!responseCallback) {
					return;
				}
				responseCallback(message.responseData);
				delete responseCallbacks[message.responseId];
			} else {
				if (message.callbackId) {
					var callbackResponseId = message.callbackId;
					responseCallback = function(responseData) {
						_js2java({
							responseId: callbackResponseId,
							responseData: responseData
						});
					};
				}

				var handler = null;
				if (message.handlerName) {
					handler = messageHandlers[message.handlerName];
				}
				try {
					if(handler!=null)
						handler(message.data, responseCallback);
				} catch (exception) {
					console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
				}
			}
		});
	}

	function registerHandler(handlerName, handler) {
		messageHandlers[handlerName] = handler;
	}

	function callHandler(handlerName, data, responseCallback) {
		_js2java({
			handlerName: handlerName,
			data: data
		}, responseCallback);
	}

	var WebViewJavascriptBridge = window.WebViewJavascriptBridge = {
		init: init,
		registerHandler: registerHandler,
		callHandler: callHandler,
		_fetchQueue: _fetchQueue,
		_js2java: _js2java,
		_java2js: _java2js
	};

	_createQueueReadyIframe(document);

})();
