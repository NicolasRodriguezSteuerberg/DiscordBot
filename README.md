# Discord bot - Java, Spring, JDA y PostgreSQL
Bot de Discord desarrollado en Java utilizando Spring, JDA y PostgreSQL, enfocado en la gestión de comunidad, gamificación por actividad y sistema de música con controles interactivos.

## Funcionalidades
### Bienvenida y despedida
- Mensajes totalmente configurables mediante embeds
- Título, fecha y canal de reglas (solo bienvenidas) opcionales
- Diferentes mensajes dinámicos con etiquetas (%u -> para mencionar al usuario)
- Imagen del usuario integrada
- Canal configurable (o en el canal del comando)

### Sistema de puntuación
- Puntos por actividad en texto
- Puntos por tiempo en canales de voz
- Ranking de texto y voz
- Visualización con paginación mediante botones
- Consulta de puntuación individual

### Sistema de música
- Reproducción de canciones con cola
- Visualización de la canción actual con botones interactivos:
  - Pausar / Reanudar
  - Skip
  - Stop
- Visualización de playlist con paginación mediante botones


## Estado del proyecto
El proyecto se encuentra funcional y terminado, aunque existen áreas que pueden ser mejoradas, como:
- Refactorización de ciertas partes del código
- Mejora de la arquitectura interna
- Agregar más logs

El objetivo principal del proyecto fue consolidar conocimientos en Java con Spring.


## Ejecución
1. Clonar el repositorio
2. Configurar mediante variables de entorno
```text
- `DISCORD_BOT_TOKEN`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
```
3. Ejecutar la aplicación a través de maven
