#ifndef Hybrid_h
#define Hybrid_h

////HybridCallback
//typedef void (^HybridCallback)(id responseData);
//
////HybridHandler
//typedef void (^HybridHandler)(id data, HybridCallback responseCallback);


typedef void (^HybridCallback)(id responseData);

typedef void (^HybridHandler)(id data, HybridCallback responseCallback);

#endif /* Hybrid_h */


