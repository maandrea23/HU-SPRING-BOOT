# Eventify — Semanas 1 y 2

API de eventos y lugares con datos persistentes. Este proyecto adapta la base ya
creada con Spring Initializr; conserva el paquete `com.example.productoapi` y la
clase `ProductoApiApplication` para que puedas seguir usando tu configuración del IDE.

## 1. Comprende las dependencias

Abre `pom.xml`: es la lista de herramientas que Maven descarga para tu proyecto.

| Dependencia | Para qué la usamos |
| --- | --- |
| `spring-boot-starter-web` | Spring MVC, conversión JSON y servidor Tomcat |
| `spring-boot-starter-data-jpa` | JPA, Hibernate y repositorios para guardar en tablas |
| `h2` | Base de datos local persistente en `./data/eventify` |
| `lombok` | Generar getters, setters y constructores de los modelos |
| `springdoc-openapi-starter-webmvc-ui` | Documentar y probar la API con Swagger |
| `spring-boot-starter-test` | JUnit 5, Mockito y MockMvc; solo durante las pruebas |

Usamos Java 21, Spring Boot 3.5.16 y springdoc 2.9.1 para ajustarnos al enunciado
que pide `starter-web` y JUnit 5. La base original usaba Boot 4.1.1, cuyo starter
MVC recomendado es `starter-webmvc` y cuyas pruebas usan JUnit 6 por defecto.
Boot 3.5.16 es la última versión OSS de la rama 3.5; esta elección corresponde al
requisito del curso. Antes de desplegar un producto real, revisa la versión soportada
con tu docente. No necesitamos JPA, H2 ni MySQL en esta semana.

Referencias: [Spring Boot 3.5.16](https://spring.io/blog/2026/06/25/spring-boot-3-5-16-available-now/)
y [compatibilidad de springdoc](https://springdoc.org/v2/).

## 2. Reconoce la estructura

Todos estos paquetes están dentro de `src/main/java/com/example/productoapi`:

```text
ProductoApiApplication.java  ← punto de entrada
model/                      ← Event y Venue: los datos
repository/                 ← almacenamiento en Map
service/                    ← validaciones de negocio
controller/                 ← peticiones y respuestas HTTP
exception/                  ← errores controlados
config/                     ← datos iniciales al arrancar
```

El recorrido es `Controller → Service → Repository`.
La respuesta vuelve al cliente como JSON. Spring MVC organiza la atención de
peticiones; en una API REST no necesitamos plantillas HTML para presentar los datos.

## 3. Empieza leyendo los modelos

Abre `model/Event.java`. Un POJO es una clase Java que representa datos:

- `Long id`: identificador generado por el repositorio. No se envía al crear.
- `String nombre`: nombre obligatorio.
- `LocalDate fecha`: fecha sin hora, por ejemplo `2026-10-15`.
- `String descripcion`: texto opcional.

`Venue` contiene `id`, `nombre`, `direccion` y `capacidad`.
Usamos `Integer` para poder detectar que la capacidad no se envió (`null`).
Esta semana los eventos y lugares son catálogos independientes.

Lombok genera código al compilar:

- `@Data`: getters, setters, `toString`, `equals` y `hashCode`.
- `@NoArgsConstructor`: constructor sin argumentos.
- `@AllArgsConstructor`: constructor con todos los atributos, en orden.

En la semana 2, `@Entity` indica que cada objeto se guarda en una tabla. `@Table`
define los nombres `events` y `venues`. `@Id` identifica cada fila y
`@GeneratedValue` permite que H2 genere el ID. `@Column(nullable = false)` convierte
las reglas importantes en restricciones de la tabla.

## 4. Entiende el repositorio

Abre `repository/EventRepository.java`. Esta interfaz hereda `JpaRepository`.
Spring Data crea su implementación automáticamente: ofrece `save`, `findById`,
`findAll`, `delete` y paginación sin que escribamos SQL.

`findByNombreContaining` es una consulta derivada: Spring lee su nombre y genera
una consulta que encuentra los nombres que contienen el texto indicado.

H2 almacena el catálogo en `data/eventify.mv.db`. Ese archivo no se versiona con
Git y los datos se conservan cuando detienes y vuelves a iniciar la aplicación.

## 5. Entiende el servicio y la inyección por constructor

Abre `service/EventService.java`.
`@Service` identifica la clase que aplica las reglas del negocio.
Antes de llamar al repositorio comprueba que el nombre tenga contenido y la fecha exista.
No se exige una fecha futura porque el enunciado no lo pide.
En lugares, también se exige dirección y capacidad mayor que cero.

```java
private final EventRepository repository;

public EventService(EventRepository repository) {
    this.repository = repository;
}
```

El servicio necesita un repositorio para funcionar. Spring se lo entrega por
el constructor. Eso es **inyección de dependencias**. `final` indica que esa referencia
no puede sustituirse después de construir el servicio. Con un solo constructor,
Spring no necesita `@Autowired`.

Un **Bean** es un objeto que Spring crea y administra. Los repositorios,
servicios y controladores son Beans gracias a sus anotaciones. Normalmente existe
una instancia por contexto de Spring: por eso las peticiones comparten el catálogo.
Los modelos se crean con `new` y no son Beans.

## 6. Entiende el controlador y los errores

Abre `controller/EventController.java`:

- `@RestController`: recibe peticiones y devuelve datos, normalmente JSON.
- `@RequestMapping`: define la ruta base.
- `@GetMapping`: permite consultar.
- `@PostMapping`: permite registrar.
- `@RequestBody`: convierte el JSON recibido en un objeto Java.
- `ResponseEntity`: permite elegir el estado HTTP y el cuerpo de respuesta.
- `@Tag` y `@Operation`: dan nombres y descripciones en Swagger.

| Método | Ruta | Respuesta correcta |
| --- | --- | --- |
| GET | `/api/events?page=0&size=10&sort=nombre,asc` | 200 y página de eventos |
| POST | `/api/events` | 201 y evento creado |
| GET | `/api/events/{id}` | 200, o 404 si no existe |
| PUT | `/api/events/{id}` | 200 y evento actualizado |
| DELETE | `/api/events/{id}` | 204, o 404 si no existe |
| GET | `/api/venues?page=0&size=10&sort=nombre,asc` | 200 y página de lugares |
| POST | `/api/venues` | 201 y lugar creado |
| GET, PUT, DELETE | `/api/venues/{id}` | 200/204, o 404 si no existe |

El servicio lanza `DatosInvalidosException` cuando los datos no cumplen las reglas.
`ApiExceptionHandler`, anotado con `@RestControllerAdvice`, transforma ese error
en una respuesta 400 con un campo `mensaje`. También controla JSON ilegible o
una fecha con formato incorrecto. No se almacena el registro inválido.

## 7. Entiende el seeder

Abre `config/SeedConfig.java`. `@Configuration` declara una clase de configuración.
`@Bean` registra el objeto que devuelve el método `cargarDatosIniciales()`.
Ese objeto es un `CommandLineRunner`: Spring ejecuta su tarea al terminar de arrancar.
La carga pasa por los servicios, así que respeta las mismas validaciones.

En `src/main/resources/application.properties`:

```properties
eventify.seed.enabled=true
```

Con `true` se crean un evento y un lugar de ejemplo. Con `false` y un **reinicio**,
ambos catálogos comienzan vacíos. Cambiar la propiedad sin reiniciar no borra datos.

## 8. Ejecuta y prueba manualmente

1. En IntelliJ, recarga el proyecto Maven después de cambiar `pom.xml`.
2. Ejecuta `ProductoApiApplication` con el botón junto a `main`.
3. Abre http://localhost:8080/swagger-ui.html.
4. Expande una operación y pulsa **Try it out**, luego **Execute**.

Alternativamente, desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

Si tienes Maven instalado, también puedes usar `mvn spring-boot:run`.

Para `POST /api/events`, pega:

```json
{
  "nombre": "Taller de Spring Boot",
  "fecha": "2026-10-15",
  "descripcion": "Mi primer evento"
}
```

Espera **201** y el objeto con un `id`. Después ejecuta `GET /api/events`:
el evento debe aparecer en una lista con estado **200**.

Para `POST /api/venues`, usa:

```json
{
  "nombre": "Auditorio Central",
  "direccion": "Calle 10 # 20-30",
  "capacidad": 200
}
```

Repite el POST de eventos con `"nombre": "   "`. Espera **400**:

```json
{"mensaje": "El nombre es obligatorio"}
```

Comprueba con GET que la cantidad de registros no aumentó.
Un GET de la semana 2 devuelve una página. Por ejemplo, un catálogo vacío responde:

```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0
}
```

Prueba la paginación con:

```text
GET http://localhost:8080/api/events?page=0&size=5&sort=nombre,asc
```

Para buscar por una parte del nombre añade `nombre`, por ejemplo:

```text
GET http://localhost:8080/api/events?nombre=Spring&page=0&size=5
```

Para actualizar en Postman, usa `PUT http://localhost:8080/api/events/1`, selecciona
**Body → raw → JSON** y envía el objeto completo, sin necesidad de incluir `id`:

```json
{
  "nombre": "Taller Spring Boot actualizado",
  "fecha": "2026-10-21",
  "descripcion": "Nueva información"
}
```

Para eliminar usa `DELETE http://localhost:8080/api/events/1`. Una eliminación
correcta responde **204 No Content**, por eso no tiene cuerpo de respuesta. Si el
ID no existe, los tres métodos por ID responden **404** con un `mensaje` claro.

## 9. Lee y ejecuta las pruebas

```bash
./mvnw test
```

Empieza por `src/test/java/com/example/productoapi/service/EventServiceTest.java`.
Las pruebas de servicios usan JUnit 5 y Mockito **sin iniciar Spring**:

1. Preparar: crear una entrada y definir qué devolverá el repositorio simulado.
2. Actuar: llamar a `service.registrar()`.
3. Verificar: comprobar el resultado y que se llamó a `guardar()`.

`@Mock` crea un repositorio simulado. `when(...).thenReturn(...)` programa su respuesta.
`verifyNoInteractions(repository)` demuestra que un registro inválido nunca llega al repositorio.
Las pruebas cubren nombre nulo, vacío o con espacios, campos obligatorios y listado vacío.

Las pruebas de repositorios usan `@DataJpaTest`: arrancan solamente la capa JPA
con una H2 temporal y comprueban el guardado, la consulta derivada y las páginas.
`CatalogoHttpTest` verifica el CRUD, 200/201/204/400/404, paginación y Swagger.
Las pruebas nunca escriben en `data/eventify`.

## Orden sugerido para estudiar

Recorre los pasos 1 a 3 primero. Luego sigue un registro por repositorio, servicio
y controlador (pasos 4 a 6). Finalmente revisa el seeder, prueba Swagger y lee una
prueba unitaria. No hace falta memorizar todas las anotaciones en una sola sesión.
