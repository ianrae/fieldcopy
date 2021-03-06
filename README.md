FIELDCOPY

FieldCopy is a simple bean-copying library for Java.

[![Javadocs](https://www.javadoc.io/badge/org.dnal-lang/fieldcopy.svg)](https://www.javadoc.io/doc/org.dnal-lang/fieldcopy)

```
<dependency>
   <groupId>org.dnal-lang</groupId>
   <artifactId>fieldcopy</artifactId>
   <version>0.2.0</version>
</dependency>
```

FieldCopy has a fluent API.

```java
FieldCopier copier = FieldCopy.createCopier();
copier.copy(sourceObj, destinationObj).autoCopy().execute();
```

### Features
 * automatically copy fields with matching field names.
 * specify fields to copy explicity using include or exclude.
 * define default values to use if source value is null.
 * converts most basic scalar values automatically
 * define custom converters

### Performance

 FieldCopy is reflection-based.  This is somewhat slower than other
 bean-copying libraries that use dynamic bytecode generation. 
 However, FieldCopy has two advantages:
  * very fast startup time.  This is important for test-driven development where tests are run every few minutes.
  * simplicity.  Some environments are not friendly to dynamic bytecode generation.

See also "Caching" below.

### Extensibility
 FieldCopy has a layered architecture.  Its underlying FieldCopyService can be replaced so that 
 FieldCopy can be used for copying to and from other data sources.  For example, the project comes
 with a sample Java property to bean copier called PropertyCopy.

## Examples

For best performance, use a long-lived CopierFactory to create FieldCopier instances (see Caching).  
```java
CopierFactory copierFactory = FieldCopy.createFactory();
```
We will use _copierFactory_ in the following examples.

#### Example 1. Copy only specific fields
Use the _field_ method to specify individual fields to copy.

```java
FieldCopier copier = copierFactory.createCopier();
copier.copy(sourceObj, destinationObj).field("firstName", "firstName").field("lastName", "lastName").execute();
```

Here, _sourceObj.getFirstName()_ and _getLastName()_ will be copied to _destObj.setFirstName()_ and _dest.setLastName()_, respectively.

#### Example 2. When source and destination fields are the same, you can use _include_.  
The following is equivalent to example 1.

```java
FieldCopier copier = copierFactory.createCopier();
copier.copy(sourceObj, destinationObj).include("firstName", "lastName").execute();
```

Note that you can also pass in destination class.

```java
FieldCopier copier = copierFactory.createCopier();
UserDTO destinationObj = copier.copy(sourceObj).include("firstName", "lastName").execute(UserDTO.class);
```


#### Example 3. Copy all fields except some fields using _exclude_.

```java
FieldCopier copier = copierFactory.createCopier();
copier.copy(sourceObj, destinationObj).exclude("password", "loginCount").autoCopy().execute();
```

Note. You can combine use of _field_, _exclude_, _include_,  and  _autoCopy_.  
_include_ and _exclude_ are used to override _autoCopy_. _exclude_ has priority over _include_.  _field_ has priority over the other three. 

#### Example 4.  Provide default values
Specify a default value to use when a field's value is null.

```java
FieldCopier copier = copierFactory.createCopier();
copier.copy(sourceObj, destinationObj)
  .field("isAdmin", "adminFlag").defaultValue(false)
  .field("maxSessionTime", "maxSessionTime").defaultValue(3600)
  .execute();
```

#### Example 5.  Define a custom converter
To customize date to string conversions, create a ValueConverter.
  The _canConvert_ method chooses which types or fields that the converter wants to handle.  The _convertValue_ method does the conversion.

```java
public class MyDateConverter implements ValueConverter {
   private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
   @Override
   public boolean canConvert(FieldInfo source, FieldInfo dest) {
      return source.fieldClass.equals(Date.class) && dest.fieldClass.equals(String.class);
   }
   @Override
   public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
      if (value == null) {
        return null;
      }
      Date dt = (Date) value;
      return sdf.format(dt);
   }
}
```

Use the converter in the _withConverters_ method.

```java
FieldCopier copier = copierFactory.createCopier();
copier.copy(src, dest).withConverters(new MyDateConverter()).field("purchaseDate", "orderDateStr").execute();
```
You can specify multiple converters in withConverters.

#### Example 6. Nested objects.  
When FieldCopy encounters a field that is not a scalar value, a list, or array, it
 assumes the field is a bean and performs a autoCopy() of the field's fields.  If you wish to
 control the copying of sub-objects, use a FieldMapping.  Its API is virtually identical to FieldCopier:

```java
FieldCopyMapping mapping = copier.createMapping(Address.class, AddressDTO.class).exclude("createTS").autoCopy().build();
```

Pass the mapping to the main copier using _withMappings_ method

```java
FieldCopier copier = copierFactory.createCopier();
copier.copy(src, dest).withMapings(mapping).autoCopy().execute();
```

#### Example 6.  Additional source values
If the source object is missing a value, or doesn't have a proper getter method, the _includeSourceValues_ method can be used.  Specify a name and a value, and FieldCopy will use it instead of trying to get the value from the bean.

In the following example, the src object has _id()_ instead of _getId()_, so we use _includeSourceValues_. The destination field _dto.id_ will be set.

```java
FieldCopier copier = copierFactory.createCopier();
copier.copy(src, dto).includeSourceValues("id", src.id()).autoCopy().execute();
```

## Additional Information
### Caching
FieldCopy uses Java reflection to build field information about the source and destination classes it copies.  It keeps a cache of this information so that on all subsequent copying it can use the cached information.  Thus the performance impact of using reflection is minimized.

The best way to take advantage of this is to use CopierFactory. Make the factory a member variable of your service or controller class, and use it to create single-use FieldCopier objects. 

```java
public class OrderService {
  private CopierFactory factory = FieldCopy.createFactory();
...
  public OrderDTO doSomething(Order order) {
     ....
     FieldCopier copier = factory.createCopier();
     return copier.copy(order).autoCopy().execute(OrderDTO.class);

```






