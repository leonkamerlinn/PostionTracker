package com.leon.positiontracker.ui.activity.main;

import android.app.Activity;


import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.leon.positiontracker.R;
import com.leon.positiontracker.databinding.ActivityMainBinding;
import com.leon.positiontracker.di.scope.ActivityScoped;
import com.tbruyelle.rxpermissions2.RxPermissions;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Leon on 2.2.2018..
 */

@Module
public abstract class MainActivityModule {

    @Binds
    abstract Activity provideActivity(MainActivity mainActivity);


    @Provides
    @ActivityScoped
    static RxPermissions providePermission(MainActivity activity) {
        return new RxPermissions(activity);
    }


    @Provides
    @ActivityScoped
    static MainViewModel provideMainViewModel(MainActivity activity, MainViewModelFactory mainViewModelFactory) {
        return ViewModelProviders.of(activity, mainViewModelFactory).get(MainViewModel.class);

    }

    @Provides
    @ActivityScoped
    static ActivityMainBinding provideViewBinding(MainActivity activity, MainViewModel viewModel) {
        ActivityMainBinding binding = DataBindingUtil.setContentView(activity, R.layout.activity_main);
        binding.setLifecycleOwner(activity);
        binding.setViewModel(viewModel);


        return binding;
    }


}