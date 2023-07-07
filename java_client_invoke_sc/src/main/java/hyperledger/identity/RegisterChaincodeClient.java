package hyperledger.identity;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap.SimpleEntry;
import java.security.cert.X509Certificate;

public class RegisterChaincodeClient {

    private Contract contract;

    public RegisterChaincodeClient(Gateway gateway, String CHANNEL_NAME, String CHAINCODE_NAME) {
        var network = gateway.getNetwork(CHANNEL_NAME);
        this.contract = network.getContract(CHAINCODE_NAME);
    }

    public void run(X509Certificate certificate, Path PRIV_PATH) throws Exception {
        final SimpleEntry<String,String> toPush = computeDidKeyPair(certificate);
        System.out.println("Private Key:");
        System.out.println();
        System.out.println(computePrivateKey(PRIV_PATH).toString());
        System.out.println();
        System.out.println("Public Key:");
        System.out.println();
        System.out.println(toPush.getValue());
        System.out.println();
        System.out.println("DID:");
        System.out.println();
        System.out.println(toPush.getKey());
        
        contract.submitTransaction("put", toPush.getKey(), toPush.getValue());
    }

    public SimpleEntry<String,String> computeDidKeyPair(java.security.cert.X509Certificate cer){
        String publicKeyAsString = cer.getPublicKey().toString();
        String DID_ = computeDID(cer);
        return new SimpleEntry<String,String>(DID_, publicKeyAsString);
    }

    public String computeDID(java.security.cert.X509Certificate cer){
        String publicKeyPEM = Base64.getEncoder().encodeToString(cer.getPublicKey().getEncoded());
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(publicKeyPEM.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String hashedPublicKeyPEM = hexString.toString();
        return "did:hlf:"+hashedPublicKeyPEM;
    }

    public PrivateKey computePrivateKey(Path PRIV_PATH) throws Exception {
        String privKeyPEM = new String(Files.readAllBytes(PRIV_PATH));
        String privKeyEncoded = privKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
        byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privKeyEncoded);
        KeyFactory kf = KeyFactory.getInstance("EC");
        PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(pkcs8EncodedBytes));

        BigInteger keyValueInt = null;
        if (privKey instanceof ECPrivateKey) {
            ECPrivateKey ecPrivateKey = (ECPrivateKey) privKey;
            keyValueInt = ecPrivateKey.getS();
            System.out.println("Private key: " + ecPrivateKey.getS());
        }
        String privateKeyHex = keyValueInt.toString(16);
        System.out.println("Private key in hexadecimal: " + privateKeyHex);

        return privKey;
    }

}