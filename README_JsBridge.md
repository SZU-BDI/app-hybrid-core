
#

JsBridge
JSB
WebViewJavascriptBridge

// we now dont support registerHandler anymore....coz we don't think the web should provide func for app to call...
cmpjsbridge
  .callHandler($actionName, $param, function( callback_rt ){
	};

	//.registerHandler($actionName, function( $param, $callback ){
	//	//calc $rt;
	//	$callback( $rt );
	//});

	window.WebViewJavascriptBridge.callHandler( funcname, data, function( rt ){
		if (typeof(rt)=='string'){
			try{ rt=s2o(rt); } catch(ex){};
		}
		if (cb) cb(rt);
	});

NEW PROBLEM:

1.
using ifrm hack is not good for iOS? need to find a nother better way...
2.
using ifrm.src have a problem of length
3.
loadURL in android have the problem of URL length too.
