package pcep.sensor;


public class SensorConfigurationException extends Exception {

	private static final long serialVersionUID = -22892784152744198L;
	private SensorConfiguration conf;
	private String key;
	
	public SensorConfigurationException(SensorConfiguration conf, String key) {
		this.conf = conf;
		this.key = key;
	}
	
	@Override
	public String getMessage() {
		return conf + " is missing value for " + key;
	}

}
