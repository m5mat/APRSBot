package cafe.deadbeef.aprs_bridge.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import cafe.deadbeef.aprs_bridge.events.AprsMessageEvent;
import cafe.deadbeef.aprs_bridge.events.AprsPacketEvent;
import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.InformationField;
import net.ab0oo.aprs.parser.MessagePacket;
import net.ab0oo.aprs.parser.Position;

@Component
public class LocationCacheManualUpdateProcessor extends AbstractProcessor {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private LocationCacheProcessor locationCacheProcessor;
    
	@Autowired private ApplicationEventPublisher applicationEventPublisher;
	
	private String keyword = "POSN";	// This is the callsign used to allow non-GPS stations to update their location
	
	public void onApplicationEvent(AprsMessageEvent event) {
		// Take position updates by message for non-GPS stations
		
		logger.debug("Processing message");
		
		if ( event.getMessagePacket().getTargetCallsign().equals(this.keyword) ) {
			if ( event.getMessagePacket().getMessageBody().equals("?") ) {
				// Tell the user what their position is, according to us
				
				// Send an ack first
				InformationField ackInfo = new MessagePacket(event.getPacket().getSourceCall(), "ack", event.getMessagePacket().getMessageNumber());
				APRSPacket ackResponse = new APRSPacket(keyword, event.getPacket().getSourceCall(), null, ackInfo);
				applicationEventPublisher.publishEvent(new AprsPacketEvent(this, ackResponse));
				
				// Now send the actual message
				InformationField info = new MessagePacket(event.getPacket().getSourceCall(), locationCacheProcessor.getPosition(event.getPacket().getSourceCall()).toDecimalString(), "1");
				APRSPacket response = new APRSPacket(keyword, event.getPacket().getSourceCall(), null, info);
				applicationEventPublisher.publishEvent(new AprsPacketEvent(this, response));
			} else {
			
				// TODO: Make this capable of handling Maidenhead Grids
			
				String[] positionParts = event.getMessagePacket().getMessageBody().split(" ");
				
				locationCacheProcessor.setPosition(event.getPacket().getSourceCall(), new Position(
						Double.parseDouble(positionParts[0]),
						Double.parseDouble(positionParts[1])
				));
				
				logger.debug(String.format("Stored position for %s (%s)", event.getPacket().getSourceCall(), locationCacheProcessor.getPosition(event.getPacket().getSourceCall()).toString()));
			
			}
		}	
	}

}
