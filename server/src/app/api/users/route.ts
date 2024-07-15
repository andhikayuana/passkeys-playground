import client from "@/app/lib/mongodb";

export async function GET() {
  // // const res = await fetch('https://jokes-bapack2-api.yuana.id/v1/text/random', { cache: 'no-store' })
  // let data = await res.json()

  // await client.connect()
  // const users = await User.find({})

  // const db = client.db("sample_mflix")
  // const collection = db.collection("users")
  // const users = await collection.find()

  // return Response.json({ users })


  // return Response.json({ data })

  try {
    // Connect the client to the server	(optional starting in v4.7)
    await client.connect()
    // Send a ping to confirm a successful connection
    // await client.db("admin").command({ ping: 1 });
    // console.log("Pinged your deployment. You successfully connected to MongoDB!");

    const db = client.db("sample_mflix")
    const collection = db.collection<User>("asdf")

    const email = 'jarjit@spam4.me'
    const exists = await collection.find({
      email: email
    }, { limit: 1})

    console.log(exists)

    if (exists) {
      const nganu = await collection.findOne<User>({
        email: email
      })

      // console.log(nganu)
      return Response.json(nganu)
    }

    


    // const test = await collection.insertOne({name: 'jarjit', email: email})
    // const cursor = await collection.find({}).limit(10)

    // const result = await cursor.map(user => {

    //   return { email: user.email, name: user.name } as User
    // }).toArray()

    const result = 'a'


    return Response.json({ msg: 'hello', data: result })

  } catch (e) {
    console.error(e);
  } finally {
    // Ensures that the client will close when you finish/error
    await client.close();

    // return Response.json({ msg: 'final' })
  }

}