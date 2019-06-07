package com.leon.positiontracker.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.leon.positiontracker.R;
import com.leon.positiontracker.db.LocationTrackerDatabase;
import com.leon.positiontracker.helper.RxLocation;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Leon on 2.2.2018..
 */

@Module
public abstract class ApplicationBindingModule {

    @Binds
    abstract Context bindContext(Application application);


    @Provides
    static RxLocation provideRxLocation(Context context) {
        return RxLocation.getInstance(context);
    }

    @Provides
    static SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    @Provides
    static LocationTrackerDatabase provideLocationTrackerDatabase(Application application) {
        return LocationTrackerDatabase.getInstance(application.getApplicationContext());
    }



}