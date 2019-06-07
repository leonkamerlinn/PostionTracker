package com.leon.positiontracker.application;

import android.app.Application;


import com.leon.positiontracker.di.modules.ActivityBindingModule;
import com.leon.positiontracker.di.modules.ApplicationBindingModule;
import com.leon.positiontracker.di.modules.BroadcastReceiverBindingModule;
import com.leon.positiontracker.di.modules.FragmentDialogBindingModule;
import com.leon.positiontracker.di.modules.ServiceBindingModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Leon on 2.2.2018..
 */

@Singleton
@Component(modules = {
        ApplicationBindingModule.class,
        ActivityBindingModule.class,
        ServiceBindingModule.class,
        FragmentDialogBindingModule.class,
        BroadcastReceiverBindingModule.class,
        AndroidSupportInjectionModule.class
})
public interface ApplicationComponent extends AndroidInjector<MainApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        ApplicationComponent.Builder application(Application application);
        ApplicationComponent build();
    }

}
