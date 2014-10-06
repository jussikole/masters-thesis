package pcep.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class AstekaExecutor {
	
	private Logger log = Logger.getLogger(AstekaExecutor.class);
	private File jarFile;
	private File jksFile;
	
	
	public AstekaExecutor(String path, String jarFilename, String jksFilename) throws IOException {
		log.info("Using Asteka jar in " + path);
		this.jarFile = new File(path, jarFilename);
		this.jksFile = new File(path, jksFilename);
		if (!jarFile.exists()) {
			throw new IOException("Asteka jar not found in " + jarFile);
		}
		if (!jksFile.exists()) {
			throw new IOException("Server jks not found in " + jksFile);
		}
	}
	
	private boolean copyFile(File sourceFile, File targetFile) {
		log.debug("Copying " + sourceFile + " to " + targetFile);
		try {
			FileUtils.copyFile(sourceFile, targetFile);
		} catch (IOException e) {
			log.error("Unable to copy");
			return false;
		}
		return true;
	}
	
	public void retrieve(String targetFolderPath, String startDate, String startTime, String endDate, String endTime) throws DataLoadException {
		File targetFolder = new File(targetFolderPath);
		targetFolder.mkdirs();
		
		if (targetFolder.listFiles().length > 0) {
			log.error("Folder " + targetFolderPath + " is not empty");
			throw new DataLoadException("Folder " + targetFolderPath + " is not empty");
		}
		
		File targetJarFile = new File(targetFolderPath, jarFile.getName());
		File targetJksFile = new File(targetFolderPath, jksFile.getName());

		if (!copyFile(jarFile, targetJarFile) || !copyFile(jksFile, targetJksFile)) {
			throw new DataLoadException("Copying jar files failed");
		}
		
		StringBuilder cmd = new StringBuilder();
		cmd.append("java -jar ");
		cmd.append(targetJarFile.getName());
		cmd.append(" ");
		cmd.append(startDate);
		cmd.append(" ");
		cmd.append(startTime);
		cmd.append(" ");
		cmd.append(endDate);
		cmd.append(" ");
		cmd.append(endTime);
		
		long processStartTimestamp; 
		try {
			File execDir = targetJarFile.getParentFile().getAbsoluteFile();
			log.info("Executing " + cmd.toString() + " in " + execDir);
			processStartTimestamp = System.currentTimeMillis();
			Process process = Runtime.getRuntime().exec(cmd.toString(), null, execDir);
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				log.debug(line);
			}
			in.close();
		} catch (IOException e) {
			log.error("Failed to run jar file: " + e.getMessage());
			throw new DataLoadException("Running jar file failed");
		}
		log.info("Finished in " + (System.currentTimeMillis() - processStartTimestamp) + " ms");
		
		log.debug("Removing jar and jks files...");
		targetJarFile.delete();
		targetJksFile.delete();
	}
	
	public static void main(String[] args) throws IOException, DataLoadException {
		AstekaExecutor exec = new AstekaExecutor("src/main/resources", "asteka.jar", "server.jks");
		exec.retrieve("src/main/resources/test/", "20130102", "00:00:00", "20130102", "23:59:59");
	}
}
