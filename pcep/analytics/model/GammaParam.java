package pcep.analytics.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;

import pcep.analytics.LabeledSample;

public class GammaParam extends ModelParam {

	private int a0;
	private int a;
	private double lnGamma0;

	public GammaParam(Dataset dataset, int a0) {
		this.a0 = a0;
		reset();
		List<Double> distances = new ArrayList<Double>();
		EuclideanDistance dm = new EuclideanDistance();
		for(int i=0; i<dataset.size(); i++) {
			for (int j=0; j<dataset.size(); j++) {
				if (i < j) {
					distances.add(dm.calculateDistance(dataset.get(i), dataset.get(j)));
				}
			}
		}
		Collections.sort(distances);
		double median = distances.get(distances.size()/2);
		this.lnGamma0 = Math.log(1.0 / Math.pow(median, 2));
	}
	
	
	@Override
	public boolean hasNext() {
		return a <= a0;
	}

	@Override
	public Double next() {
		double r = Math.exp(lnGamma0 - a);
		a++;
		return r;
	}

	@Override
	public String getName() {
		return "gamma";
	}

	@Override
	public void reset() {
		this.a = -a0;
	}

	@Override
	public double[] getValues() {
		double[] values = new double[2*a0+1];
		int i = 0;
		while(hasNext()) {
			values[i] = next();
		}
		return values;
	}
	
//	public static void main(String[] args) {
//		Dataset dataset = new DefaultDataset();
//		Instance i1 = new DenseInstance(new double[]{1,2,3});
//		Instance i2 = new DenseInstance(new double[]{4,2,-2});
//		Instance i3 = new DenseInstance(new double[]{1,5,6});
//		dataset.add(i1);
//		dataset.add(i2);
//		dataset.add(i3);
//		
//		GammaParam g = new GammaParam(dataset, 3);
//		
//		while (g.hasNext()) {
//			System.out.println(g.next());
//		}
//	}

}
