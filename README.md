# MetaMapa: Sistema de Mapeo Colaborativo Descentralizado

MetaMapa es una plataforma diseñada para la recopilación, visibilización y mapeo geográfico de información basada en inteligencia colectiva. El sistema permite reportar hechos de interés social de forma organizada en el tiempo y el espacio.

## Contexto y Arquitectura
El proyecto se basa en una arquitectura de microservicios y servicios distribuidos para garantizar la disponibilidad y veracidad de la información:
- Servicio Agregador: Consolida datos provenientes de múltiples orígenes.
- Fuentes de Datos: Soporte para fuentes estáticas (Datasets CSV), dinámicas (contribuciones de usuarios) y proxies (integración con APIs externas).
- Servicio de Estadísticas: Generación de dashboards sobre la actividad y tendencias de los hechos reportados.

## Tecnologías y Protocolos
- API REST: Interfaz principal para la interoperabilidad entre diferentes instancias de la plataforma.
- Protocolos Avanzados: Implementación de gRPC y interfaces GraphQL para consultas dinámicas eficientes.
- Persistencia: Uso de ORM para el mapeo de objetos a esquemas relacionales independientes por servicio.

## Características Técnicas
- Algoritmos de Consenso: Lógica configurable para validar hechos basados en múltiples menciones o mayorías.
- Detección de Spam: Integración de componentes para el filtrado automático de solicitudes de eliminación maliciosas.
- Observabilidad: Implementación de herramientas de monitoreo, trazas y métricas para el control de salud de los servicios.

## Documentación de Diseño
El repositorio incluye diagramas de despliegue, de componentes y de clases que reflejan la evolución del sistema a través de seis entregas técnicas.
