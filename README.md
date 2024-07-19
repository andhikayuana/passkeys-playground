# passkeys-playground
Demonstrate passwordless authentication using Passkeys (previously webauthn)

## Requirements
### Server
* NodeJS Latest
* NextJS 14
* Vercel (for deployment)
* MongoDB Cloud (Atlas)

### Android
* JDK 17
* Android Studio Koala + SDK
* Gradle 8
* Emulator or Real Device
* `minSdk = 24`


## Docs
### .env
to run the `server`, please setup the following environment variables below or please check [here](./server/.env.example)
| KEY  | Description | Example Value |
| ---- | ----------- | ------------- |
| MONGODB_URI | MongoDB URI connection,  please check it [here](https://www.mongodb.com/docs/drivers/node/current/fundamentals/connection/connect/#connection-uri) | `mongodb+srv://user:password@sample.host:port/?retryWrites=true&w=majority` |
| PASSKEY_RP_NAME | Relying Party Name, please check [here](https://www.w3.org/TR/webauthn-2/#webauthn-relying-party) for Relying Party | `Passkeys Playground` | 
| PASSKEY_RP_ID | Relying Party ID, please check [here](https://www.w3.org/TR/webauthn-2/#webauthn-relying-party) for Relying Party | `passkeys-playground.yuana.id` |
| PASSKEY_EXPECTED_ORIGINS | [Origin](https://www.w3.org/TR/webauthn-2/#dom-collectedclientdata-origin) represents the application or website that a request comes from | `https://passkeys-playground.yuana.id,android:apk-key-hash:<sha256_hash-of-apk-signing-cert>` |
| JWT_SECRET_KEY | JWT Secret Key | `yoursecretkey` |

### How to obtain Android App Origin
```sh
# you can use this command
keytool -list -keystore <path-to-apk-signing-keystore>
```
example here using [debug.keystore](./android/debug.keystore) and you can find the credentials on the [keystore.properties](./android/keystore.properties). after we execute the above command, it will show like this
```sh
keytool -list -keystore ./android/debug.keystore
Enter keystore password:
Keystore type: PKCS12
Keystore provider: SUN

Your keystore contains 1 entry

androiddebugkey, Jun 13, 2023, PrivateKeyEntry,
Certificate fingerprint (SHA-256): 3E:E1:30:E9:09:B0:80:F4:75:15:A3:C9:C5:5F:06:4D:B3:38:EF:B4:33:D9:FA:B1:E4:2C:C1:83:9A:3F:1D:4A
```
then we need to execute python script to get the hash

```python
import binascii
import base64
fingerprint = '3E:E1:30:E9:09:B0:80:F4:75:15:A3:C9:C5:5F:06:4D:B3:38:EF:B4:33:D9:FA:B1:E4:2C:C1:83:9A:3F:1D:4A'
print("android:apk-key-hash:" + base64.urlsafe_b64encode(binascii.a2b_hex(fingerprint.replace(':', ''))).decode('utf8').replace('=', ''))
```
the output will be 
```sh
android:apk-key-hash:PuEw6QmwgPR1FaPJxV8GTbM477Qz2fqx5CzBg5o_HUo
```
you can add to the `PASSKEY_EXPECTED_ORIGINS`

### How to create Digital Asset Links
this is Digital Asset Links or `.well-known/assetlinks.json` file. you can refer to [this](https://developer.android.com/identity/sign-in/credential-manager#add-support-dal), also there is [tool](https://developers.google.com/digital-asset-links/tools/generator) here for generate and test the Digital Asset Links

### API Specs
todo

### Android Demo
| Using PIN       | Using Fingerprint          |
| ------------- | ------------- |
| ![using-pin](./docs/using-pin.gif) | ![using-fingerprint](./docs/using-fingerprint.gif) |

## References
* [passkeys.dev](https://passkeys.dev/)
* [w3c webauthn](https://www.w3.org/TR/webauthn-2/#sctn-intro)
* [Sign in your user with Credential Manager](https://developer.android.com/identity/sign-in/credential-manager)
* [WebAuthn Playground](https://opotonniee.github.io/webauthn-playground/)
* [Auth0 WebAuthn](https://webauthn.me/)
* [passwordlress.id - WebAuthn Playground](https://webauthn.passwordless.id/demos/playground.html)