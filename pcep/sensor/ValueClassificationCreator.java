package pcep.sensor;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import pcep.db.HibernateUtil;
import pcep.db.Sensor;
import pcep.db.ValueClassification;
import pcep.db.ValueClassificationRange;


public class ValueClassificationCreator {
	

	
	public ValueClassificationCreator() {

	}
	
	public void create() {
		createCO2Classification();
		createVOCClassiciation();
		createParticleClassification();
		createElectricityClassification();
		createHeatingClassification();
	}
	
	private List<Sensor> getSensors(String...keywords) {
		Criteria criteria = HibernateUtil.criteria(Sensor.class);
		Criterion[] nameCriteria = new Criterion[keywords.length];
		for (int i=0; i<nameCriteria.length; i++) {
			nameCriteria[i] = Restrictions.ilike("name", "%" + keywords[i] + "%");
		}
		
		criteria.add(Restrictions.or(nameCriteria));

		@SuppressWarnings("unchecked")
		List<Sensor> sensors = (List<Sensor>) criteria.list();
		
		return sensors;
	}
	
	private List<Sensor> getSensors(Integer...sensorIds) {
		Criteria criteria = HibernateUtil.criteria(Sensor.class);
		criteria.add(Restrictions.in("sensorId", sensorIds));

		@SuppressWarnings("unchecked")
		List<Sensor> sensors = (List<Sensor>) criteria.list();
		
		return sensors;
	}
	

	private void createClassification(Integer[] sensorIds, ValueClassificationRange...rangeArray) {
		List<Sensor> sensors = getSensors(sensorIds);	
		ValueClassification classification = new ValueClassification();
		classification.setSensors(sensors);
		
		List<ValueClassificationRange> ranges = new ArrayList<ValueClassificationRange>();
		for (ValueClassificationRange range : rangeArray) {
			range.setClassification(classification);
			ranges.add(range);
		}
		classification.setRanges(ranges);

		HibernateUtil.save(classification);
		for (Sensor sensor : sensors) {
			sensor.setClassification(classification);
			HibernateUtil.update(sensor);
		}
	}
	
	
	private void createCO2Classification() {
		createClassification(new Integer[]{55,59,646,648,652,818},  
				new ValueClassificationRange("S1", 0, 0),
				new ValueClassificationRange("S2", 700, 1), 
				new ValueClassificationRange("S3", 900, 2), 
				new ValueClassificationRange("S4", 1200, 3),
				new ValueClassificationRange("max", 2000, 4)
		);
	}
	
	private void createVOCClassiciation() {
		createClassification(new Integer[]{844,845}, 
				new ValueClassificationRange("GVOC1", 0, 0), 
				new ValueClassificationRange("GVOC2", 10, 1), 
				new ValueClassificationRange("GVOC3", 20, 2),
				new ValueClassificationRange("GVOC4", 30, 3)
		);
	}
	
	private void createParticleClassification() {
		createClassification(new Integer[]{789}, 
				new ValueClassificationRange("PC1", 0, 0), 
				new ValueClassificationRange("PC2", 300, 1), 
				new ValueClassificationRange("PC3", 1050, 2),
				new ValueClassificationRange("PC4", 3000, 3)
		);
	}
	
	
	private void createElectricityClassification() {
		createClassification(new Integer[]{60}, 
				new ValueClassificationRange("EC1", 0, 0), 
				new ValueClassificationRange("EC2", 2, 1), 
				new ValueClassificationRange("EC3", 3, 2),
				new ValueClassificationRange("EC3", 4, 3)
		);
	}
	
	private void createHeatingClassification() {
//		createClassification(new Integer[]{}, 
//				new ValueClassificationRange("HC1", 0), 
//				new ValueClassificationRange("HC2", 10), 
//				new ValueClassificationRange("HC3", 12)
//		);
	}
	
	
	
	public static void main(String[] args) {
		HibernateUtil.open();
		HibernateUtil.begin();
		
		(new ValueClassificationCreator()).create();
		
		HibernateUtil.commit();
		HibernateUtil.close();
	}
}
