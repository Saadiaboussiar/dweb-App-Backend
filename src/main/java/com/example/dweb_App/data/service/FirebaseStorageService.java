package com.example.dweb_App.data.service;

import com.google.cloud.storage.Storage;

public class FirebaseStorageService {
    private Storage storage;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }
}
