package pcep.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@FilterDef(name="sensors", parameters=@ParamDef( name="sensorIds", type="string" ), defaultCondition="sensorId in (:sensorIds)" )
public class MeasurementValue implements DatabaseItem {
	private int id;
	private Sensor sensor;
	private MeasurementUnit measurementUnit;
	private double value;
	
	public MeasurementValue() {
		
	}
	public MeasurementValue(Sensor sensor, MeasurementUnit measurementUnit, double value) {
		this.sensor = sensor;
		this.measurementUnit = measurementUnit;
		this.value = value;
	}
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne
	public Sensor getSensor() {
		return sensor;
	}
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	@ManyToOne
	public MeasurementUnit getMeasurementUnit() {
		return measurementUnit;
	}
	public void setMeasurementUnit(MeasurementUnit measurementUnit) {
		this.measurementUnit = measurementUnit;
	}
	
	public String toString() {
		return "Value (#" + id + ") " + value + " from sensor #" + sensor.getSensorId();
	}
	
	@Transient
	public int getSensorId() {
		return sensor.getSensorId();
	}
	
}
