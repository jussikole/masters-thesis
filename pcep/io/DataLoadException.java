package pcep.io;

public class DataLoadException extends Exception {

	private static final long serialVersionUID = 1567213982818875045L;

	private String reason;

	public DataLoadException(String reason) {
		this.reason = reason;
	}
	
	public String getMessage() {
		return "Data load from Asteka failed because: " + reason;
	}
}
