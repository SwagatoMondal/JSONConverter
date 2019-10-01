package com.swag.jsonconverter.rules;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class RuleKey {
    private String fieldName;
    private Class<?> originClass;

    public RuleKey(@NonNull String fieldName, @NonNull Class<?> originClass) {
        this.fieldName = fieldName;
        this.originClass = originClass;
    }

    @Override
    public int hashCode() {
        return fieldName.hashCode() + originClass.getName().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof RuleKey) {
            RuleKey newKey = (RuleKey) obj;
            return fieldName.equals(newKey.fieldName) && originClass == newKey.originClass;
        } else {
            return false;
        }
    }
}
