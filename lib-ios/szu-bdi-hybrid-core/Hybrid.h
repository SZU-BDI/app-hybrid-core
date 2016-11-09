#ifndef Hybrid_h
#define Hybrid_h

//HybridCallback
typedef void (^HybridCallback)(id responseData);

//HybridHandler
typedef void (^HybridHandler)(id data, HybridCallback responseCallback);


typedef void (^WVJBResponseCallback)(id responseData);

typedef void (^WVJBHandler)(id data, WVJBResponseCallback responseCallback);

#endif /* Hybrid_h */


