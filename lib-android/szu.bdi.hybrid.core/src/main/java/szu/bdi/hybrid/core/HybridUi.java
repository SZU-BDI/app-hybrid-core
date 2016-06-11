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

    //not design yet...  在 HybridService 放一个全局池，然后当 HybridUi 被发动应该带上ID，
    // 然后该activity启动时堆到这个池，然后下次可以驱动关闭。。。还有没有更好方法？
//    public void hide() {
//        Intent intent = new Intent(_ctx, HybridUiActivity.class);
////        intent.addFlags(Intent);
//        _intent = intent;
////        _ctx.s(intent);
//    }
}
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(_ctx, HybridUiActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.putExtra("bClose", true);
//                _intent = intent;
//                _ctx.startActivity(intent);
//            }
//        }, 3000);