import client from "@/app/lib/mongodb";
import { verifyAuthenticationResponse, VerifyAuthenticationResponseOpts } from "@simplewebauthn/server";
import { NextRequest, NextResponse } from "next/server";
import jwt from "jsonwebtoken";

export async function POST(request: NextRequest) {

  const { username, response } = await request.json()

  if (!username) {
    return NextResponse.json({ message: 'please input your username correctly' }, { status: 400 })
  }

  if (username.length < 8) {
    return NextResponse.json({ message: 'your username at least 8 chars' }, { status: 400 })
  }

  try {
    await client.connect()

    const db = client.db('passkeys_demo')
    const users = db.collection<User>('users')

    const userExists = await users.countDocuments({ username: username }, { limit: 1 }) === 1

    if (!userExists) {
      return NextResponse.json({ message: 'user not found, please register start first' }, { status: 404 })
    }

    const sessions = db.collection('sessions')
    const sessionExists = await sessions.countDocuments({ username: username, type: 'login' }, { limit: 1 }) === 1
    if (!sessionExists) {
      return NextResponse.json({ message: 'your session not found, please login start first' }, { status: 404 })
    }
    const currentSession = await sessions.findOne({ username: username, type: 'login' })

    const credentials = db.collection('credentials')
    const passkeyExists = await credentials.countDocuments({ username: username }) > 0

    if (!passkeyExists) {
      return NextResponse.json({ message: 'could not find passkey for your account' }, { status: 404 })
    }

    const passkey = await credentials.findOne({ username: username, id: response.id })

    const opts: VerifyAuthenticationResponseOpts = {
      response: response,
      expectedChallenge: currentSession?.challenge,
      expectedOrigin: process.env.PASSKEY_EXPECTED_ORIGINS?.split(',') || [],
      expectedRPID: process.env.PASSKEY_RP_ID || '',
      authenticator: {
        credentialID: passkey?.id,
        credentialPublicKey: new Uint8Array(passkey?.publicKey.buffer),
        counter: passkey?.counter,
        transports: passkey?.transports
      }
    } 
    
    const verification = await verifyAuthenticationResponse(opts)
    const { verified, authenticationInfo } = verification

    if (!verified) {
      return NextResponse.json({ message: 'verification failed' }, { status: 400 })
    }

    await credentials.updateOne({ id: response.id }, { $set: { "counter": authenticationInfo.newCounter } })
    await sessions.deleteOne({ username: username, type: 'login' })

    const token = jwt.sign({ userId: username }, process.env.JWT_SECRET_KEY || 'secret')

    return NextResponse.json({ message: 'verification successfully', token: token })
  } catch (_e) {
    const e = _e as Error
    return NextResponse.json({ message: e.message }, { status: 500 })
  } finally {
    await client.close()
  }
}