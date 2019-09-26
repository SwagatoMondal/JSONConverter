package com.swag.sample;

import androidx.annotation.Nullable;

import com.swag.jsonconverter.JSONConverter;

import org.json.JSONObject;

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
    public InnerEntity1 innerEntity1 = new InnerEntity1(new InnerEntity2("New Inner2 String"));

    public Entity() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Entity) {
            Entity newObj = (Entity) obj;
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

    public static class InnerEntity2 implements JSONConverter.JSONEntity {
        private String string = "Inner2 string";

        public InnerEntity2() {}

        public InnerEntity2(String string) {
            this.string = string;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof InnerEntity2) {
                InnerEntity2 newObj = (InnerEntity2) obj;
                return string.equals(newObj.string);
            } else {
                return false;
            }
        }

        @Override
        public JSONObject toJSON() {
            return JSONConverter.toJSON(this);
        }
    }
}
