package pcep.analytics.model.trainer;

import org.apache.log4j.Logger;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import pcep.analytics.ParamTools;
import pcep.analytics.model.Model;
import pcep.analytics.model.ModelParam;


public class ModelGridSearchTrainer extends ModelTrainer {
	private Logger log = Logger.getLogger(ModelGridSearchTrainer.class);
	private double test;
	
	public ModelGridSearchTrainer(double test, Model model) {
		super(model);
		this.test = test;
	}
	
	@Override
	public void train() throws Exception {
		Model model = this.getModel();
		log.info("Using grid search without cross validation (test=" + test + ")");
		long t = System.currentTimeMillis();
		Dataset dataset = model.preprocess(allSamples());
		log.info("Preprocessing took " + (System.currentTimeMillis()-t) + " ms");
		t = System.currentTimeMillis();
		Classifier classifier = model.gridSearch(dataset, test, model.calculateGridParams(dataset));
		log.info("Grid search took " + (System.currentTimeMillis()-t) + " ms");
		model.setClassifier(classifier);
	}

	
}
