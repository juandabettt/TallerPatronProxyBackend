Eres un desarrollador backend senior especializado en Spring Boot. Tu tarea es implementar un backend completo y funcional en el proyecto Spring Boot existente.
CONTEXTO DEL PROYECTO:

El proyecto ya existe con Spring Boot 3.5.13, Maven, Java 21
Package base: com.example.demo
NO crear nuevo proyecto, trabajar sobre el existente

ARQUITECTURA REQUERIDA:
1. DTOs:
GenerationRequest  → campo: String prompt
GenerationResponse → campos: String generatedText, int tokensUsed
2. Interfaz:
AIGenerationService → método: GenerationResponse generate(GenerationRequest request)
3. Implementaciones en orden de cadena:
MockAIGenerationService     → implementa AIGenerationService (servicio real al final de la cadena)
RateLimitProxyService       → implementa AIGenerationService (primer filtro)
QuotaProxyService           → implementa AIGenerationService (segundo filtro)
4. Cadena de proxies obligatoria:
Request → RateLimitProxyService → QuotaProxyService → MockAIGenerationService → Response
COMPORTAMIENTO DE CADA CLASE:
MockAIGenerationService:

Simula generación con Thread.sleep(1200)
Retorna texto aleatorio predefinido (mínimo 5 textos distintos en un array)
Calcula tokensUsed como prompt.length() / 4 + longitud del texto generado

RateLimitProxyService:

Recibe por constructor: la siguiente instancia de AIGenerationService + String userId
Verifica requests por minuto según plan del usuario
Si supera el límite: lanza excepción que el controller mapea a HTTP 429 con header Retry-After: 60
Planes: FREE=10 req/min, PRO=60 req/min, ENTERPRISE=sin límite

QuotaProxyService:

Recibe por constructor: la siguiente instancia de AIGenerationService + String userId
Verifica cuota mensual de tokens
Descuenta tokensUsed DESPUÉS de que MockAI responde
Si cuota agotada ANTES de llamar: lanza excepción que el controller mapea a HTTP 402
Planes: FREE=50000 tokens/mes, PRO=500000 tokens/mes, ENTERPRISE=sin límite

ESTADO EN MEMORIA:
Crear clase UserState con:
String userId
String plan              → valor inicial: "FREE"
int requestsThisMinute   → valor inicial: 0
long tokensUsedThisMonth → valor inicial: 0
LocalDateTime resetDate  → primer día del mes siguiente
List<DailyUsage> history → lista de los últimos 7 días
Crear clase DailyUsage con:
LocalDate date
long tokensUsed
int requestCount
Usar Map<String, UserState> estático inicializado con userId = "user-1" en FREE.
ENDPOINTS:
POST /api/ai/generate

Body: GenerationRequest
Pasa por la cadena completa RateLimit → Quota → Mock
Response 200: GenerationResponse
Response 429: con header Retry-After: 60
Response 402: body con mensaje "Quota exceeded"

GET /api/quota/status

Response: tokensUsed, tokensRemaining, resetDate, plan

GET /api/quota/history

Response: lista de DailyUsage de los últimos 7 días

POST /api/quota/upgrade

Cambia plan de FREE a PRO para user-1
Response: mensaje confirmación + nuevo plan

TAREAS PROGRAMADAS:
java@Scheduled(fixedRate = 60000)       → resetear requestsThisMinute a 0 para todos los usuarios
@Scheduled(cron = "0 0 0 1 * *")   → resetear tokensUsedThisMonth a 0 el día 1 de cada mes
Agregar @EnableScheduling en la clase principal.
CONFIGURACIÓN:
application.properties:
propertiesserver.port=${PORT:8080}
spring.main.allow-bean-definition-overriding=true
Clase CorsConfig:
Permitir todos los orígenes → allowedOrigins("*")
Permitir métodos → GET, POST, PUT, OPTIONS
Mapping → /api/**
MANEJO DE EXCEPCIONES:
Crear GlobalExceptionHandler con @RestControllerAdvice:

RateLimitException → 429 + header Retry-After: 60
QuotaExceededException → 402 + body con mensaje
Exception genérica → 500 + mensaje

INYECCIÓN DE DEPENDENCIAS:
Crear clase ProxyChainConfig con @Configuration que construya el bean así:
java@Bean
public AIGenerationService aiGenerationService() {
    MockAIGenerationService mock = new MockAIGenerationService();
    QuotaProxyService quota = new QuotaProxyService(mock, "user-1");
    RateLimitProxyService rateLimit = new RateLimitProxyService(quota, "user-1");
    return rateLimit;
}
RESTRICCIONES:

Sin base de datos ni JPA
Sin Spring Security
Sin dependencias extra al pom.xml excepto las ya existentes de Spring Web y Lombok
Lombok para getters/setters/constructores en DTOs y modelos
El proyecto debe compilar con ./mvnw clean package -DskipTests sin errores

ENTREGA:
Genera cada archivo con su ruta completa desde la raíz del proyecto. Por ejemplo:
src/main/java/com/example/demo/service/AIGenerationService.java
