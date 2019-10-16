package com.swag.jsonconverter.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.swag.jsonconverter.Ignore;

import java.util.HashMap;
import java.util.Map;

public class InnerEntity1 {
    public Entity.InnerStaticEntity innerStaticEntity;
    private String string = "Inner1 string";
    InnerEntity2 innerEntity2 = new InnerEntity2(32.56);
    @Ignore
    private String extra = "InnerEntity1 : Extra string to be ignored";
    @NonNull
    public Map<String, Boolean> map = new HashMap<>();

    InnerEntity1(Entity.InnerStaticEntity innerStaticEntity) {
        this.innerStaticEntity = innerStaticEntity;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof InnerEntity1) {
            InnerEntity1 newObj = (InnerEntity1) obj;
            if (newObj.map == null) {
                return false;
            } else if (map.size() != newObj.map.size()) {
                return false;
            } else {
                for (String key : map.keySet()) {
                    if (newObj.map.containsKey(key) &&
                            (map.get(key) != null && map.get(key).equals(newObj.map.get(key)))) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
            return innerStaticEntity.equals(newObj.innerStaticEntity) && string.equals(newObj.string)
                    && innerEntity2.equals(newObj.innerEntity2);
        } else {
            return false;
        }
    }

    public class InnerEntity2 {
        private double y;
        protected InnerEntity3 innerEntity3 = new InnerEntity3(10);

        InnerEntity2(double y) {
            this.y = y;
        }

        public class InnerEntity3 {
            private int x;

            InnerEntity3(int x) {
                this.x = x;
            }

            @Override
            public boolean equals(@Nullable Object obj) {
                if (obj instanceof InnerEntity3) {
                    InnerEntity3 entity3 = (InnerEntity3) obj;
                    return x == entity3.x;
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof InnerEntity2) {
                InnerEntity2 entity2 = (InnerEntity2) obj;
                return y == entity2.y && innerEntity3.equals(entity2.innerEntity3);
            } else {
                return false;
            }
        }
    }
}
