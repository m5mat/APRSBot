package cafe.deadbeef.aprs_bridge.processors;

import org.springframework.context.ApplicationListener;

import cafe.deadbeef.aprs_bridge.events.AprsMessageEvent;

public abstract class AbstractProcessor implements ApplicationListener<AprsMessageEvent> {
	private String keyword;
	public abstract void onApplicationEvent(AprsMessageEvent event);
}
