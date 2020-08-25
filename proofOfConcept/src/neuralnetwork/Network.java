package neuralnetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Network {

	public static void main(String args[]) throws IOException {

		double[] cur = new double[8];
		double[] correct = new double[1];
		ArrayList<Layer> layers = new ArrayList<>();

		layers.add(new Layer(8, 6));
		layers.add(new Layer(6, 5));
		layers.add(new Layer(5, 1));

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Training Data");

		for (int j = 0; j < 30000; j++) {
			int c = (int) Math.floor(Math.random() * 256);
			int change = 0, prev = 0;
			for (int i = 0; i < 8; i++) {
				if (((c & (1 << i)) >> i) != prev)
					change++;
				prev = ((c & (1 << i)) >> i);
			}
			
			if ((c & (1 << 7)) > 0) change++;
			
			correct[0] = 0;
			if (change == 2)
				correct[0] = 1;
			
			for (int i = 0; i < 8; i++) {
				cur[i] = ((c & (1 << i)) >> i);
			}			
			move(cur, layers, correct);
		}
		System.out.println("Tests");
		while (true) {
			String ln = br.readLine();

			if (ln == "")
				break;

			StringTokenizer st = new StringTokenizer(ln);

			for (int i = 0; i < 8; i++)
				cur[i] = Integer.parseInt(st.nextToken());

			double[] ans = Arrays.copyOf(cur, cur.length);
			for (Layer l : layers)
				ans = l.move(ans);

			for (double a : ans) {
				System.out.print(a + " ");
			}
			System.out.println();
		}
	}

	private static void move(double cur[], ArrayList<Layer> layers, double correct[]) {
		double[] c2 = Arrays.copyOf(cur, cur.length);
		for (Layer l : layers)
			c2 = l.move(c2);

//        for (int i = 0; i < c2.length; i++)
//            System.out.print(c2[i] + " ");
//        System.out.println(cost(cur, layers, correct));

		ArrayList<Double> delta = new ArrayList<>();

		double init = cost(cur, layers, correct);
		for (int i = 0; i < layers.size(); i++) {
			double cost = 0;
			for (int j = 0; j < layers.get(i).st(); j++) {
				for (int k = 0; k < layers.get(i).en(); k++) {
					layers.get(i).weightAlter(0.0001D, j, k);
					cost = cost(cur, layers, correct);
					layers.get(i).weightAlter(-0.0001D, j, k);
					delta.add((init - cost) * 10000D);
					// System.out.println(init + " " + cost + " " + (init -
					// cost) * 10000D);
				}
			}

			for (int j = 0; j < layers.get(i).en(); j++) {
				layers.get(i).biasAlter(0.0001D, j);
				cost = cost(cur, layers, correct);
				layers.get(i).biasAlter(-0.0001D, j);
				delta.add((init - cost) * 10000D);
				// System.out.println(init + " " + cost + " " + (init - cost) *
				// 10000D);
			}
//            System.out.println();
		}

		int ind = 0;
		for (int i = 0; i < layers.size(); i++) {
			for (int j = 0; j < layers.get(i).st(); j++) {
				for (int k = 0; k < layers.get(i).en(); k++) {
					layers.get(i).weightAlter(delta.get(ind++), j, k);
				}
			}

			for (int j = 0; j < layers.get(i).en(); j++) {
				layers.get(i).biasAlter(delta.get(ind++), j);
			}
		}
	}

	private static double cost(double cur[], ArrayList<Layer> layers, double correct[]) {
		double[] ans = Arrays.copyOf(cur, cur.length);
		for (Layer l : layers)
			ans = l.move(ans);

		double cost = 0D;
		for (int i = 0; i < ans.length; i++)
			cost += (ans[i] - correct[i]) * (ans[i] - correct[i]);

		return cost;
	}

}
