# Justificaciones de diseño
[Justificaciones de Diseño.pdf](https://github.com/user-attachments/files/19828372/Justificaciones.de.Diseno.pdf)

### 1. Modelado de Fuente

- Aunque para esta capa solo implementamos `FuenteEstatica`, modelar la clase `Fuente` como abstracta proporciona un modelo extensible y mantenible, es decir, en un futuro se podrán agregar nuevas fuentes sin modificar clases existentes.
- Decidimos el uso de clase abstracta y no interfaz por el simple hecho de que creemos que se compartirán atributos y comportamiento entre las distintas fuentes (`FuenteEstatica`, `FuenteDinamica`, `FuenteIntermediaria`), entonces tenerla abstracta nos permite que distintas implementaciones puedan armarse sin alterar el resto del sistema.
- La responsabilidad de lectura de los hechos es de la `FuenteEstatica`, priorizando simplicidad (KISS) y entregabilidad. Igualmente, sabemos que a futuro convendría delegar esa lectura a una clase especializada, para mejorar los principios de diseño y las capas del sistema.
- Para el método `leerHechos()` de la clase `FuenteEstatica`, al cual le corresponde el leer los hechos de los archivos CSV, usamos la biblioteca `open.csv`, agregando las dependencias correspondientes. Como el tipo de fecha brindado por la cátedra es diferente, tuvimos que cambiar el tipo de formato con `DateTimeFormatter`.

### 2. Modelado de usuarios

- Decidimos no incluir la clase `Visualizador` en el modelo ya que no representa una entidad con lógica de dominio, sino un rol de usuario que interactúa con el sistema a través de casos de uso. Es por esta misma razón que métodos como `rechazarSolicitud()`, `aceptarSolicitud()`, `subirHecho()`, etc. todavía no son implementados.
- El `Administrador`, en cambio, sí fue modelado como atributo en la clase `Solicitud` de tipo `Administrador` (clase), para guardar trazabilidad de las acciones que realiza (como aceptar o rechazar las solicitudes de eliminación).
- Para el caso de los `Contribuyentes`, por ahora son simplemente una clase con los atributos que se pedían, pero como se mencionó previamente, no se implementan aún los casos de uso.

### 3. Criterios de pertenencia en colecciones

- `Criterio` fue definida como una interfaz, con múltiples implementaciones (`CriterioTitulo`, `CriterioLugar`, etc.) ya que de esta forma permitimos agregar nuevos tipos de criterios sin modificar las clases existentes.
- El modelado de `Criterio` permite implementar el Patrón Strategy, ya que se encapsulan distintas formas de resolver el mismo problema en diferentes clases.
- Preferimos el uso de una interfaz por sobre una clase abstracta, ya que solo necesitamos definir un “contrato”; no hay un solo comportamiento en común entre las clases que implementan esa interfaz, es decir, no forzamos a compartir una implementación en común. Además, tampoco tenemos atributos o estados en común entre las subclases.
- Este diseño nos permite lograr: alta cohesión (cada clase `Criterio` tiene una única responsabilidad), bajo acoplamiento (las colecciones no necesitan saber cómo se implementa el criterio, solo deben invocar su interfaz), extensibilidad (podemos agregar nuevos criterios sin tocar el resto del sistema) y facilita la mantenibilidad (permite realizar cambios de manera sencilla).

### 4. Colecciones

- De la clase `Coleccion`, agregamos el método `filtrarHechos()` y algunos métodos que ayudaban a esto (para mayor expresividad). Cuando se filtran los hechos, la colección elige de la fuente qué hechos le pertenecen y cuáles no según los criterios de la misma.
- También implementamos un método para agregar nuevos criterios y así poder volver a filtrar.
- La creación de las colecciones quedará para futuras entregas, pero con este modelado debería poderse realizar de manera correcta.

### 5. Hechos

- Modelamos los Hechos con los atributos necesarios para cumplir con los requisitos de modelado de esta etapa.
- Hicimos al atributo `Etiqueta` de `Hecho` como clase, ya que de esta forma mejora la consistencia de datos. Por ahora, la clase está vacía ya que no tiene mayor utilidad que para los test.
- El `contenidoMultimedia` del hecho no lo implementamos ahora para no tener código muerto (no entra en esta capa).
- Para `Origen` decidimos hacerlo `ENUM` para mejor consistencia de datos y porque (al menos por ahora) no tiene un comportamiento, ni se agregan nuevos tipos.

### 6. Solicitudes de eliminación

- Decidimos hacer una casilla de eliminación como Singleton, ya que solo se necesita una instancia de la misma que almacene todas las solicitudes y el `Administrador` podrá accederla sin guardarla en una variable (la casilla actuaría como una clase global). Esta decisión se debe a que cuando se envía una solicitud a la página, esta se debería almacenar en algún lado hasta que el administrador decida atenderla, entonces creamos ese lugar de almacenamiento.
- Luego se van almacenando en una lista de la cual, al atender, se empezaría por la más antigua (FIFO). Una vez atendida la solicitud por el administrador, esta se guarda teniendo nuevos datos como el administrador que la atendió y cuándo fue atendida.
- Además, las solicitudes solo se almacenan si cumplen el requisito necesario (en este caso una justificación de al menos 500 caracteres).
- Una vez que se acepta una solicitud, el hecho cambia su estado de `eliminado` a verdadero, no pudiendo ser más colocado en colecciones (luego en futuras entregas el ser eliminado también impedirá su visualización).
- Dándole la responsabilidad de atender las solicitudes a nuestra casilla se reduce el acoplamiento entre clases y mejora la cohesión al encapsular toda la responsabilidad de revisión en un único lugar.
- Modelamos las solicitudes mediante la clase `Solicitud`, asociada a un `Hecho` con una `justificación`.

### 7. Tests

- Implementamos varios test basados en lo pedido en los escenarios de prueba que nos fueron entregados.
- Para estos test, la mayoría de cosas fueron hechas de manera manual, por ejemplo, el crear una colección, ya que nuestro objetivo con los tests era simplemente verificar si nuestras implementaciones podrían soportar lo que sigue en futuras entregas.
