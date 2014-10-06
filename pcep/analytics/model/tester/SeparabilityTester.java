package pcep.analytics.model.tester;

import java.util.List;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.DatasetTools;

import pcep.analytics.Label;
import pcep.analytics.LabeledSample;
import pcep.analytics.model.Model;
import pcep.analytics.model.SampleCollector;
import pcep.io.FileAppendable;
import pcep.utils.DateUtils;

public class SeparabilityTester extends SampleCollector implements FileAppendable {
	private Model model;
	private String line;
	
	public SeparabilityTester(Model model) {
		this.model = model;
	}

	public double test() throws Exception {
		// Calculates Fisher's Linear Discriminant
		// http://www.csd.uwo.ca/~olga/Courses/CS434a_541a/Lecture8.pdf
		
		Dataset dataset = model.preprocess(allSamples());
		
		Dataset posDataset = new DefaultDataset();
		Dataset negDataset = new DefaultDataset();

		for (Instance instance : dataset) {
			if (instance.classValue().equals(Label.POSITIVE)) {
				posDataset.add(instance);
			}
			else {
				negDataset.add(instance);
			}
		}
		
		Instance posAvg = DatasetTools.average(posDataset);
		Instance negAvg = DatasetTools.average(negDataset);
		
		double posScattering = 0.0;
		double negScattering = 0.0;
		
		for (double value : DatasetTools.standardDeviation(posDataset, posAvg)) {
			posScattering += Math.pow(value, 2);
		}
		
		for (double value : DatasetTools.standardDeviation(negDataset, negAvg)) {
			negScattering += Math.pow(value, 2);
		}
		
		EuclideanDistance dm = new EuclideanDistance();
		double fld = Math.pow(dm.calculateDistance(posAvg, negAvg), 2) / (posScattering + negScattering);
		this.line = "Fisher's Linear Discriminant = " + fld;
		return fld;
	}

	@Override
	public String getFilename() {
		return "separability";
	}

	@Override
	public String getFileExtension() {
		return "txt";
	}

	@Override
	public String getHeader() {
		return "Class separability test with " + model.getName() + " at " + DateUtils.formattedDate(System.currentTimeMillis());
	}

	@Override
	public boolean hasNextLine() {
		return line != null;
	}

	@Override
	public String getNextLine() {
		String r = line;
		this.line = null;
		return r;
	}
}
