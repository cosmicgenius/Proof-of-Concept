package math;

public class Test {

	public static void main(String args[]) throws MatrixException {
		
		double[][] m1 = {{1,3,5},
						 {-1,5,0},
						 {8,1,5}}; 
		
		double[][] m2 = {{0,9,1},
						 {4,9,10},
						 {-10,9,3}};
		
		Matrix matrix1 = new Matrix(m1);
		Matrix matrix2 = new Matrix(m2);

		System.out.println(matrix1.multiply(matrix2).toString());
		
		Matrix what = new Matrix(1, 1);
		what.setElement(0, 0, 10);
		System.out.println(what.toString() + what.applyElementWise((x) -> 1 / (1 + Math.exp(-x))).toString());
		
		System.out.println(matrix2.stack(3));
	}
	
}
