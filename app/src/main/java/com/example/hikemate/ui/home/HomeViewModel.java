package com.example.hikemate.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    // MutableLiveData untuk lokasi
    private final MutableLiveData<String> mLocation;

    // MutableLiveData untuk greeting
    private final MutableLiveData<String> mGreeting;

    public HomeViewModel() {

        mLocation = new MutableLiveData<>();
        mLocation.setValue("Gunung Lawu");

        mGreeting = new MutableLiveData<>();
        mGreeting.setValue("Halo, Atha!");
    }

    // Mendapatkan LiveData untuk lokasi
    public LiveData<String> getLocation() {
        return mLocation;
    }

    // Mendapatkan LiveData untuk greeting
    public LiveData<String> getGreeting() {
        return mGreeting;
    }
}