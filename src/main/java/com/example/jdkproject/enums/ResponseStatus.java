package com.example.jdkproject.enums;

public enum ResponseStatus {
    SUCCESS(200), BAD_REQUEST(400), NOT_FOUND(404);

    final int code;

    ResponseStatus(int code) {
        this.code = 0;
    }

    public int getCode() {
        return this.code;
    }
}
