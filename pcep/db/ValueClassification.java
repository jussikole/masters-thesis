package pcep.db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class ValueClassification {
	private int id;
	private List<Sensor> sensors;
	private List<ValueClassificationRange> ranges;
	
	public ValueClassification(List<Sensor> sensors, List<ValueClassificationRange> ranges) {
		this.sensors = sensors;
		this.ranges = ranges;
	}
	
	public ValueClassification() {
		
	}
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@OneToMany(mappedBy="classification", cascade=CascadeType.ALL)
	public List<Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}

	@OneToMany(mappedBy="classification", cascade=CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	public List<ValueClassificationRange> getRanges() {
		return ranges;
	}
	public void setRanges(List<ValueClassificationRange> ranges) {
		this.ranges = ranges;
	}


}
