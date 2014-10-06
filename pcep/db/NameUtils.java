package pcep.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class NameUtils {
	private static Logger log = Logger.getLogger(NameUtils.class);
	
	private static Pattern sensorFilePattern = Pattern.compile("^sensors(\\d+)_(\\d+)\\.txt$");
	private static Pattern measurementFilePattern = Pattern.compile("^measurements(\\d+)_(\\d+)\\.txt$");
	private static Pattern measurementHeaderPattern = Pattern.compile("^ID_(\\d+)_(\\d+)_(\\d+)\\(DOUBLE\\)$");
	
	public static int[] splitSensorFileName(String name) throws Exception {
		return parseGroup(sensorFilePattern, name); 
	}
	
	public static int[] splitMeasurementFileName(String name) throws Exception {
		return parseGroup(measurementFilePattern, name); 
	}
	
	public static int[] splitMeasurementHeaderPattern(String header) throws Exception {
		return parseGroup(measurementHeaderPattern, header); 
	}
	
	private static int[] parseGroup(Pattern pattern, String name) throws Exception {
		Matcher matcher = pattern.matcher(name);
		int[] result;
		if (matcher.matches()) {
			result = new int[matcher.groupCount()];
			try {
				for (int i=0; i<result.length; i++) {
					result[i] = Integer.parseInt(matcher.group(i+1));
				}
			} catch (IllegalStateException e) {
				log.error("Invalid filename " + name + " for pattern " + pattern);
				throw new Exception("Invalid filename: " + name);
			}
		}
		else {
			throw new Exception("Invalid filename: " + name);
		}
		return result;
	}
}
