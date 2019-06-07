package com.leon.positiontracker.application;


import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;


/**
 * Created by Leon on 2.2.2018..
 */

public class MainApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerApplicationComponent
                .builder()
                .application(this)
                .build();
    }


}