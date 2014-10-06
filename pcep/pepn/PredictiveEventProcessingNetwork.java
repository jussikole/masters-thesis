package pcep.pepn;

import java.util.HashSet;
import java.util.Set;

import pcep.analytics.Label;
import pcep.analytics.model.Model;
import pcep.analytics.model.SampleCollector;
import pcep.epn.EventProcessingNetwork;
import pcep.sensor.SensorConfiguration;

public class PredictiveEventProcessingNetwork implements Runnable {
	private EventProcessingNetwork epn;
	private SampleCollector sampleCollector;
	
	private Set<CollectorEpa> collectorEpas = new HashSet<CollectorEpa>();
	private Set<TrainingEpa> trainingEpas = new HashSet<TrainingEpa>();
	private Set<PredictingEpa> predictingEpas = new HashSet<PredictingEpa>();
	
	private AlertingEpa alertingEpa;
	
	public PredictiveEventProcessingNetwork(EventProcessingNetwork epn, SampleCollector sampleCollector) {
		this.epn = epn;
		this.sampleCollector = sampleCollector;
	}
	
	public void observe(SensorConfiguration conf) {
		// Collector EPA
		CollectorEpa collector = new CollectorEpa(conf.getSensor(), conf.getWindowLength(), conf.getWindowDifference());
		epn.addEpa(collector);
		collectorEpas.add(collector);
		
		// Training EPAs
		String complexEventName = epn.getSink().getComplexEventFactory().getComplexEventName();
		// Positive
		TrainingEpa positiveTrainingEpa = new TrainingEpa(sampleCollector, Label.POSITIVE, conf.getWaitingInterval(), conf.getEventInterval(), complexEventName);
		epn.addEpa(positiveTrainingEpa);
		trainingEpas.add(positiveTrainingEpa);
		// Negative
		TrainingEpa negativeTrainingEpa = new TrainingEpa(sampleCollector, Label.NEGATIVE, conf.getWaitingInterval(), conf.getEventInterval(), complexEventName);
		epn.addEpa(negativeTrainingEpa);
		trainingEpas.add(negativeTrainingEpa);

	}
	
	public void setSampleCollector(SampleCollector sampleCollector) {
		this.sampleCollector = sampleCollector;
		for (TrainingEpa trainingEpa : trainingEpas) {
			trainingEpa.setSampleCollector(sampleCollector);
		}
	}
	
	public void predictWith(Model model) {
		// Predicting EPA
		PredictingEpa predictingEpa = new PredictingEpa(model);
		epn.addEpa(predictingEpa);
		predictingEpas.add(predictingEpa);
		
		if (alertingEpa == null) {
			// Alerting EPA
			this.alertingEpa = new AlertingEpa(epn.getSink().getComplexEventFactory());
			epn.addEpa(alertingEpa);
		}
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
	public void setTraining(boolean on) {
		for (TrainingEpa trainingEpa : trainingEpas) {
			trainingEpa.toggle(on);
		}
	}
	
	public void setPredicting(boolean on) {
		for (PredictingEpa predictingEpa : predictingEpas) {
			predictingEpa.toggle(on);
		}
	}
	
}
