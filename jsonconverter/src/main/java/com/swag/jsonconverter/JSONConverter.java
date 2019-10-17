package com.swag.jsonconverter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.swag.jsonconverter.rules.ListRule;
import com.swag.jsonconverter.rules.MapRule;
import com.swag.jsonconverter.rules.Rule;
import com.swag.jsonconverter.rules.RuleKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class JSONConverter<T> {

    private static final String TAG = JSONConverter.class.getSimpleName();
    private static boolean loggingEnabled;

    /**
     * Method to enable logging
     * @param enabled {@code true} if logging is enabled, otherwise {@code false}
     */
    public static void loggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
    }

    private Map<RuleKey, Rule> rules = new HashMap<>();

    private static boolean compareObject(@NonNull Object o1, @NonNull Object o2) {
        if (o1.getClass() == int.class && ((int) o1) != ((int) o2)) {
            return false;
        } else if (o1.getClass() == long.class && ((long) o1) != ((long) o2)) {
            return false;
        } else if (o1.getClass() == boolean.class && ((boolean) o1) != ((boolean) o2)) {
            return false;
        } else if (o1.getClass() == double.class && ((double) o1) != ((double) o2)) {
            return false;
        } else if (o1.getClass() == Integer.class && o2.getClass() == Long.class) {
            return ((Integer) o1).intValue() == (((Long) o2).intValue());
        } else return o1.equals(o2);
    }

    /**
     * Method to compare two {@link JSONArray} objects. This will check each of the entries in first
     * JSON to it's equivalent in the second and compare the values.
     * @param json1 Represents first JSON
     * @param json2 Represents second JSON
     * @return {@code true} if they are equal, otherwise false
     */
    public static boolean compareJSON(@NonNull JSONArray json1, @NonNull JSONArray json2) {
        if (json1.length() == json2.length()) {
            for (int i = 0; i < json1.length(); i++) {
                try {
                    final Object o1 = json1.get(i);
                    final Object o2 = json2.get(i);

                    if (o1 instanceof JSONObject && o2 instanceof JSONObject) {
                        if (!compareJSON((JSONObject) o1, (JSONObject) o2)) {
                            return false;
                        }
                    } else if (o1 instanceof JSONArray && o2 instanceof JSONArray) {
                        if (!compareJSON((JSONArray) o1, (JSONArray) o2)) {
                            return false;
                        }
                    } else {
                        if (!compareObject(o1, o2)) {
                            return false;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to compare two {@link JSONObject} objects. This will check each of the entries in
     * first JSON to it's equivalent in the second and compare the values.
     * @param json1 Represents first JSON
     * @param json2 Represents second JSON
     * @return {@code true} if they are equal, otherwise false
     */
    public static boolean compareJSON(@NonNull JSONObject json1, @NonNull JSONObject json2) {
        if (json1.length() == json2.length()) {
            Iterator<String> keys = json1.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    final Object o1 = json1.get(key);
                    final Object o2 = json2.get(key);
                    if (o1 instanceof JSONObject && o2 instanceof JSONObject) {
                        if (!compareJSON((JSONObject) o1, (JSONObject) o2)) {
                            return false;
                        }
                    } else if (o1 instanceof JSONArray && o2 instanceof JSONArray) {
                        if (!compareJSON((JSONArray) o1, (JSONArray) o2)) {
                            return false;
                        }
                    } else {
                        if (!compareObject(o1, o2)) {
                            return false;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
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

    public JSONConverter<T> addRule(@NonNull RuleKey key, @NonNull Rule types) {
        rules.put(key, types);
        return this;
    }

    private static boolean jsonSupported(Class<?> type) {
        return int.class == type || Integer.class == type || boolean.class == type
                || Boolean.class == type || double.class == type || Double.class == type
                || float.class == type || Float.class == type || long.class == type
                || Long.class == type || String.class == type;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private JSONObject toJSON(@NonNull Object object, Class<?> type) {
        try {
            JSONObject jsonObject = null;
            if (loggingEnabled) {
                Log.i(TAG, "toJSON : Processing for " + type.getSimpleName());
            }

            // Process super classes
            if (type.getSuperclass() != null) {
                Class parentType = type.getSuperclass();
                if (loggingEnabled) {
                    Log.i(TAG, "toJSON : Processing for parent " + parentType.getSimpleName());
                }
                jsonObject = toJSON(object, parentType);
            }

            if (null == jsonObject) {
                jsonObject = new JSONObject();
            }

            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(object) == null) {
                    if (loggingEnabled) {
                        Log.i(TAG, "toJSON : Ignoring for field as no entry in Object " + field.getName());
                    }
                    continue;
                }
                Class<?> fieldType = field.getType();

                if (Modifier.isStatic(field.getModifiers())/*Ignore static variables*/) {
                    continue;
                } else if (field.isAnnotationPresent(Ignore.class)/*Ignoring @Ignore variables*/) {
                    continue;
                } else if (isNonStaticInnerClass(type, fieldType)/*Ignoring outer class ref*/) {
                    continue;
                }

                if (int.class == fieldType || Integer.class == fieldType) {
                    jsonObject.put(field.getName(), (int) field.get(object));
                } else if (boolean.class == fieldType || Boolean.class == fieldType) {
                    jsonObject.put(field.getName(), (boolean) field.get(object));
                } else if (double.class == fieldType || Double.class == fieldType) {
                    jsonObject.put(field.getName(), (double) field.get(object));
                } else if (float.class == fieldType || Float.class == fieldType) {
                    jsonObject.put(field.getName(), (float) field.get(object));
                } else if (long.class == fieldType || Long.class == fieldType) {
                    jsonObject.put(field.getName(), (long) field.get(object));
                } else if (String.class == fieldType || JSONObject.class == fieldType || JSONArray.class == fieldType) {
                    jsonObject.put(field.getName(), field.get(object));
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    final Rule types = rules.get(new RuleKey(field.getName(), type));
                    if (types instanceof MapRule) {
                        final JSONObject mapJson = new JSONObject();
                        final Object mapObj = field.get(object);
                        if (mapObj != null) {
                            Map map = (Map) mapObj;
                            for (Object entry : map.keySet()) {
                                final Object val = ((MapRule) types).read(entry, map);
                                if (val != null) {
                                    final Object put = jsonSupported(val.getClass()) ? val :
                                            toJSON(val, val.getClass());
                                    mapJson.put(entry.toString(), put);
                                }
                            }
                        }
                        jsonObject.put(field.getName(), mapJson);
                    } else {
                        if (loggingEnabled) {
                            Log.i(TAG, "toJSON : Ignoring field, as Map rule not defined - " +
                                    fieldType.getSimpleName());
                        }
                    }
                } else if (List.class.isAssignableFrom(fieldType)) {
                    final Rule types = rules.get(new RuleKey(field.getName(), type));
                    if (types instanceof ListRule) {
                        final JSONArray array = new JSONArray();
                        final Object listObj = field.get(object);
                        if (listObj != null) {
                            List list = (List) listObj;
                            for (Object o : list) {
                                final Object put = jsonSupported(o.getClass()) ? o :
                                        toJSON(o, o.getClass());
                                array.put(put);
                            }
                        }
                        jsonObject.put(field.getName(), array);
                    } else {
                        if (loggingEnabled) {
                            Log.i(TAG, "toJSON : Ignoring field, as List rule not defined - " +
                                    fieldType.getSimpleName());
                        }
                    }
                } else {
                    final Object fieldObj = field.get(object);
                    if (fieldObj != null) {
                        jsonObject.put(field.getName(), toJSON(fieldObj, fieldObj.getClass()));
                    } else {
                        if (loggingEnabled) {
                            Log.i(TAG, "toJSON : Ignoring field, as type can't be converted - " +
                                    fieldType.getSimpleName());
                        }
                    }
                }
            }

            if (loggingEnabled) {
                Log.i(TAG, "Returning string");
            }
            return jsonObject;
        } catch (Exception e) {
            if (loggingEnabled) {
                Log.i(TAG, "Returning null");
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Method to convert the given object to an equivalent {@link JSONObject}. This method will also
     * consider the member variables in the conversion process in infinite cycle. Any variable
     * whose type doesn't fall under the types supported type by {@link JSONObject} will be ignored.
     * @param object The object to be converted
     * @return the equivalent {@link JSONObject}, otherwise {@code null}
     */
    @Nullable
    public JSONObject toJSON(@NonNull T object) {
        return toJSON(object, object.getClass());
    }

    /**
     * Method to convert the given {@link JSONObject} to an object equivalent to the given type.
     * Ensure the given object(s) taken into conversion has a default zero argument constructor.
     * @param jsonObject JSON equivalent of the object to converted
     * @param type type of the final object to constructed
     * @return an equivalent object of the given type filled with the values in the given JSON,
     * otherwise {@code null} in error scenario.
     */
    public T fromJSON(@NonNull JSONObject jsonObject, @NonNull Class<T> type) {
        return type.cast(fromJSON(jsonObject, type, null, null));
    }

    private static Object getValueForType(@NonNull Class<?> type) {
        if (int.class == type || long.class == type) {
            return 0;
        } else if (boolean.class == type) {
            return false;
        } else if (double.class == type || float.class == type) {
            return 0.0;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Object fromJSON(@NonNull JSONObject jsonObject, @NonNull Class<?> type,
                            @Nullable Object enclosing, @Nullable Object object) {
        try {
            if (loggingEnabled) {
                Log.i(TAG, "fromJSON : Processing for " + type.getSimpleName() + "; " +
                        type + "; " + enclosing);
            }

            if (null == object) {
                try {
                    Constructor<?>[] constructors = type.getDeclaredConstructors();
                    // Default constructor
                    if (constructors.length == 0) {
                        object = type.newInstance();
                    } else {
                        // Declared constructor
                        Constructor<?> constructor = constructors[0];
                        constructor.setAccessible(true);
                        final int length =  constructor.getParameterTypes().length;
                        // Default constructor
                        if (length == 0) {
                            object = constructor.newInstance();
                        } else {
                            int i = 0;
                            Object[] params = new Object[length];
                            for (Class p : constructor.getParameterTypes()) {
                                params[i++] = getValueForType(p);
                            }
                            object = constructor.newInstance(params);
                        }
                    }
                } catch (Exception g) {
                    if (loggingEnabled) {
                        Log.e(TAG, "Something went wrong while creating object for : " + type +
                                ", hence ignoring");
                        g.printStackTrace();
                    }
                    return null;
                }
            }

            // Process super classes
            if (type.getSuperclass() != null) {
                Class parentType = type.getSuperclass();
                if (loggingEnabled) {
                    Log.i(TAG, "fromJSON : Processing for parent " + parentType.getSimpleName());
                }
                object = fromJSON(jsonObject, parentType, enclosing, object);
            }

            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (!jsonObject.has(field.getName())) {
                    if (loggingEnabled) {
                        Log.i(TAG, "fromJSON : Ignoring for field as no entry in JSON " + field.getName());
                    }
                    continue;
                }
                Class fieldType = field.getType();

                // Ignore static, and force ignored variables
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                } else if (field.isAnnotationPresent(Ignore.class)) {
                    continue;
                }

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
                    field.set(object, jsonObject.optString(field.getName()));
                } else if (JSONObject.class == fieldType) {
                    field.set(object, jsonObject.getJSONObject(field.getName()));
                }  else if (JSONArray.class == fieldType) {
                    field.set(object, jsonObject.getJSONArray(field.getName()));
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    final Rule types = rules.get(new RuleKey(field.getName(), type));
                    if (types instanceof MapRule) {
                        final JSONObject mapJson = jsonObject.optJSONObject(field.getName());
                        if (mapJson != null) {
                            if (loggingEnabled) {
                                Log.i(TAG, "fromJSON : Found Map object - " + mapJson.toString());
                            }
                            final MapRule mapRule = (MapRule) types;
                            Map toAssign = mapRule.construct();
                            Iterator<String> keys = mapJson.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                final Object put = jsonSupported(mapRule.getValueClass()) ?
                                        mapRule.getValueClass().cast(mapJson.get(key)) :
                                        fromJSON(mapJson.getJSONObject(key), mapRule.getValueClass());
                                mapRule.write(key, put, toAssign);
                            }
                            field.set(object, toAssign);
                        }
                    } else {
                        if (loggingEnabled) {
                            Log.i(TAG, "fromJSON : Ignoring field, as Map rule not defined - " +
                                    fieldType.getSimpleName());
                        }
                    }
                } else if (List.class.isAssignableFrom(fieldType)) {
                    final Rule types = rules.get(new RuleKey(field.getName(), type));
                    if (types instanceof ListRule) {
                        final JSONArray listJson = jsonObject.optJSONArray(field.getName());
                        if (listJson != null) {
                            if (loggingEnabled) {
                                Log.i(TAG, "fromJSON : Found List object");
                            }
                            final ListRule listRule = (ListRule) types;
                            List toAssign = listRule.construct();
                            for (int i = 0; i < listJson.length(); i++) {
                                final Object val = listJson.get(i);
                                final Object put = jsonSupported(val.getClass()) ?
                                        listRule.getValueClass().cast(val) :
                                        fromJSON(listJson.getJSONObject(i), listRule.getValueClass());
                                toAssign.add(put);
                            }
                            field.set(object, toAssign);
                        }
                    } else {
                        if (loggingEnabled) {
                            Log.i(TAG, "fromJSON : Ignoring field, as List rule not defined - " +
                                    fieldType.getSimpleName());
                        }
                    }
                } else {
                    final JSONObject obj = jsonObject.optJSONObject(field.getName());
                    if (obj != null) {
                        if (loggingEnabled) {
                            Log.i(TAG, "fromJSON : Found JSONObject - " + obj.toString());
                        }
                        field.set(object, fromJSON(obj, fieldType, object, null));
                    } else {
                        if (loggingEnabled) {
                            Log.i(TAG, "fromJSON : Found null JSONObject or malformed object, hence ignored - "
                                    + fieldType.getSimpleName());
                        }
                    }
                }
            }

            return object;
        } catch (Exception e) {
            if (loggingEnabled) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
