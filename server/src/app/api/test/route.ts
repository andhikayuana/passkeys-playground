import client from "@/app/lib/mongodb";
import { NextRequest, NextResponse } from "next/server";
import jwt from "jsonwebtoken";

export function GET(request: NextRequest) {

  return NextResponse.json({ message: 'just test it here'})

}