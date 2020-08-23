package NeuralNetwork;

public class Neuron {
    private double weights[];

    public Neuron(int weights) {
        this.weights = new double[weights];

        for (int i = 0; i < weights; i++) {
            this.weights[i] = (Math.random()*2)-1;
        }
    }

    public int getWeightsAmount() {
        return weights.length;
    }

    public double getWeight(int neuron) {
        return weights[neuron];
    }

    public void setWeight(int neuron, double weight) {
        weights[neuron] = weight;
    }
}
