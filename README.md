# passkeys-playground
Demonstrate passwordless authentication using Passkeys (previously webauthn)

## Requirements
### Server
* NodeJS Latest
* NextJS 14
* Vercel (for deployment)
* MongoDB Cloud (Atlas)
* Postman or other prefered REST Client

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

You can find the Digital Asset Links [here](./server/public/.well-known/assetlinks.json)

### Example API

#### [POST] /api/register-start

##### Request
```json
{
    "username": "ismailbinmail"
}
```

##### Response
```json
{
    "challenge": "KiNBQAeRhvHb80XWMTrPpB4aRbXdzkgSTXlHbVQBWJ4",
    "rp": {
        "name": "GDG Jogja Passkeys Playground",
        "id": "passkeys-playground.yuana.id"
    },
    "user": {
        "id": "ZpZDLwWhugOB4oX-",
        "name": "ismailbinmail",
        "displayName": "ismailbinmail"
    },
    "pubKeyCredParams": [
        {
            "alg": -7,
            "type": "public-key"
        },
        {
            "alg": -257,
            "type": "public-key"
        }
    ],
    "timeout": 60000,
    "attestation": "none",
    "excludeCredentials": [
        {
            "id": "9vkkEphCpjLiRDSKoc_6tQ",
            "transports": [
                "internal",
                "hybrid"
            ],
            "type": "public-key"
        }
    ],
    "authenticatorSelection": {
        "residentKey": "required",
        "userVerification": "required",
        "authenticatorAttachment": "platform",
        "requireResidentKey": true
    },
    "extensions": {
        "credProps": true
    }
}
```

#### [POST] /api/register-finish
##### Request
```json
{
    "username": "ismailbinmail",
    "response": {
        "rawId": "MeJ0ehon9mQuZ_alL1mXSQ",
        "authenticatorAttachment": "platform",
        "type": "public-key",
        "id": "MeJ0ehon9mQuZ_alL1mXSQ",
        "response": {
            "clientDataJSON": "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiUks1UTlQU2kwemhlNmt5aGF1UW9nYVpqX2dBZnJzQUMta0xFTHhfblFUYyIsIm9yaWdpbiI6ImFuZHJvaWQ6YXBrLWtleS1oYXNoOlB1RXc2UW13Z1BSMUZhUEp4VjhHVGJNNDc3UXoyZnF4NUN6Qmc1b19IVW8iLCJhbmRyb2lkUGFja2FnZU5hbWUiOiJpZC55dWFuYS5wYXNza2V5cy5wbGF5Z3JvdW5kIn0",
            "attestationObject": "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YViUoGZbvUQa336AvazqMeIpN6zXjYsxLZ59xUF3ueSSzH5dAAAAAOqbjWZNAR0hPOS2tIy1ddQAEDHidHoaJ_ZkLmf2pS9Zl0mlAQIDJiABIVgg42p01q0RPlUAred7x-ovoqhR0Hc9TnfmW7LhhgO1ksIiWCD7ekvbuzY3ZdJf8il7FJdnx_MODQwIZ97qkm2bHt1Onw",
            "transports": [
                "internal",
                "hybrid"
            ],
            "authenticatorData": "oGZbvUQa336AvazqMeIpN6zXjYsxLZ59xUF3ueSSzH5dAAAAAOqbjWZNAR0hPOS2tIy1ddQAEDHidHoaJ_ZkLmf2pS9Zl0mlAQIDJiABIVgg42p01q0RPlUAred7x-ovoqhR0Hc9TnfmW7LhhgO1ksIiWCD7ekvbuzY3ZdJf8il7FJdnx_MODQwIZ97qkm2bHt1Onw",
            "publicKeyAlgorithm": -7,
            "publicKey": "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE42p01q0RPlUAred7x-ovoqhR0Hc9TnfmW7LhhgO1ksL7ekvbuzY3ZdJf8il7FJdnx_MODQwIZ97qkm2bHt1Onw"
        },
        "clientExtensionResults": {
            "credProps": {
                "rk": true
            }
        }
    }
}
```
##### Response
```json
{
    "message": "registration successfully",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJpc21haWxiaW5tYWlsIiwiaWF0IjoxNzIxNDQ1ODI3fQ.7hPIlw_pA2cAsfkeihlKlQILgwqKx-6AuzL_gRfpTGQ"
}
```

#### [POST] /api/login-start
##### Request
```json
{
    "username": "ismailbinmail"
}
```
##### Response
```json
{
    "rpId": "passkeys-playground.yuana.id",
    "challenge": "SffU2U2CJynY8u_hhRBJkl8bGpqbBOXBrFxdvaEqkSI",
    "allowCredentials": [
        {
            "id": "MeJ0ehon9mQuZ_alL1mXSQ",
            "transports": [
                "internal",
                "hybrid"
            ],
            "type": "public-key"
        }
    ],
    "timeout": 60000,
    "userVerification": "required"
}
```

#### [POST] /api/login-finish
##### Request
```json
{
    "username": "ismailbinmail",
    "response": {
        "rawId": "MeJ0ehon9mQuZ_alL1mXSQ",
        "authenticatorAttachment": "platform",
        "type": "public-key",
        "id": "MeJ0ehon9mQuZ_alL1mXSQ",
        "response": {
            "clientDataJSON": "eyJ0eXBlIjoid2ViYXV0aG4uZ2V0IiwiY2hhbGxlbmdlIjoiU2ZmVTJVMkNKeW5ZOHVfaGhSQkprbDhiR3BxYkJPWEJyRnhkdmFFcWtTSSIsIm9yaWdpbiI6ImFuZHJvaWQ6YXBrLWtleS1oYXNoOlB1RXc2UW13Z1BSMUZhUEp4VjhHVGJNNDc3UXoyZnF4NUN6Qmc1b19IVW8iLCJhbmRyb2lkUGFja2FnZU5hbWUiOiJpZC55dWFuYS5wYXNza2V5cy5wbGF5Z3JvdW5kIn0",
            "authenticatorData": "oGZbvUQa336AvazqMeIpN6zXjYsxLZ59xUF3ueSSzH4dAAAAAA",
            "signature": "MEUCICMDs3Qcc5RFLeT9xYThpY1T30KtZczgkywZH6mZd3pFAiEAl5Eq2SeydN4vvBKC55BvfzatKsOhBTLIv7G6XMUNedc",
            "userHandle": "ZpZDLwWhugOB4oX-"
        },
        "clientExtensionResults": {}
    }
}
```
##### Response
```json
{
    "message": "verification successfully",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJpc21haWxiaW5tYWlsIiwiaWF0IjoxNzIxNDQ2MjU0fQ.7banCT6X1zotGaWv0Lmm-hTmyHQXtZUoPMelTdbbmgA"
}
```

#### [GET] /api/profile
##### Headers
`Authorization=Bearer <token_here>`

##### Response
```json
{
    "message": "success",
    "profile": {
        "_id": "6696432f05a1ba0381e285fe",
        "username": "ismailbinmail"
    }
}
```


### Android Demo
| Using PIN       | Using Fingerprint          |
| ------------- | ------------- |
| ![using-pin](./docs/using-pin.gif) | ![using-fingerprint](./docs/using-fingerprint.gif) |

## References
* [passkeys.dev](https://passkeys.dev/)
* [w3c webauthn 8 Apr 2021](https://www.w3.org/TR/webauthn-2/#sctn-intro) and [w3c webauthn 17 Jul 2024](https://w3c.github.io/webauthn/)
* [Sign in your user with Credential Manager](https://developer.android.com/identity/sign-in/credential-manager)
* [WebAuthn Playground](https://opotonniee.github.io/webauthn-playground/)
* [Auth0 WebAuthn](https://webauthn.me/)
* [passwordlress.id - WebAuthn Playground](https://webauthn.passwordless.id/demos/playground.html)