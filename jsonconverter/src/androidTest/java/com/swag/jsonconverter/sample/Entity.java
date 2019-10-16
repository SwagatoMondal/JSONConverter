package com.swag.jsonconverter.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.swag.jsonconverter.Ignore;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private boolean boolVar = true;
    private Boolean BooleanVar = Boolean.FALSE;
    private int intVar = 10;
    private Integer integerVar = 20;
    private float floatVar = 30.3f;
    private Float FloatVar = 40.3f;
    private double doubleVar = 50.05;
    private Double DoubleVar = 60.05;
    private long longVar = 1000L;
    private Long LongVar = 2000L;
    private String string = "Random string";
    public InnerEntity1 innerEntity1 = new InnerEntity1(new InnerStaticEntity("New Inner2 String"));
    @Ignore
    private String extra = "Entity : Extra string to be ignored";
    @NonNull
    public Map<String, InnerStaticEntity> map = new HashMap<>();

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Entity) {
            Entity newObj = (Entity) obj;
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
            return newObj.boolVar == boolVar && newObj.BooleanVar.equals(BooleanVar) &&
                    newObj.intVar == intVar && newObj.integerVar.equals(integerVar) &&
                    newObj.floatVar == floatVar && newObj.FloatVar.equals(FloatVar) &&
                    newObj.doubleVar == doubleVar && newObj.DoubleVar.equals(DoubleVar) &&
                    newObj.longVar == longVar && newObj.LongVar.equals(LongVar) &&
                    string.equals(newObj.string) && innerEntity1.equals(newObj.innerEntity1);
        } else {
            return false;
        }
    }

    public static class InnerStaticEntity {
        private String string;
        @Ignore
        private String extra = "InnerStaticEntity : Extra string to be ignored";

        public InnerStaticEntity(String string) {
            this.string = string;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof InnerStaticEntity) {
                InnerStaticEntity newObj = (InnerStaticEntity) obj;
                return string.equals(newObj.string);
            } else {
                return false;
            }
        }

        @NonNull
        @Override
        public String toString() {
            return string;
        }
    }
}
