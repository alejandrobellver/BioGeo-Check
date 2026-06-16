package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Empresa
import com.example.biogeo_check.data.model.Invitacion
import com.example.biogeo_check.data.model.Trabajador
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.gotrue.OtpType

/**
 * Repositorio encargado de gestionar la seguridad, el control de acceso y el aprovisionamiento
 * inicial de entidades relacionales (Empresas, Jefes y Trabajadores Invitados) en Supabase.
 */
class AuthRepository(private val supabase: SupabaseClient) {

    // =============================================================================================
    // PROCESOS DE ALTA Y REGISTRO (SIGN UP)
    // =============================================================================================

    /**
     * Ejecuta el alta transaccional de una nueva empresa en el ecosistema de la app junto a su Jefe administrador.
     */
    suspend fun registrarJefeYEmpresa(
        email: String,
        contrasena: String,
        nombreEmpresa: String,
        cif: String,
        direccion: String,
        cp: Int,
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
     */
    suspend fun login(email: String, contrasena: String): Trabajador {
        supabase.auth.signInWith(Email) {
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

/// =============================================================================================
    // RECUPERACIÓN NATIVA OTP Y SEGURIDAD
    // =============================================================================================

    /**
     * Envía un correo de recuperación al usuario usando el metodo nativo resetPasswordForEmail.
     */
    suspend fun enviarCodigoRecuperacion(email: String) {
        supabase.auth.resetPasswordForEmail(
            email = email.trim()
        )
    }

    /**
     * Verifica el código OTP para re-autenticar al usuario temporalmente (Firma Real v2.5.0).
     */
    suspend fun verificarCodigoOTP(email: String, codigo: String) {
        supabase.auth.verifyEmailOtp(
            type = OtpType.Email.EMAIL,
            email = email.trim(),
            token = codigo.trim()
        )
    }

    /**
     * Actualiza la contraseña directamente (se usa cuando ya hay una sesión de recuperación activa).
     */
    suspend fun actualizarContrasenaOlvidada(nuevaContrasena: String) {
        supabase.auth.updateUser {
            password = nuevaContrasena
        }
    }

    /**
     * Actualiza la contraseña del usuario actualmente autenticado en Supabase.
     */
    suspend fun cambiarContrasena(nuevaContrasena: String) {
        supabase.auth.updateUser {
            password = nuevaContrasena
        }
    }
    /**
     * Destruye de forma segura los tokens de acceso locales y finaliza la sesión activa del usuario actual en el servidor.
     */
    suspend fun logout() {
        supabase.auth.signOut()
    }
}