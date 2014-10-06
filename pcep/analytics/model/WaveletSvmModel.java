package pcep.analytics.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.GridSearch;
import libsvm.LibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.weka.ToWekaUtils;

import org.apache.log4j.Logger;

import pcep.analytics.LabeledSample;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.unsupervised.attribute.Wavelet;

public class WaveletSvmModel extends Model {
	

	
	private final double ACCEPTED_LENGTH = 0.9; // of the largest vector
	private Logger log = Logger.getLogger(WaveletSvmModel.class);
	private libsvm.svm_parameter svmParams;
	
	private int a0;
	private int c0;
	
	public WaveletSvmModel(int a0, int c0) {
		super();
		this.a0 = a0;
		this.c0 = c0;
	}
	

	
	private Instances toWekaInstances(List<LabeledSample> samples) {
		Dataset dataset = new DefaultDataset();
		int maxVectorLength = 0;
		for (LabeledSample sample : samples) {
			if (sample.size() > maxVectorLength) {
				maxVectorLength = sample.size();
			}
		}
		int vectorLength = (int) Math.floor(ACCEPTED_LENGTH * maxVectorLength);
		log.debug("Setting vector length to " + vectorLength + " (max: " + maxVectorLength + ")");
		double[] values;
		for (LabeledSample sample : samples) {
			values = sample.getValues(vectorLength);
			if (values.length != vectorLength) {
				continue;
			}
			dataset.add(new DenseInstance(values, sample.getLabel()));
		}
		return (new ToWekaUtils(dataset)).getDataset();
	}

	@Override
	public Dataset preprocess(List<LabeledSample> samples) throws Exception {

		log.info("Applying wavelet filter");

		Wavelet waveletFilter = new Wavelet();
		waveletFilter.setAlgorithm(new SelectedTag(Wavelet.ALGORITHM_HAAR, Wavelet.TAGS_ALGORITHM));
		waveletFilter.setAlgorithm(new SelectedTag(Wavelet.PADDING_ZERO, Wavelet.TAGS_PADDING));
		
		Instances instances = toWekaInstances(samples);
		waveletFilter.setInputFormat(instances);
		
		for (int i=0; i<instances.numInstances(); i++) {
			waveletFilter.input(instances.instance(i));
		}
		
		waveletFilter.batchFinished();

		Dataset dataset = new DefaultDataset();
		weka.core.Instance output;
		int i = 0;
		while ((output = waveletFilter.output()) != null) {
			Instance instance = new DenseInstance(output.toDoubleArray(), samples.get(i++).getLabel()); 
			dataset.add(instance);
		}
		
		// Normalize
//		log.info("Normalizing wavelet vectors");
//		NormalizeMidrange nmr = new NormalizeMidrange();
//		nmr.filter(dataset);
		
		return dataset;
	}

	@Override
	public Classifier gridSearchWithCV(Dataset dataset, int folds, ModelParam...params) throws Exception {
		double[] C = new double[0];
		double[] gamma = new double[0];
		
		for (ModelParam param : params) {
			if (param.getName().equals("C")) {
				C = param.getValues();
			}
			else if (param.getName().equals("gamma")) {
				gamma = param.getValues();
			}
		}

		if (C.length == 0) {
			throw new Exception("Missing values for SVM parameter: C");
		}
		if (gamma.length == 0) {
			throw new Exception("Missing values for SVM parameter: gamma");
		}
		log.debug("C={" + joinArray(C) + "}");
		log.debug("gamma={" + joinArray(gamma) + "}");
		
		LibSVM svm = new LibSVM();
		LibSVM.setPrintInterface(LibSVM.svm_print_console);
		
		libsvm.svm_parameter svmParams = new libsvm.svm_parameter();
		
		GridSearch gridSearch = new GridSearch(svm, dataset, folds);
		this.svmParams = gridSearch.search(svmParams, C, gamma);
		
		svm.setParameters(this.svmParams);
	
		return classifier;
	}
	
	private String joinArray(double[] array) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<array.length; i++) {
			buffer.append(array[i]);
			if (i < array.length-1) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

	@Override
	public Classifier initialize(Map<String, Double> params) {
		LibSVM svm = new LibSVM();
		//LibSVM.setPrintInterface(LibSVM.svm_print_console);
		this.svmParams = new libsvm.svm_parameter();
		svmParams.C = params.get("C");
		svmParams.gamma = params.get("gamma");
		log.info("Using parameters C=" + params.get("C") + " and " + "gamma=" + params.get("gamma"));
		svm.setParameters(svmParams);
		return svm;
	}


	@Override
	public String toString() {
		return "Wavelet SVM Model";
	}

	@Override
	public Map<String, Double> getState() {
		Map<String, Double> state = new HashMap<String, Double>();
		state.put("C", svmParams.C);
		state.put("gamma", svmParams.gamma);
		return state;
	}

	@Override
	public String getName() {
		return "WAVELET_SVM";
	}



	@Override
	public ModelParam[] calculateGridParams(Dataset dataset) {
		ModelParam gamma = new GammaParam(dataset, a0);
		ModelParam c = new GridParam("C", 0, 1, c0+1);
		return new ModelParam[]{gamma,c};
	}
}
