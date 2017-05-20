package com.audioapplication.Models;

import io.realm.RealmObject;

public class AudioResponse {
    public String message;
    public AudioPayload payload;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AudioPayload getPayload() {
        return payload;
    }

    public void setPayload(AudioPayload payload) {
        this.payload = payload;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean success;
}

