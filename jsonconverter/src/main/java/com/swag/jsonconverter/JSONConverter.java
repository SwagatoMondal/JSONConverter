package com.swag.jsonconverter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class JSONConverter {

    private static final String TAG = JSONConverter.class.getSimpleName();
    // Unique key to identify the type of the class
    private static final String TYPE = TAG + "_type_ZZZ";

    /**
     * Method to get a runtime class path of the given class, which is used by
     * {@link Class#forName(String)} to create object.
     * @param cls The class
     * @return the runtime class path of the given class
     */
    private static String getName(Class<?> cls) {
        String name = cls.getCanonicalName();
        if (cls.isMemberClass() && cls.getEnclosingClass() != null) {
            name = getName(cls.getEnclosingClass()) + "$" + cls.getSimpleName();
        }
        Log.i(TAG, "getName : Found object - " + name);
        return name;
    }

    /**
     * Method to determine whether the given field is the reference added by compiler of the
     * enclosing class of the given inner class.
     * @param cls The inner class
     * @param field The field
     * @return {@code true} if the given field is the reference added by compiler of the
     *      enclosing class of the given inner class or {@code false}
     */
    private static boolean isNonStaticInnerClass(Class<?> cls, Class<?> field) {
        return !Modifier.isStatic(cls.getModifiers()) && cls.getEnclosingClass() == field;
    }

    /**
     * Method to convert the given object to an equivalent {@link JSONObject}. This method will also
     * consider the member variables in the conversion process in infinite cycle. Any variable
     * whose type doesn't fall under the types supported type by {@link JSONObject} will be ignored.
     * @param object The object to be converted
     * @return the equivalent {@link JSONObject}, otherwise {@code null}
     */
    @Nullable
    public static JSONObject toJSON(@NonNull Object object) {
        try {
            final JSONObject jsonObject = new JSONObject();
            Class type = object.getClass();
            Log.i(TAG, "toJSON : Processing for " + type.getSimpleName());
            for (Field field : type.getDeclaredFields()) {
                Class<?> fieldType = field.getType();

                if (Modifier.isStatic(field.getModifiers())/*Ignore static variables*/) {
                    continue;
                } else if (field.isAnnotationPresent(Ignore.class)/*Ignoring @Ignore variables*/) {
                    continue;
                } else if (isNonStaticInnerClass(type, fieldType)/*Ignoring outer class ref*/) {
                    continue;
                }

                field.setAccessible(true);
                Log.i(TAG, "toJSON : Field type : " + fieldType.getSimpleName());
                if (int.class == fieldType) {
                    jsonObject.put(field.getName(), field.getInt(object));
                } else if (Integer.class == fieldType) {
                    jsonObject.put(field.getName(), field.get(object) == null ? null : (int) field.get(object));
                } else if (boolean.class == fieldType) {
                    jsonObject.put(field.getName(), field.getBoolean(object));
                } else if (Boolean.class == fieldType) {
                    jsonObject.put(field.getName(), field.get(object) == null ? null : (boolean) field.get(object));
                } else if (float.class == fieldType) {
                    jsonObject.put(field.getName(), field.getFloat(object));
                }  else if (double.class == fieldType) {
                    jsonObject.put(field.getName(), field.getDouble(object));
                } else if (Double.class == fieldType) {
                    jsonObject.put(field.getName(), field.get(object) == null ? null : (double) field.get(object));
                } else if (Float.class == fieldType) {
                    jsonObject.put(field.getName(), field.get(object) == null ? null : (float) field.get(object));
                } else if (long.class == fieldType) {
                    jsonObject.put(field.getName(), field.getLong(object));
                } else if (Long.class == fieldType) {
                    jsonObject.put(field.getName(), field.get(object) == null ? null : (long) field.get(object));
                } else if (String.class == fieldType) {
                    jsonObject.put(field.getName(), field.get(object));
                } else {
                    final Object fieldObj = field.get(object);
                    if (fieldObj != null) {
                        JSONObject fieldJson = toJSON(fieldObj);
                        if (fieldJson != null) {
                            fieldJson.put(TYPE, getName(fieldType));
                        } else {
                            Log.i(TAG, "toJSON : Ignoring field, as null JSON");
                        }
                        jsonObject.put(field.getName(), fieldJson);
                    } else {
                        Log.i(TAG, "toJSON : Ignoring field, as type can't be converted");
                    }
                }
            }
            Log.i(TAG, "Returning string");
            return jsonObject;
        } catch (Exception e) {
            Log.i(TAG, "Returning null");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to convert the given {@link JSONObject} to an object equivalent to the given type.
     * Ensure the given object(s) taken into conversion has a default zero argument constructor.
     * @param jsonObject JSON equivalent of the object to converted
     * @param type type of the final object to constructed
     * @return an equivalent object of the given type filled with the values in the given JSON,
     * otherwise {@code null} in error scenario.
     */
    public static Object fromJSON(@NonNull JSONObject jsonObject, @NonNull Class<?> type) {
        return fromJSON(jsonObject, type, null);
    }

    private static Object fromJSON(@NonNull JSONObject jsonObject, @NonNull Class<?> type,
                                   @Nullable Object enclosing) {
        try {
            Log.i(TAG, "fromJSON : Processing for " + type.getSimpleName() + "; " +
                    type + "; " + enclosing);
            Object object;
            try {
                if (!Modifier.isStatic(type.getModifiers()) && enclosing != null &&
                        type.getEnclosingClass() == enclosing.getClass()) {
                    object = type.getConstructor(enclosing.getClass()).newInstance(enclosing);
                } else {
                    object = type.getConstructor().newInstance();
                }
            } catch (NoSuchMethodException m) {
                Log.e(TAG, "Public zero argument constructor missing for : " + type +
                        ", hence ignoring");
                m.printStackTrace();
                return null;
            }

            for (Field field : type.getDeclaredFields()) {
                Class fieldType = field.getType();
                Log.i(TAG, "fromJSON : Field type : " + fieldType.getSimpleName());

                // Ignore static, and force ignored variables
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                } else if (field.isAnnotationPresent(Ignore.class)) {
                    continue;
                }

                field.setAccessible(true);

                if (int.class == fieldType) {
                    field.setInt(object, jsonObject.getInt(field.getName()));
                } else if (Integer.class == fieldType) {
                    field.set(object, jsonObject.get(field.getName()));
                } else if (boolean.class == fieldType) {
                    field.setBoolean(object, jsonObject.getBoolean(field.getName()));
                } else if (Boolean.class == fieldType) {
                    field.set(object, jsonObject.getBoolean(field.getName()));
                } else if (double.class == fieldType) {
                    field.setDouble(object, jsonObject.getDouble(field.getName()));
                } else if (Double.class == fieldType) {
                    field.set(object, jsonObject.getDouble(field.getName()));
                } else if (float.class == fieldType) {
                    field.setFloat(object, (float) jsonObject.getDouble(field.getName()));
                } else if (Float.class == fieldType) {
                    field.set(object, (float) jsonObject.getDouble(field.getName()));
                } else if (long.class == fieldType) {
                    field.setLong(object, jsonObject.getLong(field.getName()));
                } else if (Long.class == fieldType) {
                    field.set(object, jsonObject.getLong(field.getName()));
                } else if (String.class == fieldType) {
                    field.set(object, jsonObject.getString(field.getName()));
                } else {
                    final JSONObject obj = jsonObject.optJSONObject(field.getName());
                    if (obj != null && obj.has(TYPE)) {
                        Log.i(TAG, "fromJSON : Found JSONObject - " + obj.toString());
                        field.set(object, fromJSON(obj, Class.forName(obj.getString(TYPE)), object));
                    } else {
                        Log.i(TAG, "fromJSON : Found null JSONObject or malformed object, hence ignored");
                    }
                }
            }

            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
