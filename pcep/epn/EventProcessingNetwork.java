package pcep.epn;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import pcep.db.MeasurementUnit;
import pcep.epa.EventProcessingAgent;
import pcep.epn.source.EventSource;
import pcep.event.TimedEvent;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.time.CurrentTimeEvent;

public class EventProcessingNetwork extends Thread {
	private Logger log = Logger.getLogger(EventProcessingNetwork.class);
	
	private EventSource source;
	private EventSink sink;
	private BlockingQueue<MeasurementUnit> queue;
	private Set<EventProcessingAgent> epas;
	private String networkName;
	private EPRuntime runtime;
	private long lastSentTime = 0L;
	private boolean internalClock;
	private EventRouter router;
	
	public EventProcessingNetwork(String networkName, boolean internalClock, EventSource source, EventSink sink) {
		this.networkName = networkName;
		this.source = source;
		this.sink = sink;
		this.queue = new LinkedBlockingQueue<MeasurementUnit>();
		this.epas = new HashSet<EventProcessingAgent>();
		this.internalClock = internalClock;
	}
	
	public EventSink getSink() {
		return sink;
	}
	
	public void configure() {
		Configuration config = new Configuration();
		if (!internalClock) {
			log.info("Using external clock");
			config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
		}
		
		
		for (EventProcessingAgent epa : epas) {
			epa.registerEventTypes(config);
		}
		source.registerEventTypes(config);
		sink.registerEventTypes(config);
		
		EPServiceProvider epService = EPServiceProviderManager.getProvider(networkName, config);
		EPAdministrator admin = epService.getEPAdministrator();
		
		for (EventProcessingAgent epa : epas) {
			epa.registerStatements(admin);
		}
		sink.registerStatements(admin);
		
		this.runtime = epService.getEPRuntime();
		this.router = new EventRouter(runtime);
		
		for (EventProcessingAgent epa : epas) {
			epa.setRouter(router);
		}
		
		log.info("Registered event types:");
		for (EventType eventType : admin.getConfiguration().getEventTypes()) {
			log.info(" - " + eventType.getName() + ": " + eventType.getUnderlyingType().getName());
		}
		
		log.info("Registered EPLs:");
		for (String statementName : admin.getStatementNames()) {
			log.info(" - " + admin.getStatement(statementName).getText());
		}
	}
	
	private void send(TimedEvent event) {
		if (!internalClock && (lastSentTime < 1 || event.getTime() > lastSentTime)) {
			CurrentTimeEvent time = new CurrentTimeEvent(event.getTime());
			runtime.sendEvent(time);
			lastSentTime = event.getTime();
		}
		runtime.sendEvent(event);
	}
	
	public void route(TimedEvent event) {
		
	}
	
	public void addEpa(EventProcessingAgent epa) {
		this.epas.add(epa);
	}
	
	public BlockingQueue<MeasurementUnit> getQueue() {
		return queue;
	}

	@Override
	public void run() {
		while (true) {
			MeasurementUnit unit = queue.poll();
			if (unit != null) {
				send(unit);
			} 
			else {
				try {
					log.debug("Sleeping 1 sec");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
				if (queue.size() == 0) {
					break;
				}
			}
		}
		log.debug("EPN finished");
	}
}
