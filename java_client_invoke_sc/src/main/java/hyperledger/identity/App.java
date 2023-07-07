package hyperledger.identity;

import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.X509Identity;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.security.cert.X509Certificate;s

public final class App {

    private static Path CERT_PATH;
    private static Path TLS_CERT_PATH;
    private static String PEER_ENDPOINT;
    private static String OVERRIDE_AUTH;
    private static Path PRIV_PATH;
    private static String CHANNEL_NAME;
    private static String CHAINCODE_NAME;
    private static String MSP_ID;
    private static Path KEY_DIR_PATH;

    public static void main(String[] args) throws Exception {
        OrganizationConfig orgConfig = new OrganizationConfig("1", "mychannel", "register");
        CERT_PATH = orgConfig.getCertPath();
        TLS_CERT_PATH = orgConfig.getTlsCertPath();
        PEER_ENDPOINT = orgConfig.getPeerEndpoint();
        MSP_ID = orgConfig.getMspId();
        KEY_DIR_PATH = orgConfig.getKeyDirPath();

        CHANNEL_NAME = orgConfig.getChannelName();
        CHAINCODE_NAME = orgConfig.getChaincodeName();
        PRIV_PATH = orgConfig.getPrivPath();
        OVERRIDE_AUTH = orgConfig.getOverrideAuth();
        
        System.out.println("App is running!");
        System.out.println(KEY_DIR_PATH);

        NetworkClient networkClient = new NetworkClient(CERT_PATH, TLS_CERT_PATH, MSP_ID, PEER_ENDPOINT, OVERRIDE_AUTH, KEY_DIR_PATH);

        var channel = networkClient.getChannel();
        Identity identity = networkClient.getIdentity();
        Signer signer = networkClient.getSigner();
        
        var builder = Gateway.newInstance().identity(identity).signer(signer).connection(channel)
            .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
            .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
            .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
            .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));
        
        try (var gateway = builder.connect()) {
            if (CHAINCODE_NAME == "register"){
                RegisterChaincodeClient chaincodeClient = new RegisterChaincodeClient(gateway, CHANNEL_NAME, CHAINCODE_NAME);
                if (identity instanceof X509Identity) {
                    X509Identity x509Identity = (X509Identity) identity;
                    X509Certificate certificate = x509Identity.getCertificate();
                    chaincodeClient.run(certificate, PRIV_PATH);
                }
            }else if (CHAINCODE_NAME == "random"){
                RandomChaincodeClient chaincodeClient = new RandomChaincodeClient(gateway, CHANNEL_NAME, CHAINCODE_NAME);
                chaincodeClient.run();
            }
            
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}