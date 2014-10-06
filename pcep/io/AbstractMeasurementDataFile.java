package pcep.io;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import pcep.db.DatabaseItem;


public abstract class AbstractMeasurementDataFile<T extends DatabaseItem> {
	private static final String COLUMN_DELIMITER = ";";
	private Logger log = Logger.getLogger(AbstractMeasurementDataFile.class);
	
	private File file;
	
	public AbstractMeasurementDataFile(File file) {
		this.file = file;
	}
	
	public List<T> parse() throws Exception {
		List<T> items = new ArrayList<T>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		
		long t = System.currentTimeMillis();
		parseHeader(line.split(COLUMN_DELIMITER));
		
		t = System.currentTimeMillis();
		int counter = 0;
		T unit;
		while ((line = reader.readLine()) != null) {
			unit = parseLine(line.split(COLUMN_DELIMITER));
			if (unit != null) {
				items.add(unit);
				counter++;
			}

		}
		//log.debug("Read " + counter + " lines in " + (System.currentTimeMillis()-t) + " ms");
		reader.close();
		return items;
	}
	
	public abstract void parseHeader(String[] columns) throws Exception;
	public abstract T parseLine(String[] columns);

}
