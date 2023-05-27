# SpringBootAPI

## Sesion 1

¡Ahora está contigo! Haz el mismo procedimiento que hice en clase. Cree el proyecto inicial a través del sitio web
[Spring Initializr](https://start.spring.io/), importarlo en IntelliJ y finalmente cree una clase Controller como Hello World.

```java
@RestController
@RequestMapping("hello")
public class HelloController {

    @GetMapping
    public String holaMundo() {
        return "Hello World Spring!";
    }
}
```


