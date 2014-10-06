package pcep.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import net.sf.javaml.classification.Classifier;

public class OutputWriter {
	
	private static Logger log = Logger.getLogger(OutputWriter.class);
	
	public static final String RESOURCES_DIR = "src/main/resources";
	public static final String DATA_DIR = "data";
	public static final String OUTPUT_DIR = "output";
	
	public static final String CLASSIFIER_FILE_NAME = "classifier";
	public static final String CLASSIFIER_FILE_EXT = "ser";

	public static final String MODEL_FILE_NAME = "model";
	public static final String MODEL_FILE_EXT = "txt";
	
	public static final String CONFIGURATION_FILE_NAME = "configuration";
	public static final String CONFIGURATION_FILE_EXT = "txt";
	
	public static final String PERFORMANCE_FILE_NAME = "test";
	public static final String PERFORMANCE_FILE_EXT = "txt";
	
	private File outputFolder;
	
	public OutputWriter(String scenario, int sessionId) {
		this.outputFolder = new File(new File(new File(RESOURCES_DIR, scenario), OUTPUT_DIR), Integer.toString(sessionId));
		this.outputFolder.mkdirs();
	}
	
	public OutputWriter(String scenario) {
		this(scenario, getNextSessionId(scenario));
	}
	
	private static int getNextSessionId(String scenario) {
		return (new File(new File(RESOURCES_DIR, scenario), OUTPUT_DIR)).listFiles().length+1;
	}
	
	public File save(FileWritable item) throws IOException {
		return this.save(item, null);
	}
	
	public File save(FileWritable item, String note) throws IOException {
		File file = getFile(item.getFilename(), item.getFileExtension());
		FileWriter writer = new FileWriter(file);
		writer.write(item.getHeader());
		if (note != null) {
			writer.write("\n");
			writer.write(note);
		}
		writer.write("\n\n");
		for (String line : item.getLines()) {
			writer.write(line);
			writer.write("\n");
		}
		writer.close();
		log.info("Saved " + item + " to " + file);
		return file;
	}
	
	public File save(FileAppendable item) throws IOException {
		if (!item.hasNextLine()) {
			return null;
		}
		
		File file = getFile(item.getFilename(), item.getFileExtension());
		FileWriter writer = new FileWriter(file);
		writer.write(item.getHeader());
		writer.write("\n\n");
		writer.write(item.getNextLine());
		writer.close();
		log.info("Saved " + item + " to " + file);
		return file;
	}
	
	public void append(File file, FileAppendable item) throws IOException {
		if (!item.hasNextLine()) {
			return;
		}
		
		FileWriter writer = new FileWriter(file, true);
		writer.write("\n");
		writer.write(item.getNextLine());
		writer.close();
		log.info("Appended " + item + " to " + file);
	}
	
	public File save(FileWritable[] items) throws IOException {
		File file = getFile(items[0].getFilename(), items[0].getFileExtension());
		FileWriter writer = new FileWriter(file);
		for (FileWritable item : items) {
			writer.write(item.getHeader());
			writer.write("\n");
			for (String line : item.getLines()) {
				writer.write(line);
				writer.write("\n");
			}
			writer.write("\n\n");
		}
		writer.close();
		log.info("Saved " + items.length + " to " + file);
		return file;
	}
	
	public File save(Classifier classifier) throws IOException {
		File file = getFile(CLASSIFIER_FILE_NAME, CLASSIFIER_FILE_EXT);
		FileOutputStream out1 = new FileOutputStream(file);
		ObjectOutputStream objectOutput = new ObjectOutputStream(out1);
		objectOutput.writeObject(classifier);
		objectOutput.close();
		out1.close();
		log.info("Saved classifier to " + file);
		return file;
	}
	
	public Classifier load(int classifierId) throws IOException, ClassNotFoundException {
		File file = new File(outputFolder, classifierId + "_" + CLASSIFIER_FILE_NAME + "." + CLASSIFIER_FILE_EXT);
		FileInputStream in = new FileInputStream(file);
		ObjectInputStream objectIn = new ObjectInputStream(in);
		Classifier classifier = (Classifier) objectIn.readObject();
		objectIn.close();
		in.close();
		log.info("Loaded classifier from " + file);
		return classifier;
	}
	
	private File getFile(String name, String extension) {
		int counter = 1;
		File file = null;
		while (file == null || file.exists()) {
			file = new File(outputFolder, (counter++) + "_" + name + "." + extension);
		}
		return file;
	}
}
