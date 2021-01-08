# Simple neural network written in Java
This is my implementation of a feedforward neural network I wrote in Java to learn more about machine learning and how neural networks work.
Backpropagation algorithm is used for training and the default activation function is logistic(sigmoid) function.

# Usage
Create a new neural network object
```java
//Neural network with 2 input and 3 output neurons
NeuralNetwork neuralNetwork = new NeuralNetwork(2, 3);

//Neural network with 4 input, 6 hidden and 2 output neurons
NeuralNetwork neuralNetwork = new NeuralNetwork(4, 6, 2);

//Neural network with 16 input neurons, 2 layers of hidden neurons and 10 output neurons
NeuralNetwork neuralNetwork = new NeuralNetwork(16, 16, 8, 10);
```

You can set a learning rate which is used for training
```java
neuralNetwork.setLearningRate(0.01);
```

You can change the activation function
```java
//ReLU
neuralNetwork.setActivationFunction(new IActivationFunction(){
    public double activation(double x) {
        return x < 0 ? 0 : x;
    }
    public double derivative(double y) {
        return y < 0 ? 0 : 1;
    }
});
```

Neural network can be serialized, deserialized and copied
```java
String json = neuralNetwork.serialize();
//or
String json = NeuralNetwork.serialize(neuralNetwork);

NeuralNetwork neuralNetworkDeserialized = NeuralNetwork.deserialize(json);
NeuralNetwork neuralNetworkCopied = neuralNetwork.copy();
```

Feedforward data
```java
double[] outputs = neuralNetwork.predict(inputs); //An array of double values is used as input
```

Train neural network
```java
neuralNetwork.train(inputs, outputs); //Arrays of double values are used as inputs and outputs
```
