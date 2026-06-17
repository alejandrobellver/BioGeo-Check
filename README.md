# BioGeo-Check

BioGeo-Check es una aplicacion Android diseñada para la gestion integral de recursos humanos y el control de presencia laboral (fichaje). El sistema permite a las empresas cumplir con la normativa de registro de jornada laboral, asegurando la veracidad de los datos mediante la combinacion de seguridad biometrica y geolocalizacion.

## Caracteristicas Principales

*   **Autenticacion y Roles:** Sistema basado en roles (Jefe y Trabajador). Solo los administradores pueden registrar la empresa e invitar a los trabajadores, garantizando que no existan cuentas no autorizadas.
*   **Fichaje Seguro:** Registro de entrada y salida protegido mediante validacion biometrica (huella dactilar o reconocimiento facial del dispositivo) y comprobacion de ubicacion (geovalla mediante sensor GPS).
*   **Gestion de Departamentos:** Creacion y administracion de departamentos, permitiendo asignar empleados a diferentes secciones de forma estructurada.
*   **Panel de Administracion:** Herramientas de visualizacion para los administradores, donde pueden consultar el historial de fichajes y gestionar la plantilla de empleados.
*   **Recuperacion de Cuentas:** Flujo seguro de recuperacion de contrasenas mediante codigo de un solo uso (OTP) enviado por correo electronico.

## Arquitectura y Tecnologias

El proyecto ha sido desarrollado siguiendo los principios de Clean Architecture y el patron MVVM (Model-View-ViewModel), asegurando una estricta separacion de responsabilidades.

### Frontend (Android)
*   **Lenguaje:** Kotlin.
*   **Interfaz de Usuario:** Jetpack Compose (UI declarativa).
*   **Navegacion:** Navigation Compose.
*   **Concurrencia:** Kotlin Coroutines y Flow/StateFlow para la gestion reactiva del estado.
*   **Hardware:** Biometric API y FusedLocationProviderClient (Location Services).

### Backend (Supabase)
*   **Base de Datos:** PostgreSQL con uso de UUIDs y restricciones referenciales.
*   **Autenticacion:** Supabase Auth (GoTrue API).
*   **Conexion:** SDK oficial de Supabase para Kotlin (supabase-kt).

## Requisitos del Sistema

Para compilar y ejecutar este proyecto, se requiere:
*   Android Studio (version Koala o superior recomendada).
*   Android SDK (minSdkVersion 26 - Android 8.0 Oreo).
*   Conexion a Internet activa en el dispositivo o emulador.
*   Dispositivo con soporte para sensores biometricos y GPS.

## Configuracion del Entorno

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/alejandrobellver/BioGeo-Check.git
    ```
2.  **Configuracion de Supabase:**
    Es necesario disponer de una instancia de Supabase configurada. Se deben establecer las variables de entorno de conexion (URL y API KEY) en la configuracion del cliente de red (`SupabaseClient.kt`).
3.  **Compilacion:**
    Abrir el proyecto con Android Studio y sincronizar los archivos de Gradle. Compilar el proyecto en un emulador o dispositivo fisico.

## Estructura del Proyecto

El codigo fuente principal se encuentra en `app/src/main/java/com/example/biogeo_check/` y esta dividido en:
*   `data/`: Modelos de datos (Data Classes), configuracion de red y Repositorios.
*   `ui/`: Pantallas desarrolladas en Jetpack Compose, componentes reutilizables, gestion de temas y ViewModels.
*   `util/`: Clases de utilidad y helpers (por ejemplo, `LocationHelper.kt`).

## Licencia y Uso

Desarrollado como Proyecto de Fin de Grado (TFG) / Ciclo Formativo.
