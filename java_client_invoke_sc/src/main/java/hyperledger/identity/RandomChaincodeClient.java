package hyperledger.identity;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap.SimpleEntry;

public class RandomChaincodeClient {

    private Contract contract;

    public RandomChaincodeClient(Gateway gateway, String CHANNEL_NAME, String CHAINCODE_NAME) {
        var network = gateway.getNetwork(CHANNEL_NAME);
        this.contract = network.getContract(CHAINCODE_NAME);
    }

    public void run() throws Exception {
        KeyPair pair = generateKeyPair();
        final SimpleEntry<String,String> toPush = computeDidKeyPair(pair);

        System.out.println("Private Key:");
        System.out.println();
        System.out.println(pair.getPrivate().toString());
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

    public SimpleEntry<String,String> computeDidKeyPair(KeyPair pair){
        String publicKeyAsString = pair.getPublic().toString();
        String DID_ = computeDID(pair);
        return new SimpleEntry<String,String>(DID_, publicKeyAsString);
    }

    public String computeDID(KeyPair pair){
        String publicKeyPEM = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
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
        return "random:hlf:"+hashedPublicKeyPEM;
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        SecureRandom random = SecureRandom.getInstanceStrong(); // cryptographically strong random number generator
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        keyGen.initialize(ecSpec, random);
        KeyPair pair = keyGen.generateKeyPair();
        return pair;
    }

}
