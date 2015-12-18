package com.moolileo.meetup.streamreader;

import java.io.IOException;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * Websocket Client
 *
 * @author Oscar Ruesga
 */
@ClientEndpoint
public class WebsocketClientEndpoint {

    Session session = null;
    private MessageHandler messageHandler;
    private URI uri;

    public WebsocketClientEndpoint(URI endpointURI) {
        try {
        	uri = endpointURI;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("opening websocket");
        this.session = session;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
    	if (reason.getReasonPhrase().equals("Illegal UTF-8 Sequence")) {
    		System.out.println( reason.getReasonPhrase() );
    		
            try {
            	WebSocketContainer container = ContainerProvider.getWebSocketContainer();
				container.connectToServer(this, uri);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    		return;
    	}
        System.out.println("closing websocket. Reason: " + reason.getReasonPhrase());
        this.session = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
    	System.out.println("client: received message "+message);
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    /**
     * register message handler
     *
     * @param message
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param user
     * @param message
     * @throws IOException 
     */
    public void sendMessage(String message) throws IOException {
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * Message handler.
     *
     * @author Oscar Ruesga
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}
