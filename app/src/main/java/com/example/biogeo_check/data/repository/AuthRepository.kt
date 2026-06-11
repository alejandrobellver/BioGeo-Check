package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Empresa
import com.example.biogeo_check.data.model.Invitaciones
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
        // 1. Registrar al usuario en Supabase Auth
        val authResponse = supabase.auth.signUpWith(Email) {
            this.email = email
            password = contrasena
        }
        val userId = authResponse?.id ?: throw Exception("Error al crear el usuario en Auth")

        // 2. Crear y guardar la empresa en la tabla
        val nuevaEmpresa = Empresa(
            nombreEmpresa = nombreEmpresa,
            cif = cif,
            direccion = direccion
        )
        // Usamos select() para que Supabase nos devuelva la empresa con el ID que ha generado
        val empresaInsertada = supabase.postgrest["empresa"].insert(nuevaEmpresa) {
            select()
        }.decodeSingle<Empresa>()
        // Guarda el objeto entero y si no hay nada ponemos null y te dice null.

        // 3. Vincular al jefe en la tabla trabajador
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


        // 4.  OPTIMIZACIÓN: Creamos la lista completa en memoria primero
        val listaTrabajadoresAInsertar = listaInvitados.map { emailEmpleado ->
         Invitaciones(
             email = emailEmpleado,
             empresaId =empresaInsertada.empresaId,
             rol = "TRABAJADOR",
         )
    }

// 🚀 Un único viaje a internet: Supabase recibe la lista entera y la inserta de golpe
    if (listaTrabajadoresAInsertar.isNotEmpty()) {
        supabase.postgrest["invitaciones"].insert(listaTrabajadoresAInsertar)
    }
    }

    suspend fun activarCuentaTrabajador(
        email: String,
        contrasena: String,
        nombre: String,       // 🚀 NUEVO: Viene de la UI
        apellidos: String,    // 🚀 NUEVO
        dni: String           // 🚀 NUEVO
    ) {
        // 1. Buscamos si el correo existe previamente en la tabla de invitaciones
        val invitado = supabase.postgrest["invitaciones"]
            .select { filter { eq("email", email) } }
            .decodeSingleOrNull<Invitaciones>()

        if (invitado == null) {
            throw Exception("Este correo no está autorizado por ninguna empresa.")
        }

        // 2. Creamos su cuenta en Supabase Auth
        val authResponse = supabase.auth.signUpWith(Email) {
            this.email = email
            password = contrasena
        }
        val nuevoUserId = authResponse?.id ?: throw Exception("Error al crear el usuario en Auth")

        //val nuevoUserId = authResponse.user?.id ?: throw Exception("Error al crear la cuenta en Auth")

        // 3. Insertamos en la tabla trabajador con los datos reales de la UI 🌟
        val nuevoTrabajador = Trabajador(
            trabajadorId = nuevoUserId,
            empresaId = invitado.empresaId,
            nombre = nombre,       // 👈 Mapeado con lo que escribió el usuario
            apellidos = apellidos, // 👈 Mapeado
            dni = dni,             // 👈 Mapeado
            email = email,
            rol = invitado.rol
        )
        supabase.postgrest["trabajador"].insert(nuevoTrabajador)

        // 4. Borramos la invitación
        supabase.postgrest["invitaciones"].delete {
            filter { eq("email", email) }
        }
    }
    suspend fun login(email: String, contrasena: String): Trabajador {
        // 1. Iniciar sesión en Supabase Auth
        val authResponse = supabase.auth.signInWith(Email) {
            this.email = email
            password = contrasena
        }
        val userId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("Error al iniciar sesión en el sistema")

        // 2. Traer el perfil del trabajador desde la base de datos usando el ID
        val trabajador = supabase.postgrest["trabajador"]
            .select { filter { eq("trabajador_id", userId) } }
            .decodeSingleOrNull<Trabajador>()

        return trabajador ?: throw Exception("No se encontró el perfil de este trabajador en la empresa.")
    }
    suspend fun logout() {
        supabase.auth.signOut()
    }
}

