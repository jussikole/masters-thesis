package pcep.event;

import java.util.Date;


public abstract class ComplexEvent implements TimedEvent {
	private Date time;
	private boolean warning;
	
	public ComplexEvent(Date time) {
		this.time = time;
		this.warning = false;
	}
	
	public void setToWarning() {
		this.warning = true;
	}
	
	public boolean isWarning() {
		return warning;
	}
	
	@Override
	public Date getTimestamp() {
		return time;
	}

	@Override
	public long getTime() {
		return time.getTime();
	}
	
	public String toString() {
		return "Complex event" + warningText() + "at " + time + ": " + getMessage();
	}
	
	private String warningText() {
		return warning ? " WARNING " : " ";
	}
	
	public abstract String getMessage();

}
