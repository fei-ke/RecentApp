package com.fei_ke.recentapp.provider;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class CocktailListAdapterService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i("CocktailListAdapterService","onGetViewFactory");
        return new CocktailListAdapterFactory(this.getApplicationContext());
    }
}
