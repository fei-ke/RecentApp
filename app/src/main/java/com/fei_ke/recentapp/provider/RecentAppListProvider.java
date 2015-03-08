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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 杨金阳 on 15/2/2015.
 */
public class RecentAppListProvider extends SlookCocktailProvider {

    private static final String TAG = "RecentAppListProvider";
    private ActivityManager mActivityManager;

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
            ActivityManager.RecentTaskInfo recentTaskInfo = intent.getParcelableExtra(Constants.EXTRA_RECENT_TASK);

            int persistentId = recentTaskInfo.persistentId;
            if (Settings.isSwitchOn()) {
                closeApp(context, persistentId);
            } else {
                if (recentTaskInfo.id != -1) {
                    getActivityManager(context).moveTaskToFront(persistentId, 0);
                } else {
                    context.startActivity(recentTaskInfo.baseIntent);
                }
            }

        } else if (action == Constants.COCKTAIL_LIST_ADAPTER_SWITCH_CLICK) {
            Settings.setSwitchOn(!Settings.isSwitchOn());
            notifyDateSetChange(context);
        }

    }

    private ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    private void closeApp(Context context, int persistentId) {
        ActivityManager am = getActivityManager(context);
        try {
            Method method = ActivityManager.class.getMethod("removeTask", int.class, int.class);
            method.setAccessible(true);
            method.invoke(am, persistentId, 1);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
