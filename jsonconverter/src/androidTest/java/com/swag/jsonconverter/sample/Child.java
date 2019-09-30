package com.swag.jsonconverter.sample;

import androidx.annotation.Nullable;

public class Child extends Parent {

    private String childString = "Child string";

    public Child() {}

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Child) {
            Child newObj = (Child) obj;
            return super.equals(obj) && childString.equals(newObj.childString);
        } else {
            return false;
        }
    }
}
