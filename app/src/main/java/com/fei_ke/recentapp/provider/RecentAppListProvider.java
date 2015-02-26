package com.fei_ke.recentapp.provider;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.fei_ke.recentapp.R;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;

/**
 * Created by 杨金阳 on 15/2/2015.
 */
public class RecentAppListProvider extends SlookCocktailProvider {

    private static final String TAG = "RecentAppListProvider";

    @Override
    public void onUpdate(Context context, SlookCocktailManager cocktailManager, int[] cocktailIds) {
        Log.d(TAG, "onUpdate");

        Intent intent = new Intent(context, CocktailListAdapterService.class);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        views.setRemoteAdapter(R.id.widgetlist, intent);
        //views.setEmptyView(R.id.widgetlist, R.id.emptylist);

        Intent itemClickIntent = new Intent(context, RecentAppListProvider.class);
        itemClickIntent.setAction(Constants.COCKTAIL_LIST_ADAPTER_CLICK_ACTION);

        PendingIntent itemClickPendingIntent = PendingIntent.getBroadcast(context, 1,
                itemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widgetlist, itemClickPendingIntent);

        Intent switchIntent = new Intent(Constants.COCKTAIL_LIST_ADAPTER_SWITCH_CLICK);
        PendingIntent switchPendingIntent = PendingIntent.getBroadcast(context, 1, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imageSwitch, switchPendingIntent);

        for (int i = 0; i < cocktailIds.length; i++) {
            cocktailManager.updateCocktail(cocktailIds[i], views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (action == Constants.COCKTAIL_LIST_ADAPTER_CLICK_ACTION) {
            PendingIntent p = intent.getParcelableExtra(Constants.EXTRA_CONTENT_INTENT);
            if (p != null) {
                try { p.send(); } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            } else {
                String packageName=intent.getStringExtra(Constants.EXTRA_PACKAGE_NAME);
                closeApp(context, packageName);
            }
        } else if (action == Constants.COCKTAIL_LIST_ADAPTER_SWITCH_CLICK) {
            Settings.setSwitchOn(!Settings.isSwitchOn());
            notifyDateSetChange(context);
        }

    }

    private void closeApp(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onVisibilityChanged(Context context, int cocktailId, int visibility) {
        super.onVisibilityChanged(context, cocktailId, visibility);
        Log.d(TAG, "onVisibilityChanged");
        if (visibility == SlookCocktailManager.COCKTAIL_VISIBILITY_SHOW) {
            notifyDateSetChange(context);
        } else {
            Settings.setSwitchOn(false);
        }
    }

    private void notifyDateSetChange(Context context) {
        SlookCocktailManager mgr = SlookCocktailManager.getInstance(context);
        int[] cocktailIds = mgr.getCocktailIds(new ComponentName(context,
                RecentAppListProvider.class));
        for (int i = 0; i < cocktailIds.length; i++) {
            mgr.notifyCocktailViewDataChanged(cocktailIds[i], R.id.widgetlist);
        }
    }
}
