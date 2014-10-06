package pcep.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ValueClassificationRange {
	private int id;
	private String name;
	private double lowerLimit;
	private ValueClassification classification;
	private int level;
	
	public ValueClassificationRange(String name, double lowerLimit, int level) {
		this.name = name;
		this.lowerLimit = lowerLimit;
		this.level = level;
	}

	public ValueClassificationRange() {
		
	}
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public double getLowerLimit() {
		return lowerLimit;
	}
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	@ManyToOne
	public ValueClassification getClassification() {
		return classification;
	}
	public void setClassification(ValueClassification classification) {
		this.classification = classification;
	}

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
	public String toString() {
		return name + "(lower: " + lowerLimit + ", level: " + level + ")";
	}


}
