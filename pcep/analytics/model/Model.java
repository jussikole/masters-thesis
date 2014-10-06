package pcep.analytics.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import org.apache.log4j.Logger;

import pcep.analytics.Label;
import pcep.analytics.LabeledSample;
import pcep.analytics.ParamTools;
import pcep.analytics.PerformanceTools;
import pcep.io.FileWritable;
import pcep.io.OutputWriter;
import pcep.utils.DateUtils;

public abstract class Model implements FileWritable {	
	private Logger log = Logger.getLogger(Model.class);
	
	protected Classifier classifier;
	private Map<String, Double> params;
	private List<String> iterations = new ArrayList<String>();
	
	public Model() {

	}
	

	public Classifier gridSearch(Dataset dataset, double test, ModelParam...params) {
		int trainingSamples = (int) (test * dataset.size());
		Dataset training = new DefaultDataset();
		Dataset testing = new DefaultDataset();
		int counter = 0;
		for (Instance instance : dataset) {
			if (counter++<trainingSamples) {
				training.add(instance.copy());
			}
			else {
				testing.add(instance.copy());
			}
		}
		
		log.info("Using " + training.size() + " training samples and " + testing.size() + " testing samples");
		
		Classifier bestClassifier = null;
		Classifier currentClassifier = null;
		double accuracy = 0.0;
		double bestAccuracy = 0.0;
		PerformanceMeasure performance;
		for (Map<String, Double> p : ParamTools.getCombinations(params)) {
			currentClassifier = initialize(p);
			log.info("Building classifier");
			currentClassifier.buildClassifier(training);
			long t = System.currentTimeMillis();
			performance = EvaluateDataset.testDataset(currentClassifier, testing).get(Label.POSITIVE);
			log.info("Finished in " + (System.currentTimeMillis()-t) + " ms: " + performance);
			accuracy = performance.getAccuracy();
			if (accuracy > bestAccuracy) {
				bestAccuracy = accuracy;
				bestClassifier = currentClassifier;
			}
			saveIteration(p, performance);
		}
		if (bestClassifier == null) {
			bestClassifier = currentClassifier;
		}
		
		return bestClassifier;
	}
	
	public Classifier initialize() {
		return initialize(params);
	}
	
	public void setClassifier(Classifier newClassifier) {
		if (newClassifier == null) {
			log.warn(getName() + " received an empty classifier");
		}
		this.classifier = newClassifier;
	}
	
	public Classifier getClassifier() {
		return classifier;
	}
	
	public Label classify(LabeledSample sample) {
		return (Label) classifier.classify(new DenseInstance(sample.getValues()));
	}
	
	
	@Override
	public String getFilename() {
		return OutputWriter.MODEL_FILE_NAME;
	}

	@Override
	public String getFileExtension() {
		return OutputWriter.MODEL_FILE_EXT;
	}

	@Override
	public String getHeader() {
		StringBuilder builder = new StringBuilder();
		builder.append("Model: " + getName() + "\n");
		builder.append("Saved: " + DateUtils.formattedDate(System.currentTimeMillis()) + "\n");
		builder.append("Params: ");
		Map<String, Double> state = getState();
		for (String key : state.keySet()) {
			builder.append(key + "=" + state.get(key) + " ");
		}
		return builder.toString();
	}

	@Override
	public List<String> getLines() {
		return iterations;
	}

	
	protected void saveIteration(Map<String, Double> params, PerformanceMeasure performance) {
		StringBuilder builder = new StringBuilder();
		builder.append("Iterations:\n");
		for (String key : params.keySet()) {
			builder.append(key + "=" + params.get(key) + " ");
		}
		builder.append("\t");
		builder.append(PerformanceTools.format(performance));
		iterations.add(builder.toString());
	}
	
	public abstract String getName();
	public abstract Map<String, Double> getState();
	public abstract Dataset preprocess(List<LabeledSample> samples) throws Exception;
	public abstract ModelParam[] calculateGridParams(Dataset dataset);
	public abstract Classifier initialize(Map<String, Double> params);
	public abstract Classifier gridSearchWithCV(Dataset dataset, int folds, ModelParam...params) throws Exception;
}
