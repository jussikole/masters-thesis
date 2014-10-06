package pcep.analytics.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.fastdtw.FastDTW;

import org.apache.log4j.Logger;

import pcep.analytics.Label;
import pcep.analytics.LabeledSample;
import pcep.analytics.ParamTools;
import pcep.analytics.PerformanceTools;

public class DtwKnnModel extends Model {

	private Logger log = Logger.getLogger(DtwKnnModel.class);
	
	
	private final double ACCEPTED_LENGTH = 0.9; // of the largest vector

	private int radius;
	private int k;
	
	public DtwKnnModel() {
		super();
	}
	
	@Override
	public Classifier initialize(Map<String, Double> params) {
		this.radius = params.get("radius").intValue();
		this.k = params.get("k").intValue();
		
		log.debug("Building dtw and knn model with radius=" + radius + " and k=" + k);
		DistanceMeasure dm = new FastDTW(radius);
		Classifier classifier = new KNearestNeighbors(k, dm);
		return classifier;
	}

	@Override
	public Dataset preprocess(List<LabeledSample> samples) {
		int maxVectorLength = 0;
		for (LabeledSample sample : samples) {
			if (sample.size() > maxVectorLength) {
				maxVectorLength = sample.size();
			}
		}
		int vectorLength = (int) Math.floor(ACCEPTED_LENGTH * maxVectorLength);
		log.debug("Setting vector length to " + vectorLength + " (max: " + maxVectorLength + ")");
		
		
		Dataset dataset = new DefaultDataset();
		for (LabeledSample sample : samples) {
			if (sample.getValues(vectorLength).length == vectorLength) {
				dataset.add(new DenseInstance(sample.getValues(), sample.getLabel()));
			}
		}
		return dataset;
	}

	@Override
	public Classifier gridSearchWithCV(Dataset dataset, int folds, ModelParam... params) {
		Classifier bestClassifier = null;
		Classifier currentClassifier = null;
		double acd = 0.0;
		double bestAcd = 0.0;
		PerformanceMeasure performance;
		for (Map<String, Double> p : ParamTools.getCombinations(params)) {
			currentClassifier = initialize(p);
			CrossValidation cv = new CrossValidation(currentClassifier);
			
			log.info("Cross validating...");
			long t = System.currentTimeMillis();
			performance = cv.crossValidation(dataset).get(Label.POSITIVE);
			log.info("Finished in " + (System.currentTimeMillis()-t) + " ms: " + performance);
			acd = PerformanceTools.calculateAcd(performance);
			if (acd > bestAcd) {
				bestAcd = acd;
				bestClassifier = currentClassifier;
			}
			saveIteration(p, performance);
		}
		return bestClassifier;
	}
	
	@Override
	public String toString() {
		return "DTW kNN model";
	}

	@Override
	public Map<String, Double> getState() {
		Map<String, Double> state = new HashMap<String, Double>();
		state.put("radius", 1.0*radius);
		state.put("k", 1.0*k);
		return state;
	}

	@Override
	public String getName() {
		return "DTW_KNN";
	}

	@Override
	public ModelParam[] calculateGridParams(Dataset dataset) {
		ModelParam k = new GridParam("k", 3, 2, 1);
		ModelParam radius = new GridParam("radius", 5, 1, 1);
		return new ModelParam[]{k, radius};
	}




}
