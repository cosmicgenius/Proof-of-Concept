package math;

public class Matrix {

	/**
	 * The internal matrix
	 */
	private double[][] matrix;

	/**
	 * Creates a matrix filled with a certain value
	 */
	public Matrix(double fill, int length, int width) throws MatrixException {
		if (length == 0 || width == 0)
			throw new MatrixException("Cannot have size 0.");

		matrix = new double[length][width];

		for (int i = 0; i < length; i++)
			for (int j = 0; j < width; j++)
				this.setElement(i, j, fill);
	}

	/**
	 * Creates a blank matrix (all zeroes)
	 */
	public Matrix(int length, int width) throws MatrixException {
		if (length == 0 || width == 0)
			throw new MatrixException("Cannot have size 0.");

		matrix = new double[length][width];
	}

	/**
	 * Copies the matrix
	 */
	public Matrix(Matrix old) throws MatrixException {
		matrix = new double[old.getLength()][old.getWidth()];

		for (int i = 0; i < old.getLength(); i++)
			for (int j = 0; j < old.getWidth(); j++)
				this.setElement(i, j, old.getElement(i, j));
	}

	/**
	 * Creates a matrix filled with a certain value with the same size as a certain
	 * matrix
	 */
	public Matrix(double fill, Matrix old) throws MatrixException {
		matrix = new double[old.getLength()][old.getWidth()];

		for (int i = 0; i < old.getLength(); i++)
			for (int j = 0; j < old.getWidth(); j++)
				this.setElement(i, j, fill);
	}

	/**
	 * Returns the identity matrix of a certain size
	 */
	public static Matrix identity(int size) throws MatrixException {
		Matrix result = new Matrix(size, size);

		for (int i = 0; i < size; i++)
			result.setElement(i, i, 1);

		return result;
	}
	
	/**
	 * Returns a string representation of the matrix
	 */
	public String toString() {
		String str = "";
		
		for (int i = 0; i < this.getLength(); i++) {
			for (int j = 0; j < this.getWidth(); j++)
				str += this.getElement(i, j) + " ";
			str += System.getProperty("line.separator");
		}
		return str;
		
	}

	/**
	 * Returns this + rhs
	 */
	public Matrix plus(Matrix rhs) throws MatrixException {
		if (!(this.getLength() == rhs.getLength() && this.getWidth() == rhs.getWidth()))
			throw new MatrixException("Summands are different sizes.");

		Matrix sum = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				sum.setElement(i, j, this.getElement(i, j) + rhs.getElement(i, j));

		return sum;
	}

	/**
	 * Returns the element-wise product of this and rhs
	 */
	public Matrix elementwiseTimes(Matrix rhs) throws MatrixException {
		if (!(this.getLength() == rhs.getLength() && this.getWidth() == rhs.getWidth()))
			throw new MatrixException("Factors are different sizes.");

		Matrix prod = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				prod.setElement(i, j, this.getElement(i, j) * rhs.getElement(i, j));

		return prod;
	}

	/**
	 * Returns this + the constant c
	 * 
	 * @throws MatrixException
	 */
	public Matrix plus(double c) throws MatrixException {
		return this.plus(new Matrix(c, this));
	}

	/**
	 * Returns the product of this and the constant c
	 */
	public Matrix times(double c) throws MatrixException {
		return this.times(new Matrix(c, this));
	}

	/**
	 * Returns the product of this and rhs
	 */
	public Matrix times(Matrix rhs) throws MatrixException {
		if (!(this.getWidth() == rhs.getLength()))
			throw new MatrixException("Cannot multiply matrices if LHS has different width than RHS's length.");

		Matrix result = new Matrix(this.getLength(), rhs.getWidth());

		for (int i = 0; i < this.getLength(); i++) {
			for (int j = 0; j < rhs.getWidth(); j++) {
				double c = 0;
				for (int k = 0; k < this.getWidth(); k++) {
					c += this.getElement(i, k) * rhs.getElement(k, j);
				}
				result.setElement(i, j, c);
			}
		}
		return result;
	}

	/**
	 * Returns the element-wise logarithm
	 */
	public Matrix log() throws MatrixException {
		Matrix result = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				result.setElement(i, j, Math.log(this.getElement(i, j)));

		return result;
	}

	/**
	 * Returns the element-wise exponential
	 */
	public Matrix exp() throws MatrixException {
		Matrix result = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				result.setElement(i, j, Math.exp(this.getElement(i, j)));

		return result;
	}

	/**
	 * Returns the element-wise negation
	 */
	public Matrix neg() throws MatrixException {
		return this.times(-1);
	}

	/**
	 * Returns the element-wise inverse
	 */
	public Matrix elementwiseInv() throws MatrixException {
		Matrix result = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				result.setElement(i, j, 1 / this.getElement(i, j));

		return result;
	}
	/**
	 * Returns the transpose
	 */
	public Matrix transpose() throws MatrixException {
		Matrix result = new Matrix(this.getWidth(), this.getLength());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				result.setElement(j, i, this.getElement(i, j));

		return result;
	}
	
//	/**
//	 * Returns the determinant
//	 */
//	public Matrix det() {
//		
//	}

	/**
	 * Returns the length
	 */
	public int getLength() {
		return matrix.length;
	}

	/**
	 * Returns the width
	 */
	public int getWidth() {
		if (this.getLength() == 0)
			return 0;
		return matrix[0].length;
	}

	/**
	 * Sets an element to a given value
	 */
	public void setElement(int i, int j, double x) {
		matrix[i][j] = x;
	}

	/**
	 * Returns an element
	 */
	public double getElement(int i, int j) {
		return matrix[i][j];
	}
}