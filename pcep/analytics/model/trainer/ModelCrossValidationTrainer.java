package pcep.analytics.model.trainer;

import org.apache.log4j.Logger;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import pcep.analytics.model.Model;
import pcep.analytics.model.ModelParam;

public class ModelCrossValidationTrainer extends ModelTrainer {
	private Logger log = Logger.getLogger(ModelCrossValidationTrainer.class);
	private int folds;
	
	public ModelCrossValidationTrainer(int folds, Model model, ModelParam...params) {
		super(model, params);
		this.folds = folds;
	}
	
	@Override
	public void train() throws Exception {
		Model model = this.getModel();
		log.info("Using cross validation with " + folds + " folds");
		long t = System.currentTimeMillis();
		Dataset dataset = getModel().preprocess(allSamples());
		log.info("Preprocessing took " + (System.currentTimeMillis()-t) + " ms");
		t = System.currentTimeMillis();
		Classifier classifier = model.gridSearchWithCV(dataset, folds, params);
		log.info("Grid search took " + (System.currentTimeMillis()-t) + " ms");
		model.setClassifier(classifier);
	}

}
