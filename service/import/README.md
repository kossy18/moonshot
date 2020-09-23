# 

This is a Java library which makes is easy to map values from documents to Java objects.

## Usage
### CSV document
Consider the sample csv document below

| ID  | TITLE  | QTY    | COST  | DIST  | CODE  |  DESCRIPTION  |
| --- | -----  | -----: | -----:| ----: | ----: | ------------  |
| 1   | "Book" | 2.446  | 5.25  | 0.5   | 001   | "A nice book" |
| 2   |  Pen   | 3      | 1.52  | 0.3   | 002   | "A blue pen"  |

##### Mapping rows to a Java Bean
First is to create a POJO, in this case, the Product class.

```java
public class Product {
    private long id;

    private String name;

    private String description;

    private int quantity;

    private float price;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String title, int code) {
        this.name = title + " - " + code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
```

The PriceConverter is a property converter which is needed in the xml mapping below
```java
public class PriceConverter implements PropertyConverter {

    @Override
    public Object convert(String extras, Object... args) {
        double cost = (double) args[0];
        double discount = (double) args[1];

        return Double.valueOf((discount * cost) + cost).floatValue();
    }
}
```

Next is to write the xml mapping

```xml
<si xmlns="http://andrea.com/schema/si">
    <cell-converter name="numConverter" value="com.andrea.service.importer.converters.NumberConverter"/>
    <property-converter name="priceConverter" value="com...PriceConverter"/>
  
    <class name="com...Product">
        <property name="id">
            <columns>
                <column name="id" converter-ref="numConverter" converter-data="long"/>
            </columns>
        </property>
        <property name="name">
            <columns>
                <column name="(.*)itl(.*)"/>
                <column name="code" converter-ref="numConverter" converter-data="integer"/>
            </columns>
        </property>
        <property name="description"/>
        <property name="quantity">
            <columns>
                <column name="q(.*)y" converter-ref="numConverter" converter-data="integer"/>
            </columns>
        </property>
        <property name="price" converter-ref="priceConverter">
            <columns>
                <column name="cost" converter-ref="numConverter" converter-data="double" />
                <column name="^dist$" converter-ref="numConverter" converter-data="double" />
            </columns>
        </property>
    </class>

   <!-- Multiple class tags can be added as well -->
   <!-- Or a include tag can be added to reference files containing other class tags -->
   <!-- For example: <include file="..."/>  -->
</si>
```
*  **cell-converter**: The `cell-converter` is used to convert the value of a cell from a `String` to another type.
In the above mapping, the builtin [NumberConverter](https://github.com/kossy18/moonshot/blob/master/service/import/src/main/java/com/andrea/service/importer/converters/NumberConverter.java)
is used to convert a `String` to an Integer, Float, Double, Long, Byte or Short.
To use a `cell-converter`, in your mapping, it must be declared first. Two converters with the same name should not be declared since
duplicate declarations will be overwritten by the latest one.
The name of the `cell-converter` is optional. If not specified, the fully qualified name of the converter class will be used as the name.

* **property-converter**: The `property-converter` is used to convert objects from one type to another. This is useful in the case that a property
of class is made up of multiple values from the document, a `property-converter` is useful in that scenario.
For example, a csv document which have the values `cost` and `discount` and needs to be mapped to a property of a class `price`.
The two values, `cost` and `discount` will be supplied to the `property-converter` which then creates the property `price` of the
target class. See the `PriceConverter` class above for an example.

* **class**: This represents the class that needs to be mapped. A class has a name attribute, which is the fully qualified name of the class
 to be mapped, which is located at runtime via Java reflection. It can also contain one or more `property` tags.
 
* **property**: A `property` represents an instance variable of the class to be mapped. A property contains a name attribute which must exist in
the mapper class. It can also contain a `converter-ref` and a `converter-data`. A `converter-ref` is a reference to the declared `cell-converter`
or `property-converter`. Whilst the `converter-data` is any additional data which is required by the converter. They are both optional.
In the case of the builtin [NumberConverter](https://github.com/kossy18/moonshot/blob/master/service/import/src/main/java/com/andrea/service/importer/converters/NumberConverter.java),
the `converter-data` is the Number type the `String` should be converted to.
The `property` can contain a `column` attribute which represents the header name in the document which will be mapped to the `property`.
So a document with header name, description will be mapped to the `property` if it's `column` attribute has the value, description as well.
The `column` attribute can contain regex strings. 
The `property` can also contain a `columns` tag if additional cells need to be mapped to the property. If the `property` does not contain a `columns` tag,
or a `column` attribute, the document must contain a header whose name is equal to the name of the `property` because the name of the `property` will be
used as the mapping value instead.
The `property` can have an `order` attribute. The `order` attribute is used to guarantee the invocation of the `property` during the mapping process. 
For example, consider the Java object below:
```java
public class Image {
    private URL url;
    private ImageIcon icon;

    public void setUrl(String path) {
       this.url = new File(path).toURI().toURL();
    }
    public void setIcon(String description) {
        this.image = new ImageIcon(url);
        this.image.setDescription(description);
    }   
}
```
During the mapping process, we will want the `setUrl` method to be called before the `setIcon` else, a NullPointerException will be
thrown because the `url` will be null. This is where the `order` attribute comes into play. In the xml mapping, the `url` should have a higher `order`
value than the `icon` property so that `setUrl` will be invoked first before `setIcon`. If no `order` is specified, 0 is assigned as the `order` and if
two properties ave the same `order` value, the property's name will be used for the comparison.
