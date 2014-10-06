package pcep.analytics;

import pcep.event.TimedEvent;

public interface LabeledSample extends TimedEvent {
	public Label getLabel();
	public void setLabel(Label label);
	public double[] getValues();
//	public double[] getEveryDthValue(int d);
	public double[] getValues(int limit);
	public int size();
	
}
