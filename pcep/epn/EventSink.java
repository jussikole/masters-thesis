package pcep.epn;

import org.apache.log4j.Logger;

import pcep.epa.EventProcessingAgent;
import pcep.event.ComplexEvent;
import pcep.event.factory.ComplexEventFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class EventSink implements EventProcessingAgent, UpdateListener {

	private Logger log = Logger.getLogger(EventSink.class);
	private ComplexEventFactory complexEventFactory;
	private int counter = 0;
	
	public EventSink(ComplexEventFactory complexEventFactory) {
		this.complexEventFactory = complexEventFactory;
	}
	
	public ComplexEventFactory getComplexEventFactory() {
		return complexEventFactory;
	}
	
	
	@Override
	public void registerStatements(EPAdministrator admin) {
		EPStatement stm = admin.createEPL("SELECT * FROM " + complexEventFactory.getComplexEventName());
		stm.addListener(this);
	}

	@Override
	public void registerEventTypes(Configuration config) {
		if (!config.isEventTypeExists(complexEventFactory.getComplexEventName())) {
			config.addEventType(complexEventFactory.getComplexEventName(), complexEventFactory.getComplexEventClassName());
		}
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		ComplexEvent event = (ComplexEvent) newEvents[0].getUnderlying();
		if (event.isWarning()) {
			log.info(event);
		}
		counter++;
	}
	
	public int getCounter() {
		return counter;
	}


	@Override
	public void setRouter(EventRouter router) {

	}

}
