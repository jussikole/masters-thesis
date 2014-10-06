package pcep.analytics.model;



public class GridParam extends ModelParam {
	
	private String name;
	private int n;
	private int counter;
	private double start;
	private double step;
	
	public GridParam(String name, double start, double step, int n) {
		this.name = name;
		this.start = start;
		this.step = step;
		this.n = n;
		this.reset();
	}
	
	@Override
	public boolean hasNext() {
		return counter < n;
	}

	@Override
	public Double next() {
		return start + (counter++) * step;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void reset() {
		this.counter = 0;
	}

	@Override
	public double[] getValues() {
		double[] values = new double[n];
		int i = 0;
		while (hasNext()) {
			values[i++] = next();
		}
		reset();
		return values;
	}


}
