package NeuralNetwork;

public class Neuron {
    private double weights[];

    /** 
     * @param weights Amount of connections this neuron has with neurons on the next layer
     */
    public Neuron(int weights) {
        this.weights = new double[weights];

        for (int i = 0; i < weights; i++) {
            this.weights[i] = (Math.random()*2)-1;
        }
    }

    /**
     * @return Returns the amount of connections with the neurons on the next layer
     */
    public int getWeightsAmount() {
        return weights.length;
    }

    /**
     * @param neuron The index of the connection
     * @return Returns the weight of the connection
     */
    public double getWeight(int neuron) {
        return weights[neuron];
    }

    /**
     * @param neuron The index of the connection
     * @param weight Weight to set
     */
    public void setWeight(int neuron, double weight) {
        weights[neuron] = weight;
    }
}
