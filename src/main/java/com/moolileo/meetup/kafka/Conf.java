package com.moolileo.meetup.kafka;

import java.util.HashMap;

public class Conf {
    
	public final static Conf INSTANCE = new Conf();
    private HashMap <String, String> conf;
    
    public void setConf(String param, String value) {
    	if (!conf.containsKey(param)){
    		throw new IllegalArgumentException("the configuration property is not valid");
    	}
    	if (value == null || value.isEmpty()) {
    		throw new IllegalArgumentException("the value is empty or null");
    	}
    	conf.put(param, value);
    }
    
    public String getConf(String param) {
    	final String value = conf.get(param);
    	if (value == null || value.isEmpty()) {
    		throw new IllegalArgumentException("the configuration property not exists");
    	}
    	return value;
    }
    
    /**
     * Class constructor: Note this is a Utility class so it should not be
     * instantiated.
    **/
    private Conf() {
       conf = new HashMap<String,String>();
       
       //MEETUP WEBSOCKETS ENDPOINT_URIS
      
       conf.put("WS_RSVPS", "ws://stream.meetup.com/2/rsvps");
       conf.put("WS_PHOTOS", "ws://stream.meetup.com/2/photos");
       conf.put("WS_CHECKINS", "ws://stream.meetup.com/2/checkins");
       conf.put("WS_COMMENTS", "ws://stream.meetup.com/2/event_comments");
       
       //KAFKA DEFAULT_VALUES
       conf.put("KAFKA_ZK_HOST", "localhost:2181");
       conf.put("KAFKA_TOPIC", "");
       conf.put("KAFKA_BROKER_LIST", "localhost:9092"); //Broker list comma separated
    }

}
