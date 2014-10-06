package pcep.event;

import java.util.Date;

import pcep.db.Sensor;
import pcep.db.ValueClassificationRange;
import pcep.utils.DateUtils;


public class ClassificationEvent extends ComplexEvent {
	private Sensor sensor;
	private ValueClassificationRange range;
	private String length;
	private int level;
	private Date time;

	public ClassificationEvent(Date time, Sensor sensor, Integer level, String length) {
		super(time);
		this.sensor = sensor;
		this.level = level;
		if (level > 0) {
			this.range = sensor.getRange(level);
		}
		this.length = length;
		this.time = time;
	}
	
	public ClassificationEvent(Date time, Sensor sensor) {
		this(time, sensor, -1, null);
	}
	
	public int getLevel() {
		return range.getLevel();
	}

	public Sensor getSensor() {
		return sensor;
	}
	
	public ValueClassificationRange getRange() {
		return range;
	}
	
	public String getLength() {
		return length;
	}
	
	@Override
	public String getMessage() {
		if (range == null) {
			return sensor + " will reach critical range";
		}
		else {
			return sensor + " reached " + range + " for " + length;
		}
	}

}
