package pcep.epa;

import org.apache.log4j.Logger;

import pcep.db.Sensor;
import pcep.db.ValueClassificationRange;
import pcep.epn.EventRouter;
import pcep.event.ClassificationEvent;
import pcep.sensor.SensorConfiguration;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;

public class ClassificationEpa implements EventProcessingAgent {
	private Logger log = Logger.getLogger(ClassificationEpa.class);
	private Sensor sensor;
	private String exceedTime;
	private int minLevel;
	
	public ClassificationEpa(SensorConfiguration conf) {
		this.sensor = conf.getSensor();
		this.exceedTime = conf.getExceedTime();
		this.minLevel = conf.getMinLevel();
	}
	
	public void registerStatements(EPAdministrator admin) {
		for (ValueClassificationRange range : sensor.getClassification().getRanges()) {
			if (range.getLevel() < minLevel) {
				continue;
			}
			String epl = String.format(
					"INSERT INTO " +
						"ClassificationEvent " +
					"SELECT " +
						"timestamp, " +
						"value('%d').sensor, " +
						"%d, " +
						"'%s' " +
					"FROM MeasurementUnit.win:time(%s) " +
					"HAVING min(value('%d').value) > %f",
				
			sensor.getSensorId(),
			range.getLevel(),
			exceedTime,
			exceedTime,
			sensor.getSensorId(),
			range.getLowerLimit()	
			);
			admin.createEPL(epl);
		}
	}

	@Override
	public void registerEventTypes(Configuration config) {
		config.addEventType("ClassificationEvent", ClassificationEvent.class);
	}

	@Override
	public void setRouter(EventRouter router) {
		// TODO Auto-generated method stub
		
	}
}
