package com.swag.jsonconverter.rules;

import androidx.annotation.NonNull;

import com.swag.jsonconverter.Constructor;

import java.util.Map;

public final class MapRule<K, V> extends Rule<Map<K, V>> {

    private Class<V> value;

    public MapRule(@NonNull Constructor<Map<K, V>> constructor,
                   @NonNull Class<V> value) {
        super(constructor);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public Map<K, V> construct() {
        return (Map<K, V>) constructor.construct();
    }

    public Class<V> getValueClass() {
        return value;
    }

    public V read(K key, @NonNull Map<K, V> map) {
        return map.get(key);
    }

    public void write(K key, V value, @NonNull Map<K, V> map) {
        map.put(key, value);
    }
}
