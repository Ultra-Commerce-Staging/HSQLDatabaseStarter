# Ultra HSQL Database Starter

## Requirements

Spring Boot 1.4+.

## Usage
This project makes starting up an HSQL Database easier. Simply include the dependency into your `pom.xml`:

```xml
<dependency>
    <groupId>com.ultracommerce</groupId>
    <artifactId>ultra-boot-starter-hsql-database</artifactId>
    <version>5.2.0-RC1</version>
</dependency>
```

Once this is on the claspath, the [`DatabaseAutoConfiguration`](src/main/java/com/ultracommerce/autoconfigure/DatabaseAutoConfiguration.java) class is initialized and sets up Spring `DataSource`s that Ultra needs in order to operate, while tying the lifecycle of `HSQLDB` to startup and shutdown of the Ultra project.
