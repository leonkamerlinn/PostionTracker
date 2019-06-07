package com.leon.positiontracker.di.modules;


import com.leon.positiontracker.db.location.RxLocationService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBindingModule {
    @ContributesAndroidInjector()
    abstract RxLocationService locationService();
}
