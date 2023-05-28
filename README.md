# SpringBootAPI

## Session 1

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

## Session 2

Cuando desarrollamos una API y queremos que todos sus recursos estén
disponibles para cualquier cliente HTTP, una de las cosas que nos viene a
la mente es CORS (Cross-Origin Resource Sharing), en Español, “Intercambio
de recursos con diferentes orígenes” Si aún no te ha pasado, no te
preocupes, es normal tener errores de CORS al consumir y poner a
disposición las APIs.

Imagen

Pero al fin y al cabo, ¿qué es CORS, qué provoca errores y cómo evitarlos en
nuestras APIs con Spring Boot?

### CORS

CORS es un mecanismo utilizado para agregar encabezados HTTP que le
indican a los navegadores que permitan que una aplicación web se ejecute
en un origen y acceda a los recursos desde un origen diferente. Este tipo
de acción se denomina cross-origin HTTP request. En la práctica, les dice
a los navegadores si se puede acceder o no a un recurso en particular.

Pero, ¿por qué ocurren los errores? ¡Es hora de entender!

### Same-origin policy

Por defecto, una aplicación Front-end, escrita en JavaScript, solo puede
acceder a los recursos ubicados en el mismo origen de la solicitud. Esto
sucede debido a la política del mismo origen (same-origin policy), que es
un mecanismo de seguridad de los navegadores que restringe la forma en que
un documento o script de un origen interactúa con los recursos de otro.
Esta política tiene como objetivo detener los ataques maliciosos.

Dos URL comparten el mismo origen si el protocolo, el puerto (si se
especifica) y el host son los mismos. Comparemos posibles variaciones
considerando la URL ```https://cursos.alura.com.br/category/programacao```:

URL | Resultado | Motivo
----|-----------|-------
https://cursos.alura.com.br/category/front-end | Mismo origen | Solo camino diferente
http://cursos.alura.com.br/category/programacao | Error de CORS | Protocolo diferente (http)
https://faculdade.alura.com.br:80/category/programacao | Error de CORS | Host diferente

Ahora, la pregunta sigue siendo: ¿qué hacer cuando necesitamos consumir
una API con una URL diferente sin tener problemas con CORS? Como, por
ejemplo, cuando queremos consumir una API que se ejecuta en el puerto
8000 desde una aplicación React que se ejecuta en el puerto 3000.
¡Compruébalo!

Al enviar una solicitud a una API de origen diferente, la API debe devolver
un header llamado Access-Control-Allow-Origin. Dentro de ella es necesario
informar los diferentes orígenes que serán permitidas de consumir la API,
en nuestro caso: ```Access-Control-Allow-Origin: http://localhost:3000```.

Puede permitir el acceso desde cualquier origen utilizando el símbolo *
(asterisco): ```Access-Control-Allow-Origin: *```. Pero esta no es una
medida recomendada, ya que permite que fuentes desconocidas accedan al
servidor, a menos que sea intencional, como en el caso de una API pública.
Ahora veamos cómo hacer esto en Spring Boot correctamente.

### Habilitación de diferentes orígenes en Spring Boot

Para configurar el CORS y permitir que un origen específico consuma la API,
simplemente cree una clase de configuración como la siguiente:

```java
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");
    }
}
```

http://localhost:3000 sería la dirección de la aplicación Front-end y
.allowedMethods los métodos que se permitirán ejecutar. Con esto, podrás
consumir tu API sin problemas desde una aplicación front-end.

### DTO (record)

Lanzado oficialmente en Java 16, pero disponible experimentalmente desde
Java 14. Record es un recurso que le permite representar una clase
inmutable, que contiene solo atributos, constructor y métodos de lectura,
de una manera muy simple y ágil.

Este tipo de clase encaja perfectamente para representar clases DTO, ya que
su objetivo es únicamente representar datos que serán recibidos o devueltos
por la API, sin ningún tipo de comportamiento.

Para crear una clase DTO inmutable, sin la utilización de Record, era
necesario escribir mucho código. Veamos un ejemplo de una clase DTO que
representa un teléfono:

```java
public final class Telefono {

    private final String ddd;
    private final String numero;

    public Telefono(String ddd, String numero) {
        this.ddd = ddd;
        this.numero = numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ddd, numero);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Telefono)) {
            return false;
        } else {
            Telefono other = (Telefono) obj;
            return Objects.equals(ddd, other.ddd)
              && Objects.equals(numero, other.numero);
        }
    }

    public String getDdd() {
        return this.ddd;
    }

    public String getNumero() {
        return this.numero;
    }
}
```

Ahora, con Record todo ese código se puede resumir en una sola línea:

```java
public record Telefono(String ddd, String numero){}
```

Deberá crear una clase Controller:

```java
@RestController
@RequestMapping("pacientes")
public class PacienteController {

    @PostMapping
    public void registrar(@RequestBody DatosRegistroPaciente datos) {
        System.out.println("datos recebidos: " +datos);
    }

}
```

También deberá crear un DTO:

```java
public record DatosRegistroPaciente(
        String nombre,
        String email,
        String telefono,
        String documentoIdentidad,
        DatosDireccion direccion
) {
}
```

El DTO ```DatosDireccion``` será el mismo utilizado en la funcionalidad de
registro de médicos.