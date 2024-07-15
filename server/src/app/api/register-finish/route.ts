import client from "@/app/lib/mongodb";
import { verifyRegistrationResponse, VerifyRegistrationResponseOpts } from "@simplewebauthn/server";
import { Binary } from "mongodb";
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
    const sessionExists = await sessions.countDocuments({ username: username, type: 'register' }, { limit: 1 }) === 1
    if (!sessionExists) {
      return NextResponse.json({ message: 'your session not found, please register start first' }, { status: 404 })
    }
    const currentSession = await sessions.findOne({ username: username, type: 'register' })

    const opts: VerifyRegistrationResponseOpts = {
      response: response,
      expectedChallenge: currentSession?.challenge,
      expectedOrigin: process.env.PASSKEY_EXPECTED_ORIGINS?.split(',') || [],
      expectedRPID: process.env.PASSKEY_RP_ID
    }

    const verification = await verifyRegistrationResponse(opts)
    const { verified, registrationInfo } = verification

    if (!verified) {
      return NextResponse.json({ message: 'verification failed' }, { status: 400 })
    }

    const credentials = db.collection('credentials')
    await credentials.insertOne({
      username: currentSession?.username,
      webAuthnUserID: currentSession?.user.id,
      id: registrationInfo?.credentialID,
      publicKey: new Binary(registrationInfo?.credentialPublicKey),
      counter: registrationInfo?.counter,
      deviceType: registrationInfo?.credentialDeviceType,
      backedUp: registrationInfo?.credentialBackedUp,
      transports: response.response.transports
    })

    await sessions.deleteOne({ username: username })

    const token = jwt.sign({ userId: username }, process.env.JWT_SECRET_KEY || 'secret')

    return NextResponse.json({ message: 'registration successfully', token: token })
  } catch (error) {
    return NextResponse.json({ message: error.message }, { status: 500 })
  } finally {
    await client.close()
  }

}