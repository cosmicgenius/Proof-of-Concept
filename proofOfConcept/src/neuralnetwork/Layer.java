package neuralnetwork;

import math.Matrix;
import math.MatrixException;

public class Layer {
	public static double epsilon = 0.00001D, step = 0.1D;

	public Matrix weight, bias, dw, db;

	public Layer(int st, int en) throws MatrixException {
		weight = new Matrix(st, en);
		bias = new Matrix(1, en);
		dw = new Matrix(st, en);
		db = new Matrix(1, en);
		for (int i = 0; i < en; i++) {
			for (int j = 0; j < st; j++)
				weight.setElement(j, i, Network.rand.nextGaussian());
			bias.setElement(0, i, Network.rand.nextGaussian());
		}
	}

	public void findPartial(double oCost) throws MatrixException {
		for (int i = 0; i < weight.getLength(); i++) {
			for (int j = 0; j < weight.getWidth(); j++) {
				weight.setElement(i, j, weight.getElement(i, j) + epsilon);
				dw.setElement(i, j, step * (oCost - Network.cost()) / epsilon);
				weight.setElement(i, j, weight.getElement(i, j) - epsilon);
			}
		}
		for (int i = 0; i < bias.getWidth(); i++) {
			bias.setElement(0, i, bias.getElement(0, i) + epsilon);
			db.setElement(0, i, step * (oCost - Network.cost()) / epsilon);
			bias.setElement(0, i, bias.getElement(0, i) - epsilon);
		}
	}
	
	public void move() throws MatrixException {
//		System.out.println(dw.toString());
//		System.out.println(db.toString());
		weight = weight.add(dw);
		bias = bias.add(db);
	}
}
