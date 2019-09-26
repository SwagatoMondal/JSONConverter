package com.swag.jsonconverter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JSONConverter {

    private static final String TAG = JSONConverter.class.getSimpleName();
    private static final String TYPE = "typeZZZ";

    private static String getName(Class<?> cls) {
        String name = cls.getCanonicalName();
        if (cls.isMemberClass() && cls.getEnclosingClass() != null) {
            name = cls.getEnclosingClass().getCanonicalName() + "$" + cls.getSimpleName();
        }
        Log.i(TAG, "getName : Found object - " + name);
        return name;
    }

    /**
     * Method to convert the given object to an equivalent {@link JSONObject}. This method will also
     * consider the member variables which implements {@link JSONEntity} in the conversion process
     * in infinite cycle. Any variable whose type doesn't fall under the types supported type by
     * {@link JSONObject} and {@link JSONEntity} will be ignored.
     * @param object The object to be converted
     * @return the equivalent {@link JSONObject}, otherwise {@code null}
     */
    @Nullable
    public static JSONObject toJSON(@NonNull Object object) {
        try {
            final JSONObject jsonObject = new JSONObject();
            Class type = object.getClass();
            for (Field field : type.getDeclaredFields()) {
                Class<?> fieldType = field.getType();

                // Ignore static, and force ignored variables
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                } else if (field.isAnnotationPresent(Ignore.class)) {
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
                } else if (JSONEntity.class.isAssignableFrom(fieldType)) {
                    final Method toJSON = fieldType.getMethod("toJSON");
                    final Object fieldObj = toJSON.invoke(field.get(object));
                    if (fieldObj != null) {
                        JSONObject fieldJson = (JSONObject) fieldObj;
                        fieldJson.put(TYPE, getName(fieldType));
                        jsonObject.put(field.getName(), fieldJson);
                    }
                } else {
                    Log.i(TAG, "toJSON : Ignoring field, as type can't be converted");
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
    public static Object fromJSON(@NonNull JSONObject jsonObject, @NonNull Class<? extends  Object> type) {
        try {
            Object object;
            try {
                object = type.getConstructor().newInstance();
            } catch (NoSuchMethodException m) {
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
                } else if (JSONEntity.class.isAssignableFrom(fieldType)) {
                    final JSONObject obj = jsonObject.getJSONObject(field.getName());
                    Log.i(TAG, "fromJSON : Found JSONEntity - " + obj.toString());
                    field.set(object, fromJSON(obj, Class.forName(obj.getString(TYPE))));
                } else {
                    Log.i(TAG, "fromJSON : Ignoring field, as type can't be converted");
                }
            }

            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface JSONEntity {
        JSONObject toJSON();
    }
}
