import "jsr:@supabase/functions-js/edge-runtime.d.ts"
import { createClient } from "npm:@supabase/supabase-js@2"

console.log("Hello from invite-employee edge function!")

Deno.serve(async (req) => {
  try {
    // Para manejar CORS
    if (req.method === 'OPTIONS') {
      return new Response('ok', { headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
      } })
    }

    const { email, empresa_id, departamento_id, contrato_id } = await req.json()

    if (!email || !empresa_id) {
      return new Response(JSON.stringify({ error: "Faltan datos obligatorios (email, empresa_id)" }), { 
        status: 400,
        headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }
      })
    }

    const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''

    // Usar la clave service_role para saltarse RLS y tener permisos de Admin
    const supabaseAdmin = createClient(supabaseUrl, supabaseServiceKey, {
      auth: {
        autoRefreshToken: false,
        persistSession: false
      }
    })

    // 1. Invitar al usuario a través de Supabase Auth (esto dispara el email)
    const { data: inviteData, error: inviteError } = await supabaseAdmin.auth.admin.inviteUserByEmail(email)

    if (inviteError) {
      console.error("Error invitando usuario:", inviteError)
      return new Response(JSON.stringify({ error: inviteError.message }), { 
        status: 400,
        headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }
      })
    }

    // Truco: Como solo queríamos usar el sistema de Supabase para ENVIAR el correo,
    // eliminamos al usuario de Auth inmediatamente. Así el empleado podrá registrarse 
    // desde cero en la app con su propia contraseña sin que le diga que el email ya existe.
    if (inviteData?.user?.id) {
      await supabaseAdmin.auth.admin.deleteUser(inviteData.user.id)
    }

    // 2. Insertar en la tabla 'invitaciones'
    const { error: dbError } = await supabaseAdmin.from('invitaciones').insert({
      email: email,
      empresa_id: empresa_id,
      departamento_id: departamento_id,
      contrato_id: contrato_id,
      rol: 'TRABAJADOR'
    })

    if (dbError) {
      console.error("Error guardando en BD:", dbError)
      return new Response(JSON.stringify({ error: dbError.message }), { 
        status: 500,
        headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }
      })
    }

    return new Response(JSON.stringify({ success: true, message: "Invitación enviada y guardada exitosamente" }), {
      headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' },
    })

  } catch (err) {
    console.error("Internal Server Error:", err)
    return new Response(JSON.stringify({ error: "Internal Server Error" }), { 
      status: 500,
      headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }
    })
  }
})
