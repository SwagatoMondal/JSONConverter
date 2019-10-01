package com.swag.jsonconverter;

import androidx.annotation.NonNull;

public interface Constructor<T> {
    @NonNull T construct();
}
