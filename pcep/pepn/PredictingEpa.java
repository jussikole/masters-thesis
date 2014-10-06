package pcep.pepn;

import org.apache.log4j.Logger;

import pcep.analytics.Label;
import pcep.analytics.model.Model;
import pcep.epa.EventProcessingAgent;
import pcep.epa.TogglableEpa;
import pcep.epn.EventRouter;
import pcep.event.AlertEvent;
import pcep.event.MeasurementVector;


import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class PredictingEpa implements EventProcessingAgent, UpdateListener, TogglableEpa {
	
	private Logger log = Logger.getLogger(PredictingEpa.class);
	private EventRouter router;
	private Model model;
	private EPStatement statement;
	
	public PredictingEpa(Model model) {
		this.model = model;
	}


	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		MeasurementVector vector = (MeasurementVector) newEvents[0].getUnderlying();
		Label label = model.classify(vector);
		if (label == Label.POSITIVE) {
			router.route(new AlertEvent(vector));
		}
	}


	@Override
	public void setRouter(EventRouter router) {
		this.router = router;
	}

	@Override
	public void registerStatements(EPAdministrator admin) {
		this.statement = admin.createEPL("select * from MeasurementVector");
		statement.addListener(this);
	}

	@Override
	public void registerEventTypes(Configuration config) {
		config.addEventType("AlertEvent", AlertEvent.class.getName());
	}


	@Override
	public void toggle(boolean on) {
		if (on && statement.isStopped()) {
			statement.start();
		}
		else if (!on && statement.isStarted()) {
			statement.stop();
		}
	}

}
