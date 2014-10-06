package pcep.io;

import java.io.File;
import java.io.FilenameFilter;

public class FileTools {
	public static String[] listFiles(File folder, final String filePrefix) {
		FilenameFilter fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(filePrefix);
			}
		};
		return folder.list(fileFilter);
	}
}
