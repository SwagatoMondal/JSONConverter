package com.swag.jsonconverter.sample;

import androidx.annotation.Nullable;

public class GrandParent {
    private String GPString = "GrandParent string";

    public GrandParent() {}

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GrandParent) {
            GrandParent newObj = (GrandParent) obj;
            return GPString.equals(newObj.GPString);
        } else {
            return false;
        }
    }
}
