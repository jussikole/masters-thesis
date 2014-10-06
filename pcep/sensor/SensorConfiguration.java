package pcep.sensor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pcep.db.Sensor;
import pcep.epn.source.FileEventSource;
import pcep.io.FileWritable;
import pcep.io.OutputWriter;
import pcep.utils.DateUtils;

public class SensorConfiguration implements FileWritable {
	private Sensor sensor;
	
	private String windowLength;
	private String waitingInterval;
	private String eventInterval;
	private String windowDifference;
	private String exceedTime;
	private int minLevel;
	
	public SensorConfiguration(Sensor sensor) {
		this.sensor = sensor;
	}
	
	public SensorConfiguration(int sensorId) throws IOException {
		this.sensor = Sensor.load(sensorId);
	}
	
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public Sensor getSensor() {
		return sensor;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Configuration for " + sensor + ":\n");
		builder.append(" - windowLength=" + windowLength + "\n");
		builder.append(" - waitingInterval=" + waitingInterval + "\n");
		builder.append(" - eventInterval=" + eventInterval + "\n");
		builder.append(" - windowDifference=" + windowDifference + "\n");
		builder.append(" - exceedTime=" + exceedTime + "\n");
		builder.append(" - minLevel=" + minLevel + "\n");
		return builder.toString();
	}

	public String getWindowLength() {
		return windowLength;
	}

	public void setWindowLength(String windowLength) {
		this.windowLength = windowLength;
	}

	public String getWaitingInterval() {
		return waitingInterval;
	}

	public void setWaitingInterval(String waitingInterval) {
		this.waitingInterval = waitingInterval;
	}

	public String getEventInterval() {
		return eventInterval;
	}

	public void setEventInterval(String eventInterval) {
		this.eventInterval = eventInterval;
	}

	public String getWindowDifference() {
		return windowDifference;
	}

	public void setWindowDifference(String windowDifference) {
		this.windowDifference = windowDifference;
	}

	public String getExceedTime() {
		return exceedTime;
	}

	public void setExceedTime(String exceedTime) {
		this.exceedTime = exceedTime;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	
	public static SensorConfiguration getDefaultConfiguration(Sensor sensor, SensorType type) {
		return SensorConfiguration.fillWithDefaults(new SensorConfiguration(sensor), type);
	}
	
	public static SensorConfiguration getDefaultConfiguration(int sensorId, SensorType type) throws IOException {
		return SensorConfiguration.fillWithDefaults(new SensorConfiguration(sensorId), type);
	}
	
	private static SensorConfiguration fillWithDefaults(SensorConfiguration conf, SensorType type) {
		if (type == SensorType.TEMPERATURE) {
			
		}
		else if (type == SensorType.CO2) {
			conf.setWindowLength("4 hours");
			conf.setWaitingInterval("30 min");
			conf.setEventInterval("2 hours");
			conf.setWindowDifference("2 hours"); // 60 min?
			conf.setExceedTime("20 min");
			conf.setMinLevel(2);
		}
		else if (type == SensorType.VOC) {
			
		}
		else if (type == SensorType.HEATING) {
			
		}
		else if (type == SensorType.WATER) {
			
		}
		else if (type == SensorType.PARTICLES) {
			
		}

		return conf;
	}

	@Override
	public String getFilename() {
		return OutputWriter.CONFIGURATION_FILE_NAME;
	}

	@Override
	public String getFileExtension() {
		return OutputWriter.CONFIGURATION_FILE_EXT;
	}

	@Override
	public String getHeader() {
		return "Configuration for " + sensor + " at " + DateUtils.formattedDate(System.currentTimeMillis());
	}

	@Override
	public List<String> getLines() {
		List<String> lines = new ArrayList<String>();
		lines.add("windowLength = " + windowLength);
		lines.add("waitingInterval = " + waitingInterval);
		lines.add("eventInterval = " + eventInterval);
		lines.add("windowDifference = " + windowDifference);
		lines.add("exceedTime = " + exceedTime);
		lines.add("minLevel = " + minLevel);
		return lines;
	}

}
