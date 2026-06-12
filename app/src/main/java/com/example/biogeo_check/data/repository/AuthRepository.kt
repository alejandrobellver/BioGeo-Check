package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Empresa
import com.example.biogeo_check.data.model.Invitacion
import com.example.biogeo_check.data.model.Trabajador
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

class AuthRepository(private val supabase: SupabaseClient) {

suspend fun registrarJefeYEmpresa(
    email: String,
    contrasena: String,
    nombreEmpresa: String,
    cif: String,
    direccion: String,
    nombreJefe: String,
    apellidosJefe: String,
    dniJefe: String,
    listaInvitados: List<String>
) {
        val authResponse = supabase.auth.signUpWith(Email) {
            this.email = email
            password = contrasena
        }
        val userId = authResponse?.id ?: throw Exception("Error al crear el usuario en Auth")

        val nuevaEmpresa = Empresa(
            nombreEmpresa = nombreEmpresa,
            cif = cif,
            direccion = direccion
        )
        val empresaInsertada = supabase.postgrest["empresa"].insert(nuevaEmpresa) {
            select()
        }.decodeSingle<Empresa>()

        val nuevoJefe = Trabajador(
            trabajadorId = userId,
            empresaId = empresaInsertada.empresaId,
            nombre = nombreJefe,
            apellidos = apellidosJefe,
            dni = dniJefe,
            email = email,
            rol = "JEFE"
        )
        supabase.postgrest["trabajador"].insert(nuevoJefe)

        val listaTrabajadoresAInsertar = listaInvitados.map { emailEmpleado ->
         Invitacion(
             email = emailEmpleado,
             empresaId =empresaInsertada.empresaId,
             rol = "TRABAJADOR",
         )
    }

    if (listaTrabajadoresAInsertar.isNotEmpty()) {
        supabase.postgrest["invitaciones"].insert(listaTrabajadoresAInsertar)
    }
    }

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
            rol = invitado.rol
        )
        supabase.postgrest["trabajador"].insert(nuevoTrabajador)

        supabase.postgrest["invitaciones"].delete {
            filter { eq("email", email) }
        }
    }
    suspend fun login(email: String, contrasena: String): Trabajador {
        val authResponse = supabase.auth.signInWith(Email) {
            this.email = email
            password = contrasena
        }
        val userId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("Error al iniciar sesión en el sistema")

        val trabajador = supabase.postgrest["trabajador"]
            .select { filter { eq("trabajador_id", userId) } }
            .decodeSingleOrNull<Trabajador>()

        return trabajador ?: throw Exception("No se encontró el perfil de este trabajador en la empresa.")
    }
    suspend fun logout() {
        supabase.auth.signOut()
    }
}

