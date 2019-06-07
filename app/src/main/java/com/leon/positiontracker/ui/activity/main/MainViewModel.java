package com.leon.positiontracker.ui.activity.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leon.positiontracker.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainViewModel extends AndroidViewModel {


    private final String UNKNOWN;
    private final String NONE;
    private MutableLiveData<String> mAddress = new MutableLiveData<>();
    private MutableLiveData<String> mTrackingButtonText = new MutableLiveData<>();
    private MutableLiveData<Calendar> mFromCalendar = new MutableLiveData<>();
    private MutableLiveData<Calendar> mToCalendar = new MutableLiveData<>();
    private MutableLiveData<String> mFromText = new MutableLiveData<>();
    private MutableLiveData<String> mToText = new MutableLiveData<>();
    private MutableLiveData<Boolean> mEnableButton = new MutableLiveData<>();


    public MainViewModel(Application application) {
        super(application);
        UNKNOWN = application.getResources().getString(R.string.unknown);
        NONE = application.getResources().getString(R.string.none);
        setAddress(UNKNOWN);
        mFromCalendar.setValue(Calendar.getInstance());
        mToCalendar.setValue(Calendar.getInstance());
        mFromText.setValue(NONE);
        mToText.setValue(NONE);
        mEnableButton.setValue(false);
    }

    public LiveData<Boolean> getEnableButton() {
        return mEnableButton;
    }


    public void setAddress(String text) {
        mAddress.setValue(text);
    }

    public LiveData<String> getAddress() {
        return mAddress;
    }

    public void setTrackingButtonText(String text) {
        mTrackingButtonText.setValue(text);
    }

    public LiveData<String> getTrackingButtonText() {
        return mTrackingButtonText;
    }
    public Calendar getFromCalendar() {
        return mFromCalendar.getValue();
    }

    public Calendar getToCalendar() {
        return mToCalendar.getValue();
    }

    public void setFromCalendar(int year, int monthOfYear, int dayOfMonth) {
        getFromCalendar().set(Calendar.YEAR, year);
        getFromCalendar().set(Calendar.MONTH, monthOfYear);
        getFromCalendar().set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setFromText();
    }

    public void setToCalendar(int year, int monthOfYear, int dayOfMonth) {
        getToCalendar().set(Calendar.YEAR, year);
        getToCalendar().set(Calendar.MONTH, monthOfYear);
        getToCalendar().set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setToText();
    }

    private void setFromText() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        mFromText.setValue(format.format(getFromCalendar().getTime()));
        if (isDateRangeSet()) {
            mEnableButton.setValue(true);
        } else {
            mEnableButton.setValue(false);
        }
    }

    public LiveData<String> getFromText() {
        return mFromText;
    }

    private boolean isDateRangeSet() {
        return (!(getFromText().getValue().equals(NONE)) && !(getToText().getValue().equals(NONE)));
    }

    private void setToText() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        mToText.setValue(format.format(getToCalendar().getTime()));
        if (isDateRangeSet()) {
            mEnableButton.setValue(true);
        } else {
            mEnableButton.setValue(false);
        }
    }

    public LiveData<String> getToText() {
        return mToText;
    }

    public long getFromMiliseconds() {
        return getFromCalendar().getTimeInMillis();
    }

    public long getToMiliseconds() {
        return getToCalendar().getTimeInMillis();
    }


}
