import client from "@/app/lib/mongodb";
import { generateRegistrationOptions, GenerateRegistrationOptionsOpts } from "@simplewebauthn/server";
import { NextRequest, NextResponse } from "next/server";

type RegisterStartRequest = {
    username: String
}

export async function POST(request: NextRequest) {

    const { username } = await request.json() as RegisterStartRequest

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

        const user = await users.findOneAndUpdate(
            { username: username },
            { $setOnInsert: { username: username } },
            {
                upsert: true
            }
        )
        const credentials = db.collection('credentials')
        const passkeys = await credentials.find({ username: username })
            .map(passkey => ({ id: passkey.id, transports: passkey.transports}))
            .toArray()

        const opts: GenerateRegistrationOptionsOpts = {
            rpName: process.env.PASSKEY_RP_NAME as string,
            rpID: process.env.PASSKEY_RP_ID as string,
            userID: user?._id.id,
            userName: user?.username as string,
            userDisplayName: user?.username as string,
            timeout: 60000,
            attestationType: 'none',
            excludeCredentials: passkeys,
            authenticatorSelection: {
                residentKey: 'required',
                userVerification: 'required'
            }
        }
        const options = await generateRegistrationOptions(opts)

        const sessions = db.collection('sessions')
        await sessions.findOneAndUpdate(
            {
                username: user?.username,
                type: 'register'
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
}