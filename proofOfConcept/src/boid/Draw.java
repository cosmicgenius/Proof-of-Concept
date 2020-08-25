package boid;

import java.util.ArrayList;

import main.App;
import main.Util;

public class Draw {

	static ArrayList<Boid> boids = new ArrayList<>();
	static ArrayList<Obstacle> obstacles = new ArrayList<>();
	
	public static void main(String args[]) {
		App.construct(800, 800, "Boids");
		
		for (int i = 50; i <= 800 - 50; i += 50) {
			for (int j = 50; j <= 800 - 50; j += 50) {
				boids.add(new Boid(i, j, false));
			}
		}
		
		Util.reset();
		
		while (true) {
			Util.tick();

			for (Boid b : boids) {
				b.draw();
				b.accelerate();
				b.move();
			}
			
			for (Obstacle o : obstacles) {
				o.draw();
			}
			
			if (Util.click()) {
				obstacles.add(new Obstacle(App.mouseX(), App.mouseY()));
			}
			
			Util.reset();
			App.frame();
		}
	}
}
