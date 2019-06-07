package com.leon.positiontracker.ui.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.leon.positiontracker.R;
import com.leon.positiontracker.databinding.ActivityMainBinding;
import com.leon.positiontracker.db.LocationTrackerDatabase;
import com.leon.positiontracker.db.location.RxLocation;
import com.leon.positiontracker.db.location.RxLocationService;
import com.leon.positiontracker.services.LocationTrackerService;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public class MainActivity extends DaggerAppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int UPDATE_ADDRESS = 1;
    public static final int TRACKING_YES = 2;
    public static final int TRACKING_NO = 3;
    public static final String IS_TRACKING = "is_tracking";
    public static final String ACTIVITY_MESSENGER = "activity_messenger";
    private static final String FROM_CALENDAR_TYPE = "from_calendar_type";
    private static final String TO_CALENDAR_TYPE = "to_calendar_type";

    @Inject
    RxPermissions rxPermission;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    ActivityMainBinding binding;
    @Inject
    MainViewModel viewModel;

    @Inject
    LocationTrackerDatabase db;

    ReplaySubject<Boolean> mReplaySubject = ReplaySubject.create();
    ReplaySubject<GoogleMap> mMapSubject = ReplaySubject.create();


    private GoogleMap mMap;
    private boolean mBound;
    private MarkerOptions mCurrentLocationMarker;
    private Messenger mActivityMessenger = new Messenger(new IncomingHandler(this));
    private Messenger mServiceMessenger;



    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @SuppressLint("CheckResult")
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBound = true;
            mServiceMessenger = new Messenger(service);
            mReplaySubject.onNext(mBound);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mReplaySubject.onNext(mBound);
        }
    };


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        mCurrentLocationMarker = new MarkerOptions();

        binding.bottomSheetLinearLayout.trackingButton.setOnClickListener(v -> {
            startLocationTrackerService();
        });
        binding.fab.setOnClickListener(v -> {
            requestMyLocation();
        });


        getBottomSheetbehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);

        getBottomSheetbehavior().setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        setupMap(savedInstanceState);
        requestLocation();

        binding.bottomSheetLinearLayout.fromLinearLayout.setOnClickListener(v -> {
            DatePickerDialog dpd = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                viewModel.setFromCalendar(year, monthOfYear, dayOfMonth);
            },
                viewModel.getFromCalendar().get(Calendar.YEAR),
                viewModel.getFromCalendar().get(Calendar.MONTH),
                viewModel.getFromCalendar().get(Calendar.DAY_OF_MONTH)
            );

            dpd.show(getSupportFragmentManager(), "DatePickerDialogFrom");
        });

        binding.bottomSheetLinearLayout.toLinearLayout.setOnClickListener(v -> {
            DatePickerDialog dpd = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                        viewModel.setToCalendar(year, monthOfYear, dayOfMonth);
                    },
                    viewModel.getToCalendar().get(Calendar.YEAR),
                    viewModel.getToCalendar().get(Calendar.MONTH),
                    viewModel.getToCalendar().get(Calendar.DAY_OF_MONTH)
            );

            dpd.show(getSupportFragmentManager(), "DatePickerDialogTo");
        });

        binding.bottomSheetLinearLayout.showButton.setOnClickListener(v -> {
            db.locationDao().getLocationsRangeLiveData(viewModel.getFromMiliseconds(), viewModel.getToMiliseconds()).observe(this, rxLocations -> {
               drawPrimaryLinePath(rxLocations, mMap);
            });
        });
    }

    private void drawPrimaryLinePath( List<RxLocation> listLocsToDraw, GoogleMap map) {
        if (map == null) {
            return;
        }

        if (listLocsToDraw.size() < 2) {
            return;
        }

        map.clear();

        map.addMarker(mCurrentLocationMarker);

        PolylineOptions options = new PolylineOptions();

        options.color(Color.parseColor( "#CC0000FF" ));
        options.width(5);
        options.visible(true);

        for ( RxLocation locRecorded : listLocsToDraw ) {
            options.add(new LatLng( locRecorded.getLatitude(), locRecorded.getLongitude()));
        }

        map.addPolyline(options);

    }

    private BottomSheetBehavior<LinearLayoutCompat> getBottomSheetbehavior() {
        return BottomSheetBehavior.from(getBottomLinearLayout());
    }

    private LinearLayoutCompat getBottomLinearLayout() {
        return binding.bottomSheetLinearLayout.parentLinearlayout;
    }

    @SuppressLint("CheckResult")
    private void requestLocation() {
        Observable.combineLatest(mReplaySubject, mMapSubject, (aBoolean, googleMap) -> {
            if (!aBoolean) return googleMap;

            rxPermission.requestEach(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(permission -> {
                if (permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (permission.granted) {
                        requestMyLocation();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        buildAlertMessageNoGps();
                    }
                }
            });

            return googleMap;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
    }



    private void setupMap(Bundle savedInstanceState) {
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.onResume();

        binding.mapView.getMapAsync(map -> {
            mMap = map;
            mMapSubject.onNext(mMap);
        });
    }

    private void requestMyLocation() {
        Message msg = Message.obtain(null, LocationTrackerService.SHOW_MY_LOCATION);
        msg.replyTo = mActivityMessenger;
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateCurrentAddress(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            viewModel.setAddress(address);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private CameraUpdate zoomingLocation(Location location) {
        mCurrentLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        mMap.addMarker(mCurrentLocationMarker);
        return CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13);

    }

    private void startLocationTrackerService() {
        Intent intent = new Intent(this, LocationTrackerService.class);
        intent.putExtra(IS_TRACKING, isTracking());
        intent.putExtra(ACTIVITY_MESSENGER, mActivityMessenger);
        startService(intent);
    }

    private boolean isTracking() {
        return getString(R.string.stop_tracking).equals(viewModel.getTrackingButtonText().getValue());
    }



    private void animateToLocationPosition(Location location) {
        mMap.animateCamera(zoomingLocation(location));
    }


    private void updateAddressAndMoveToMyLocation(Location location) {
        animateToLocationPosition(location);
        updateCurrentAddress(location);
    }



    private class IncomingHandler extends Handler {
        private MainActivity mMainActivity;

        IncomingHandler(MainActivity mainActivity) {
            mMainActivity = mainActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_ADDRESS:
                    if (msg.obj instanceof Location) {
                        Location location = (Location) msg.obj;
                        updateAddressAndMoveToMyLocation(location);
                        RxLocationService.insert(getApplicationContext(), new RxLocation(location));
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }


            if (msg.arg1 == TRACKING_NO) {
                String text = mMainActivity.getString(R.string.start_tracking);
                viewModel.setTrackingButtonText(text);
            } else if (msg.arg1 == TRACKING_YES) {
                String text = mMainActivity.getString(R.string.stop_tracking);
                viewModel.setTrackingButtonText(text);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        binding.mapView.onStart();
        Intent intent = new Intent(this, LocationTrackerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.mapView.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}
