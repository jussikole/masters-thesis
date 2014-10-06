package pcep.pepn;

import org.apache.log4j.Logger;

import pcep.db.Sensor;
import pcep.epa.EventProcessingAgent;
import pcep.epn.EventRouter;
import pcep.event.MeasurementVector;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.SafeIterator;
import com.espertech.esper.client.UpdateListener;

public class CollectorEpa implements EventProcessingAgent, UpdateListener {
	private Logger log = Logger.getLogger(CollectorEpa.class);
	private Sensor sensor;
	private String windowLength;
	private String windowDifference;
	private int counter = 0;
	private EPStatement statement;
	private EventRouter router;

	
	public CollectorEpa(Sensor sensor, String windowLength, String windowDifference) {
		this.sensor = sensor;
		this.windowLength = windowLength;
		this.windowDifference = windowDifference;
	}
	
	
	@Override
	public void registerStatements(EPAdministrator admin) {
		String epl =
				//"on pattern[every timer:interval(" + windowDifference + ")] " + 
				"SELECT value('" + sensor.getSensorId() + "').value AS value, " +
				"value('" + sensor.getSensorId() + "').measurementUnit.time AS time " +
				"FROM MeasurementUnit.win:time(" + windowLength + ") " +
				"output snapshot every " + windowDifference;
		this.statement = admin.createEPL(epl);
		statement.addListener(this);
	}

	@Override
	public void registerEventTypes(Configuration config) {
		config.addEventType("MeasurementVector", MeasurementVector.class.getName());
	}


	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		counter++;
		SafeIterator<EventBean> iter = statement.safeIterator();
		MeasurementVector vector = new MeasurementVector();
		
		int i = 0;
		EventBean eb = null;
		while (iter.hasNext()) {
			eb = iter.next();
			if (i == 0) {
				vector.setStart((long) eb.get("time")); 
			}
			vector.addValue((double) eb.get("value")); 
			i++;
		}
		if (eb == null) {
			log.warn("Empty vector detected");
			iter.close();
			return;
		}
		
		vector.setEnd((long) eb.get("time"));
		

//		log.debug(vector);
		vector.setSensor(sensor);
		router.route(vector);
		iter.close();
	}
	
	public int getCounter() {
		return counter;
	}


	@Override
	public void setRouter(EventRouter router) {
		this.router = router;
	}



}
