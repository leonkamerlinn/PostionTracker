package com.leon.positiontracker.di.modules;


import com.leon.positiontracker.di.scope.ActivityScoped;
import com.leon.positiontracker.ui.activity.main.MainActivity;
import com.leon.positiontracker.ui.activity.main.MainActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity mainActivity();

}
