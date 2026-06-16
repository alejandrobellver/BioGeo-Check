package com.example.biogeo_check.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://cgxkdbyoufrcccpyqfya.supabase.co",
        supabaseKey = "sb_publishable_lIjmC-nVqlmGwxrd6c43PQ_4zDwW5PY"
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
        install(Functions)

        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
        })
    }
}