from classes import Node

print(int(0/2))
current_cc = "0;1"
current_cc = current_cc[0:2] + str(int(current_cc[-1]) + 1)
print(current_cc)

"""
A = Node("A", _private_key_hex="4606e039b9caa6afa8dd40f06014a71294c049021f98b98c2275b5df750e779e")
B = Node("B", _private_key_hex="30aa3a42324342afc9645472eb5a5bb3c6acc6d35e393e6ed2dae80dbe6fe5d5")
C = Node("C", _private_key_hex="a07996627ab8d45597fa8dd94622c3cc3142954598865e12d12f0b69194b2af1")
D = Node("D", _private_key_hex="2203f39a2b85630a388e0292775eda35ccc0b369c64818814d35219263077ab9")
AB = Node("AB", left=A, right=B)
CD = Node("CD", left=C, right=D)
ABCD = Node("ABCD", left=AB, right=CD)
"""

"""

###################### 1

> Task :run
App is running!
Private Key:

Private key: 31674048030682098399161535714006564043743467910783846966381775630034298369950
Private key in hexadecimal: 4606e039b9caa6afa8dd40f06014a71294c049021f98b98c2275b5df750e779e
sun.security.ec.ECPrivateKeyImpl@407e

Public Key:

Sun EC public key, 256 bits
  public x coord: 55187259867154465132841776668880708796873048264383947753560353619911235327845
  public y coord: 96798226995756995759882744894894770583283653153171201254420946298169782732885
  parameters: secp256r1 [NIST P-256, X9.62 prime256v1] (1.2.840.10045.3.1.7)

DID:

did:hlf:8d9a93dbeb87dc9fe28ca913e4644c055c523f6c22e425831051a97e3b4f3f1d

BUILD SUCCESSFUL in 17s

###################### 2

> Task :run
App is running!
Private Key:

Private key: 22011782818946989808695447602888737319611849026315223826884900190691850708437
Private key in hexadecimal: 30aa3a42324342afc9645472eb5a5bb3c6acc6d35e393e6ed2dae80dbe6fe5d5
sun.security.ec.ECPrivateKeyImpl@fffe9e1b

Public Key:

Sun EC public key, 256 bits
  public x coord: 66355409279749532778516003476901446851732520495920436964991066009342516545369
  public y coord: 101293014540099717713246750731916875578326717031224215983987357605568337656721
  parameters: secp256r1 [NIST P-256, X9.62 prime256v1] (1.2.840.10045.3.1.7)

DID:

did:hlf:551c31e798fcaa8dddcaee83ca7338bee5a7806d97b800df3d69a5463242298f

BUILD SUCCESSFUL in 11s

###################### 3

> Task :run
App is running!
Private Key:

Private key: 72584882185111732610288549929125441440218188379874547364813412630299113368305
Private key in hexadecimal: a07996627ab8d45597fa8dd94622c3cc3142954598865e12d12f0b69194b2af1
sun.security.ec.ECPrivateKeyImpl@3022

Public Key:

Sun EC public key, 256 bits
  public x coord: 12602116392180054865642563298499856572198243130225590493242064396510430811447
  public y coord: 39226833994911658506018319891955010022788921932057210327008336817549415645374
  parameters: secp256r1 [NIST P-256, X9.62 prime256v1] (1.2.840.10045.3.1.7)

DID:

did:hlf:ff8fee13567ed9b4c3103bd7dea6728dfa69811723ea5c6ea6ba49618d7c0880

BUILD SUCCESSFUL in 6s

###################### 4

> Task :run
App is running!
Private Key:

Private key: 15385618673802749925183652351070246168844158018662386461756970277801160047289
Private key in hexadecimal: 2203f39a2b85630a388e0292775eda35ccc0b369c64818814d35219263077ab9
sun.security.ec.ECPrivateKeyImpl@ffffe153

Public Key:

Sun EC public key, 256 bits
  public x coord: 66568631130206905314629980330367320829413919558929398176307914183229543394937
  public y coord: 16241860767306187905523229956779487600757320568334126436393520627087441929189
  parameters: secp256r1 [NIST P-256, X9.62 prime256v1] (1.2.840.10045.3.1.7)

DID:

did:hlf:cbef4ad02b50cb24685566c7cdca0cb68a6f3476e374ba899a19c8086ac82773

BUILD SUCCESSFUL in 8s


"""

"""
from ecdsa import SigningKey, NIST256p

# Let's assume you have a private key in hexadecimal form:
hex_private_key = "2203f39a2b85630a388e0292775eda35ccc0b369c64818814d35219263077ab9"

# Convert the hexadecimal private key to bytes:
private_key_bytes = bytes.fromhex(hex_private_key)

# Create a signing key from the private key bytes:
signing_key = SigningKey.from_string(private_key_bytes, curve=NIST256p)

public_key = signing_key.get_verifying_key()
# Print out the public key in hexadecimal form:
print("hex: ", public_key.to_string().hex())

# Extract the x and y coordinates from the public key:
x_coord = public_key.pubkey.point.x()
y_coord = public_key.pubkey.point.y()

# Convert the coordinates to hexadecimal and print them:
x_coord_hex = hex(x_coord)[2:]
y_coord_hex = hex(y_coord)[2:]
print("  public x coord (hex): ", x_coord_hex)
print("  public y coord (hex): ", y_coord_hex)

# Print out the rest of the public key details:
print("Sun EC public key, 256 bits")
print("  public x coord: ", x_coord)
print("  public y coord: ", y_coord)
print("  parameters: secp256r1 [NIST P-256, X9.62 prime256v1] (1.2.840.10045.3.1.7)")
"""