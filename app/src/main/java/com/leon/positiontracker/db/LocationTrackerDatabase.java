package com.leon.positiontracker.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.leon.positiontracker.db.location.RxLocation;
import com.leon.positiontracker.db.location.LocationDao;


@Database(entities = {RxLocation.class}, version = 1)
public abstract class LocationTrackerDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = LocationTrackerDatabase.class.getSimpleName();

    public abstract LocationDao locationDao();


    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile LocationTrackerDatabase INSTANCE;

    public static LocationTrackerDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocationTrackerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocationTrackerDatabase.class, DATABASE_NAME)
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this codelab.
                            .fallbackToDestructiveMigration()
                            //.allowMainThreadQueries()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }



    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };



}
