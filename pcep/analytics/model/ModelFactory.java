package pcep.analytics.model;

import org.apache.log4j.Logger;

public class ModelFactory {
	private static Logger log = Logger.getLogger(ModelFactory.class);
	
	public static Model build(String type) throws Exception {
		if (type.equals("DTW_KNN")) {
			return new DtwKnnModel();
		}
		else if (type.equals("WAVELET_SVM")) {
			return new WaveletSvmModel();
		}
		throw new Exception("No model found for " + type);
	}
}
