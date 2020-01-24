package cafe.deadbeef.aprs_bridge.events;

import org.springframework.context.ApplicationEvent;

import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.MessagePacket;

public class AprsMessageEvent extends ApplicationEvent {
	
	APRSPacket packet;

	public AprsMessageEvent(Object source, APRSPacket packet) {
		super(source);
		this.packet = packet;
	}
	
	public APRSPacket getPacket() {
		return this.packet;
	}
	
	public MessagePacket getMessagePacket() {
		return (MessagePacket) this.packet.getAprsInformation();
	}

}
