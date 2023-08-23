# JAVA code -- Chaincode interaction -- workflow
## 1
The main method begins by setting up configurations using an OrganizationConfig object.  
## 2
A new gRPC connection is established with the peer endpoint using the newGrpcConnection method, which takes into account the necessary TLS credentials.  
## 3
A Gateway.Builder instance is created with the necessary identity and signer information, derived from the X509 certificate and private key. The builder is also configured with specific timeout options for various types of gRPC calls.  
## 4
A Gateway connection is opened using the builder. This connection is wrapped in a try-with-resources block to ensure it gets closed after use.  
## 5
An instance of the App class is created, passing in the Gateway instance. The run method is then invoked on the App instance, which performs the core business logic of the application.  
## 6
In the run method, a unique asset ID is generated, and a DID (Decentralized Identifier) key pair is computed using the certificate. The smart contract's "put" function is invoked using this asset ID, and the computed public key as arguments.  
## 7
The computeDidKeyPair method computes the public key from the X509 certificate and the DID. The computeDID method generates the DID by creating a SHA-256 hash of the public key, encoding it in hex format, and prepending the 'did:hlf:' string.  
## 8
The computePrivateKey method reads the private key from the PRIV_PATH, removes the "BEGIN" and "END" lines, decodes the result using Base64 and generates a PrivateKey object from this. It further converts this private key into a hexadecimal string representation.  