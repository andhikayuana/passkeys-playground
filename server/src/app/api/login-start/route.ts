import client from '@/app/lib/mongodb'
import { generateAuthenticationOptions, GenerateAuthenticationOptionsOpts } from '@simplewebauthn/server'
import { NextRequest, NextResponse } from 'next/server'

export async function POST(request: NextRequest) {

  const { username }: { username: string } = await request.json()

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

    const credentials = db.collection('credentials')
    const passkeys = await credentials.find({ username: username })
      .map(passkey => ({ id: passkey.id, transports: passkey.transports }))
      .toArray()

    const opts: GenerateAuthenticationOptionsOpts = {
      rpId: process.env.PASSKEY_RP_ID,
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
  } catch (error) {    
    return NextResponse.json({ message: error.message }, { status: 500 })
  } finally {
    await client.close()
  }

  return NextResponse.json({ msg: 'hello' })
}