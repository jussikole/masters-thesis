package pcep.analytics.model;

import java.util.Iterator;

public abstract class ModelParam implements Iterator<Double>  {
	@Override
	public void remove() {
		// NOT Implemented
	}
	
	public abstract String getName();
	public abstract void reset();
	
	public abstract double[] getValues();
}
