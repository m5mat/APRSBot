package cafe.deadbeef.aprs_bridge.processors;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import cafe.deadbeef.aprs_bridge.events.AprsMessageEvent;
import cafe.deadbeef.aprs_bridge.events.AprsPacketEvent;
import cafe.deadbeef.aprs_bridge.events.AprsPositionEvent;
import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.InformationField;
import net.ab0oo.aprs.parser.MessagePacket;
import net.ab0oo.aprs.parser.Position;

@Component
public class LocationCacheProcessor implements ApplicationListener<AprsPositionEvent> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, Position> locationCache = new HashMap<String, Position>();
	
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

	public void onApplicationEvent(AprsPositionEvent event) {
		locationCache.put(event.getPacket().getSourceCall(), event.getPositionPacket().getPosition());
	}
	
	public void setPosition(String callsign, Position position) {
		this.locationCache.put(callsign, position);
	}

	public Position getPosition(String callsign) {
		return locationCache.get(callsign);
	}
	
	

}
