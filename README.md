# FIELDCOPY

FieldCopy is a library for creating Java Bean mappers.  It uses code generation to create *converter* classes. Each *converter* converts a source Java Bean object to an destination object of a different class.

Converters are defined in JSON files using a simple syntax. Here is a converter from CustomerEntity to CustomerDTO:

```json
"converters": [{
  "types": "com.company.CustomerEntity -> com.company.CustomerDTO",
  "fields": [
   "firstName -> firstName",
   "lastName -> lastName",
   "type-> customerType default(BUSINESS)"
  ]
 }]
```

This would generate the following converter class:

```java
public class CustomerEntityToCustomerDTOConverter<CustomerEntity, CustomerDTO> {
  @Override
  public CustomerDTO convert(CustomerEntity src, CustomerDTO dest, ConverterContext ctx) {
    // firstName -> firstName
	String tmp1 = src.getFirstName();
	dest.setFirstName(tmp1);
    
    // lastName -> lastName
	String tmp2 = src.getLastName();
	dest.setLastName(tmp2);
	
    // type -> customerType
    CustomerType tmp3 = src.getType();
    if (tmp3 == null) tmp3 = CustomerType.BUSINESS;
    dest.setCustomerType(tmp3);
    
    return dest;
  }
}
```

### Features

FieldCopy has many features to help with conversion:

* converts basic types such as conversion between int and Integer, Long, Double, and other Number types. See [Built-In Conversions](#built-in-conversions)


* handles Optional fields, adding or removing Optional as needed. See [Optional](#optional)

* default values can be defined that are used when a field is null. See [default](#default)

* handles copying of nested objects (also called sub-objects) using converters. See [Sub-Objects](#sub-objects)

* fields within sub-objects can be copied, such as "address.city -> city". See [Sub-Object Fields](#sub-object-fields)

* "auto" can be used to automatically copy all fields of the source object. See [auto](#auto)

* "custom" can be used when you want to write the conversion code for a field. See [custom](#custom)

* You can write your own converters and use them from a FieldCopy-created converter, and vice versa. See [Additional Converters](#additional_conveters)

### Usage

Add FieldCopy to your project

```xml
<dependency>
   <groupId>org.dnal-lang</groupId>
   <artifactId>fieldcopy</artifactId>
   <version>0.5.0</version>
</dependency>
```

FieldCopy is used in two phases.

#### Phase 1: Code Generation
Write the JSON file to define the converters. Then used FieldCopy to generation converter classes.  
This is normally done at build time.  

In addition to the converter classes, a *group class* (shown below as MyGroup.class) created, It is a registry of the
converters.

#### Phase 2: Use The Converters
Use the converters in your application. FieldCopy has a fluent API that is initialized using the *group class*. 

Use the Fluuent API to FieldCopy instance.  It is thread-safe and can be used throughout your application.
   
```java
FieldCopy fc = FieldCopy.using(MyGroup.class).build();
```

Use the FieldCopy instance to get a converter for a given source and destination class, and then use it.
The converter method *convert* returns the converted object.

```java
Converter<CustomerEntity, CustomerDTO> converter = fc.getConverter(CustomerEntity.class, CustomerDTO.class);

CustomerDTO dest = converter.convert(src, new CustomerDTO()); 
```


## JSON File Syntax
Here is a complete Fieldcopy JSON file. It defines two converters.


```json
{
 "version": "1.0",
 "config": {
    "defaultSourcePackage":"com.company.entities",
    "defaultDestinationPackage":"com.company.dtos"
 },
 "additionalNamedConverters": { },
 "additionalConverters": [],
 "converters": [{
   "types": "CustomerEntity -> CustomerDTO",
   "additionalConverters": [],
   "additionalNamedConverters": { },
   "fields": [
     "firstName -> firstName",
     "lastName -> lastName",
     "type-> customerType default(BUSINESS)"
  ]}, 
 {
  "types": "AddressEntity -> AddressDTO",
   "additionalConverters": [],
   "additionalNamedConverters": { },
  "fields": [
   "auto",
  ]
 }]
}
```

The JSON file has several parts.

#### *version*
Must be "1.0"

#### *config*
The follow configuration values can be used:

| Name  | Optional | Description |
| ----- | ------ | ------------- |
| defaultSourcePackage  | Yes | Java package to use in 'types' for the source class if none is specified  |
| defaultDestinationPackage  |  Yes | Java package to use in 'types' for the source class if none is specified  |
| defaultDestinationPackage  |  Yes | Can be used to enable validating date & time values at code generation time. See [Date and Time Format](#date-and-time-format)  |
| localDateFormat  |  Yes |  See [Date and Time Format](#date-and-time-format)  |
| localTimeFormat  |  Yes |  See [Date and Time Format](#date-and-time-format)  |
| localDateTimeFormat  |  Yes |  See [Date and Time Format](#date-and-time-format)  |
| zonedDateFormat  |  Yes |  See [Date and Time Format](#date-and-time-format)  |
| utilDateFormat  |  Yes |  See [Date and Time Format](#date-and-time-format)  |

#### *additionalConverters* and *additionalNamedConverters*
If you write any converters yourself, they are registered here.  See [Additional Converters](#additional_conveters)

#### *converters*
An array of converter definitions. Each definition consists of:

##### **package**
A string that contains the package name to use for source and destination classes.
*package* is otpional. You can define the package directly in *types*, or in
*defaultSourcePackage*, *defaultDestinationPackage*.

Example:

```json
"package" : "com.company.entities"
```

##### **types**
A string that contains a source class name + "->" + a destination class name.  If either class name does not contain a package then defaultSourcePackage or defaultDestinationPackage are used.

Example:

```json
"types" : "com.company.entities.CustomerEntity -> com.company.dtos.CustomerDTO"
```

The source class is the type of object that the converter reads data from.
The destination class is the type of object that the converter writes data to.

##### **name**
This is an optional field, where a name for the converter can be defined. Normally converters are identified by their source and destination classes.  However, the *using* modifier lets a field specify a converter by name, and can be useful if there are several converters for the same source and destintion classes.

Example:

```json
"name" : "MySpecialCustomerConverter"
```


##### **fields**
An array of strings, where each string defines a field to be copied.

The syntax is source field name + "->" + destination field name [optional modifiers].
Each field name that is mentioned must have a getter method or the field must be public.

Example:

```json
"fields": [
   "firstName -> firstName",
   "type-> customerType default(BUSINESS)"
   "id -> id required"
   ...
]
```

The first string can also be a value to be copied to the destination field name. This is used
to populate the destination object with specific values

Here we assign an enum value of Color.RED to the destination field *favoriteColor*. Note the use of single quotes as a string delimiter. 
You can use single or double quotes for string value, but single quotes are simpler within JSON.

```json
"fields": [
   "'RED' -> favoriteColor",
   ...
]
```
More about values HERE.

Each field can also have some modifiers.

| Name  | Optional | Description |
| ----- | ------ | ------------- |
| *auto*  | Yes | If specified then all fields that exist in both source and destination classes will be copied.|
| *custom*  | Yes | Specifies that converter class will be an abstract base class with an abstract method for converting this field. You will need write the derived class and implement the method.|
| *default* | Yes | A default value that will be used if the source object field is null. |
| *exclude*  |  Yes | When *auto* is used, *exclude* can be used to list fields that should not be copied by *auto*. |
| *required* | Yes | Indicates that the source value must not be null. The converter will throw an exception if it is. |
| *using* | Yes | Specifies a specific converter by name to use when converting this field. |

See [Field Modifiers](#field-modifiers) for more information.

## Field Modifiers

### *auto*
The *auto* command finds all matching source and destination fields and generates field definitions.  The source and destination fields must have the same name (case-sensitive) to be matched.

```json
"fields": [
   "auto",
   ...
]
```

It is equivalent to listing all the matching fields. For example, if three fields ("firstName", "lastName", and "birthDate") exist in 
source and destination classes, then *auto* is equivalent to.

```json
  "firstName -> firstName",
  "lastName -> lastName",
  "bithDate -> birthDate"
```

You can use *auto* for matchhing fields and then list the other fields explicitly.   

```json
"fields": [
   "auto",
   "points -> loyaltyPoints",
   "payStat -> paymentStatus"
   ...
]
```

### *custom*
Specifies that converter class will be an abstract base class with an abstract method for converting this field. You will need write the derived class and implement the method.

```json
"fields": [
   "birthDate -> birthDate custom",
   ...
]
```

This would generate an abstract method in the converter class. The abstract method is given the src field's value, and the overall source and destination objects.

```java
protected abstract LocalDate convertBithDate(LocalDate srcValue, Customer src, Customer dest, ConverterContext ctx);
```

### *default*
A default value that will be used if the source object field is null. The syntax supports numbers, strings, booleans, enums, and dates and times.

| Type  | Example | Description |
| ----- | ------ | ------------- |
| number  | default(-1) | The default value is -1|
| number  | default(110.45) | The default value is 110.45|
| string  | default('Boston') | The default value is "Boston". Either ' or " can be used to delimit string values.|
| string  | default(\"Boston\") | same as above. However double quotes require escaping within JSON|
| boolean  | default(true) | The default value is true|
| enum  | default('RED') | Used when the field is an enum, and the given value must be a member of the enum, such as Color.RED|
| dates and times  | default('2022-02-28') | Used when the field is a Java date or time. See [Date and Time Format](#date-and-time-formatting) for more information|


Here we assign an enum value of Color.RED to the destination field *favoriteColor* if *favColor* is null.

```json
"fields": [
   "favColor -> favoriteColor default('RED')",
   ...
]
```

### *exclude*
When *auto* is used, *exclude* can be used to list fields that should not be copied by *auto*.

```json
"fields": [
   "auto",
   "exclude lastName, birthDate, paymentStatus",
   ...
]
```

### *required*
Indicates that the source value must not be null. The converter will throw an exception if it is.

Here is an example where firstName is optional but lastName is required.

```json
"fields": [
   "firstName -> firstName",
   "lastName -> lastName required",
   ...
]
```

### *using*
Specifies a specific converter by name to use when converting this field.
The *using* modifier lets a field specify a converter by name, and can be useful if there are several converters for the same source and destintion classes.

```json
"fields": [
   "originalCustomer -> customer using(SpecialCustomerConverter)",
   ...
]
```

## Built-In Convervsions

FieldCopy automatically performs many common conversions between a source and destination fields.
It converts between primitive types such as *int* and scalar types such as *Integer*.

For example, if the source class contains a field "int points' and the destination class has
a field "Integer numPoints", then the following

```json
   "points -> numPoints",
```

is supported, and will generate Java code

```java
int tmp1 = src.getPoints();
int tmp2 = Integer.valueOf(tmp1);
dest.setNumPoints(tmp2);
```

#### *byte* and *Byte*
When the destination field is *byte* (or *Byte*), the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *byte*  | direct | *Byte*  | direct |
| *short*  | direct | *Short*  | x.byteValue() |
| *int*  | direct | *Integer*  | x.byteValue() |
| *long*  | direct | *Long*  | x.byteValue() |
| *float*  | cast  | *Float*  | x.byteValue() |
| *double*  | cast  | *Double*  | x.byteValue() |
| *String*  | Byte.parseByte(x) | *Character* | Byte.valueOf(x.charValue()) |

#### *short* and *Short*
When the destination field is *short* (or *Short*), the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *byte*  | direct | Byte  | direct |
| *short*  | direct | *Short*  | x.shortValue() |
| *int*  | cast | *Integer*  | x.shortValue() |
| *long*  | cast | *Long*  | x.shortValue() |
| *float*  | cast  | *Float*  | cx.shortValue() |
| *double*  | cast  | *Double*  | x.shortValue() |
| *String*  | Short.parseShort(x) | *Character* | Short.valueOf(x.charValue()) |

#### *int* and *Integer*
When the destination field is *int* (or *Integer*), the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *byte*  | direct | *Byte*  | direct |
| *short*  | direct | *Short*  | x.intValue() |
| *int*  | direct | *Integer*  | x.intValue() |
| *long*  | cast | *Long*  | x.intValue() |
| *float*  | cast  | *Float*  | cx.intValue() |
| *double*  | cast  | *Double*  | x.intValue() |
| *String*  | Integer.parseInteger(x) | *Character* | Integer.valueOf(x.charValue()) |

#### *long* and *Long*
When the destination field is *long* (or *Long*), the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *byte*  | direct | *Byte*  | direct |
| *short*  | direct | *Short*  | x.longValue() |
| *int*  | direct | *Integer*  | x.longValue() |
| *long*  | direct | *Long*  | x.longValue() |
| *float*  | cast  | *Float*  | cx.longValue() |
| *double*  | cast  | *Double*  | x.longValue() |
| *String*  | Long.parseLong(x) | *Character* | Long.valueOf(x.charValue()) |

#### *float* and *Float*
When the destination field is *float* (or *Float*), the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *byte*  | direct | *Byte*  | direct |
| *short*  | direct | *Short*  | x.floatValue() |
| *int*  | direct | *Integer*  | x.floatValue() |
| *long*  | direct | *Long*  | x.floatValue() |
| *float*  | direct  | *Float*  | cx.floatValue() |
| *double*  | cast  | *Double*  | x.floatValue() |
| *String*  | Float.parseFloat(x) | *Character* | Float.valueOf(x.charValue()) |

#### *double* and *Double*
When the destination field is *double* (or *Double*), the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *byte*  | direct | *Byte*  | direct |
| *short*  | direct | *Short*  | x.doubleValue() |
| *int*  | direct | *Integer*  | x.doubleValue() |
| *long*  | direct | *Long*  | x.doubleValue() |
| *float*  | direct  | *Float*  | cx.doubleValue() |
| *double*  | direct  | *Double*  | x.doubleValue() |
| *String*  | Double.parseDouble(x) | *Character* | Double.valueOf(x.charValue()) |

#### *boolean* and Boolean
When the destination field is *boolean* (or Boolean), the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *boolean*  | direct  | Boolean  | x.booleanValue() |
| *String*  | Boolean.parseBoolean(x) |

#### char and *Character*
When the destination field is char (or *Character*), the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *byte*  | cast | *Byte*  | *Character*.valueOf((char)x.intValue()) |
| *short*  | cast | *Short*  | "" |
| *int*  | cast | *Integer*  | "" |
| *long*  | cast | Long  | "" |
| *float*  | cast  | *Float*  | "" |
| *double*  | cast  | *Double*  | "" |
| *String*  | Character.toString(x) | *Character* | direct |

#### *String*
When the destination field is *String*, the following built-in conversions are supported:

| Source Type  | Type Of Conversion | Source Type  | Type Of Conversion |
| ---------- | ---------------- | ---------- | ---------------- |
| *byte*  | Byte.valueOf(x).toString() | Byte  | x.toString() |
| *short*  | Short.valueOf(x).toString() | *Short*  | "" |
| *int*  | Integer.valueOf(x).toString() | *Integer*  | "" |
| *long*  | Long.valueOf(x).toString() | *Long*  | "" |
| *float*  | Float.valueOf(x).toString()  | *Float*  | "" |
| *double*  | Double.valueOf(x).toString()  | *Double*  | "" |
| *String*  | direct | *Character* | direct |

## Optional
FieldCopy automatically converts fields that use java.util.Optional as needed for example. If the source field is of 
type Optional<String> and the destination is non-Optional, the generated code will orElse(null) to extract the value.

```java
Optional<String> tmp1 = src.getName();
dest.setName(tmp1.orElse(null));
```

Conversion in the other direction (from non-Optional to Optional) uses Optional.ofNullable()

```java
String tmp1 = src.getName();
dest.setName(Optional.ofNullable(tmp1);
```

## Lists
FieldCopy supports java.util.List fields and will do a shallow copy of their contents to a new ArrayList.


## Sub-Objects
FieldCopy considers a field that is a Java bean to be a "sub-object".  It looks for a registered converter
and uses it if available.

For example, if Customer.addr is of type Address, and you have defined converters for both Customer and Address in the JSON file, FieldCopy will use the Address converter.

```java
Address tmp1 = src.getAddr();
if (tmp1 != null) {
  ObjectConverter<Address,Address> conv2 = ctx.locate(Address.class, Address.class);
  Address tmp3 = conv2.convert(tmp1, new Address(), ctx);
  dest.setAddr(tmp3);
}
```

If there is no converter for a sub-object, FieldCopy simply assigns the source value to the destination field.

```java
Address tmp1 = src.getAddr();
dest.setAddr(tmp1);
```

## Sub-Object Fields
FieldCopy can extract a single field of a sub-object and copy it to a destination field.  For example, if Address has a field named *city*, and we want to copy it to AddressDTO.city, we can use this field conversion:

```json
   "addr.city -> city",
```

FieldCopy can also copy a source field to a single field of a destination object.

```json
   "cityName -> addr.city",
```

The generated code will create the destination sub-object if it doesn't exist

```java
String tmp1 = src.getCityName();
Address tmp2 = (dest.getAddr() == null) ? new Address() : dest.getAddr();
tmp2.setCity(tmp1)
```
## Additional Converters
FieldCopy creates a converter class for each "converter" defined in the JSON file.

In addition, you can write your own converter classes. In the constructor you indicate the source and destination types.  For example, let's write a String to String converter that converts a string to upper-case.

```java
public static class ToUpperCaseConverter extends ObjectConverterBase {
  public ToUpperCaseConverter() {
    super(String.class, String.class);
  }

  @Override
  public Object convert(Object src, Object dest, ConverterContext ctx) {
    String s = (String) src;
    return s.toUpperCase(Locale.ROOT);
  }
}
```
Then you mention your additional converter in the JSON file, either as a named or un-named converter;
Additional Converters can be defined at the root level, or within a "converter" section to be private to that converter.

### Named Converters
A named converter is configured in *additionalNamedConverters* and given a unique name.  Named converters allow you
to have multiple converters for the same source and destination classes, and select which one to use with the *using* modifier.

```json
"additionalNamedConverters": { 
  "toUpperConvert" : "com.company.convertrs.ToUpperCaseConverter"
},
```
Then specify when to use them with the *using* modifier.

```json
"taxCode -> taxCode using(toUpperConvert)"
```

### Unnamed Converters
A un-named converter is configured in *additionalConverters* and it is used whenever its source and destination class match a field conversion.

For example, you could create a converter that converts LocalDates to a String with a special format.

```json
"additionalConverters": [
  "com.company.convertrs.MyRegionalDateConverter"
],
```
This converter will be used for any field with a LocalDate to String conversion.

```json
"orderDate -> orderDateStr"
```


## Date and Time Format
FieldCopy can convert strings to Java 8 date & time classes.  It can also render date & time objects to strings.

Date and Time fields use ISO format by default.

| Java Class  | Format |
| ----- |  ------------- |
| LocalDate  | yyyy-MM-dd |
| LocalTime | HH:mm:ss |
| LocalDateTime | yyyy-MM-dd'T'HH:mm:ss |
| ZonedDateTime | yyyy-MM-dd'T'HH:mm:ssXXX |
| java.util.Date | yyyy-MM-dd'T'HH:mm:ss |

The formats can be changed in the JSON file *config* section to any format String supported by DateTimeFormatter.

| Name  | Optional | Description |
| ----- | ------ | ------------- |
| localDateFormat  |  Yes |  any format string supported for LocalDate  |
| localTimeFormat  |  Yes |  any format string supported for LocalTime  |
| localDateTimeFormat  |  Yes |  any format string supported for LocalDateTime  |
| zonedDateFormat  |  Yes |  any format string supported for ZonedDateTime  |
| utilDateFormat  |  Yes |  any format string supported for SimpleDateFormat  |

These can be changed to any other valid DateTimeFormatter format, or SimpleDateFormat for java.util.Date.
The config section in the JSON files can be used to define the formats that you wish to use.

| Name  | Description |
| ----- |  ------------- |
| localDateFormat  |  A LocalDate format such as "2022-02-28"  |
| localTimeFormat  |  A LocalDate format such as "18:30:55"  |
| localDateTimeFormat | A LocalDate format such as "2022-02-28T18:30:55"  |
| zonedDateFormat  |   A LocalDate format such as "2022-02-28T18:30:55-05:00[America/New_York]"  |
| utilDateFormat  |   A java.util.Date format such as "2022-02-28". Note. If this config value is not set, the default is to use the same format as localDateTimeFormat  |

Be aware that no date and time parsing or formatting is done during code generation.

| Name  | Description |
| ----- |  ------------- |
| validateDateAndTimeValues  |  If *true* then date & time string values are validated at code generation time.  Any field whose left-side is a value, such as "2022-02-28" and right side is one of the supporte date and time fields, will be validated. An exception is thrown if the value string can't be parsed using the given format for that date or time class.  |


#### Runtime
There are two ways to configure date & time formats at runtime.

The first is to load the formats from the JSON using *ConfigJsonParser* class

```java
String json = //...read the json file into a string....
ConfigJsonParser configParser = new ConfigJsonParser();
FieldCopyOptions configOptions = configParser.parseConfig(json);

//create FieldCopy using configOptions
FieldCopy fc = FieldCopy.with(MyGroup.class).loadOptionsFromConfig(configOptions).build();
```

The *RuntimeOptions* will now use date & time formats that were defined in the JSON file.

The second way is to configure *RuntimeOptions* directly

```java
RuntimeOptions initialOptions = new RuntimeOptions();
initialOptions.localDateFormatter = DateTimeFormatter.ofPattern("yyyy/mm/dd");

FieldCopy fc = FieldCopy.with(FieldCopyTests.MyGroup.class).options(initialOptions).build();
```

















