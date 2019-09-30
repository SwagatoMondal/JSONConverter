package com.swag.jsonconverter.sample;

import androidx.annotation.Nullable;

public class Parent extends GrandParent {
    private String string = "Parent string";

    public Parent() {}

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Parent) {
            Parent newObj = (Parent) obj;
            return string.equals(newObj.string);
        } else {
            return false;
        }
    }
}
