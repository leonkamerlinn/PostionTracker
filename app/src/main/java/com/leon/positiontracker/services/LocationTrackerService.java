package com.leon.positiontracker.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.Nullable;


import com.leon.positiontracker.db.location.RxLocationService;
import com.leon.positiontracker.ui.activity.main.MainActivity;
import com.leon.positiontracker.helper.RxLocation;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LocationTrackerService extends Service implements Observer<Location> {

    public static final int SHOW_MY_LOCATION = 1;
    public static final int TOGGLE_TRACKING = 2;
    private Messenger mMessenger = new Messenger(new IncomingHandler(this));
    private static RxLocation rxLocation = null;



    @SuppressLint("CheckResult")
    @Override
    public void onCreate() {
        synchronized (LocationTrackerService.class) {
            if (rxLocation == null) {
                rxLocation = RxLocation.getInstance(getApplicationContext());
                rxLocation.setProvider(LocationManager.GPS_PROVIDER);
                rxLocation.setMinTime(5000); // every 5 seconds;
                rxLocation.setMinDistance(10); // every 10 meters;
                try {
                    rxLocation.getLastKnownLocation();
                } catch (RxLocation.ProviderIsNotEnabledException e) {
                    e.printStackTrace();
                }
                rxLocation.getLocationObservable().subscribe(this);
            }
        }

    }


    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Messenger activityMessenger = intent.getParcelableExtra(MainActivity.ACTIVITY_MESSENGER);
        Message msg = Message.obtain(null, LocationTrackerService.TOGGLE_TRACKING);
        msg.replyTo = activityMessenger;
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return START_REDELIVER_INTENT;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }


    public void startTracking() throws RxLocation.ProviderIsNotEnabledException {
        rxLocation.start();
    }

    public void stopTracking(){
        rxLocation.stop();
    }

    @Override
    public void onDestroy() {
        stopTracking();
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Location location) {
        RxLocationService.insert(getApplicationContext(), new com.leon.positiontracker.db.location.RxLocation(location));
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }


    private class IncomingHandler extends Handler {
        private Context context;

        IncomingHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_MY_LOCATION:

                    try {

                        Location location = rxLocation.getLastKnownLocation();
                        int isTracking = (rxLocation.isTracking()) ? MainActivity.TRACKING_YES : MainActivity.TRACKING_NO;
                        Message message = Message.obtain(null, MainActivity.UPDATE_ADDRESS, isTracking, 0, location);
                        msg.replyTo.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TOGGLE_TRACKING:
                    try {
                        if (rxLocation.isTracking()) {
                            stopTracking();
                            stopSelf();
                        } else {
                            startTracking();
                        }

                        int isTracking = (rxLocation.isTracking()) ? MainActivity.TRACKING_YES : MainActivity.TRACKING_NO;
                        Message message = Message.obtain(null, 0, isTracking, 0);

                        msg.replyTo.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


}
