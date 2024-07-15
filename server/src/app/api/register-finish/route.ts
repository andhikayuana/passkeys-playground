import client from "@/app/lib/mongodb";
import { verifyRegistrationResponse, VerifyRegistrationResponseOpts } from "@simplewebauthn/server";
import { NextRequest, NextResponse } from "next/server";


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
    const sessionExists = await sessions.countDocuments({ username: username }, { limit: 1 }) === 1
    if (!sessionExists) {
      return NextResponse.json({ message: 'user not found, please register start first' }, { status: 404 })
    }
    const currentSession = await sessions.findOne({ username: username })

    const opts: VerifyRegistrationResponseOpts = {
      response: response,
      expectedChallenge: currentSession?.challenge,
      expectedOrigin: process.env.PASSKEY_EXPECTED_ORIGINS?.split(',') || [],
      expectedRPID: process.env.PASSKEY_RP_ID,
      requireUserVerification: false
    }

    const verification = await verifyRegistrationResponse(opts)
    const { verified, registrationInfo } = verification

    if (!verified) {
      return NextResponse.json({ message: 'Not verified' }, { status: 400 })
    }

    const credentials = db.collection('credentials')
    await credentials.insertOne({
      username: currentSession?.username,
      webAuthnUserID: currentSession?.user.id,
      id: registrationInfo?.credentialID,
      publicKey: registrationInfo?.credentialPublicKey,
      counter: registrationInfo?.counter,
      deviceType: registrationInfo?.credentialDeviceType,
      backedUp: registrationInfo?.credentialBackedUp,
      transports: response.response.transports
    })

    await sessions.deleteOne({ username: username })

    return NextResponse.json({ message: 'registration successfully' })

  } catch (error) {
    return NextResponse.json({ message: error.message }, { status: 500 })
  } finally {
    await client.close()
  }

}