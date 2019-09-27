# JSONConverter
A simple Java object to JSONObject and vice versa converter

## How to use
Ensure that the object you want to convert has a **default zero argument public constructor**. If your
class has non-primitive type member variables, ensure that those classes have variables which can be
converted to JSON in order to be considered for conversion. For further reference, refer the classes in this package :
https://github.com/SwagatoMondal/JSONConverter/tree/master/jsonconverter/src/androidTest/java/com/swag/jsonconverter/sample
### Convert Java object to JSONObject
```JSONConverter.toJSON(obj) : JSONObject```
### Convert JSONObject to Java object
```JSONConverter.fromJSON(jsonString, classType) : Object```
### Misc
You can ignore fields which you do not want to be part of JSON by adding @Ignore annotation

## As Library
The library equivalent is also available in library folder in root folder, just integrate the same in app.
