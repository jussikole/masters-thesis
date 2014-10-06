package pcep.epa;

import java.util.Set;

import pcep.epn.EventProcessingNetwork;
import pcep.epn.EventRouter;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;

public interface EventProcessingAgent {
	
	public abstract void setRouter(EventRouter router);
	public abstract void registerStatements(EPAdministrator admin);
	public abstract void registerEventTypes(Configuration config);
}
