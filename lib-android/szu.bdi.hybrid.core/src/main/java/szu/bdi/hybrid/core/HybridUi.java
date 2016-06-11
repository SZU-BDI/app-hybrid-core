package szu.bdi.hybrid.core;

import android.content.Context;
import android.content.Intent;

//import static android.content.Intent.*;

//UI as the Activity Wrapper
public class HybridUi {
    Intent _intent = null;
    Context _ctx = null;

    public void show(Context ctx) {
        _ctx = ctx;
        Intent intent = new Intent(_ctx, HybridUiActivity.class);
//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _intent = intent;
        _ctx.startActivity(intent);
    }

    public void show() {
        this.show(HybridTools.getAppContext());
    }

    //not support for now
//    public void hide() {
//        Intent intent = new Intent(_ctx, HybridUiActivity.class);
////        intent.addFlags(Intent);
//        _intent = intent;
////        _ctx.s(intent);
//    }
}
