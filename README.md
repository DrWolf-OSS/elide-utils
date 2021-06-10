# DrWolf Elide Utils

[![](https://jitpack.io/v/drwolf-oss/elide-utils.svg)](https://jitpack.io/#drwolf-oss/elide-utils)


Elide helper classes for Play (2.8.8)

extend `it.drwolf.elide.ElideCfg` with your checks

extend `it.drwolf.elide.JsonApi` with your user class

add routes:
```routes
GET     /api/json/*resource     your.package.JsonApi.get(resource: String,request: Request)
POST    /api/json/*resource     your.package.JsonApi.post(resource: String,request: Request)
PATCH   /api/json/*resource     your.package.JsonApi.patch(resource: String,request: Request)
DELETE  /api/json/*resource     your.package.JsonApi.delete(resource: String,request: Request)
```

you can implement `it.drwolf.elide.security.Secured` in your entities 
and use `it.drwolf.elide.security.ReadableCheck` and `it.drwolf.elide.security.WritableCheck` 