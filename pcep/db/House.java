package pcep.db;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.hibernate.Query;

import com.espertech.esper.client.annotation.Transient;

@Entity
public class House implements DatabaseItem {
	private int id;
	private int houseId;
	private String name;
	private Map<Integer, Sensor> sensors;
	private Set<MeasurementUnit> units;
	
	public House() {
		
	}
	
	public House(int houseId, String name) {
		this.setHouseId(houseId);
		this.setName(name);
		this.sensors = new HashMap<Integer, Sensor>();
		this.units = new HashSet<MeasurementUnit>();
		
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
	public int getHouseId() {
		return houseId;
	}
	public void setHouseId(int houseId) {
		this.houseId = houseId;
	}

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy="house", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@MapKey(name="sensorId")
	public Map<Integer, Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(Map<Integer, Sensor> sensors) {
		this.sensors = sensors;
	}
	
	@Transient
	public Sensor getSensor(int id) {
		return sensors.get(id);
	}

	@OneToMany(mappedBy="house", cascade=CascadeType.ALL)
	public Set<MeasurementUnit> getUnits() {
		return units;
	}
	public void setUnits(Set<MeasurementUnit> units) {
		this.units = units;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("House #" + houseId + " name: " + name + "\n");
		buffer.append("Sensors (" + sensors.size() + "):\n");
		for (int sensorId : sensors.keySet()) {
			buffer.append(" - " + sensors.get(sensorId) + "\n");
		}
		buffer.append("Units: " + units.size() + "\n");
		
		int totalValues = 0;
		for (MeasurementUnit unit : units) {
			totalValues += unit.getMeasurementValues().size();
		}
		
		buffer.append("Values: " + totalValues);
		
		return buffer.toString();
	}

	public static House load(int id) throws IOException {
		Query query = HibernateUtil.query("from House where houseId = :id");
		query.setParameter("id", id);
		Object o = query.uniqueResult();
		if (o == null) {
			throw new IOException("House doesn't exist!");
		}
		return (House) o;
	}

	public boolean exists() {
		try {
			House.load(houseId);
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}
}
