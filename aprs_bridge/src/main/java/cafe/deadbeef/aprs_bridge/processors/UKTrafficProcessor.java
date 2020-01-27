package cafe.deadbeef.aprs_bridge.processors;

import cafe.deadbeef.aprs_bridge.events.AprsMessageEvent;

public class UKTrafficProcessor extends AbstractProcessor {
	
	private String keyword = "UKTRAF";

	@Override
	public void onApplicationEvent(AprsMessageEvent event) {
		if ( event.getMessagePacket().getTargetCallsign().equals(keyword) ) {
			
		}
	}

}
