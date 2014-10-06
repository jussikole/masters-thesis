package pcep.epn.source;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import pcep.db.House;
import pcep.db.MeasurementUnit;
import pcep.io.FileTools;
import pcep.io.MeasurementFile;
import pcep.io.OutputWriter;
import pcep.sensor.SensorConfiguration;

public class FileEventSource extends EventSource {
	private Logger log = Logger.getLogger(FileEventSource.class);
	

	
	public static final File getOutputDir(String session) {
		return new File(new File(OutputWriter.RESOURCES_DIR, session), OutputWriter.OUTPUT_DIR);
	}
	
	public static final String SENSOR_FILE_PREFIX = "sensors";
	public static final String MEASUREMENT_FILE_PREFIX = "measurements";
	
	private File[] subFolders;
	
	private House house;
	private int[] sensorIds;
	private int startIndex;
	private int endIndex;
	private int currentIndex;
	private String session;
	
	public FileEventSource(House house, String session, SensorConfiguration[] sensors) {
		super();
		this.currentIndex = 0;
		this.house = house;
		this.sensorIds = new int[sensors.length];
		this.session = session;
		for (int i=0; i<sensors.length; i++) {
			sensorIds[i] = sensors[i].getSensor().getSensorId();
		}
		File dataFolder = new File(new File(OutputWriter.RESOURCES_DIR, session), OutputWriter.DATA_DIR);
		
		this.subFolders = dataFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}
		});
		this.endIndex = subFolders.length-1;
	}
	
	public void setRange(int startIndex, int endIndex) {
		this.startIndex = Math.min(startIndex, subFolders.length-1);
		this.endIndex = Math.min(endIndex, subFolders.length-1);
		this.currentIndex = startIndex;
	}

	
	@Override
	protected boolean hasMoreData() {
		return currentIndex >= startIndex && currentIndex <= endIndex && currentIndex < subFolders.length;
	}

	public int size() {
		return subFolders.length;
	}
	
	@Override
	protected List<MeasurementUnit> loadBatch() throws Exception {
		String[] fileNames = FileTools.listFiles(subFolders[currentIndex], MEASUREMENT_FILE_PREFIX);

		MeasurementFile file;
		List<MeasurementUnit> units = new ArrayList<MeasurementUnit>();
		for (String fileName : fileNames) {
			file = new MeasurementFile(new File(subFolders[currentIndex], fileName), house, sensorIds);
			units.addAll(file.parse());
		}
		
		currentIndex++;
		return units;
	}

	@Override
	public void reset() {
		currentIndex = startIndex;
		super.reset();
	}

	@Override
	public String getBatchDescription() {
		// TODO Auto-generated method stub
		return null;
	}




}
