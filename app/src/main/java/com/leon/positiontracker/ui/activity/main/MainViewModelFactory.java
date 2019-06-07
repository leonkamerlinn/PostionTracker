package com.leon.positiontracker.ui.activity.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;



import javax.inject.Inject;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final MainActivity mActivity;

    @Inject
    public MainViewModelFactory(MainActivity mainActivity) {
        mActivity = mainActivity;
    }

    @NonNull
    @Override
    public MainViewModel create(Class modelClass) {
        return new MainViewModel(mActivity.getApplication());
    }
}
