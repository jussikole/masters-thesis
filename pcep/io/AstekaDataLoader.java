package pcep.io;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import pcep.db.HibernateUtil;
import pcep.db.House;
import pcep.db.MeasurementUnit;
import pcep.db.MeasurementValue;
import pcep.db.Sensor;

public class AstekaDataLoader {
	private Logger log = Logger.getLogger(AstekaDataLoader.class);
	
	public static final String RESOURCES_DIR = "src/main/resources";
	private static final String JAR_NAME = "asteka.jar";
	private static final String JKS_NAME = "server.jks";
	

	
	private static final String DEFAULT_START_TIME = "00:00:00";
	private static final String DEFAULT_END_TIME = "23:59:59";
	
	public static final String SENSOR_FILE_PREFIX = "sensors";
	public static final String MEASUREMENT_FILE_PREFIX = "measurements";
	
	private House house;
	private int[] sensorIds;

	
	public static enum Target {
		SENSORS,
		MEASUREMENTS
	}
	
	public AstekaDataLoader(House house, int[] sensorIds) {
		this.house = house;
		this.sensorIds = sensorIds;
	}

	public void parseSensors(String sourceFolder) throws Exception {
		File folder = new File(RESOURCES_DIR, sourceFolder);
		log.debug("Loading directory " + folder);
		
		File[] subFolders = folder.listFiles();
		for (File subFolder : subFolders) {
			if (subFolder.isDirectory()) {
				parseSensors(subFolder);
			}
		}
	}
	

	public void parseMeasurements(String sourceFolder) throws Exception {
		File folder = new File(RESOURCES_DIR, sourceFolder);
		log.debug("Loading directory " + folder);
		
		File[] subFolders = folder.listFiles();
		for (File subFolder : subFolders) {
			if (subFolder.isDirectory()) {
				parseMeasurements(subFolder, sensorIds);
			}
		}
	}
	
	

	

	public void parseSensors(File folder) throws Exception {
		String[] sensorFilenames = FileTools.listFiles(folder, SENSOR_FILE_PREFIX);
		log.debug("Found " + sensorFilenames.length + " sensor files in " + folder);
		
		SensorGroupFile sensorGroupFile;
		for (String sensorFilename : sensorFilenames) {
			log.debug("Parsing file " + sensorFilename);
			long t = System.currentTimeMillis();
			sensorGroupFile = new SensorGroupFile(new File(folder, sensorFilename), house);
			for (Sensor sensor : sensorGroupFile.parse()) {
				house.getSensors().put(sensor.getSensorId(), sensor);
				sensor.setHouse(house);
			}
			log.debug("Finished in " + (System.currentTimeMillis() - t) + " ms");
		}
	}

	private void parseMeasurements(File folder, int[] sensorIds) throws Exception {
		String[] measurementFilenames = FileTools.listFiles(folder, MEASUREMENT_FILE_PREFIX);
		log.debug("Found " + measurementFilenames.length + " measurement files in " + folder);
		

		MeasurementFile measurementFile;
		for (String measurementFilename : measurementFilenames) {
			log.debug("Parsing file " + measurementFilename);
			
			measurementFile = new MeasurementFile(new File(folder, measurementFilename), house, sensorIds);
			List<MeasurementUnit> units = measurementFile.parse();
			
			int counter1 = 0;
			int counter2 = 0;
			long t = System.currentTimeMillis();
			for (MeasurementUnit unit : units) {
				HibernateUtil.insert(unit);
				counter1++;
				for (MeasurementValue value : unit.getMeasurementValues()) {
					HibernateUtil.insert(value);
					counter2++;
				}

			}
			log.debug("Inserted "  + counter1 + " units and " + counter2 + " values in " + (System.currentTimeMillis()-t) + " ms");
		}
		
	}
	
	public void loadData(String targetFolder, Calendar start, Calendar end, int days) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		File folder = new File(RESOURCES_DIR, targetFolder);
		
		Calendar current = (Calendar) start.clone();
		String formattedDate;
		while (current.before(end)) {
			formattedDate = dateFormat.format(current.getTime());
			File workDir = new File(folder, formattedDate);
			File[] files = workDir.listFiles();
			if (files == null || files.length == 0) {
				log.info("Folder" + formattedDate + " is empty, downloading files");
				AstekaExecutor astekaExecutor = new AstekaExecutor(RESOURCES_DIR, JAR_NAME, JKS_NAME);
				astekaExecutor.retrieve(workDir.toString(), formattedDate, DEFAULT_START_TIME, formattedDate, DEFAULT_END_TIME);
			}
			else {
				log.debug("Folder " + workDir + " contains data");
			}
			current.add(Calendar.DATE, days);
		}
	}

}
