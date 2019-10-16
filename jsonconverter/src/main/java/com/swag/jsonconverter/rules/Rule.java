package com.swag.jsonconverter.rules;

import androidx.annotation.NonNull;

import com.swag.jsonconverter.Constructor;

public abstract class Rule<T> {

    Constructor<?> constructor;

    Rule(@NonNull Constructor<T> constructor) {
        this.constructor = constructor;
    }
}
