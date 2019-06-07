package com.leon.positiontracker.db.location;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.leon.positiontracker.db.LocationTrackerDatabase;

import javax.inject.Inject;

import dagger.android.DaggerIntentService;

public class RxLocationService extends DaggerIntentService {
    private static String TAG = RxLocationService.class.getSimpleName();
    private static final String ACTION_INSERT = TAG + ".INSERT";
    private static final String ACTION_UPDATE = TAG + ".UPDATE";
    private static final String ACTION_DELETE = TAG + ".DELETE";

    // Intent extras
    private static final String EXTRA_LOCATION = "extra_location";

    @Inject
    LocationTrackerDatabase database;

    public RxLocationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (action.equals(ACTION_INSERT)) {
            handleInsert(intent);
        } else if(action.equals(ACTION_DELETE)) {
            handleDelete(intent);
        } else if (action.equals(ACTION_UPDATE)) {
            handleUpdate(intent);
        }
     }

    private void handleUpdate(Intent intent) {
        RxLocation location = intent.getParcelableExtra(EXTRA_LOCATION);
        database.locationDao().update(location);
    }

    private void handleDelete(Intent intent) {
        RxLocation location = intent.getParcelableExtra(EXTRA_LOCATION);
        database.locationDao().delete(location);
    }

    private void handleInsert(Intent intent) {
        RxLocation location = intent.getParcelableExtra(EXTRA_LOCATION);
        database.locationDao().insert(location);
        System.out.println(location);
    }

    public static void insert(Context context, RxLocation location) {
        Intent intent = new Intent(context, RxLocationService.class);
        intent.setAction(ACTION_INSERT);
        intent.putExtra(EXTRA_LOCATION, location);
        context.startService(intent);
    }

    public static void delete(Context context, RxLocation location) {
        Intent intent = new Intent(context, RxLocationService.class);
        intent.setAction(Intent.ACTION_DELETE);
        intent.putExtra(EXTRA_LOCATION, location);
        context.startService(intent);
    }

    public static void update(Context context, RxLocation location) {
        Intent intent = new Intent(context, RxLocationService.class);
        intent.setAction(ACTION_UPDATE);
        intent.putExtra(EXTRA_LOCATION, location);
        context.startService(intent);
    }
}
