package pcep.analytics.model.tester;

import java.util.Map;

import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;

import org.apache.log4j.Logger;

import pcep.analytics.Label;
import pcep.analytics.LabeledSample;
import pcep.analytics.PerformanceTools;
import pcep.analytics.model.Model;
import pcep.analytics.model.SampleCollector;
import pcep.io.FileAppendable;
import pcep.io.OutputWriter;
import pcep.utils.DateUtils;

public class ModelTester extends SampleCollector implements FileAppendable {
	private Logger log = Logger.getLogger(ModelTester.class);
	private Model model;
	private String line;
	
	public ModelTester(Model model) {
		super();
		this.model = model;
	}
	
	public PerformanceMeasure test() throws Exception {
		if (first == null || last == null) {
			return null;
		}
		
		String start = DateUtils.formattedDate(first.getTimestamp());
		String end = DateUtils.formattedDate(last.getTimestamp());
		
		int p = 0;
		int n = 0;
		for (LabeledSample s : samples) {
			if (s.getLabel().equals(Label.POSITIVE)) {
				p++;
			}
			else if (s.getLabel().equals(Label.NEGATIVE)) {
				n++;
			}
		}
		
		log.info("Testing with " + p + " positive samples and " + n + " negative samples");
		Dataset dataset = model.preprocess(allSamples());
		
		log.info("Data from " + start + " to " + end);
		long t = System.currentTimeMillis();
		Map<Object, PerformanceMeasure> performance = EvaluateDataset.testDataset(model.getClassifier(), dataset);
		log.info("Testing took " + (System.currentTimeMillis()-t) + " ms");
		
		StringBuilder builder = new StringBuilder();
		builder.append(start);
		builder.append(" - ");
		builder.append(end);
		builder.append("   ");
		builder.append("(+" + p + ")");
		builder.append(" (-" + n + ")");
		builder.append("   ");
		builder.append(PerformanceTools.format(performance.get(Label.POSITIVE)));
		//builder.append(" NEG: " + PerformanceTools.format(performance.get(Label.NEGATIVE)));
		
		this.line = builder.toString();
		return performance.get(Label.POSITIVE);
	}
	
	@Override
	public boolean hasNextLine() {
		return line != null;
	}

	@Override
	public String getFilename() {
		return OutputWriter.PERFORMANCE_FILE_NAME;
	}

	@Override
	public String getFileExtension() {
		return OutputWriter.PERFORMANCE_FILE_EXT;
	}

	@Override
	public String getHeader() {
		return "Test started " + DateUtils.formattedDate(System.currentTimeMillis()) + " with " + model; 
	}

	@Override
	public String getNextLine() {
		String r = line;
		this.line = null;
		return r;
	}
	
	public String toString() {
		return "Model tester";
	}
}
