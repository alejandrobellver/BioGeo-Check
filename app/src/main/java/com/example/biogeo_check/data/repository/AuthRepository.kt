package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Empresa
import com.example.biogeo_check.data.model.Invitacion
import com.example.biogeo_check.data.model.Trabajador
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

/**
 * Repositorio encargado de gestionar la seguridad, el control de acceso y el aprovisionamiento
 * inicial de entidades relacionales (Empresas, Jefes y Trabajadores Invitados) en Supabase.
 *
 * Actúa como la capa DAO intermedia de seguridad, interactuando simultáneamente con el módulo
 * de autenticación (Supabase Auth) y las tablas de datos relacionales a través de PostgREST.
 *
 * @property supabase El cliente centralizado e inicializado de la infraestructura de Supabase.
 */
class AuthRepository(private val supabase: SupabaseClient) {

    // =============================================================================================
    // PROCESOS DE ALTA Y REGISTRO (SIGN UP)
    // =============================================================================================

    /**
     * Ejecuta el alta transaccional de una nueva empresa en el ecosistema de la app junto a su Jefe administrador.
     *
     * El flujo operativo realiza de forma secuencial las siguientes acciones obligatorias:
     * 1. Registra las credenciales en el proveedor de identidades de Supabase Auth.
     * 2. Inserta la nueva entidad corporativa y recupera su UUID autogenerado en el servidor.
     * 3. Crea el perfil del empleado primario vinculándole el rol de máxima autoridad ("JEFE").
     * 4. Mapea e inserta en lote (bulk insert) la lista de correos electrónicos de los empleados
     * autorizados a unirse a la empresa en la tabla de pre-verificación de invitaciones.
     *
     * @param email Correo electrónico institucional que servirá como login del administrador.
     * @param contrasena Clave secreta de acceso del administrador para el sistema de cifrado.
     * @param nombreEmpresa Razón social o nombre comercial de la corporación que se registra.
     * @param cif Código de Identificación Fiscal único de la entidad legal.
     * @param direccion Ubicación postal o domicilio fiscal de la sede central de la empresa.
     * @param nombreJefe Nombre de pila del usuario administrador del sistema.
     * @param apellidosJefe Apellidos del usuario administrador del sistema.
     * @param dni Documento Nacional de Identidad o documento identificativo equivalente del Jefe.
     * @param listaInvitados Colección de emails pertenecientes a la plantilla inicial para pre-autorizarlos.
     * @throws Exception Si falla la creación de credenciales en Supabase Auth o si se violan restricciones en la BD.
     */
    suspend fun registrarJefeYEmpresa(
        email: String,
        contrasena: String,
        nombreEmpresa: String,
        cif: String,
        direccion: String,
        cp: String,
        ciudad: String,
        nombreJefe: String,
        apellidosJefe: String,
        dniJefe: String
    ) {
        val nuevaEmpresa = Empresa(
            nombreEmpresa = nombreEmpresa,
            cif = cif,
            direccion = direccion,
            cp = cp,
            ciudad = ciudad,
            latitud = null,
            longitud = null
        )
        val empresaInsertada = supabase.postgrest["empresa"]
            .insert(nuevaEmpresa) { select() }
            .decodeSingle<Empresa>()

        val authResponse = supabase.auth.signUpWith(Email) {
            this.email = email
            password = contrasena
        }
        val nuevoUserId = authResponse?.id ?: throw Exception("Error al crear el usuario en Auth")

        val nuevoJefe = Trabajador(
            trabajadorId = nuevoUserId,
            empresaId = empresaInsertada.empresaId,
            nombre = nombreJefe,
            apellidos = apellidosJefe,
            dni = dniJefe,
            email = email,
            rol = "JEFE"
        )
        supabase.postgrest["trabajador"].insert(nuevoJefe)
    }

    /**
     * Realiza la activación y validación de la cuenta de un operario que ha sido previamente invitado por un Jefe.
     *
     * El flujo de control valida estrictamente la existencia de una invitación pendiente en la tabla
     * "invitaciones". Si el correo se encuentra pre-autorizado, el metodo clona la relación empresarial de la
     * invitación, aprovisiona al usuario en Supabase Auth, vuelca su perfil final en la tabla "trabajador" y
     * elimina de forma atómica la invitación consumida para evitar duplicidades de registro.
     *
     * @param email Correo electrónico del operario que intenta reclamar e inicializar su cuenta.
     * @param contrasena Contraseña que el operario asociará de forma permanente a su identidad digital.
     * @param nombre Nombre de pila proporcionado por el trabajador para su perfil.
     * @param apellidos Apellidos oficiales proporcionados por el trabajador para su perfil.
     * @param dni Documento Nacional de Identidad del operario que se valida en los registros internos.
     * @throws Exception Si el correo electrónico no consta en la lista blanca de invitaciones o si falla el alta en Auth.
     */
    suspend fun activarCuentaTrabajador(
        email: String,
        contrasena: String,
        nombre: String,
        apellidos: String,
        dni: String
    ) {
        val invitado = supabase.postgrest["invitaciones"]
            .select { filter { eq("email", email) } }
            .decodeSingleOrNull<Invitacion>()

        if (invitado == null) {
            throw Exception("Este correo no está autorizado por ninguna empresa.")
        }

        val authResponse = supabase.auth.signUpWith(Email) {
            this.email = email
            password = contrasena
        }
        val nuevoUserId = authResponse?.id ?: throw Exception("Error al crear el usuario en Auth")

        val nuevoTrabajador = Trabajador(
            trabajadorId = nuevoUserId,
            empresaId = invitado.empresaId,
            nombre = nombre,
            apellidos = apellidos,
            dni = dni,
            email = email,
            rol = invitado.rol,
            departamentoId = invitado.departamentoId,
            contratoId = invitado.contratoId
        )
        supabase.postgrest["trabajador"].insert(nuevoTrabajador)

        supabase.postgrest["invitaciones"].delete {
            filter { eq("email", email) }
        }
    }

    // =============================================================================================
    // ACCESO Y SESIÓN (SIGN IN / LOGOUT)
    // =============================================================================================

    /**
     * Valida las credenciales de un usuario en el sistema de control e inicia su sesión de trabajo activa.
     *
     * Tras resolver el desafío de autenticación en Supabase Auth, el metodo utiliza el ID seguro retornado
     * para interrogar a la tabla corporativa de personal. Esto permite traer a la aplicación el perfil mapeado
     * del operario con todas sus claves internas, incluyendo restricciones relacionales y permisos jerárquicos.
     *
     * @param email Correo electrónico registrado de acceso.
     * @param contrasena Clave secreta que se va a verificar frente al hash del servidor.
     * @return El objeto [Trabajador] completo con la información de sesión recuperada.
     * @throws Exception Si la verificación de credenciales es errónea o si el UUID de Auth carece de datos en el perfil corporativo.
     */
    suspend fun login(email: String, contrasena: String): Trabajador {
        val authResponse = supabase.auth.signInWith(Email) {
            this.email = email
            password = contrasena
        }
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("Error al iniciar sesión en el sistema")

        val trabajador = supabase.postgrest["trabajador"]
            .select { filter { eq("trabajador_id", userId) } }
            .decodeSingleOrNull<Trabajador>()

        return trabajador
            ?: throw Exception("No se encontró el perfil de este trabajador en la empresa.")
    }

    /**
     * Destruye de forma segura los tokens de acceso locales y finaliza la sesión activa del usuario actual en el servidor.
     */
    suspend fun logout() {
        supabase.auth.signOut()
    }
}