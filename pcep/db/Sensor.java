package pcep.db;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.Query;

import com.espertech.esper.client.annotation.Transient;

@Entity
public class Sensor implements DatabaseItem {
	private int id;
	private int sensorId;
	private String name;
	private House house;
	private ValueClassification classification;
	
	public Sensor() {
		
	}
	
	public Sensor(House house, int sensorId, String name) {
		this.sensorId = sensorId;
		this.name = name;
	}
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(unique=true)
	public int getSensorId() {
		return sensorId;
	}
	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	public House getHouse() {
		return house;
	}
	public void setHouse(House house) {
		this.house = house;
	}
	
	public String toString() {
		return "Sensor #" + sensorId + " (" + name + ")";
	}
	
	public static Sensor load(int id) throws IOException {
		Query query = HibernateUtil.query("from Sensor where sensorId = :id");
		query.setParameter("id", id);
		Object o = query.uniqueResult();

		if (o == null) {
			throw new IOException("No sensor found with id " + id);
		}
		return (Sensor) o;
	}

	@ManyToOne
	public ValueClassification getClassification() {
		return classification;
	}
	public void setClassification(ValueClassification classification) {
		this.classification = classification;
	}
	
	@Transient
	public ValueClassificationRange getRange(int level) {
		for (ValueClassificationRange range : classification.getRanges()) {
			if(range.getLevel() == level) {
				return range;
			}
		}
		return null;
	}
	
	public boolean exists() {
		try {
			Sensor.load(sensorId);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
}
