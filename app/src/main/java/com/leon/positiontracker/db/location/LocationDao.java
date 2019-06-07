package com.leon.positiontracker.db.location;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Observable;

@Dao
public abstract class LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(RxLocation location);

    @Delete
    public abstract void delete(RxLocation location);

    @Update
    public abstract void update(RxLocation location);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAll(RxLocation... locations);

    @Query("SELECT * FROM location")
    public abstract Observable<List<RxLocation>> getLocationsObservable();

    @Query("SELECT * FROM location")
    public abstract LiveData<List<RxLocation>> getLocationsLiveData();

    @Query("SELECT * FROM location WHERE timestamp >= :fromTimestamp AND timestamp <= :toTimestamp")
    public abstract Observable<List<RxLocation>> getLocationsRangeObservable(long fromTimestamp, long toTimestamp);

    @Query("SELECT * FROM location WHERE timestamp >= :fromTimestamp AND timestamp <= :toTimestamp")
    public abstract LiveData<List<RxLocation>> getLocationsRangeLiveData(long fromTimestamp, long toTimestamp);
}
