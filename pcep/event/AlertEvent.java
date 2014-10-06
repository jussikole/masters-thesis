package pcep.event;

import java.util.Date;

public class AlertEvent implements TimedEvent {
	private MeasurementVector vector;
	
	public AlertEvent(MeasurementVector vector) {
		this.vector = vector;
	}
	
	public MeasurementVector getVector() {
		return vector;
	}
	
	
	@Override
	public Date getTimestamp() {
		return vector.getTimestamp();
	}

	@Override
	public long getTime() {
		return vector.getTime();
	}
}
