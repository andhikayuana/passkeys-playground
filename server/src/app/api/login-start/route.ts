import {
  generateAuthenticationOptions,
  verifyAuthenticationResponse,
} from '@simplewebauthn/server'

export async function GET(request: Request) {
    
   
    return Response.json({ msg: 'hello' })
  }