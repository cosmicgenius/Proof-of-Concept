package boid;

import main.App;
import math.Complex;

public class Obstacle {

	Complex pos;
	
	public Obstacle(int x, int y) {
		pos = new Complex(x, y);
	}

	public void draw() {
		App.color("red");
		App.fillCircle((int) pos.re(), (int) pos.im(), 5);
	}
}
