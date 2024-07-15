import { NextRequest, NextResponse } from "next/server";
import jwt, { JwtPayload } from "jsonwebtoken";
import client from "@/app/lib/mongodb";

export async function GET(request: NextRequest) {
    try {
        const headers = request.headers
        const token = headers.get('authorization')?.replace('Bearer ', '') as string
        const decoded = jwt.verify(token, process.env.JWT_SECRET_KEY || '') as JwtPayload

        await client.connect()

        const db = client.db('passkeys_demo')
        const users = db.collection<User>('users')
        const user = await users.findOne({ username: decoded.userId }, { limit: 1})

        return NextResponse.json({ message: 'success', profile: user })
    } catch (error) {
        return NextResponse.json({ message: 'Unauthorized' }, { status: 401 })
    } finally {
        await client.close()
    }
}