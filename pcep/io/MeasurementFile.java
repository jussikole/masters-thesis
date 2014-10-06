package pcep.io;

import java.io.File;

import org.apache.log4j.Logger;

import pcep.db.House;
import pcep.db.MeasurementUnit;
import pcep.db.MeasurementValue;
import pcep.db.NameUtils;
import pcep.db.Sensor;

public class MeasurementFile extends AbstractMeasurementDataFile<MeasurementUnit> {
	private static Logger log = Logger.getLogger(MeasurementFile.class);
	private Sensor[] sensorColumns;
	private House house;
	private int[] sensorIds;
	
	public MeasurementFile(File file, House house, int[] sensorIds) throws Exception {
		super(file);
		int[] ids = NameUtils.splitMeasurementFileName(file.getName());
		if (ids[0] != house.getHouseId()) {
			throw new DataLoadException("File " + file + " is from house " + ids[0] + " while it should be from " + house.getHouseId());
		}
		this.house = house;
		this.sensorIds = sensorIds;
	}

	@Override
	public void parseHeader(String[] columns) throws Exception {
		sensorColumns = new Sensor[columns.length];
		int[] ids;
		// column 0 is timestamp
		for (int i=1; i<columns.length; i++) {
			ids = NameUtils.splitMeasurementHeaderPattern(columns[i]);
			boolean relevant = false;
			for (int j=0; j<sensorIds.length; j++) {
				if (sensorIds[j] == ids[2]) {
					relevant = true;
					break;
				}
			}
			
			if (!relevant) {
				continue;
			}

			sensorColumns[i] = house.getSensor(ids[2]);
			sensorColumns[i].setHouse(house);
		}
	}
	
	@Override
	public MeasurementUnit parseLine(String[] columns) {
		MeasurementUnit unit = new MeasurementUnit(house, Long.parseLong(columns[0]));
		MeasurementValue value;
		boolean hasValues = false;
		for (int i=1; i<columns.length; i++) {
			if (sensorColumns[i] != null) {
				value = new MeasurementValue(sensorColumns[i], unit, Double.parseDouble(columns[i]));
				unit.addValue(value);
				hasValues = true;
			}
		}
		
		if (hasValues) {
			unit.setHouse(house);
			return unit;	
		}
		else {
			return null;
		}
	}
}
