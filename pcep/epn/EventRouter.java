package pcep.epn;

import pcep.event.TimedEvent;

import com.espertech.esper.client.EPRuntime;

public class EventRouter {
	private EPRuntime runtime;
	
	public EventRouter(EPRuntime runtime) {
		this.runtime = runtime;
	}
	
	public void route(TimedEvent event) {
		runtime.route(event);
	}
}
