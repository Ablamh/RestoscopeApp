package com.example.restoacopeapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import java.util.Map;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;

public class AccessibilityViewModel extends ViewModel {
    private final MutableLiveData<Map<String, Boolean>> mobiliteData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> photosData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Boolean>> deficienceVisuelleData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Boolean>> deficienceAuditiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userId = new MutableLiveData<>();

    // Mobilité physique
    public void saveMobiliteData(Map<String, Boolean> data) {
        mobiliteData.setValue(data);
    }
    public LiveData<Map<String, Boolean>> getMobiliteData() {
        return mobiliteData;
    }

    // Photos
    public void savePhotosData(List<String> photos) {
        photosData.setValue(photos);
    }
    public LiveData<List<String>> getPhotosData() {
        return photosData;
    }

    // Déficience visuelle
    public void saveDeficienceVisuelleData(Map<String, Boolean> data) {
        deficienceVisuelleData.setValue(data);
    }
    public LiveData<Map<String, Boolean>> getDeficienceVisuelleData() {
        return deficienceVisuelleData;
    }

    // Déficience auditive
    public void saveDeficienceAuditiveData(Map<String, Boolean> data) {
        deficienceAuditiveData.setValue(data);
    }
    public LiveData<Map<String, Boolean>> getDeficienceAuditiveData() {
        return deficienceAuditiveData;
    }

    // User ID
    public void setUserId(String id) {
        userId.setValue(id);
    }
    public LiveData<String> getUserId() {
        return userId;
    }
}