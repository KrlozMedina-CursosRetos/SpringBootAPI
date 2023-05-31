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

## Session 3

La configuración de una aplicación Spring Boot se realiza en archivos
externos, y podemos usar el archivo de propiedades o el archivo YAML.
En este “Para saber más”, abordaremos las principales diferencias entre
ellos.

### Archivo de propiedades

De forma predeterminada, Spring Boot accede a las configuraciones
definidas en el archivo application.properties, que utiliza un formato
```clave=valor```:

```springdataql
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/clinica
spring.datasource.username=root
spring.datasource.password=root
```

Cada fila es una configuración única, por lo que necesitamos expresar
datos jerárquicos usando los mismos prefijos para nuestras claves, es
decir, necesitamos repetir los prefijos, en este caso spring y datasource.

### Configuración YAML

YAML es otro formato muy utilizado para definir datos de configuración
jerárquicos, como se hace en Spring Boot.

Tomando el mismo ejemplo de nuestro archivo application.properties,
podemos convertirlo a YAML cambiando su nombre a application.yml y
modificando su contenido a:

```springdataql
spring:
    datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/clinica
        username: root
        password: root
```

Con YAML, la configuración se ha vuelto más legible ya que no contiene
prefijos repetitivos. Además de la legibilidad y la reducción de
repeticiones, el uso de YAML facilita el almacenamiento de variables de
configuración del entorno, como lo recomienda [12 Factor App](https://12factor.net/), una
metodología conocida y utilizada que define 12 mejores prácticas para
crear una aplicación moderna, escalable y de sencillo mantenimiento.

### Pero después de todo, ¿qué formato usar?

A pesar de las ventajas que nos aportan los archivos YAML frente al
archivo properties, la decisión de elegir uno u otro es una cuestión de
gusto personal. Además, no se recomienda tener ambos tipos de archivos 
en el mismo proyecto al mismo tiempo, ya que esto puede generar problemas 
inesperados en la aplicación.

Si elige usar YAML, tenga en cuenta que escribirlo al principio puede 
ser un poco laborioso debido a sus reglas de tabulación.

### Para saber más: ¿Qué hay de las clases DAO?

En algunos proyectos Java, dependiendo de la tecnología elegida, es común
encontrar clases que siguen el patrón DAO, usado para aislar el acceso a
los datos. Sin embargo, en este curso usaremos otro patrón, conocido como
Repositorio.

Pero entonces pueden surgir algunas preguntas: ¿cuál es la diferencia
entre los dos enfoques y por qué esta elección?

### Patrón DAO

El patrón de diseño DAO, también conocido como Data Access Object, se
utiliza para la persistencia de datos, donde su objetivo principal es
separar las reglas de negocio de las reglas de acceso a la base de datos. 
En las clases que siguen este patrón, aislamos todos los códigos que se 
ocupan de conexiones, comandos SQL y funciones directas a la base de 
datos, para que dichos códigos no se esparzan a otras partes de la 
aplicación, algo que puede dificultar el mantenimiento del código y 
también el intercambio de tecnologías y del mecanismo de persistencia.

### Implementación

Supongamos que tenemos una tabla de productos en nuestra base de datos. 
La implementación del patrón DAO sería la siguiente:

Primero, será necesario crear una clase básica de dominio ```Producto```:

```java
public class Producto {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;

    // constructores, getters y setters
}
```

A continuación, necesitaríamos crear la clase ```ProductoDao```, que 
proporciona operaciones de persistencia para la clase de dominio 
```Producto```:

```java
public class ProductoDao {

    private final EntityManager entityManager;

    public ProductoDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void create(Producto producto) {
        entityManager.persist(producto);
    }

    public Producto read(Long id) {
        return entityManager.find(Producto.class, id);
    }

    public void update(Producto producto) {
        entityManger.merge(producto);
    }

    public void remove(Producto producto) {
        entityManger.remove(producto);
   }

}
```

En el ejemplo anterior, se utilizó JPA como tecnología de persistencia 
de datos de la aplicación.

### Padrón Repository

Según el famoso libro Domain-Driven Design de Eric Evans:

```text
El repositorio es un mecanismo para encapsular el almacenamiento,
recuperación y comportamiento de búsqueda, que emula una colección de
objetos.
```

En pocas palabras, un repositorio también maneja datos y oculta consultas 
similares a DAO. Sin embargo, se encuentra en un nivel más alto, más cerca 
de la lógica de negocio de una aplicación. Un repositorio está vinculado a 
la regla de negocio de la aplicación y está asociado con el agregado de 
sus objetos de negocio, devolviéndolos cuando es necesario.

Pero debemos estar atentos, porque al igual que en el patrón DAO, las 
reglas de negocio que están involucradas con el procesamiento de 
información no deben estar presentes en los repositorios. Los 
repositorios no deben tener la responsabilidad de tomar decisiones, 
aplicar algoritmos de transformación de datos o brindar servicios 
directamente a otras capas o módulos de la aplicación. Mapear entidades 
de dominio y proporcionar funcionalidades de aplicación son 
responsabilidades muy diferentes.

Un repositorio se encuentra entre las reglas de negocio y la capa de 
persistencia:

1. Proporciona una interfaz para las reglas comerciales donde se accede a 
los objetos como una colección;

2. Utiliza la capa de persistencia para escribir y recuperar datos 
necesarios para persistir y recuperar objetos de negocio.

Por lo tanto, incluso es posible utilizar uno o más DAOs en un repositorio.

### ¿Por qué el padrón repositorio en lugar de DAO usando Spring?

El patrón de repositorio fomenta un diseño orientado al dominio, lo que 
proporciona una comprensión más sencilla del dominio y la estructura de 
datos. Además, al usar el repositorio de Spring, no tenemos que 
preocuparnos por usar la API de JPA directamente, simplemente creando 
los métodos, que Spring crea la implementación en tiempo de ejecución, 
lo que hace que el código sea mucho más simple, pequeño y legible.

### Para saber más: validación con Bean Validatión

Como se explicó en el video anterior, el Bean Validation se compone de 
varias anotaciones que se deben agregar a los atributos en los que 
queremos realizar las validaciones. Hemos visto algunas de estas 
anotaciones, como @NotBlank, que indica que un atributo String no 
puede ser nulo o vacío.

Sin embargo, existen decenas de otras anotaciones que podemos utilizar
en nuestro proyecto, para los más diversos tipos de atributos. Puede 
consultar una lista de las principales anotaciones de Bean Validation en 
la [documentación oficial](https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#builtinconstraints)
de la especificación.

## Session 4

### Uso de la anotación ```@JsonIgnore```

En esta situación, podríamos usar la anotación @JsonIgnore, que nos ayuda 
a ignorar ciertas propiedades de una clase Java cuando se serializa en un 
objeto JSON.

Su uso consiste en agregar la anotación a los atributos que queremos 
ignorar cuando se genera el JSON. Por ejemplo, supongamos que tenemos 
una entidad JPA 'Empleado', en la que queremos ignorar el atributo 'salario':

```java
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "Empleado")
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;

    @JsonIgnore
    private BigDecimal salario;

    //restante del código omitido…
}
```

En el ejemplo anterior, el atributo 'salario' de la clase 'Empleado' no se 
mostrará en las respuestas JSON y el problema estaría resuelto.

Sin embargo, puede haber algún otro endpoint de la API en el que 
necesitemos enviar el salario de los empleados en el JSON, en cuyo caso 
tendríamos problemas, ya que con la anotación ```@JsonIgnore``` tal atributo 
nunca se enviará en el JSON, y al eliminar la anotación se enviará el 
atributo siempre. Por lo tanto, perdemos la flexibilidad de controlar 
cuándo se deben enviar ciertos atributos en el JSON y cuándo no.

### DTO

El patrón DTO (Data Transfer Object) es un patrón arquitectónico que se
usó ampliamente en aplicaciones Java distribuidas (arquitectura 
cliente/servidor) para representar los datos que eran enviados y 
recibidos entre aplicaciones cliente y servidor.

El patrón DTO puede (y debe) usarse cuando no queremos exponer todos los 
atributos de alguna entidad en nuestro proyecto, una situación similar a 
los salarios de los empleados que discutimos anteriormente. Además, con la 
flexibilidad y la opción de filtrar qué datos se transmiten, podemos 
ahorrar tiempo de procesamiento.

### Para saber más: parámetros de paginación

Como aprendimos en videos anteriores, por defecto, los parámetros 
utilizados para realizar la paginación y el ordenamiento deben llamarse 
```page```, ```size``` y ```sort```. Sin embargo, Spring Boot permite modificar los 
nombres de dichos parámetros a través de la configuración en el archivo 
```application.properties```.

Por ejemplo, podríamos traducir al español los nombres de estos parámetros 
con las siguientes propiedades:

```springdataql
spring.data.web.pageable.page-parameter=pagina
spring.data.web.pageable.size-parameter=tamano
spring.data.web.sort.sort-parameter=orden
```

Por lo tanto, en solicitudes que usen paginación, debemos usar estos 
nombres que fueron definidos. Por ejemplo, para listar los médicos de 
nuestra API trayendo solo 5 registros de la página 2, ordenados por email 
y en orden descendente, la URL de solicitud debe ser:

```springdataql
http://localhost:8080/medicos?tamano=5&pagina=1&orden=email,desc
```

### Para saber más: propiedades de Spring Boot

A lo largo de los cursos, tuvimos que agregar algunas propiedades al 
archivo application.properties para hacer configuraciones en el proyecto, 
como, por ejemplo, configuraciones de acceso a la base de datos.

Spring Boot tiene cientos de propiedades que podemos incluir en este 
archivo, por lo que es imposible memorizarlas todas. Por ello, es 
importante conocer la documentación que enumera todas estas propiedades, 
ya que eventualmente necesitaremos consultarla.

Puede acceder a la documentación oficial en el enlace: [Common Application 
Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html).

### Para saber más: personalización de mensajes de error

Es posible que haya notado que Bean Validation tiene un mensaje de error 
para cada una de sus anotaciones. Por ejemplo, cuando la validación falla 
en algún atributo anotado con ```@NotBlank```, el mensaje de error será: must 
not be blank.

Estos mensajes de error no se definieron en la aplicación, ya que son 
mensajes de error estándar de Bean Validation. Sin embargo, si lo desea, 
puede personalizar dichos mensajes.

Una de las formas de personalizar los mensajes de error es agregar el 
atributo del mensaje a las anotaciones de validación:

```java
public record DatosCadastroMedico(
    @NotBlank(message = "Nombre es obligatorio")
    String nombre,

    @NotBlank(message = "Email es obligatorio")
    @Email(message = "Formato de email es inválido")
    String email,

    @NotBlank(message = "Teléfono es obligatorio")
    String telefono,

    @NotBlank(message = "CRM es obligatorio")
    @Pattern(regexp = "\\d{4,6}", message = "Formato do CRM es inválido")
    String crm,

    @NotNull(message = "Especialidad es obligatorio")
    Especialidad especialidad,

    @NotNull(message = "Datos de dirección son obligatorios")
    @Valid DatosDireccion direccion) {}
```

Otra forma es aislar los mensajes en un archivo de propiedades, que debe 
tener el nombre ValidationMessages.properties y estar creado en el 
directorio src/main/resources:

```
nombre.obligatorio=El nombre es obligatorio
email.obligatorio=Correo electrónico requerido
email.invalido=El formato del correo electrónico no es válido
phone.obligatorio=Teléfono requerido
crm.obligatorio=CRM es obligatorio
crm.invalido=El formato CRM no es válido
especialidad.obligatorio=La especialidad es obligatoria
address.obligatorio=Los datos de dirección son obligatorios
```

Y, en las anotaciones, indicar la clave de las propiedades por el 
propio atributo ```message```, delimitando con los caracteres { e }:

```java
public record DatosRegistroMedico(
    @NotBlank(message = "{nombre.obligatorio}")
    String nombre,

    @NotBlank(message = "{email.obligatorio}")
    @Email(message = "{email.invalido}")
    String email,

    @NotBlank(message = "{telefono.obligatorio}")
    String telefono,

    @NotBlank(message = "{crm.obligatorio}")
    @Pattern(regexp = "\\d{4,6}", message = "{crm.invalido}")
    String crm,

    @NotNull(message = "{especialidad.obligatorio}")
    Especialidad especialidad,

    @NotNull(message = "{direccion.obligatorio}")
    @Valid DatosDireccion direccion) {}
```