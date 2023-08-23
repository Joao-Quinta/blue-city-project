package hyperledger.identity;

import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.protos.common.Block;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.client.ChaincodeEvent;
import org.hyperledger.fabric.client.CloseableIterator;
import java.util.concurrent.*;
import java.io.*;
import java.lang.ProcessBuilder;
import java.nio.file.Path;
import java.nio.file.Paths;
public class BlockListener {
    final Network network;
    private final ExecutorService executorService;
    private static final int TIMEOUT = 15;
    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final Path _BASE_PATH = Paths.get(BASE_PATH);
    private final Path pythonPath = _BASE_PATH.resolve(Paths.get("../python_key_computation/calledByListener.py"));
    private final Path logPath_1 = _BASE_PATH.resolve(Paths.get("../python_key_computation/logfile_1.log"));
    private final Path logPath_2 = _BASE_PATH.resolve(Paths.get("../python_key_computation/logfile_2.log"));
    public BlockListener(Network network) {
        this.network = network;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    public void run() throws IOException {
        CloseableIterator<Block> blocks = network.getBlockEvents();
        System.out.println("Listening for block events");
        try {
            while (true) {
                Future<Boolean> future = executorService.submit(blocks::hasNext);

                try {
                    System.out.println("Waiting for block");
                    Boolean result = future.get(TIMEOUT, TimeUnit.SECONDS); // Wait for 15 seconds

                    if (result != null && result) {
                        Block block = blocks.next();

                        String logMessage = String.format(
                            "Received block - header: %s, data: %s",
                            block.getHeader().toString(),
                            block.getData().toString()
                            
                        );
                        //String command = "python3 script.py " + block.getData().toString();
                        String[] command = {"python3", pythonPath.toString(), logPath_1.toString(), block.getData().toString().replace(" ", "")};
                        String[] command_ = {"python3", pythonPath.toString(), logPath_2.toString(), block.getData().toString().replace(" ", "")};
                        ProcessBuilder pb = new ProcessBuilder(command);
                        ProcessBuilder pb_ = new ProcessBuilder(command_);
                        pb.start();
                        pb_.start();
                        System.out.println(logMessage);
                    } else {
                        System.out.println("No event received within the timeout period.");
                        // Break from the loop if no events are received
                        break;
                    }
                } catch (TimeoutException e) {
                    System.err.println("Timeout while waiting for event: " + e.getMessage());
                    // Handle timeout exception
                } catch (ExecutionException | InterruptedException e) {
                    System.err.println("Error while checking for event: " + e.getMessage());
                    // Handle other exceptions
                }
            }
        } finally {
            System.out.println("IN FINALLY");
            executorService.shutdown(); // Always remember to shutdown the executor
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                } 
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
        
    }
}

/*
public class BlockListener {
    final Network network;
    private final ExecutorService executorService;
    private static final int TIMEOUT = 15;

    public BlockListener(Network network) {
        this.network = network;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    public void run() {
        CloseableIterator<ChaincodeEvent> events = network.getChaincodeEvents("registerEvent");
        System.out.println("Listening for events from chaincode 'registerEvent'...");
        try {
            while (true) {
                Future<Boolean> future = executorService.submit(events::hasNext);

                try {
                    System.out.println("Waiting for event");
                    Boolean result = future.get(TIMEOUT, TimeUnit.SECONDS); // Wait for 5 seconds

                    if (result != null && result) {
                        ChaincodeEvent event = events.next();

                        String logMessage = String.format(
                            "Received event from chaincode - name: %s, transaction ID: %s, block number: %s, payload: %s",
                            event.getEventName().toString(),
                            event.getTransactionId().toString(),
                            event.getBlockNumber(),
                            event.getPayload().toString()
                        );

                        System.out.println(logMessage);
                    } else {
                        System.out.println("No event received within the timeout period.");
                        // Break from the loop if no events are received
                        break;
                    }
                } catch (TimeoutException e) {
                    System.err.println("Timeout while waiting for event: " + e.getMessage());
                    // Handle timeout exception
                } catch (ExecutionException | InterruptedException e) {
                    System.err.println("Error while checking for event: " + e.getMessage());
                    // Handle other exceptions
                }
            }
        } finally {
            executorService.shutdown(); // Always remember to shutdown the executor
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                } 
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }
}
*/


/*
public class BlockListener {

    final Network network;

    public BlockListener(Network network) {
        this.network = network;
    }

    public void run() {
    	CloseableIterator<ChaincodeEvent> events = network.getChaincodeEvents("registerEvent");

        try (FileWriter fileWriter = new FileWriter("events.log", true); // Open the file for appending
            PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.println("test ---> "+events.toString());
                System.out.println("test ---> "+events.toString());

                while (events.hasNext()) {
                    ChaincodeEvent event = events.next();
                    printWriter.println("chaincodeEvent - name : " + event.getEventName().toString());
                    printWriter.println("chaincodeEvent - transaction id : " + event.getTransactionId().toString());
                    printWriter.println("chaincodeEvent - block number : " + event.getBlockNumber());
                    printWriter.println("chaincodeEvent - event id : " + event.getPayload().toString());

                    System.out.println("chaincodeEvent - name : " + event.getEventName().toString());
                    System.out.println("chaincodeEvent - transaction id : " + event.getTransactionId().toString());
                    System.out.println("chaincodeEvent - block number : " + event.getBlockNumber());
                    System.out.println("chaincodeEvent - event id : " + event.getPayload().toString());
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/
/*
public class BlockListener implements Runnable {

    final Network network;

    public BlockListener(Network network) {
        this.network = network;

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName(this.getClass() + "chaincode_event_listener");
                return thread;
            }
        });

        executor.execute(this);
    }

    @Override
    public void run() {
    	CloseableIterator<ChaincodeEvent> events = network.getChaincodeEvents("register");

        try (FileWriter fileWriter = new FileWriter("/home/fabric/Desktop/events.log", true); // Open the file for appending
            PrintWriter printWriter = new PrintWriter(fileWriter)) {

            while (events.hasNext()) {
                ChaincodeEvent event = events.next();
                printWriter.println("chaincodeEvent - name : " + event.getEventName().toString());
                printWriter.println("chaincodeEvent - transaction id : " + event.getTransactionId().toString());
                printWriter.println("chaincodeEvent - block number : " + event.getBlockNumber());
                printWriter.println("chaincodeEvent - event id : " + event.getPayload().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/
/*
public class BlockListener {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    private final Gateway gateway;
    private final OrganizationConfig config;

    public BlockListener(Gateway gateway, OrganizationConfig config) {
        this.gateway = gateway;
        this.config = config;

    }

    public void listen() throws InterruptedException {
        // get the network and contract
        Network network = this.gateway.getNetwork(config.getChannelName());
        Contract contract = network.getContract(config.getChaincodeName());
        network.getBlockEvents();

        network.addBlockListener(blockEvent -> {
            System.out.println("#############################################");
            System.out.println("#############################################");
            System.out.println("#############################################");
            System.out.println("Block number: " + blockEvent.getBlockNumber());
            System.out.println("Previous block hash: " + blockEvent.getPreviousBlockHash());
            System.out.println("Data hash: " + blockEvent.getDataHash());

            for (BlockEvent.TransactionEvent transactionEvent : blockEvent.getTransactionEvents()) {
                System.out.println("Transaction ID: " + transactionEvent.getTransactionID());
                System.out.println("Transaction Validation Code: " + transactionEvent.getValidationCode());
            }
        });

        // Keep the main thread running so the JVM doesn't exit
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void setup(OrganizationConfig config) throws Exception {
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(config.getWallet(), config.getUserName()).networkConfig(config.getNetworkConfigPathOrg1()).discovery(true);

        try (Gateway gateway = builder.connect()) {
            BlockListener listener = new BlockListener(gateway, config);
            listener.listen();
        }
    }
    
}
 */
