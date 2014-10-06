package pcep.io;

import java.util.List;

public interface FileWritable {
	public String getFilename();
	public String getFileExtension();
	public String getHeader();
	public List<String> getLines();
}
