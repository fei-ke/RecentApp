
package com.fei_ke.recentapp.provider;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.fei_ke.recentapp.R;

import java.util.List;

public class CocktailListAdapterFactory implements RemoteViewsFactory {
    static final String TAG = "CocktailListAdapter ";

    LruCache<String, Bitmap> iconCache = new LruCache<String, Bitmap>(30 * 1024 * 1024);
    private Context mContext;
    private ActivityManager mActivityManager;
    private List<ActivityManager.RecentTaskInfo> mAppList;
    private PackageManager mPackageManager;
    private Settings settings;

    public CocktailListAdapterFactory(Context context) {
        Log.d(TAG, "CocktailListAdapterFactory constructor ");
        mContext = context;
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mPackageManager = mContext.getPackageManager();
        settings = new Settings(context);
        getRecentApp();
    }

    @Override
    public int getCount() {
        int count = mAppList != null ? mAppList.size() : 0;
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        ActivityManager.RecentTaskInfo recentTask = mAppList.get(getCount() - position - 1);
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        contentView.setViewVisibility(R.id.imageViewClose, Settings.isSwitchOn() ? View.VISIBLE : View.GONE);
        Intent baseIntent = recentTask.baseIntent;


        String packageName = baseIntent.getComponent().getPackageName();

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(Constants.EXTRA_RECENT_TASK, recentTask);

        contentView.setOnClickFillInIntent(R.id.widget_item_layout, fillInIntent);

        Bitmap icon = iconCache.get(packageName);
        String title;

        final ResolveInfo resolveInfo = mPackageManager.resolveActivity(baseIntent, 0);
        if (resolveInfo == null) return null;

        final ActivityInfo info = resolveInfo.activityInfo;
        title = info.loadLabel(mPackageManager).toString();

        if (icon == null) {
            Drawable drawable = info.loadIcon(mPackageManager);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            icon = bitmapDrawable.getBitmap();
            iconCache.put(packageName, icon);
        }
        contentView.setImageViewBitmap(R.id.imageViewIcon, icon);
        contentView.setTextViewText(R.id.textViewLabel, title);
        return contentView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        getRecentApp();
    }

    @Override
    public void onDestroy() {
    }


    private void getRecentApp() {
        mAppList = mActivityManager.getRecentTasks(settings.getAppCount(), ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        for (ActivityManager.RecentTaskInfo running : mAppList) {
            System.out.println(running.baseIntent);
        }
    }

}
