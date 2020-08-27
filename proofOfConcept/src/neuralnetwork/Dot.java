package neuralnetwork;

import main.App;
import math.Complex;

public class Dot {

	Complex pos;
	boolean type; // true = blue, false = red
	
	public Dot(int x, int y, boolean type) {
		pos = new Complex(x, y);
		this.type = type;
	}
	
	public void draw() {
		App.color(type ? "blue" : "red");
		App.fillCircle(pos.x(), pos.y(), 5);
	}
}
