package hyperledger.identity;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;


public class NetworkClient {

    private ManagedChannel channel;
    private Identity identity;
    private Signer signer;
    private Path KEY_DIR_PATH;

    public NetworkClient(Path CERT_PATH, Path TLS_CERT_PATH, String MSP_ID, String PEER_ENDPOINT, String OVERRIDE_AUTH, Path KEY_DIR_PATH) throws IOException, CertificateException, InvalidKeyException {
    	this.KEY_DIR_PATH = KEY_DIR_PATH;
        this.signer = newSigner(KEY_DIR_PATH);
        this.identity = newIdentity(CERT_PATH, MSP_ID);
        this.channel = newGrpcConnection(TLS_CERT_PATH, PEER_ENDPOINT, OVERRIDE_AUTH);
        
    }

    private ManagedChannel newGrpcConnection(Path TLS_CERT_PATH, String PEER_ENDPOINT, String OVERRIDE_AUTH) throws IOException {
        var credentials = TlsChannelCredentials.newBuilder()
                .trustManager(TLS_CERT_PATH.toFile())
                .build();
        return Grpc.newChannelBuilder(PEER_ENDPOINT, credentials)
                .overrideAuthority(OVERRIDE_AUTH)
                .build();
    }

    private Identity newIdentity(Path CERT_PATH, String MSP_ID) throws IOException, CertificateException {
        var certReader = Files.newBufferedReader(CERT_PATH);
        java.security.cert.X509Certificate certificate = Identities.readX509Certificate(certReader);
        return new X509Identity(MSP_ID, certificate);
    }

    private Signer newSigner(Path KEY_DIR_PATH) throws IOException, InvalidKeyException {
    	var privateKeyPath = getPrivateKeyPath();
        var keyReader = Files.newBufferedReader(privateKeyPath);
        java.security.PrivateKey privateKey = Identities.readPrivateKey(keyReader);
        return Signers.newPrivateKeySigner(privateKey);
    }

    private Path getPrivateKeyPath() throws IOException {
        try (var keyFiles = Files.list(KEY_DIR_PATH)) {
            return keyFiles.findFirst().orElseThrow();
        }
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public Identity getIdentity() {
        return identity;
    }

    public Signer getSigner() {
        return signer;
    }

}

