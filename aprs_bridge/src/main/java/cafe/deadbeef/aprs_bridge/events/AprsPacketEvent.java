package cafe.deadbeef.aprs_bridge.events;

import org.springframework.context.ApplicationEvent;

import net.ab0oo.aprs.parser.APRSPacket;

public class AprsPacketEvent extends ApplicationEvent {
	
	APRSPacket packet;

	public AprsPacketEvent(Object source, APRSPacket packet) {
		super(source);
		this.packet = packet;
	}
	
	public APRSPacket getPacket() {
		return this.packet;
	}

}
