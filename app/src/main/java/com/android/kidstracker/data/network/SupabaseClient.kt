package com.android.kidstracker.data.network

import com.android.kidstracker.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    // Kita menggunakan BuildConfig yang sebelumnya sudah kita siapkan dari local.properties
    // Jika Anda ingin mengubahnya menjadi string statis/dummy, Anda cukup menggantinya dengan "URL_ANDA"
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }
}
