package neuralnetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import main.App;
import main.Iostream;
import main.Util;
import math.Matrix;
import math.MatrixException;

public class Diagram {

	static Matrix[][] colorMap = new Matrix[600][600];
	static ArrayList<Dot> dots = new ArrayList<>();

	public static void main(String args[]) throws IOException, MatrixException {
		App.construct(700, 600, "Neural Network");

		boolean dotType = false;
		boolean started = false;

		Iostream io = new Iostream();
		io.ifStream("trainingdata.txt");
		
		App.addColor("lightblue", 110, 190, 255);
		App.addColor("lightred", 255, 130, 130);

		String next = io.fReadLine();
		while (next != null) {
			StringTokenizer st = new StringTokenizer(next);

			dots.add(new Dot(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
					Boolean.parseBoolean((st.nextToken()))));

			next = io.fReadLine();
		}
		io.fclose();

		Util.reset();

		Network.start();
		Network.reDraw();

		while (true) {
			Util.tick();

			io.ofStream("trainingdata.txt", false);
			
			App.color("gray");
			App.fillRect(650, 300, 100, 600);

			if (Util.click()) {
				if (Util.checkRectangles(300, 300, 600, 600, App.mouseX(), App.mouseY(), 0, 0, true, true)) {
					dots.add(new Dot(App.mouseX(), App.mouseY(), dotType));
					io.fprintln(App.mouseX() + " " + App.mouseY() + " " + dotType);
				}
			}
			if (Util.button(650, 80, 80, 30, 5, dotType ? "blue" : "red", "white", -28, 7, 20, "Switch", "white", "black")) {
				dotType = !dotType;
			}
			if (Util.button(650, 40, 80, 30, 5, "blue", "white", -20, 7, 20, "Start", "white", "black")) {
				started = true;
			}
			
			if (started) {
				Network.move();
				Network.reDraw();
			}
			
			for (int i = 0; i < 600; i++) {
				int last = 0;
				for (int j = 1; j < 600; j++) {
					if (!colorMap[i][j].equals(colorMap[i][j - 1])) {
						color(colorMap[i][j].getElement(0, 0), colorMap[i][j].getElement(0, 1));
						App.line(i, last, i, j - 1);
						last = j;
					}
				}
				color(colorMap[i][599].getElement(0, 0), colorMap[i][599].getElement(0, 1));
				App.line(i, last, i, 599);
			}
			for (Dot d : dots) {
				d.draw();
			}
			
			App.color("white");
			App.print(Double.toString(Network.oCost), 10, 25);
			
			Util.reset();
			App.frame();
			io.fclose();
		}

	}
	public static void color(double blue, double red) {
		App.color((int) (255 - 145 * blue), (int) ((255 - 65 * blue) * (255 - 125 * red) / 255), (int) (255 - 125 * red));
	}
}
