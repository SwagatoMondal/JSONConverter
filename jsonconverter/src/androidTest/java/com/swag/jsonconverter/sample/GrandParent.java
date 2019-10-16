package com.swag.jsonconverter.sample;

import androidx.annotation.Nullable;

public class GrandParent {
    private String GPString = "GrandParent string";

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
