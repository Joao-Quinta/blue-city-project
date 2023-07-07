package hyperledger.identity;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;

public class BlockListener {


    public BlockListener(Gateway gateway, String CHANNEL_NAME, String CHAINCODE_NAME) {
        var network = gateway.getNetwork(CHANNEL_NAME);
    }



}