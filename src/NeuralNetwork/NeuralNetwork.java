package NeuralNetwork;

import java.util.Arrays;

import com.google.gson.Gson;

public class NeuralNetwork {
    private Neuron[][] neurons;

    private double learningRate = 0.1;
    private IActivationFunction activationFunction;

    /**
     * @param rate Learning rate
     */
    public void setLearningRate(double rate) {
        learningRate = rate;
    }

    /**
     * @param activationFunction The IActivationFunction interface
     */
    public void setActivationFunction(IActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    /**
     * @param layers The amount of neurons on each layer
     */
    public NeuralNetwork(int... layers) {
        neurons = new Neuron[layers.length][layers[0]];
        for (int i = 0; i < layers.length; i++) {
            neurons[i] = new Neuron[layers[i]+1];
            for (int j = 0; j < layers[i]+1; j++) {
                if(i < layers.length-1) 
                    neurons[i][j] = new Neuron(layers[i+1]);
                else 
                    if(j != layers[i])
                        neurons[i][j] = new Neuron(0);   
            }
        }

        activationFunction = new IActivationFunction() {
            public double activation(double x) {
                return 1/(1 + Math.pow(Math.E, -x));
            }
            public double derivative(double x) {
                double f = activation(x);
                return f * (1 - f);
            }       
        };
    }

    public NeuralNetwork copy() {
        return deserialize(serialize());
    }

    public static NeuralNetwork deserialize(String json) {
        NeuralNetwork network = new Gson().fromJson(json, NeuralNetwork.class);
        network.setActivationFunction(new IActivationFunction(){
            public double activation(double x) {
                return 1/(1 + Math.pow(Math.E, -x));
            }
            public double derivative(double x) {
                double f = activation(x);
                return f * (1 - f);
            }   
        });
        return network;
    }

    public static String serialize(NeuralNetwork network) {
        return new Gson().toJson(network);
    }

    public String serialize() {
        return serialize(this);
    }

    /**
     * @param inputs Inputs array
     * @return Outputs array
     */
    public double[] predict(double inputs[]) {
        return predictCorrected(addBias(inputs));
    }

    private double[] predictCorrected(double inputs[]) {
        if(inputs.length == neurons[0].length) {
            double answers[] = inputs;

            for (int i = 0; i < neurons.length-1; i++) {
                answers = calculateOutputs(neurons[i], answers, neurons[i+1].length-1);
                answers = activateOutputs(answers);
                if(i != neurons.length-2)
                    answers = addBias(answers);
            }

            return answers;
        }
        else
            return new double[neurons[neurons.length-1].length];
    }

    private double[] calculateOutputs(Neuron[] inputNeurons, double[] inputNumbers, int outputsAmount) {
        double answers[] = new double[outputsAmount];
        for (int i = 0; i < inputNumbers.length; i++) {
            for (int j = 0; j < outputsAmount; j++) {
                answers[j] += inputNumbers[i] * inputNeurons[i].getWeight(j);
            }
        }
        return answers;
    }

    private double[] activateOutputs(double[] outputs) {
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = activationFunction(outputs[i]);
        }
        return outputs;
    }

    private double[] addBias(double[] neurons) {
        double bias[] = Arrays.copyOf(neurons, neurons.length+1);
        bias[neurons.length] = 1;
        return bias;
    }

    /**
     * @param input Inputs array
     * @param output Outputs that you want to get from the neural network
     */
    public void train(double input[], double output[]) {
        double networkOutput[] = predict(input);

        double errors[][] = perform(output, networkOutput);

        Neuron tempNeurons[][] = neurons;
        input = addBias(input);
        double answers[] = calculateOutputs(neurons[0], input, neurons[1].length-1);

        for (int i = 0; i < neurons[0].length; i++) {
            for (int j = 0; j < neurons[0][i].getWeightsAmount(); j++) {
                double correctedWeight = neurons[0][i].getWeight(j) + learningRate * errors[1][j] * derivativeFunction(answers[j]) * input[i];
                tempNeurons[0][i].setWeight(j, correctedWeight); 
            }
        } 

        for (int i = 1; i < neurons.length-1; i++) {
            answers = activateOutputs(answers);
            double prevAnswers[] = answers;
            answers = calculateOutputs(neurons[i], answers, neurons[i+1].length-1);
            answers = addBias(answers);
            for (int j = 0; j < neurons[i].length-1; j++) {
                for (int k = 0; k < neurons[i][j].getWeightsAmount(); k++) {
                    double correctedWeight = neurons[i][j].getWeight(k) + learningRate * errors[i+1][k] * derivativeFunction(answers[k]) * prevAnswers[j]; 
                    tempNeurons[i][j].setWeight(k, correctedWeight);
                }
            }
        }

        neurons = tempNeurons;
    }

    /**
     * @param targetOutput Expected output
     * @param networkOutput Actual NN output
     * @return Array of neurons' errors
     */
    public double[][] perform(double[] targetOutput, double[] networkOutput) {
        double errors[][] = new double[neurons.length][neurons[neurons.length-1].length];

        for (int i = 0; i < neurons[neurons.length-1].length-1; i++) {
            errors[neurons.length-1][i] = targetOutput[i] - networkOutput[i];
        }
        
        for (int i = neurons.length-2; i > 0; i--) {
            errors[i] = new double[neurons[i].length];
            for (int j = 0; j < neurons[i].length; j++) { 
                for (int k = 0; k < neurons[i][j].getWeightsAmount(); k++) {
                    errors[i][j] += neurons[i][j].getWeight(k) * errors[i+1][k];
                } 
            }
        }

        return errors;
    }
    
    private int intRandom(int min, int max) {
        return (int) (Math.random()*(max-min)+min);
    }

    public double activationFunction(double x) {
        return activationFunction.activation(x);
    }

    public double derivativeFunction(double x) {
        return activationFunction.derivative(x);
    }

    public void mutate(double mutateFactor) {
        int layer = intRandom(0, neurons.length-1);
        int neuron = intRandom(0, neurons[layer].length);
        int weight = intRandom(0, neurons[layer][neuron].getWeightsAmount());
        double mutation = neurons[layer][neuron].getWeight(weight) + ((Math.random()*2)-1)*mutateFactor;
        neurons[layer][neuron].setWeight(weight, mutation);
    }

}
