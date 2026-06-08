# 📋 BioGeo-Check — To-Do Actualizado

## 1. 🗂️ Configuración Inicial y Arquitectura

| # | Tarea | Estado |
|---|-------|--------|
| 1.1 | Crear repositorio en GitHub y `.gitignore` | ✅ **Hecho** |
| 1.2 | Inicializar proyecto en Android Studio | ✅ **Hecho** |
| 1.3 | Crear estructura Clean Architecture (`data`, `ui`, `core`) | ✅ **Hecho** |
| 1.4 | Crear Data Classes (`Empresa`, `Trabajador`, `Fichaje`...) en `model` | ✅ **Hecho** |
| 1.5 | Primer commit estructural en `develop` | ✅ **Hecho** |

## 2. 🗄️ Base de Datos (Supabase Backend)

| # | Tarea | Estado |
|---|-------|--------|
| 2.1 | Crear tablas (`empresa`, `trabajador`, `fichaje`, `incidencia`...) | ✅ **Hecho** |
| 2.2 | Configurar UUIDs y valores por defecto | ✅ **Hecho** |
| 2.3 | Relacionar tablas con claves foráneas (Foreign Keys) | ✅ **Hecho** |
| 2.4 | Crear ENUMs para control de estados estricto | ✅ **Hecho** |

## 3. 🔌 Conexión de Red (Android -> Supabase)

| # | Tarea | Estado |
|---|-------|--------|
| 3.1 | Añadir dependencias de Supabase a Gradle | ✅ **Hecho** |
| 3.2 | Configurar variables `URL` y `API_KEY` en el proyecto | ✅ **Hecho** |
| 3.3 | Crear Singleton `SupabaseClient.kt` y probar conexión | ✅ **Hecho** |

## 4. 🔐 Repositorios y Autenticación

| # | Tarea | Estado |
|---|-------|--------|
| 4.1 | Crear `AuthRepository` para gestionar signUp y signIn | ⬜ Pendiente |
| 4.2 | Crear pantalla de Registro de Empresa (Rol: Jefe) | ⬜ Pendiente |
| 4.3 | Crear pantalla de Activación de Trabajador | ⬜ Pendiente |
| 4.4 | Crear pantalla de Login (Día a día) y validaciones | ⬜ Pendiente |
| 4.5 | Implementar redirección de UI según el Rol (`JEFE` / `TRABAJADOR`) | ⬜ Pendiente |

## 5. ⏱️ Sistema de Fichaje (UI y Lógica)

| # | Tarea | Estado |
|---|-------|--------|
| 5.1 | Crear `FichajesRepository` para operaciones CRUD | ⬜ Pendiente |
| 5.2 | Diseñar pantalla principal de fichaje (Botones Entrada/Salida) | ⬜ Pendiente |
| 5.3 | Implementar ViewModel para controlar el estado del botón | ⬜ Pendiente |
| 5.4 | Cronómetro en tiempo real (Corrutinas en ViewModel) | ⬜ Pendiente |
| 5.5 | Insertar filas en la tabla `fichaje` al pulsar el botón | ⬜ Pendiente |

## 6. 📍 Geolocalización y Mapas

| # | Tarea | Estado |
|---|-------|--------|
| 6.1 | Configurar permisos de ubicación (`ACCESS_FINE_LOCATION`) | ⬜ Pendiente |
| 6.2 | Capturar lat/lon exacta (One-Shot Location) al fichar | ⬜ Pendiente |
| 6.3 | Añadir botón Intent para "Cómo llegar" (Google Maps nativo) | ⬜ Pendiente |

## 7. 🔒 Autenticación Biométrica

| # | Tarea | Estado |
|---|-------|--------|
| 7.1 | Añadir dependencia y permisos de Biometría | ⬜ Pendiente |
| 7.2 | Interceptar el clic de fichaje con el prompt de huella | ⬜ Pendiente |

## 8. 👥 Panel Admin y Listados

| # | Tarea | Estado |
|---|-------|--------|
| 8.1 | Diseñar UI del Panel del Jefe (Listado de trabajadores) | ⬜ Pendiente |
| 8.2 | Historial de fichajes con RecyclerView/LazyColumn | ⬜ Pendiente |
| 8.3 | Pantalla de creación y gestión de incidencias | ⬜ Pendiente |

## 9. 🧹 Pulido y Entrega

| # | Tarea | Estado |
|---|-------|--------|
| 9.1 | Refactorizar código y aplicar estilos unificados (Material3) | ⬜ Pendiente |
| 9.2 | Probar flujo completo (Login -> Fichaje Biométrico -> BD) | ⬜ Pendiente |
| 9.3 | Documentar código para memoria del TFG | ⬜ Pendiente |
| 9.4 | Generar y firmar el archivo APK definitivo | ⬜ Pendiente |

---

## 📊 Resumen

| Categoría | Total | ✅ Hechos | ⬜ Pendientes |
|-----------|-------|----------|--------------|
| Configuración Inicial | 5 | **5** | 0 |
| Supabase Backend | 4 | **4** | 0 |
| Conexión de Red | 3 | **3** | 0 |
| Repositorios / Auth | 5 | **0** | 5 |
| Sistema de Fichaje | 5 | **0** | 5 |
| Geolocalización | 3 | **0** | 3 |
| Biometría | 2 | **0** | 2 |
| Panel Admin | 3 | **0** | 3 |
| Pulido Final | 4 | **0** | 4 |
| **TOTAL** | **34** | **12** | **22** |

> [!NOTE]
> **Progreso: ~35%** — Estructura base completada (Carpetas, MVVM, Modelos y Backend/Supabase listo y conectado). Siguiente fase: Lógica de Autenticación y control de accesos.
