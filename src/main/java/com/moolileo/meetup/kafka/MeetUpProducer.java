package com.moolileo.meetup.kafka;
/**
 * File: Producer.java
**/

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

// import org.apache.logging.log4j.Logger;
//import org.json.JSONObject;

import com.moolileo.meetup.streamreader.WebsocketClientEndpoint;

//import org.json.JSONException;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;



/**
 * Kafka Producer for OAM. This producer processes a csv instances file and
 * publishes to Kafka.
 *
**/
public class MeetUpProducer {
//    static Logger logger = LogManager.getLogger(Producer.class.getName());







    /**
     * Constant: Kafka domain to use.
     **/
    private static String websocket_uri = Conf.INSTANCE.getConf("WS_RSVPS");







    /**
     * Constructor for the OAMTopology.
     *
     * @param args only one arg. File path to instances.csv file.
    **/




    public static void main(String[] args) {
        try {

            Conf conf = Conf.INSTANCE;

            MeetUpProducerArgs params = new MeetUpProducerArgs(args);
            CmdLineParser parser = new CmdLineParser(params);
            parser.parseArgument(args);


            // React to option -log
            String op = params.getStream();

            if (!op.equals("RSVPS")  && !op.equals("PHOTOS") && !op.equals("CHECKINS") && !op.equals("COMMENTS")){
                System.exit(1);
            }

            conf.setConf("KAFKA_TOPIC", "MEETUP_"+op);
            websocket_uri = conf.getConf("WS_"+op);

            System.out.println("TOPIC: " + conf.getConf("KAFKA_TOPIC") );

        	
            conf.setConf("KAFKA_ZK_HOST", params.getZkHost());


            conf.setConf("KAFKA_BROKER_LIST", params.getBrokers());

            Properties props = new Properties();
            props.put("zk.connect",conf.getConf("KAFKA_ZK_HOST"));
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            props.put("producer.type", "async");
            props.put("batch.size", "50");
            props.put("compression.codec", "1");
            props.put("compression.topic", conf.getConf("KAFKA_TOPIC"));
            props.put("metadata.broker.list", conf.getConf("KAFKA_BROKER_LIST"));
            ProducerConfig config = new ProducerConfig(props);
            final kafka.javaapi.producer.Producer<String, String> producer =
            		new kafka.javaapi.producer.Producer<String, String>(config);

            // open websocket  
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI( websocket_uri ));

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                	
                	producer.send(new KeyedMessage<String, String>(Conf.INSTANCE.getConf("KAFKA_TOPIC"), message));
                	System.out.println(message);
                }
            });
            // wait for messages from websocket
            while (true) {
                Thread.sleep(0);
            }
        } catch (URISyntaxException ex) {
	        System.err.println("URISyntaxException exception: " + ex.getMessage());
	    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CmdLineException e) {
            System.exit(1);
        }
    }
}


class MeetUpProducerArgs {

    //java MeetUpProducer --stream <streamtype> [--zookeeper_host <zk_host>] [--brokers <broker_list>]

    @Option(name="-s", aliases = { "--stream" }, required = true, usage="Sets the stream type")
    private String stream;

    @Option(name = "-zk", aliases = { "--zookeeper_host" }, usage="Zookeeper host")
    private String zk_host = "localhost:2181";

    @Option(name = "-b", aliases = { "--brokers" },  usage="List of Kafka Brokers")
    private String brokers="localhost:9092";

    @Option(name = "-?", aliases = { "--help" },  usage="Help of MeetUpProducer")


    private boolean errorFree = false;

    /**
     * Class constructor: Note this is a Utility class so it should not be
     * instantiated.
     **/

    protected MeetUpProducerArgs(String... args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);

        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    /**
     * Returns whether the parameters could be parsed without an
     * error.
     *
     * @return true if no error occurred.
     */
    protected boolean isErrorFree() {
        return errorFree;
    }

    /**
     * Returns the stream type.
     *
     * @return The stream type.
     */
    public String getStream() {
        return stream.toUpperCase();
    }

    /**
     * Returns the zookeeper host.
     *
     * @return The source file.
     */
    public String getZkHost() {
        return zk_host;
    }

    /**
     * Returns the Brokers List.
     *
     * @return The source file.
     */
    public String getBrokers() {
        return brokers;
    }


}