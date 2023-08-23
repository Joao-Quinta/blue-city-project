package hyperledger.identity;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OrganizationConfig {
    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final Path CRYPTO_BASE_PATH = Paths.get(BASE_PATH);

    private final String orgName;
    private final String peerName;
    private final String userName;
    private final String mspId;
    private final String channelName;
    private final String chaincodeName;
    private final String peerEndpoint;
    private final String overrideAuth;
    private final Path cryptoPath;
    private final Path certPath;
    private final Path privPath;
    private final Path keyDirPath;
    private final Path tlsCertPath;

    public OrganizationConfig(String orgNumber, String channelName, String chaincodeName) {
        this.orgName = "org" + orgNumber + ".example.com";
        this.peerName = "peer0." + orgName;
        this.userName = "User1@" + orgName;
        this.mspId = "Org" + orgNumber + "MSP";
        this.channelName = channelName;
        this.chaincodeName = chaincodeName;
        this.peerEndpoint = "localhost:" + (orgNumber.equals("1") ? "7051" : "9051");
        this.overrideAuth = this.peerName;
        this.cryptoPath = CRYPTO_BASE_PATH.resolve(Paths.get("../fabric-samples/test-network/organizations/peerOrganizations/" + orgName));
        this.certPath = this.cryptoPath.resolve(Paths.get("users/" + userName + "/msp/signcerts/" + userName + "-cert.pem"));
        this.privPath = this.cryptoPath.resolve(Paths.get("users/" + userName + "/msp/keystore/priv_sk"));
        this.keyDirPath = this.cryptoPath.resolve(Paths.get("users/" + userName + "/msp/keystore"));
        this.tlsCertPath = this.cryptoPath.resolve(Paths.get("peers/" + peerName + "/tls/ca.crt"));

    }


    public String getOrgName() {
        return orgName;
    }

    public String getPeerName() {
        return peerName;
    }

    public String getUserName() {
        return userName;
    }

    public String getMspId() {
        return mspId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public String getPeerEndpoint() {
        return peerEndpoint;
    }

    public String getOverrideAuth() {
        return overrideAuth;
    }

    public Path getCryptoPath() {
        return cryptoPath;
    }

    public Path getCertPath() {
        return certPath;
    }

    public Path getPrivPath() {
        return privPath;
    }

    public Path getKeyDirPath() {
        return keyDirPath;
    }

    public Path getTlsCertPath() {
        return tlsCertPath;
    }
    /*
    public String getNetworkConfigPathOrg1() {
        return networkConfigPathOrg1;
    }

    public String getNetworkConfigPathOrg2() {
        return networkConfigPathOrg2;
    }
    */
}

