# Importer

This is a Java library which makes it easy to map values from documents to Java objects.

## Usage
Consider the sample document below which can be a csv or spreadsheet document.

| ID  | TITLE  | QTY    | COST  | DIST  | CODE  |  DESCRIPTION  |
| --- | -----  | -----: | -----:| ----: | ----: | ------------  |
| 1   | "Book" | 2.446  | 5.25  | 0.5   | 001   | "A nice book" |
| 2   |  Pen   | 3      | 1.52  | 0.3   | 002   |  A blue pen   |

##### Mapping rows to a Java Bean
First is to create a POJO, in this case, a Product class.

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

The PriceConverter is a property converter which is needed in the xml mapping below (It's usage will be explained later)
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
<si xmlns="http://github.com/kossy18/schema/si">
    <cell-converter name="numConverter" value="com.github.kossy18.service.importer.converters.NumberConverter"/>
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
   <!-- Or an include tag can be added to reference files containing other class tags -->
   <!-- For example: <include file="..."/>  -->
</si>
```
*  **cell-converter**: The `cell-converter` is used to convert the value of a cell from a `String` to another type.
In the above mapping, the builtin [NumberConverter](https://github.com/kossy18/moonshot/blob/master/service/import/src/main/java/com/andrea/service/importer/converters/NumberConverter.java)
is used to convert a `String` to an Integer, Float, Double, Long, Byte or Short.
To use a `cell-converter` in your mapping, it must be declared first. Two converters with the same name should not be declared since
duplicate declarations will be overwritten by the latest one.
The name of the `cell-converter` is optional. If not specified, the fully qualified name of the converter class will be used as the name.

* **property-converter**: The `property-converter` is used to convert objects from one type to another. This is useful in the case in which a property
of class is made up of multiple values from the document, a `property-converter` is useful in that scenario.
Take for example, a document which have the values `cost` and `discount` and needs to be mapped to a property of a class `price`.
The two values, `cost` and `discount` will be supplied to the `property-converter` which then creates the property `price` of the
target class. See the `PriceConverter` class above for an example.

* **class**: This represents the class that needs to be mapped. A class has a name attribute, which is the fully qualified name of the class
 to be mapped, which is located at runtime via Java reflection. It can also contain one or more `property` tags.
 
* **property**: A `property` represents an instance variable of the class to be mapped. A `property` contains a name attribute which must exist in
the mapper class. It can also contain a `converter-ref` and a `converter-data`. <br>
A `converter-ref` is a reference to the declared `cell-converter`
or `property-converter` while the `converter-data` is any additional data which is required by the converter. They are both optional.
In the case of the builtin [NumberConverter](https://github.com/kossy18/moonshot/blob/master/service/import/src/main/java/com/andrea/service/importer/converters/NumberConverter.java),
the `converter-data` is the Number type the `String` should be converted to.
The `property` can contain a `column` attribute which represents the column in the document which should be mapped to.
So a document with a header/column name called description will be mapped to the `property` if it's `column` attribute has the value called description as well.
The `column` attribute can also contain regex strings. <br>
The `property` can also contain a `columns` tag if additional cells need to be mapped to the property. If the `property` does not contain a `columns` tag,
or a `column` attribute, the document must contain a header/column whose name is equal to the name of the `property` because the name of the `property` will be
used as the mapping value instead.
The `property` can have an `order` attribute. The `order` attribute is used to guarantee the invocation of the `property` during the mapping process. 
For example, consider the Java class below:
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
thrown because `url` will be null. This is where the `order` attribute comes into play. In the xml mapping, the `url` should have a higher `order`
value than the `icon` property so that `setUrl` will be invoked first before `setIcon`. If no `order` is specified, 0 is assigned as the `order` and if
two properties have the same `order` value, the property's name will be used for the comparison.

* **columns**: `columns` are used to map values from a document to a property. `columns` can contain one or more `column` tags.
They are useful if a `property` is made up of multiple values from a document.
In the xml example above, we can see that the price property is made up of the DIST and COST values in the document.
The ordering of the `column` tags also matter. If a `property-converter` is not be used to convert the multiple `column` values to a single
one, the library will attempt to set each `column` value to the proper setter argument.
For example, consider the code snippet below:
```java
    private String path;
    private String description;

    public void setIconInfo(String path, String description) {
        this.path = path;
        this.description = description;
    }
```
and the corresponding xml mapping
```xml
    <property name="iconInfo">
        <columns>
            <column name="path" />
            <column name="desc" />
        </columns>
    </property>
```
In the case above, the `path` in the xml will be mapped to the first argument of the `setIconInfo` method while the `desc` will be 
mapped to the second argument. The name of the `column` in this case is used for mapping between the document and the setter arguments.

After the xml mapping, majority of the configuration has been done. Next is to wire it up in Java.
```Java
    ImporterConfig config = new ImporterConfig();
    config.setXmlReader(new XmlReader(new XmlHandlerImpl()));
    config.build(XML_MAPPING_PATH);
```
You create an instance of an `ImporterConfig`. The `ImporterConfig` is responsible for reading and validating if the xml is valid. It also
reads the declared converters, classes and properties and stores them in a `Map` for use later. So this should be done only once.

```Java
    DocumentReaderFactory readerFactory = new DefaultDocumentReaderFactory();
    RowSeeker seeker = readerFactory.createReader(ReaderType.CSV).read(new FileInputStream(CSV_FILE_PATH));
    
    EntityInfoProcessor<Product> processor = new EntityInfoProcessor<>(config);    
    List<Product> products = processor.process(seeker, Product.class);
    seeker.close();
```
After creating the `ImporterConfig`, next is to create a `DocumentReaderFactory` and specify what type of document is expected to be read.
`DocumentReaderFactory` has a single method `createReader(ReaderType)` which creates a `RowSeeker`.<br>
A `RowSeeker` can be likened to `java.util.Iterator`. It's job is to transverse and read each row of the document. Each transversal of the 
`RowSeeker` returns a `Row` or null if there are no more rows to be read. 
A `Row` represents the physical row of the document and it also contains cells which represents the physical cells of a row.

The `EntityInfoProcessor` wires everything together. It accepts the `ImporterConfig` to have access to the converters, classes and properties.
It also accepts a `RowSeeker` which it uses to transverse the document. With each row transversal, it creates a class object with uses
reflection to set the properties of the classes as specified in the xml mapping. After the transversal, a `List` of the created objects are 
returned.
When done, the `RowSeeker` should be closed to release any system resources associated with the open streams.

#### License
MIT