//notation: js file can only use this kind of comments
//since comments will cause error when use in webview.loadurl,
//comments will be remove by java use regexp
(function() {
    if (window.WebViewJavascriptBridge) {
        return;
    }

    var messagingIframe;
    var sendMessageQueue = [];
    var receiveMessageQueue = [];
    var messageHandlers = {};

    var CUSTOM_PROTOCOL_SCHEME = 'yy';
    var QUEUE_HAS_MESSAGE = '__QUEUE_MESSAGE__/';

    var responseCallbacks = {};
    var uniqueId = 1;

    function _createQueueReadyIframe(doc) {
        messagingIframe = doc.createElement('iframe');
        messagingIframe.style.display = 'none';
        doc.documentElement.appendChild(messagingIframe);
    }

    //set default messageHandler
    function init(messageHandler) {
        if (WebViewJavascriptBridge._messageHandler) {
            //throw new Error('WebViewJavascriptBridge.init called twice');
            if("undefined"!=typeof console){
             console.log('WebViewJavascriptBridge.init called twice');
            }
            return;
        }
        WebViewJavascriptBridge._messageHandler = messageHandler;
        var receivedMessages = receiveMessageQueue;
        receiveMessageQueue = null;
        for (var i = 0; i < receivedMessages.length; i++) {
            _dispatchMessageFromNative(receivedMessages[i]);
        }
    }

    function send(data, responseCallback) {
        _doSend({
            data: data
        }, responseCallback);
    }

    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }

    function callHandler(handlerName, data, responseCallback) {
        _doSend({
            handlerName: handlerName,
            data: data
        }, responseCallback);
    }

    //sendMessage add message, 触发native处理 sendMessage
    function _doSend(message, responseCallback) {
        if (responseCallback) {
            var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
            responseCallbacks[callbackId] = responseCallback;
            message.callbackId = callbackId;
        }

        sendMessageQueue.push(message);
        messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
    }

function o2s1(object){ if(null==object)return "null"; var type = typeof object; if('object'== type){ if (Array == object.constructor) type = 'array'; else if (RegExp == object.constructor) type = 'regexp'; else type = 'object'; } switch(type){ case 'undefined': case 'unknown': return; break; case 'function': case 'boolean': case 'regexp': return object.toString(); break; case 'number': return isFinite(object) ? object.toString() : 'null'; break; case 'string': return '"' + object.replace(/(\\|\")/g,"\\$1").replace(/\n|\r|\t/g, function(){ var a = arguments[0]; return (a == '\n') ? '\\n': (a == '\r') ? '\\r': (a == '\t') ? '\\t': "" }) + '"'; break; case 'object': var pp="";var value =""; var results = []; try{ for (var property in object) { pp=object[property]; value = o2s(pp); if (value !== undefined) results.push('"'+property + '":' + value); }; } catch(e){ } return '{' + results.join(',') + '}'; break; case 'array': var results = []; if(object.length>=0){ for(var i = 0; i < object.length; i++){ var value = o2s(object[i]); if (value !== undefined) results.push(value); }; return '[' + results.join(',') + ']'; } else{ for(k in object) { var kk=k; var value = o2s(object[k]); if (value !== undefined) results.push('"'+kk+'":'+value); } return '{' + results.join(',') + '}'; } break; } }
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

    // 提供给native调用,该函数作用:获取sendMessageQueue返回给native,由于android不能直接获取返回的内容,所以使用url shouldOverrideUrlLoading 的方式返回内容
    function _fetchQueue() {
//        var messageQueueString = JSON.stringify(sendMessageQueue);
        var messageQueueString = o2s(sendMessageQueue);
//        console.log(sendMessageQueue);
//        console.log(messageQueueString);
//console.log(o2s1(sendMessageQueue));
        sendMessageQueue = [];
        //android can't read directly the return data, so we can reload iframe src to communicate with java
        messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
    }

function s2o(s){ try{ return (new Function('return '+s))(); }catch(ex){} };

    //提供给native使用,
    function _dispatchMessageFromNative(messageJSON) {
        setTimeout(function() {
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
                //直接发送
                if (message.callbackId) {
                    var callbackResponseId = message.callbackId;
                    responseCallback = function(responseData) {
                        _doSend({
                            responseId: callbackResponseId,
                            responseData: responseData
                        });
                    };
                }

                var handler = WebViewJavascriptBridge._messageHandler;
                if (message.handlerName) {
                    handler = messageHandlers[message.handlerName];
                }
                //查找指定handler
                try {
                    handler(message.data, responseCallback);
                } catch (exception) {
                    if (typeof console != 'undefined') {
                        console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
                    }
                }
            }
        });
    }

    //提供给native调用,receiveMessageQueue 在会在页面加载完后赋值为null,所以
    function _handleMessageFromNative(messageJSON) {
        console.log(messageJSON);
        if (receiveMessageQueue) {
            receiveMessageQueue.push(messageJSON);
        } else {
            _dispatchMessageFromNative(messageJSON);
        }
    }

    var WebViewJavascriptBridge = window.WebViewJavascriptBridge = {
        init: init,
        send: send,
        registerHandler: registerHandler,
        callHandler: callHandler,
        _fetchQueue: _fetchQueue,
        _handleMessageFromNative: _handleMessageFromNative
    };

    var doc = document;
    _createQueueReadyIframe(doc);
    var readyEvent = doc.createEvent('Events');
    readyEvent.initEvent('WebViewJavascriptBridgeReady');
    readyEvent.bridge = WebViewJavascriptBridge;
    doc.dispatchEvent(readyEvent);
})();