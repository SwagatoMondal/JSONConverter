package com.swag.jsonconverter.rules;

import androidx.annotation.NonNull;

import com.swag.jsonconverter.Constructor;

import java.util.List;

public final class ListRule<E> extends Rule<List<E>> {

    private Class<E> value;

    public ListRule(@NonNull Constructor<List<E>> constructor,
                    @NonNull Class<E> value) {
        super(constructor);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public List<E> construct() {
        return (List<E>) constructor.construct();
    }

    public Class<E> getValueClass() {
        return value;
    }
}
