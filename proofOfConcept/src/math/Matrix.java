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
		if (length == 0)
			throw new MatrixException("Cannot have a matrix with length 0.");
		if (width == 0)
			throw new MatrixException("Cannot have a matrix with width 0.");

		matrix = new double[length][width];

		for (int i = 0; i < length; i++)
			for (int j = 0; j < width; j++)
				this.setElement(i, j, fill);
	}

	/**
	 * Creates a blank matrix (all zeroes)
	 */
	public Matrix(int length, int width) throws MatrixException {
		if (length == 0)
			throw new MatrixException("Cannot have a matrix with length 0.");
		if (width == 0)
			throw new MatrixException("Cannot have a matrix with width 0.");

		matrix = new double[length][width];
	}
	
	/**
	 * Creates a new matrix given a normal java matrix (2d array)
	 */
	public Matrix(double[][] matrix) throws MatrixException {
		this.matrix = matrix;
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
	public Matrix add(Matrix rhs) throws MatrixException {
		if (this.getLength() != rhs.getLength())
			throw new MatrixException("Summands have different lengths (" + this.getLength() + " and " + rhs.getLength() + ").");
		if (this.getWidth() != rhs.getWidth())
			throw new MatrixException("Summands have different widths (" + this.getWidth() + " and " + rhs.getWidth() + ").");

		Matrix sum = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				sum.setElement(i, j, this.getElement(i, j) + rhs.getElement(i, j));

		return sum;
	}

	/**
	 * Returns the element-wise product of this and rhs
	 */
	public Matrix elementwiseMultiply(Matrix rhs) throws MatrixException {
		if (this.getLength() != rhs.getLength())
			throw new MatrixException("Factors have different lengths (" + this.getLength() + " and " + rhs.getLength() + ").");
		if (this.getWidth() != rhs.getWidth())
			throw new MatrixException("Factors have different widths (" + this.getWidth() + " and " + rhs.getWidth() + ").");

		Matrix prod = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				prod.setElement(i, j, this.getElement(i, j) * rhs.getElement(i, j));

		return prod;
	}
	
	/**
	 * Returns the matrix stacked on top of itself rep times
	 * 
	 * @throws MatrixException
	 */
	public Matrix stack(int rep) throws MatrixException {
		Matrix result = new Matrix(this.getLength() * rep, this.getWidth());

		for (int i = 0; i < rep; i++)
			for (int j = 0; j < this.getLength(); j++)
				for (int k = 0; k < this.getWidth(); k++)
					result.setElement(i * this.getLength() + j, k, this.getElement(j, k));

		return result;
	}

	/**
	 * Returns this + the constant c
	 * 
	 * @throws MatrixException
	 */
	public Matrix add(double c) throws MatrixException {
		return this.add(new Matrix(c, this));
	}

	/**
	 * Returns the product of this and the constant c
	 */
	public Matrix multiply(double c) throws MatrixException {
		return this.elementwiseMultiply(new Matrix(c, this));
	}

	/**
	 * Returns the product of this and rhs
	 */
	public Matrix multiply(Matrix rhs) throws MatrixException {
		if (this.getWidth() != rhs.getLength())
			throw new MatrixException("Cannot multiply matrices if LHS's width is different from RHS's length (" + this.getWidth() + " and " + rhs.getLength() + ").");

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
		return this.multiply(-1);
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
		Matrix result = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				result.setElement(j, i, this.getElement(i, j));

		return result;
	}
	
	/**
	 * Returns sum of all elements
	 */
	public double sum() throws MatrixException {
		double sum = 0;
		
		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				sum += this.getElement(i, j);

		return sum;
	}
	
	/**
	 * Applies a function elementwise
	 */
	public Matrix applyElementWise(Operation op) throws MatrixException {
		Matrix result = new Matrix(this.getLength(), this.getWidth());

		for (int i = 0; i < this.getLength(); i++)
			for (int j = 0; j < this.getWidth(); j++)
				result.setElement(i, j, op.apply(this.getElement(i, j)));

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