package pcep.epn.source;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;

import pcep.db.MeasurementUnit;
import pcep.epa.EventProcessingAgent;
import pcep.epn.EventProcessingNetwork;
import pcep.epn.EventRouter;

public abstract class EventSource extends Thread implements EventProcessingAgent {
	private Logger log = Logger.getLogger(EventSource.class);
	private BlockingQueue<MeasurementUnit> queue;
	private int counter = 0;
	
	public EventSource() {

	}
	
	public void sendTo(EventProcessingNetwork epn) {
		this.queue = epn.getQueue();
	}

	@Override
	public void run() {
		log.debug("Event source started");
		while (hasMoreData()) {
			try {
				for (MeasurementUnit unit : loadBatch()) {
					counter++;
					queue.put(unit);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.info("Event source sent " + counter + " events");
	}
	
	@Override
	public void registerStatements(EPAdministrator admin) {
		// Nothing to do here
	}

	@Override
	public void registerEventTypes(Configuration config) {
		config.addEventType("MeasurementUnit", MeasurementUnit.class.getName());
	}
	
	@Override
	public void setRouter(EventRouter router) {
		// Nothing to do here
	}
	
	public void reset() {
		this.counter = 0;
	}
	
	protected abstract boolean hasMoreData();
	protected abstract List<MeasurementUnit> loadBatch() throws Exception;
	public abstract String getBatchDescription();
}
