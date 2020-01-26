package cafe.deadbeef.aprs_bridge.events;

import org.springframework.context.ApplicationEvent;

import net.ab0oo.aprs.parser.APRSPacket;
import net.ab0oo.aprs.parser.PositionPacket;

public class AprsPositionEvent extends ApplicationEvent {
		
		APRSPacket packet;

		public AprsPositionEvent(Object source, APRSPacket packet) {
			super(source);
			this.packet = packet;
		}
		
		public APRSPacket getPacket() {
			return this.packet;
		}
		
		public PositionPacket getPositionPacket() {
			return (PositionPacket) this.packet.getAprsInformation();
		}

	}
