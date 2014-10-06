package pcep.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pcep.analytics.Label;
import pcep.analytics.LabeledSample;
import pcep.db.Sensor;
import pcep.utils.DateUtils;


public class MeasurementVector implements TimedEvent, LabeledSample {
	private List<Double> values;
	private long start;
	private long end;
	private Label label;
	private Sensor sensor;
	
	public MeasurementVector(Label label) {
		this.values = new ArrayList<Double>();	
		this.label = label;
	}
	
	public MeasurementVector() {
		this(null);
	}
	
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
	public Sensor getSensor() {
		return sensor;
	}
	
	public void setStart(long start) {
		this.start = start;
	}
	
	public void setEnd(long end) {
		this.end = end;
	}
	
	@Override
	public Date getTimestamp() {
		return new Date(end);
	}

	@Override
	public long getTime() {
		return end;
	}
	
	public void addValue(double value) {
		values.add(value);
	}
	
	public String toString() {
		return "Vector of " + values.size() + " elements from " + DateUtils.formattedDate(start) + " to " + DateUtils.formattedDate(end);
	}
	
	public double[] getValues(int limit) {
		limit = Math.min(values.size(), limit);
		double[] result = new double[limit];
		for (int i=0; i<result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
	
	public double[] getValues() {
		return getValues(values.size());
	}


	@Override
	public Label getLabel() {
		return label;
	}

	@Override
	public void setLabel(Label label) {
		this.label = label;
	}

	@Override
	public int size() {
		return values.size();
	}

//	@Override
//	public double[] getEveryDthValue(int d) {
//		for (int i=0; i<values.size(); i+=d) {
//			
//		}
//	}
}
