package neuralNetwork;

public class Stage {
	
	public static final double signalMultiplier = .1;
	
	public Stage prev;
	public double output[];
	public byte coeffs[][];
	
	public Stage(Stage prev, int size){
		this.prev = prev;
		output = new double[size];
		if (prev != null)
			coeffs = new byte[size][prev.output.length+1];
		else
			coeffs = new byte[0][0];
	}
	/**
	 * calculates the outputs based on the input values
	 */
	public void calc(){
		if (prev == null) return;
		for (int i = 0; i < coeffs.length; i++){
			double sum = 0;
			for (int j = 0; j < coeffs[0].length-1; j++){
				sum += coeffs[i][j]*prev.output[j];
			}
			sum += coeffs[i][coeffs[0].length-1]*signalMultiplier;  //constant bias
			output[i] = sigmoid(sum);
		}
	}
	public static double sigmoid(double x) {
		return signalMultiplier/(1+Math.exp(-x/2d));  //range: 0 .. multiplier
	}
	public String toString(){
		String k = "[";
		for (int i = 0; i< coeffs.length; i++){
			k += "[";
			for (int j = 0; j < coeffs[0].length; j++){
				k += Byte.toString(coeffs[i][j])+" ";
			}
			k += "]\n ";
		}
		k+= "]\n";
		return k;
	}

}
