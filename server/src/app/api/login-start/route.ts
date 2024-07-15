import client from '@/app/lib/mongodb'
import { generateAuthenticationOptions, GenerateAuthenticationOptionsOpts } from '@simplewebauthn/server'
import { NextRequest, NextResponse } from 'next/server'

export async function POST(request: NextRequest) {

  const { username }: { username: string } = await request.json()

  const validUsernamePattern = /^[a-zA-Z0-9]{8,}$/;
  if (!validUsernamePattern.test(username as string)) {
    return NextResponse.json({ message: 'please input your username correctly' }, { status: 400 })
  }

  try {
    await client.connect()

    const db = client.db('passkeys_demo')
    const users = db.collection<User>('users')
    const userExists = await users.countDocuments({ username: username }, { limit: 1 }) === 1
    if (!userExists) {
      return NextResponse.json({ message: 'user not found, please register start first' }, { status: 404 })
    }

    const credentials = db.collection('credentials')
    const passkeys = await credentials.find({ username: username })
      .map(passkey => ({ id: passkey.id, transports: passkey.transports }))
      .toArray()

    const opts: GenerateAuthenticationOptionsOpts = {
      rpID: process.env.PASSKEY_RP_ID as string,
      allowCredentials: passkeys,
      userVerification: 'required'
    }

    const options = await generateAuthenticationOptions(opts)

    const sessions = db.collection('sessions')
    await sessions.findOneAndUpdate(
      {
        username: username,
        type: 'login'
      },
      {
        $set: { ...options }
      },
      {
        upsert: true
      }
    )

    return NextResponse.json({ ...options })
  } catch (_e) {
    const e = _e as Error
    return NextResponse.json({ message: e.message }, { status: 500 })
  } finally {
    await client.close()
  }

  return NextResponse.json({ msg: 'hello' })
}