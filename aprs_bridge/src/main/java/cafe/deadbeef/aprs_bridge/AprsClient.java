package cafe.deadbeef.aprs_bridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import cafe.deadbeef.aprs_bridge.events.AprsMessageEvent;
import cafe.deadbeef.aprs_bridge.events.AprsPacketEvent;
import cafe.deadbeef.aprs_bridge.events.AprsPositionEvent;
import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.InformationField;
import net.ab0oo.aprs.parser.MessagePacket;
import net.ab0oo.aprs.parser.Parser;
import net.ab0oo.aprs.parser.PositionPacket;
import net.ab0oo.aprs.parser.Utilities;

/**
 * This component connects to APRS-IS and spits out AprsMessageEvent and AprsPositionEvent events.
 * It also listens for AprsPacketEvent events and injects those into APRS-IS. 
 * 
 * @author macarus
 *
 */

@Component
public class AprsClient implements ApplicationListener<AprsPacketEvent> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${aprs-is.host}")
	private String server;
	
	@Value("${aprs-is.port}")
	private int port;
	
	@Value("${aprs-is.login}")
	private String username;
	
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private Socket clientSocket;
    private PrintWriter out;
    private boolean connected = false;
	private Parser aprsParser = new Parser();
	
	//@Async
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("Starting AprsClient...");
		
		while (true) {
			try {
				clientSocket = new Socket(server, port);
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				// Do login
				Thread.sleep(500);
				logger.info("Using login string: " + this.getLoginString());
				out.println(this.getLoginString());
				
				// Construct a dummy packet
				InformationField info = new MessagePacket("2E1HNK", "Testing", "001");
				APRSPacket response = new APRSPacket("2E1HNK", "ECHO", null, info);
				
				logger.info("Sample Packet: " + response.toString());
				
				connected = true;
				
				while (true) {
					String line = in.readLine();
	
					// Skip any comment lines
					if (!line.startsWith("#")) {
						if (line != null) {
							try {
								APRSPacket packet = aprsParser.parse(line);
								// messageHandler.handle(packet);
								switch (packet.getDti()) {
								case '!':
								case '/':
								case '@':
								case '=':
								case '\'':
								case '`':
								case '$':
									// Position
									logger.debug(String.format("(%s >%s> %s) %.3f, %.3f", packet.getSourceCall(), packet.getDti(), packet.getDestinationCall(), ((PositionPacket)packet.getAprsInformation()).getPosition().getLatitude(), ((PositionPacket)packet.getAprsInformation()).getPosition().getLongitude()));
									applicationEventPublisher.publishEvent(new AprsPositionEvent(this, packet));
									break;
								case ':':
									// Message
									logger.debug(String.format("(%s >%s> %s) %s", packet.getSourceCall(), packet.getDti(), ((MessagePacket)packet.getAprsInformation()).getTargetCallsign(), ((MessagePacket)packet.getAprsInformation()).getMessageBody()));
									applicationEventPublisher.publishEvent(new AprsMessageEvent(this, packet));
									break;
								
								default:
									// Unknown
									logger.error(String.format("(%s >%s> %s) UNKNOWN", packet.getSourceCall(), packet.getDti(), packet.getDestinationCall()));
									break;
								}
								
							} catch (Exception e) {
								// logger.error("Oops, exception thrown!", e);
								// e.printStackTrace();
							}
						}
						Thread.sleep(100);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					// This is probably a connection problem, wait 10 secs then try again
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
			}
		}
	}
	
	@Override
	public void onApplicationEvent(AprsPacketEvent event) {
		if ( connected ) {
			logger.info("Sending packet: " + event.getPacket().toString());
			out.println(event.getPacket().toString());
		} else {
			logger.error("Can't send packet while disconnected. Packet will be dropped");
		}
	}
	
	@PreDestroy
	public void disconnect() {
		logger.info("Exiting AprsClient");
	}
	
	private String getLoginString() {
		return String.format("user %s pass %s vers testsoftware 0.0 filter %s", username,
				this.getPasscode(username), 
				//this.getRangeFilter(51.92, -2.1, 50)
				//this.getTypeFilter(new String[] {"Status"})
				this.getTypeFilter(new String[] {"Message", "Position"})
		);
	}
	
	private String getPasscode(String callsign) {
		return String.valueOf(Utilities.doHash(callsign));
	}
	
	/**
	 * Get messaged based on the type of message
	 * @return
	 */
	private String getTypeFilter(String[] types) {
		StringBuilder filter = new StringBuilder();
		filter.append("t/");
		for ( String type : types ) {
			filter.append(type.substring(0, 1).toLowerCase());
		}
		return filter.toString();
	}
}
