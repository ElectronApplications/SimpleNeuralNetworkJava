package NeuralNetwork;

public interface IActivationFunction {
    public double activation(double x);
    public double derivative(double x);
}
