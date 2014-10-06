package pcep.analytics;

import java.util.Map;

import net.sf.javaml.classification.evaluation.PerformanceMeasure;

public class PerformanceTools {
	public static double W = 0.5;
	
	public static double calculateAcd(PerformanceMeasure measure) {
		return PerformanceTools.calculateAcd(measure.tp, measure.fp, measure.tn, measure.fn);
	}
	
	public static double calculateAcd(double[][] confusion) {
		return PerformanceTools.calculateAcd(confusion[0][0], confusion[0][1], confusion[1][0], confusion[1][1]);
	}
	
	public static double calculateAcd(double tp, double fp, double tn, double fn) {
		double tpr = tp / (tp + fn);
		double fpr = fp / (tp + fn);
		return 1 - Math.sqrt(W * Math.pow(1 - tpr, 2) + (1 - W) * Math.pow(fpr, 2));
	}

	public static String format(PerformanceMeasure p) {
		StringBuilder builder = new StringBuilder();
		builder.append("[TP=" + p.tp);
		builder.append(" FP=" + p.fp);
		builder.append(" TN=" + p.tn);
		builder.append(" FN=" + p.fn);
		builder.append(" ACC=" + p.getAccuracy() + "]");
		return builder.toString();
	}

}
