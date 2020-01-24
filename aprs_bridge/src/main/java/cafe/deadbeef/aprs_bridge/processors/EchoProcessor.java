package cafe.deadbeef.aprs_bridge.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import cafe.deadbeef.aprs_bridge.events.AprsMessageEvent;
import cafe.deadbeef.aprs_bridge.events.AprsPacketEvent;
import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.InformationField;
import net.ab0oo.aprs.parser.MessagePacket;

@Component
public class EchoProcessor extends AbstractProcessor {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String keyword = "ECHO";	// This is the value in the destination callsign
	
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void onApplicationEvent(AprsMessageEvent event) {
		if ( event.getPacket().getDestinationCall().equals(keyword) && !event.getPacket().getSourceCall().equals(keyword) ) {
			logger.info(String.format("(%s >%s> %s) %s", event.getPacket().getSourceCall(), event.getPacket().getDti(), event.getPacket().getDestinationCall(), ((MessagePacket)event.getPacket().getAprsInformation()).getMessageBody()));
			
			// Construct the response
			
			// Send an ack first
			InformationField ackInfo = new MessagePacket(event.getPacket().getSourceCall(), "ack", event.getMessagePacket().getMessageNumber());
			APRSPacket ackResponse = new APRSPacket(keyword, event.getPacket().getSourceCall(), null, ackInfo);
			applicationEventPublisher.publishEvent(new AprsPacketEvent(this, ackResponse));
			
			// Now send the actual message
			logger.info("Building response packet...");
			InformationField info = new MessagePacket(event.getPacket().getSourceCall(), event.getMessagePacket().getMessageBody(), "1");
			APRSPacket response = new APRSPacket(keyword, event.getPacket().getSourceCall(), null, info);
			applicationEventPublisher.publishEvent(new AprsPacketEvent(this, response));
		}
	}

}
