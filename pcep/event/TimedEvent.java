package pcep.event;

import java.util.Date;

public interface TimedEvent {
	public Date getTimestamp();
	public long getTime();
}
