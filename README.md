# Statham is a Json Mapper (Deserializer)

Main goal: "Make your own implementations to understand how it works"

## How to use it
Provide json string:
```json
{
  "firstName": "Andrii",
  "lastName": "Petrov",
  "email": "apetrov@gmail.com",
  "active": true,
  "age": 19,
  "address": {
    "line1": "Kyiv", 
    "line2": "Lesna"
  }
}
```
Pass json to Statham#jsonToObj method:
```java
User user = new Statham().jsonToObj(json, User.class);
```

### Limitations:
As for now there are some limitations:
* avoid using next characters inside json string key or value: `,":{}[]` 
* class should have default constuctor (but I guess you can implement you own Hydrator to handle that) 
