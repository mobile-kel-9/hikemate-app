package com.example.hikemate.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> place = new MutableLiveData<>();
    private final MutableLiveData<Float> height = new MutableLiveData<>();

    public void setPlace(String place) {
        this.place.setValue(place);
    }

    public LiveData<String> getPlace() {
        return place;
    }

    public void setHeight(float height) {
        this.height.setValue(height);
    }

    public LiveData<Float> getHeight() {
        return height;
    }
}