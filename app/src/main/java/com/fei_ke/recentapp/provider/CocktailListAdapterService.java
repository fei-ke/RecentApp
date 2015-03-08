package com.fei_ke.recentapp.provider;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CocktailListAdapterService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CocktailListAdapterFactory(this.getApplicationContext());
    }
}
