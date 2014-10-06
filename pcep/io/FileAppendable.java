package pcep.io;

import java.util.List;

public interface FileAppendable {
	public String getFilename();
	public String getFileExtension();
	public String getHeader();
	public boolean hasNextLine();
	public String getNextLine();
}
