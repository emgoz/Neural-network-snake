package neuralNetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class NeuralNet {
	public Stage stages[];

	/**
	 * C'tor
	 * 
	 * @param stageSizes
	 *            Array specifying the sizes of each layer, for example
	 *            {48,16,16,2}.
	 *            First stage has no nonlinearity
	 */
	public NeuralNet(int stageSizes[]) {
		stages = new Stage[stageSizes.length];
		Stage prev = null;
		for (int i = 0; i < stageSizes.length; i++) {
			stages[i] = new Stage(prev, stageSizes[i]);
			prev = stages[i];
		}
	}
	
	/**
	 * Loads the weights / coefficients from the linear array sequentially into the network
	 * @param coeffs	array with the coefficients ranging -128 to +127.
	 * Note: no dimension/length check is done, will crash when given wrong sized array! 
	 */
	public void loadCoeffs(byte coeffs[]) {
		int idx = 0;
		for (int s = 1; s < stages.length; s++) {
			for (int i = 0; i < stages[s].coeffs.length; i++) {
				for (int j = 0; j < stages[s].coeffs[0].length; j++) {
					stages[s].coeffs[i][j] = coeffs[idx++];
				}
			}
		}
	}
	
	/**
	 * Same like loadCoeffs(), but will fill the network symmetrically
	 * Only use when all stage sizes are even
	 * @param coeffs array with the coefficients ranging -128 to +127.
	 */

	public void loadCoeffsSymmetrical(byte coeffs[]) {
		int idx = 0;
		for (int s = 1; s < stages.length; s++) {
			if (stages[s].coeffs.length % 2 == 1) {
				System.err.println("Symmetrical Net without even sized stages. Bad.");
				return;
			}
			for (int i = 0; i < (stages[s].coeffs.length) / 2; i++) {
				for (int j = 0; j < stages[s].coeffs[0].length; j++) {
					stages[s].coeffs[i][j] = coeffs[idx];
					stages[s].coeffs[stages[s].coeffs.length - 1 - i][stages[s].coeffs[0].length - 1 - j] = coeffs[idx++];
				}
			}
		}
	}
	
	/**
	 * Calculates the output of the network for the given input
	 * 
	 * @param input		input vector (first stage values)
	 * @return			output vector
	 */

	public double[] calc(double input[]) {
		for (int i = 0; i < input.length; i++) {
			stages[0].output[i] = input[i];
		}
		for (int i = 1; i < stages.length; i++) {
			stages[i].calc();
		}
		return stages[stages.length - 1].output;
	}
	/**
	 * Calculates the need number of coefficients for given Neural Net architecture
	 * Used for DNA-length 
	 * 
	 * @param stageSizes	Array specifying the sizes of each layer, for example
	 *           			 {48,16,16,2}, same like in c'tor
	 * @param symmetrical	whether the network should be symmetrical or asymmetrical
	 * @return				number of coefficients needed
	 */

	public static int calcNumberOfCoeffs(int stageSizes[], boolean symmetrical) {
		int sum = 0;
		if (stageSizes.length < 2)
			return 0;
		for (int i = 1; i < stageSizes.length; i++) {
			if (symmetrical)
				sum += (stageSizes[i] * (stageSizes[i - 1] + 1) + 1) / 2;
			else
				sum += stageSizes[i] * (stageSizes[i - 1] + 1);
		}

		return sum;
	}

	public String toString() {
		String k = "";
		for (int s = 1; s < stages.length; s++) {
			k += "\nStage " + s + ": \n" + stages[s].toString();
		}
		return k;
	}

	/**
	 * Draws the current state of the network to Graphics
	 * 
	 * @param g		Graphics to draw to
	 * @param alpha	Transparency value, range 0 .. 1
	 * @param w		Width of the screen
	 * @param h		Height of the screen
	 */
	public void display(Graphics g, float alpha, double w, double h) {
		Graphics2D g2 = (Graphics2D) g;
		int d = 20;

		// synapses:
		for (int s = 1; s < stages.length; s++) {
			int x1 = (s) * (int) (w / (stages.length + 1));
			int x2 = (s + 1) * (int) (w / (stages.length + 1));

			for (int i = 0; i < stages[s].coeffs.length; i++) {
				for (int j = 0; j < stages[s].coeffs[0].length - 1; j++) {
					int c = stages[s].coeffs[i][j];
					if (Math.abs(c) < 48)
						continue;
					g2.setStroke(new BasicStroke(Math.abs(c) * 3 / 129));

					int y1 = (j + 1) * (int) (h / (stages[s - 1].output.length + 1));
					int y2 = (i + 1) * (int) (h / (stages[s].output.length + 1));
					float b = (float) (stages[s - 1].output[j] / Stage.signalMultiplier);
					if (c < 0)
						g.setColor(new Color(b, 0, 0));
					else
						g.setColor(new Color(0, b, 0));
					g2.drawLine(x1, y1, x2, y2);
				}
			}
		}

		// neurons:
		for (int s = 0; s < stages.length; s++) {
			int x = (s + 1) * (int) (w / (stages.length + 1));
			d = (int) (h / (stages[s].output.length + 7));
			for (int i = 0; i < stages[s].output.length; i++) {
				int y = (i + 1) * (int) (h / (stages[s].output.length + 1));

				float output = (float) (stages[s].output[i] / Stage.signalMultiplier * .8 + .2);
				g.setColor(new Color(Color.HSBtoRGB(.6f, 1, output)));
				g.fillOval(x - d / 2, y - d / 2, d, d);
			}
		}

	}
}
