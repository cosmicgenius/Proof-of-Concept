package neuralnetwork;

import java.util.Arrays;

public class Layer {
    
    private double[][] weight;
    private double[] bias;
    
    public Layer(int st, int en) {
        weight = new double[st][en];
        bias = new double[en];
        
        for (int i = 0; i < en; i++) {
            for (int j = 0; j < st; j++) 
                weight[j][i] = Math.random();
            bias[i] = Math.random();
        }
    }
    
    double[] move(double[] initial) {
        double[] ans = Arrays.copyOf(bias, bias.length);
        for (int i = 0; i < weight.length; i++) {
            for (int j = 0; j < bias.length; j++) {
                ans[j] += weight[i][j] * initial[i];
            }
        }
        for (int i = 0; i < bias.length; i++) {
            ans[i] = 1 / (1 + Math.exp(ans[i]));
        }
        return ans;
    }
    
    void weightAlter(double d, int i, int j) {
        weight[i][j] += d;
    }
    
    void biasAlter(double d, int i) {
        bias[i] += d;
    }
    
    int st() {
        return weight.length;
    }
    
    int en() {
        return bias.length;
    }
    
}
