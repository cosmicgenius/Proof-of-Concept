package neuralnetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import math.Matrix;
import math.MatrixException;

public class Network {

	static Matrix cur, correct;
	static ArrayList<Layer> layers = new ArrayList<>();
	
	static Random rand = new Random();
	static double oCost;

	public static void start() throws IOException, MatrixException {

		System.out.println("start");
				
		layers.add(new Layer(2, 4));
		layers.add(new Layer(4, 2));
						
		for (Layer l : layers) {
			System.out.println("w\n" + l.weight.toString());
			System.out.println("b " + l.bias.toString());
		}
	}
	
	public static void reDraw() throws MatrixException {
		
		cur = new Matrix(1, 2);
		for (int i = 0; i < 600; i++) {
			cur.setElement(0, 0, ((double) i) / 600);
			for (int j = 0; j < 600; j++) {
				cur.setElement(0, 1, ((double) j) / 600);
				
				Matrix result = reduce(cur);
				Diagram.colorMap[i][j] = result;
			}
		}
	}

	public static void move() throws MatrixException {			
		cur = new Matrix(Diagram.dots.size(), 2);
		correct = new Matrix(Diagram.dots.size(), 2);
		
		for (int i = 0; i < Diagram.dots.size(); i++) {
			cur.setElement(i, 0, Diagram.dots.get(i).pos.re() / 600);
			cur.setElement(i, 1, Diagram.dots.get(i).pos.im() / 600);
			correct.setElement(i, Diagram.dots.get(i).type ? 0 : 1, 1);
		}
		
		oCost = cost();
								
		for (Layer l : layers) {
			l.findPartial(oCost);
		}
		for (Layer l : layers) {
			l.move();
		}
	}

	public static double cost() throws MatrixException {
		return reduce(cur).add(correct.neg()).applyElementWise((x) -> x * x).sum();
	}
	
	public static Matrix reduce(Matrix cur) throws MatrixException {
		Matrix copy = new Matrix(cur);
		for (Layer l : layers) {
			copy = copy.multiply(l.weight).add(l.bias.stack(copy.getLength())).applyElementWise((x) -> 1 / (1 + Math.exp(-x)));
		}
		return copy;
	}

}

/*
 0 1 0
 0 1 0
 0 1 0
 */
