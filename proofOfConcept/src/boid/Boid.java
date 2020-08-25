package boid;

import main.App;
import math.Complex;

public class Boid {

	static double sight = 50, maxSpeed = 2;
	static double sepWeight = 0.01, avdWeight = 0.1, alnWeight = 0.01, cohWeight = 0.025, rndWeight = 0.001;
	
	boolean special;
	
	Complex pos, dp;
	
	public Boid(int x, int y, boolean special) {
		pos = new Complex(x, y);
		dp = new Complex(2 * Math.random() - 1, 2 * Math.random() - 1);
		
		this.special = special;
	}

	public void accelerate() {
		Complex sep = new Complex(0), avd = new Complex(0), aln = new Complex(0), coh = new Complex(0);
		Complex nearbyCom = new Complex(0);
		float tot = 0;
		for (Boid b : Draw.boids) {
			if (b != this) {
				Complex d = this.pos.subtract(b.pos);
				if (d.mag() < sight) {
					sep = sep.add(d.multiply(1 / d.mag())); // separation from other boids
					aln = aln.add(b.dp); // alignment
					nearbyCom = nearbyCom.add(b.pos); // com calculation for cohesion
					tot++;
				}
			}
		}
		for (Obstacle o : Draw.obstacles) {
			Complex d = this.pos.subtract(o.pos);
			if (d.mag() < sight) {
				avd = avd.add(d.multiply(1 / d.mag())); // separation from obstacles
			}
		}
		if (tot > 0) {
			coh = coh.add(nearbyCom.multiply(1 / tot).subtract(pos).unit()); // cohesion
		}
		Complex ddp = (new Complex(2 * Math.random() - 1, 2 * Math.random() - 1)).multiply(rndWeight); // randomness
		ddp = ddp.add(sep.multiply(sepWeight)).add(avd.multiply(avdWeight)).add(aln.multiply(alnWeight)).add(coh.multiply(cohWeight));
		
		dp = dp.add(ddp);
		
		if (dp.mag() > maxSpeed) {
			dp = dp.multiply(maxSpeed / dp.mag());
		}
	}
	
	public void move() {	
		pos = pos.add(dp);

		if (pos.re() > App.width()) {
			pos = pos.add(new Complex(-App.width(), 0));
		} else if (pos.re() < 0) {
			pos = pos.add(new Complex(App.width(), 0));
		}
		if (pos.im() > App.height()) {
			pos = pos.add(new Complex(0, -App.height()));
		} else if (pos.im() < 0) {
			pos = pos.add(new Complex(0, App.height()));
		}
	}

	public void draw() {
		double dir = dp.arg();
		
		App.color(special ? "red" : "blue");
		App.fillPolygon((int) (pos.re() + 15 * Math.cos(dir)), (int) (pos.im() + 15 * Math.sin(dir)), 
						(int) (pos.re() + 5 * Math.cos(dir + 3 * Math.PI / 7)), (int) (pos.im() + 5 * Math.sin(dir + 3 * Math.PI / 7)), 
						(int) (pos.re() + 5 * Math.cos(dir - 3 * Math.PI / 7)), (int) (pos.im() + 5 * Math.sin(dir - 3 * Math.PI / 7)));
	}

}
