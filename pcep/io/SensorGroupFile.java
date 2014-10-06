package pcep.io;

import java.io.File;

import org.apache.log4j.Logger;

import pcep.db.House;
import pcep.db.NameUtils;
import pcep.db.Sensor;

public class SensorGroupFile extends AbstractMeasurementDataFile<Sensor> {
	private static Logger log = Logger.getLogger(SensorGroupFile.class);
	private House house;
	
	public SensorGroupFile(File file, House house) throws Exception {
		super(file);
		String name = file.getName();
		int[] ids = NameUtils.splitSensorFileName(name);
		this.house = house;
	}

	@Override
	public void parseHeader(String[] columns) {
		// Nothing to do here, sensor file header always contains id and name
	}
	
	@Override
	public Sensor parseLine(String[] columns) {
		int id = Integer.valueOf(columns[0]);
		Sensor sensor = new Sensor(house, id, columns[1]);
		house.getSensors().put(sensor.getSensorId(), sensor);
		log.debug("Parsed " + sensor);
		return sensor;
	}

}
