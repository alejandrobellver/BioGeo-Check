# 📋 BioGeo-Check — To-Do

## 1. 🗂️ Configuración Inicial

| # | Tarea | Estado |
|---|-------|--------|
| 1.1 | Crear repositorio en GitHub | ✅ **Hecho** |
| 1.2 | Configurar `.gitignore` para proyecto Android | ✅ **Hecho** |
| 1.3 | Inicializar proyecto en Android Studio | ✅ **Hecho** |
| 1.4 | Crear estructura de paquetes (`data`, `domain`, `ui`) | ✅ **Hecho** |
| 1.5 | Configurar patrón MVVM básico | ✅ **Hecho** |
| 1.6 | Configurar Gradle (dependencias necesarias) | ✅ **Hecho** |

## 2. 🔌 Supabase / Backend

| # | Tarea | Estado |
|---|-------|--------|
| 2.1 | Añadir SDK de Supabase | ⬜ Pendiente |
| 2.2 | Configurar variables de entorno (API keys) | ⬜ Pendiente |
| 2.3 | Probar conexión básica con Supabase | ⬜ Pendiente |
| 2.4 | Definir endpoints REST necesarios | ✅ **Hecho** — Definidos en `ApiService.kt` |
| 2.5 | Configurar backend en FastAPI | ⬜ Pendiente |
| 2.6 | Crear endpoint de autenticación | ⬜ Pendiente |
| 2.7 | Crear endpoint de registro de fichaje | ⬜ Pendiente |
| 2.8 | Probar endpoints con Postman | ⬜ Pendiente |

## 3. 🔐 Autenticación (UI)

| # | Tarea | Estado |
|---|-------|--------|
| 3.1 | Crear pantalla de login | ✅ **Hecho** — `LoginActivity` + layout |
| 3.2 | Crear pantalla de registro | ✅ **Hecho** — `RegisterActivity` + layout |
| 3.3 | Validar inputs (email/password) | ✅ **Hecho** — En ViewModels |
| 3.4 | Implementar llamada a API login | ⬜ Pendiente — Repo creado, falta conectar |
| 3.5 | Guardar sesión del usuario | ✅ **Hecho** — `SessionManager` con DataStore |
| 3.6 | Implementar auto-login (persistencia) | ⬜ Pendiente — SessionManager listo, falta lógica en LoginActivity |

## 4. ⏱️ Sistema de Fichaje (UI)

| # | Tarea | Estado |
|---|-------|--------|
| 4.1 | Crear botón de fichaje en UI | ✅ **Hecho** — Botón circular grande |
| 4.2 | Definir estados (Entrada/Salida) | ✅ **Hecho** — `FichajeState` enum |
| 4.3 | Guardar estado actual del usuario | ✅ **Hecho** — En ViewModel |
| 4.4 | Validar flujo (no salida sin entrada) | ✅ **Hecho** — State machine en HomeViewModel |
| 4.5 | Actualizar UI según estado | ✅ **Hecho** — Cambia color, icono, texto |

## 5. 📍 Geolocalización

| # | Tarea | Estado |
|---|-------|--------|
| 5.1 | Pedir permisos de ubicación | ⬜ Pendiente — Permisos declarados en Manifest |
| 5.2 | Comprobar si GPS está activo | ⬜ Pendiente |
| 5.3 | Integrar Fused Location Provider | ⬜ Pendiente — Dependencia añadida |
| 5.4 | Obtener latitud/longitud | ⬜ Pendiente |
| 5.5 | Manejar errores de localización | ⬜ Pendiente — UI preparada |

## 6. 🗄️ Base de Datos (Supabase)

| # | Tarea | Estado |
|---|-------|--------|
| 6.1 | Crear tabla Usuarios | ⬜ Pendiente — Modelo `User` definido |
| 6.2 | Crear tabla Eventos_Fichaje | ⬜ Pendiente — Modelo `FichajeEvent` definido |
| 6.3 | Crear tabla Sedes | ⬜ Pendiente — Modelo `Sede` definido |
| 6.4 | Relacionar tablas con claves foráneas | ⬜ Pendiente |

## 7. 📡 Registro de Fichaje (Backend)

| # | Tarea | Estado |
|---|-------|--------|
| 7.1 | Enviar datos (user_id, timestamp, coords) | ⬜ Pendiente — `FichajeRequest` definido |
| 7.2 | Guardar fichaje en backend | ⬜ Pendiente |
| 7.3 | Confirmar respuesta exitosa | ⬜ Pendiente — UI de confirmación lista |

## 8. 🏢 Validación de Sede

| # | Tarea | Estado |
|---|-------|--------|
| 8.1 | Definir coordenadas de sede | ⬜ Pendiente — Modelo `Sede` listo |
| 8.2 | Calcular distancia usuario-sede | ⬜ Pendiente |
| 8.3 | Validar si está dentro del radio permitido | ⬜ Pendiente — UI de status lista |

## 9. 🔒 Autenticación Biométrica

| # | Tarea | Estado |
|---|-------|--------|
| 9.1 | Integrar API biométrica Android | ⬜ Pendiente — Dependencia añadida |
| 9.2 | Solicitar autenticación por huella | ⬜ Pendiente |
| 9.3 | Validar resultado antes de fichar | ⬜ Pendiente |

## 10. 📋 Historial de Fichajes

| # | Tarea | Estado |
|---|-------|--------|
| 10.1 | Crear RecyclerView | ✅ **Hecho** — En `activity_history.xml` |
| 10.2 | Crear Adapter | ✅ **Hecho** — `FichajeAdapter` |
| 10.3 | Consumir API de fichajes | ⬜ Pendiente — Repo listo |
| 10.4 | Mostrar lista de fichajes | ✅ **Hecho** — Datos de ejemplo |
| 10.5 | Formatear fechas correctamente | ✅ **Hecho** — `SimpleDateFormat` |

## 11. 👥 Panel Admin / Roles

| # | Tarea | Estado |
|---|-------|--------|
| 11.1 | Añadir campo rol en usuario | ✅ **Hecho** — `role` en modelo User |
| 11.2 | Restringir vistas según rol | ⬜ Pendiente |
| 11.3 | Crear vista admin | ✅ **Hecho** — `AdminActivity` + layout |
| 11.4 | Mostrar fichajes globales | ✅ **Hecho** — Datos de ejemplo |
| 11.5 | Implementar exportación a CSV/PDF | ⬜ Pendiente — Botones UI listos |

## 12. 🧹 Pulido Final

| # | Tarea | Estado |
|---|-------|--------|
| 12.1 | Mejorar UI/UX básica | ✅ **Hecho** — Tema Material3, gradientes, cards |
| 12.2 | Crear tests unitarios backend | ⬜ Pendiente |
| 12.3 | Probar login | ⬜ Pendiente |
| 12.4 | Probar fichaje | ⬜ Pendiente |
| 12.5 | Probar GPS | ⬜ Pendiente |
| 12.6 | Probar biometría | ⬜ Pendiente |
| 12.7 | Refactorizar código | ⬜ Pendiente |
| 12.8 | Eliminar código innecesario | ✅ **Hecho** — `MainActivity` eliminada |
| 12.9 | Documentar proyecto | ⬜ Pendiente |
| 12.10 | Generar APK firmado | ⬜ Pendiente |

---

## 📊 Resumen

| Categoría | Total | ✅ Hechos | ⬜ Pendientes |
|-----------|-------|----------|--------------|
| Config inicial | 6 | **6** | 0 |
| Supabase/Backend | 8 | **1** | 7 |
| Auth UI | 6 | **4** | 2 |
| Fichaje UI | 5 | **5** | 0 |
| Geolocalización | 5 | **0** | 5 |
| Base de datos | 4 | **0** | 4 |
| Registro fichaje | 3 | **0** | 3 |
| Validación sede | 3 | **0** | 3 |
| Biometría | 3 | **0** | 3 |
| Historial | 5 | **4** | 1 |
| Admin/Roles | 5 | **3** | 2 |
| Pulido final | 10 | **2** | 8 |
| **TOTAL** | **63** | **25** | **38** |

> [!NOTE]
> **Progreso: ~40%** — Toda la estructura de app, UI/pantallas, MVVM, modelos de datos y navegación están completos. Lo que falta es la lógica real: conexión con backend/Supabase, GPS, biometría, y tests.
