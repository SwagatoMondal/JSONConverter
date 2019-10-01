package com.swag.jsonconverter.rules;

import androidx.annotation.NonNull;

import com.swag.jsonconverter.Constructor;

public abstract class Rule<Object> {

    Constructor<?> constructor;

    Rule(@NonNull Constructor<?> constructor) {
        this.constructor = constructor;
    }
}
