export async function GET(request: Request) {


  return Response.json({ msg: 'hello' })
}

//todo: https://simplewebauthn.dev/docs/packages/server#additional-data-structures
//https://github.com/MasterKale/SimpleWebAuthn/blob/master/example/index.ts
//https://medium.com/@erickwendel/generic-repository-with-typescript-and-node-js-731c10a1b98e