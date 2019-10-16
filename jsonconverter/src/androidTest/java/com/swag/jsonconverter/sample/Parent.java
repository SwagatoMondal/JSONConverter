package com.swag.jsonconverter.sample;

import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Parent extends GrandParent {
    private String string = "Parent string";
    List<Boolean> list = new LinkedList<>();

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Parent) {
            Parent newObj = (Parent) obj;

            if (newObj.list == null) {
                return false;
            } else {
                Iterator<Boolean> it1 = list.iterator();
                Iterator<Boolean> it2 = newObj.list.iterator();

                while (it1.hasNext() && it2.hasNext()) {
                    Boolean b1 = it1.next();
                    Boolean b2 = it2.next();
                    if (!b1.equals(b2)) {
                        return false;
                    }
                }

                if (it1.hasNext() != it2.hasNext()) {
                    return false;
                }
            }

            return string.equals(newObj.string);
        } else {
            return false;
        }
    }
}
