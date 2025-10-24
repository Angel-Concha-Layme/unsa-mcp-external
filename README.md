# UNSA MCP Server

Servidor MCP (Model Context Protocol) para la Semana de Ciencias de la Computación de la Universidad Nacional de San Agustín (UNSA). Este proyecto proporciona herramientas y servicios para gestionar eventos, ponentes, sesiones y embeddings semánticos.

## 📋 Descripción

El UNSA MCP Server es una aplicación Spring Boot que implementa el protocolo MCP para proporcionar herramientas de IA que permiten:

- **Gestión de Eventos**: Administrar ediciones anuales de eventos académicos
- **Gestión de Ponentes**: Gestionar información de speakers y sus contactos
- **Gestión de Sesiones**: Organizar charlas, talleres y presentaciones
- **Búsqueda Semántica**: Búsqueda inteligente usando embeddings de OpenAI
- **Integración MCP**: Servidor compatible con el protocolo MCP para integración con clientes de IA

## 🏗️ Arquitectura

### Backend
- **Framework**: Spring Boot 3.5.6 con Java 21
- **Base de Datos**: PostgreSQL con extensión pgvector para embeddings
- **Migraciones**: Flyway para gestión de esquemas
- **IA**: Spring AI con OpenAI para generación de embeddings
- **Protocolo**: MCP Server para integración con clientes de IA

### Frontend
- **Framework**: React con Material-UI 7.2.0
- **Estado**: Gestión de estado centralizada
- **UI**: Componentes consistentes con MUI

## 🚀 Instalación y Deploy

### Prerrequisitos

- Java 21+
- Maven 3.6+
- PostgreSQL 16+ con extensión pgvector
- Docker y Docker Compose (opcional)
- Clave API de OpenAI

### Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```bash
# Base de datos
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=mcp
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# OpenAI
OPENAI_API_KEY=tu_clave_api_aqui
```

### Opción 1: Deploy con Docker Compose (Recomendado)

1. **Clona el repositorio**:
```bash
git clone <repository-url>
cd unsa-mcp-external
```

2. **Configura las variables de entorno**:
```bash
cp .env.example .env
# Edita .env con tus valores
```

3. **Inicia los servicios**:
```bash
cd backend
docker-compose up -d
```

4. **Ejecuta las migraciones**:
```bash
./mvnw flyway:migrate
```

5. **Inicia la aplicación**:
```bash
./mvnw spring-boot:run
```

### Opción 2: Deploy Manual

1. **Configura PostgreSQL**:
```sql
-- Instala la extensión pgvector
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;
```

2. **Configura las variables de entorno**:
```bash
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=mcp
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres
export OPENAI_API_KEY=tu_clave_api_aqui
```

3. **Construye y ejecuta**:
```bash
cd backend
./mvnw clean package
java -jar target/unsa-mcp-0.0.1-SNAPSHOT.jar
```

### Opción 3: Deploy en Producción

1. **Configura la base de datos de producción**:
```bash
# Actualiza application.properties con los valores de producción
spring.datasource.url=jdbc:postgresql://prod-host:5432/mcp_prod
spring.datasource.username=prod_user
spring.datasource.password=prod_password
```

2. **Construye el JAR**:
```bash
./mvnw clean package -Pproduction
```

3. **Despliega**:
```bash
java -jar target/unsa-mcp-0.0.1-SNAPSHOT.jar
```

## 🔧 Configuración

### Base de Datos

El proyecto utiliza las siguientes tablas principales:

- **events**: Eventos anuales (ej: Semana de Ciencias de la Computación 2024)
- **speakers**: Ponentes y speakers
- **sessions**: Sesiones, charlas y talleres
- **entity_embeddings**: Embeddings semánticos para búsqueda inteligente

### Configuración de OpenAI

```properties
# application.properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.embedding.options.model=text-embedding-3-small
```

### Configuración MCP

```properties
# MCP Server Configuration
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.protocol=STREAMABLE
```

## 📚 API Endpoints

### Eventos
- `GET /api/events` - Listar eventos
- `POST /api/events` - Crear evento
- `GET /api/events/{id}` - Obtener evento específico

### Ponentes
- `GET /api/speakers` - Listar ponentes
- `POST /api/speakers` - Crear ponente
- `GET /api/speakers/{id}` - Obtener ponente específico

### Sesiones
- `GET /api/sessions` - Listar sesiones
- `POST /api/sessions` - Crear sesión
- `GET /api/sessions/{id}` - Obtener sesión específica

### MCP Tools
- `GET /mcp/tools` - Listar herramientas MCP disponibles
- `POST /mcp/tools/{toolName}` - Ejecutar herramienta MCP

## 🧪 Testing

```bash
# Ejecutar tests
./mvnw test

# Ejecutar tests con cobertura
./mvnw test jacoco:report
```

## 📊 Monitoreo y Logs

### Logs
```properties
# Configuración de logging
logging.level.org.springframework.ai.mcp=DEBUG
logging.level.org.springframework.ai=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Health Check
- `GET /actuator/health` - Estado de la aplicación
- `GET /mcp/debug` - Información de debug MCP

## 🔍 Búsqueda Semántica

El sistema incluye capacidades de búsqueda semántica:

- **Búsqueda de Ponentes**: Encuentra speakers por similitud semántica
- **Búsqueda de Sesiones**: Localiza sesiones por contenido
- **Embeddings Automáticos**: Generación automática de embeddings para nuevas entidades

## 🛠️ Desarrollo

### Estructura del Proyecto

```
backend/
├── src/main/java/pe/unsa/mcp/
│   ├── controller/     # Controladores REST
│   ├── dto/           # Data Transfer Objects
│   ├── model/         # Entidades JPA
│   ├── repository/    # Repositorios de datos
│   ├── services/      # Lógica de negocio
│   └── mcp/          # Configuración MCP
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/  # Migraciones Flyway
└── compose.yaml       # Docker Compose
```

### Estándares de Código

- **Idioma**: Inglés para nombres de clases, métodos y variables
- **Comentarios**: Solo para decisiones no triviales
- **Validaciones**: Centralizadas en servicios
- **Manejo de errores**: Usando excepciones del core de Spring

## 🚨 Troubleshooting

### Problemas Comunes

1. **Error de conexión a PostgreSQL**:
   - Verifica que PostgreSQL esté ejecutándose
   - Confirma las credenciales en las variables de entorno

2. **Error de OpenAI API**:
   - Verifica que la clave API sea válida
   - Confirma que tengas créditos disponibles

3. **Error de migraciones**:
   - Verifica que la base de datos tenga las extensiones necesarias
   - Ejecuta las migraciones manualmente si es necesario

### Logs Útiles

```bash
# Ver logs de la aplicación
tail -f logs/application.log

# Ver logs de Docker
docker-compose logs -f
```

## 📝 Licencia

Este proyecto está desarrollado para la Universidad Nacional de San Agustín (UNSA).

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

