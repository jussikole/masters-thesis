package pcep.db;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;

import pcep.event.TimedEvent;

@Entity
public class MeasurementUnit implements DatabaseItem, TimedEvent {
	private int id;
	private Date timestamp;
	private Set<MeasurementValue> measurementValues = new HashSet<MeasurementValue>();
	private Map<String, MeasurementValue> valueMap = new HashMap<String, MeasurementValue>();
	private House house;
	
	public MeasurementUnit() {
		
	}
	
	public MeasurementUnit(House house, long unixTimestamp) {
		this.house = house;
		this.timestamp = new Date(1000*unixTimestamp);
	}

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	

	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@OneToMany(mappedBy="measurementUnit", cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Filter(name="sensors")
	public Set<MeasurementValue> getMeasurementValues() {
		return measurementValues;
	}
	public void setMeasurementValues(Set<MeasurementValue> measurementValues) {
		this.measurementValues = measurementValues;
	}
	
	public void addValue(MeasurementValue value) {
		measurementValues.add(value);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (MeasurementValue value : measurementValues) {
			buffer.append(value.getSensorId() + "=" + value.getValue() + " ");
		}
		
		
		return "Unit at " + timestamp + ": " + buffer.toString();
	}

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	public House getHouse() {
		return house;
	}
	public void setHouse(House house) {
		this.house = house;
	}

	@Transient
	@Override
	public long getTime() {
		return timestamp.getTime();
	}
	
	
	@Transient
	public MeasurementValue getValue(String sensorId) {
		if (valueMap.size() == 0) {
			for (MeasurementValue value : measurementValues) {
				valueMap.put(String.valueOf(value.getSensor().getSensorId()), value);
			}
		}
		return valueMap.get(sensorId);
	}
}
