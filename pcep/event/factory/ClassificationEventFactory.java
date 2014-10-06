package pcep.event.factory;

import pcep.event.ClassificationEvent;
import pcep.event.MeasurementVector;

public class ClassificationEventFactory extends ComplexEventFactory {

	
	@Override
	public ClassificationEvent create(MeasurementVector vector) {
		return new ClassificationEvent(vector.getTimestamp(), vector.getSensor());
	}

	@Override
	public String getComplexEventName() {
		return "ClassificationEvent";
	}

	@Override
	public String getComplexEventClassName() {
		return ClassificationEvent.class.getName();
	}

}
