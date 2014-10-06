package pcep.analytics.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import pcep.analytics.Label;
import pcep.analytics.LabeledSample;

public class SampleCollector {
	private Logger log = Logger.getLogger(SampleCollector.class);

	protected List<LabeledSample> samples;
	
	protected LabeledSample first;
	protected LabeledSample last;
	
	public SampleCollector() {
		clear();
	}
	
	public void addSample(LabeledSample sample) {
		if (first == null) {
			first = sample;
		}
		last = sample;
		
		samples.add(sample);
	}
	
	public void clear() {
		this.samples = new ArrayList<LabeledSample>();
		this.first = null;
		this.last = null;
	}

	
	protected List<LabeledSample> allSamples() {
		List<LabeledSample> newSamples = new ArrayList<LabeledSample>();
		newSamples.addAll(samples);
		
		this.clear();
		return newSamples;
	}

}
