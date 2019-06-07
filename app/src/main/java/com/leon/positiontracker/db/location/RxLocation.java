package com.leon.positiontracker.db.location;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "location"
)
public class RxLocation implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;


    @ColumnInfo(name = "longitude")
    private double mLongitude;

    @ColumnInfo(name = "latitude")
    private double mLatitude;

    @ColumnInfo(name = "timestamp")
    private long mTimestamp;

    public RxLocation() {
        mTimestamp = System.currentTimeMillis();
    }

    @Ignore
    public RxLocation(double latitude, double longitude) {
        this();
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Ignore
    public RxLocation(Location location) {
        this();
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    @Ignore
    protected RxLocation(Parcel in) {
        mId = in.readLong();
        mLongitude = in.readDouble();
        mLatitude = in.readDouble();
        mTimestamp = in.readLong();
    }

    public static final Creator<RxLocation> CREATOR = new Creator<RxLocation>() {
        @Override
        public RxLocation createFromParcel(Parcel in) {
            return new RxLocation(in);
        }

        @Override
        public RxLocation[] newArray(int size) {
            return new RxLocation[size];
        }
    };

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeDouble(mLongitude);
        dest.writeDouble(mLatitude);
        dest.writeLong(mTimestamp);
    }
}
