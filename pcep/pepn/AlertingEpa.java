package pcep.pepn;

import org.apache.log4j.Logger;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import pcep.epa.EventProcessingAgent;
import pcep.epn.EventRouter;
import pcep.event.AlertEvent;
import pcep.event.ComplexEvent;
import pcep.event.MeasurementVector;
import pcep.event.factory.ComplexEventFactory;

public class AlertingEpa implements EventProcessingAgent, UpdateListener {

	private Logger log = Logger.getLogger(AlertingEpa.class);
	private ComplexEventFactory complexEventFactory;
	private EventRouter router;
	
	public AlertingEpa(ComplexEventFactory complexEventFactory) {
		this.complexEventFactory = complexEventFactory;
	}
	
	@Override
	public void setRouter(EventRouter router) {
		this.router = router;
	}

	@Override
	public void registerStatements(EPAdministrator admin) {
		EPStatement statement = admin.createEPL("select * from AlertEvent");
		statement.addListener(this);
	}

	@Override
	public void registerEventTypes(Configuration config) {

	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		MeasurementVector vector = ((AlertEvent) newEvents[0].getUnderlying()).getVector();
		ComplexEvent complexEvent = complexEventFactory.create(vector);
		complexEvent.setToWarning();
		router.route(complexEvent);
	}

}
