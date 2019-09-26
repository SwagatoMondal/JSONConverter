package com.swag.sample;

import androidx.annotation.Nullable;

import com.swag.jsonconverter.JSONConverter;

import org.json.JSONObject;

public class InnerEntity1 implements JSONConverter.JSONEntity {
    public Entity.InnerEntity2 innerEntity2;

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

    @Override
    public JSONObject toJSON() {
        return JSONConverter.toJSON(this);
    }
}
