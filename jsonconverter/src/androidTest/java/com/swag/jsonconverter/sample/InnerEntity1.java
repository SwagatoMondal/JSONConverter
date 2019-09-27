package com.swag.jsonconverter.sample;

import androidx.annotation.Nullable;

import com.swag.jsonconverter.Ignore;

public class InnerEntity1 {
    public Entity.InnerEntity2 innerEntity2;
    private String string = "Inner1 string";
    @Ignore
    private String extra = "InnerEntity1 : Extra string to be ignored";

    public InnerEntity1() {}

    InnerEntity1(Entity.InnerEntity2 innerEntity2) {
        this.innerEntity2 = innerEntity2;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof InnerEntity1) {
            InnerEntity1 newObj = (InnerEntity1) obj;
            return innerEntity2.equals(newObj.innerEntity2);
        } else {
            return false;
        }
    }
}
