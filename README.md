# JSONConverter
A simple Java object to JSONObject and vice versa converter

## How to use
Ensure that the object you want to convert has a default public constructor
### Convert Java object to JSONObject
Use JSONConverter.toJSON(obj) : JSONObject
### Convert JSONObject to Java object
Use JSONConverter.fromJSON(jsonString, classType) : Object
### Misc
You can ignore fields which you do not want to be part of JSON by adding @Ignore annotation

## As Library
The library equivalent is also available in library folder in root folder, just integrate the same in app.
