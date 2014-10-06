package pcep.pepn;

import pcep.analytics.Label;
import pcep.analytics.model.SampleCollector;
import pcep.analytics.model.trainer.ModelTrainer;
import pcep.epa.EventProcessingAgent;
import pcep.epa.TogglableEpa;
import pcep.epn.EventRouter;
import pcep.event.MeasurementVector;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TrainingEpa implements EventProcessingAgent, UpdateListener, TogglableEpa {

	private SampleCollector sampleCollector;
	private final String PREDICTOR_EVENT_NAME = "MeasurementVector";
	private Label type;
	private String waitingTime;
	private String eventTime;
	private String complexEventName;
	private EPStatement statement;
	
	public TrainingEpa(SampleCollector sampleCollector, Label type, String waitingTime, String eventTime, String complexEventName) {
		this.sampleCollector = sampleCollector;
		this.type = type;
		this.waitingTime = waitingTime;
		this.eventTime = eventTime;
		this.complexEventName = complexEventName;
	}
	
	@Override
	public void registerStatements(EPAdministrator admin) {
		String epl =
				String.format(
				"SELECT v.* " +
				"FROM pattern[" +
					"every v=%s -> (timer:interval(%s) -> (timer:interval(%s) and%s c=%s))" +
				"]",
				PREDICTOR_EVENT_NAME,
				waitingTime,
				eventTime,
				(type == Label.NEGATIVE ? " not" : ""),
				complexEventName);
		//epl = "SELECT * FROM MeasurementUnit";
		this.statement = admin.createEPL(epl);
		statement.addListener(this);
	}

	@Override
	public void registerEventTypes(Configuration config) {
		// Nothing to do here
	}
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		MeasurementVector vector = (MeasurementVector) newEvents[0].getUnderlying();
		vector.setLabel(type);
		sampleCollector.addSample(vector);
	}

	@Override
	public void setRouter(EventRouter router) {
		// TODO Auto-generated method stub
		
	}
	
	public void setSampleCollector(SampleCollector sampleCollector) {
		this.sampleCollector = sampleCollector;
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
