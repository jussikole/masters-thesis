package pcep.analytics.model.trainer;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;

import org.apache.log4j.Logger;

import pcep.analytics.model.Model;
import pcep.analytics.model.ModelParam;
import pcep.analytics.model.SampleCollector;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class ModelTrainer extends SampleCollector implements UpdateListener, Runnable {
	
	private Logger log = Logger.getLogger(ModelTrainer.class);
	
	private Model model;
	
	public ModelTrainer(Model model) {
		this.model = model;
		this.clear();
	}
	
	
	public void train() throws Exception {
		Dataset dataset = model.preprocess(allSamples());
		Classifier classifier = model.initialize();
		long t = System.currentTimeMillis();
		classifier.buildClassifier(dataset);
		log.info("Building classifier took " + (System.currentTimeMillis()-t) + " ms");
		model.setClassifier(classifier);
	}
	


	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {

		
		try {
			// TRAIN HERE
		} catch (Exception e) {
			e.printStackTrace();
		}
		clear();
	}

	public String getName() {
		return "Model Trainer";
	}

	public Model getModel() {
		return model;
	}

	@Override
	public void run() {
		try {
			log.info("Training " + model);
			long t = System.currentTimeMillis();
			this.train();
			log.info("Training finished in " + (System.currentTimeMillis()-t) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
}
