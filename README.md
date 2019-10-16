# JSONConverter
A simple Java object to JSONObject and vice-versa converter

## How to use
If your class has non-primitive type member variables, ensure that those classes have variables which can be
converted to JSON in order to be considered for conversion. For further reference, refer the classes in this package :
https://github.com/SwagatoMondal/JSONConverter/tree/master/jsonconverter/src/androidTest/java/com/swag/jsonconverter/sample
### Convert Java object to JSONObject
```
JSONConverter<T> converter = new JSONConverter<>();
JSONObject json = converter.toJSON(obj);
```
### Convert JSONObject to Java object
```
JSONConverter<T> converter = new JSONConverter<>();
T obj = converter.fromJSON(jsonString, classType);
```
### If member variables are Map / List
Add rule, in case your class have member variables which are non-primitive and are JAVA declared classes. For example - Collections.
```
class YourClass {
    private Map<String, Boolean> map;
}

JSONConverter<T> converter = new JSONConverter<T>().addRule(new RuleKey("map", YourClass.class),
    new MapRule<>(new Constructor<Map<String, Boolean>>() {
        @NonNull
        @Override
        public Map<String, Boolean> construct() {
            return new HashMap<>();
        }
    }, Boolean.class));
JSONObject json = converter.toJSON(obj);
// Do something
T obj = converter.fromJSON(jsonString, classType);
```
### Misc
You can ignore fields which you do not want to be part of JSON by adding @Ignore annotation

## As Library
The library equivalent is also available in library folder in root folder, just integrate the same in app.
