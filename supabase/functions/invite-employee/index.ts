import "jsr:@supabase/functions-js/edge-runtime.d.ts"
import { createClient } from "npm:@supabase/supabase-js@2"

Deno.serve(async (req) => {
  try {
    if (req.method === 'OPTIONS') {
      return new Response('ok', { headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
      } })
    }

    const { email, empresa_id, departamento_id, contrato_id, rol } = await req.json()

    if (!email || !empresa_id) {
      return new Response(JSON.stringify({ error: "Faltan datos obligatorios (email, empresa_id)" }), { 
        status: 400,
        headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }
      })
    }

    const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''

    const supabaseAdmin = createClient(supabaseUrl, supabaseServiceKey, {
      auth: {
        autoRefreshToken: false,
        persistSession: false
      }
    })

    // Insertar en la tabla 'invitaciones'
    const { error: dbError } = await supabaseAdmin.from('invitaciones').insert({
      email: email,
      empresa_id: empresa_id,
      departamento_id: departamento_id,
      contrato_id: contrato_id,
      rol: rol || 'TRABAJADOR'
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
