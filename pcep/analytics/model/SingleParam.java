package pcep.analytics.model;

public class SingleParam extends ModelParam {
	private String name;
	private double value;
	private boolean used;
	
	public SingleParam(String name, double value) {
		this.name = name;
		this.value = value;
		this.used = false;
	}

	@Override
	public boolean hasNext() {
		return !used;
	}

	@Override
	public Double next() {
		Double v = used ? null : value;
		used = true;
		return v;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void reset() {
		this.used = false;
	}

	@Override
	public double[] getValues() {
		return new double[]{value};
	}

}
