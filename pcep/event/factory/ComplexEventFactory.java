package pcep.event.factory;

import pcep.event.ComplexEvent;
import pcep.event.MeasurementVector;

public abstract class ComplexEventFactory {
	public abstract ComplexEvent create(MeasurementVector vector);
	public abstract String getComplexEventName();
	public abstract String getComplexEventClassName();
}
