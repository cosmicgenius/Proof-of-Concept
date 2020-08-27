package math;

public class Complex {
	private double re; // real part
	private double im; // imaginary part

	/**
	 * Creates a complex number from a real
	 */
	public Complex(double re) {
		this.re = re;
		im = 0;
	}
	
	/**
	 * Creates a new object with the given real and imaginary parts
	 */
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}
	
	/**
	 * Copies the old complex number
	 */
	public Complex(Complex old) {
		this.re = old.re;
		this.im = old.im;
	}

	/**
	 * Returns a string representation of the complex object
	 */
	public String toString() {
		if (im < 0)
			return re + " - " + (-im) + " i";
		return re + " + " + im + " i";
	}

	/**
	 * Returns e^(i theta)
	 */
	public static Complex dir(double theta) {
		return new Complex(Math.cos(theta), Math.sin(theta));
	}

	/**
	 * Returns magnitude.
	 */
	public double mag() {
		return Math.hypot(re, im);
	}

	/**
	 * Returns argument
	 */
	public double arg() {
		return Math.atan2(im, re);
	}

	/**
	 * Adds this and rhs
	 */
	public Complex add(Complex rhs) {
		return new Complex(this.re + rhs.re, this.im + rhs.im);
	}
	
	/**
	 * Subtracts this and rhs
	 */
	public Complex subtract(Complex rhs) {
		return new Complex(this.re - rhs.re, this.im - rhs.im);
	}

	/**
	 * Multiplies this and rhs
	 */
	public Complex multiply(Complex rhs) {
		return new Complex(this.re * rhs.re - this.im * rhs.im, this.re * rhs.im + this.im * rhs.re);
	}
	
	/**
	 * Multiplies this and rhs
	 */
	public Complex multiply(double rhs) {
		return new Complex(this.re * rhs, this.im * rhs);
	}
	
	/**
	 * Returns complex with same direction but with magnitude 1
	 */
	public Complex unit() {
		return this.multiply(1 / this.mag());
	}

	/**
	 * Returns the conjugate.
	 */
	public Complex conjugate() {
		return new Complex(re, -im);
	}

	/**
	 * Returns the real part
	 */
	public double re() {
		return re;
	}

	/**
	 * Returns the imaginary part
	 */
	public double im() {
		return im;
	}
	
	/**
	 * Returns the real part rounded to the nearest integer 
	 * (Useful for drawing)
	 */
	public int x() {
		return (int) Math.round(re);
	}

	/**
	 * Returns the imaginary part rounded to the nearest integer
	 * (Useful for drawing)
	 */
	public int y() {
		return (int) Math.round(im);
	}

	/**
	 * Changes the real part
	 */
	public void setRe(int re) {
		this.re = re;
	}

	/**
	 * Changes the imaginary part
	 */
	public void setIm(int im) {
		this.im = im;
	}

	/**
	 * Returns the exponential
	 */
	public Complex exp() {
		return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
	}
	
	/**
	 * Returns the principal complex logarithm
	 */
	public Complex log() {
		return new Complex(Math.log(this.mag()), this.arg());
	}

	/**
	 * Returns if this = rhs
	 */
	public boolean equals(Complex rhs) {
		return (this.re == rhs.re) && (this.im == rhs.im);
	}

}
