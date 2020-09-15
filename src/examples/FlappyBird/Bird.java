package examples.FlappyBird;

import NeuralNetwork.NeuralNetwork;

public class Bird {
    NeuralNetwork network;
    int x;
    float y;
    float velocity;
    boolean isAlive;

    public Bird(int x, int y, NeuralNetwork network) {
        this.x = x;
        this.y = y;
        this.network = network;

        this.velocity = 0;
        this.isAlive = true;
    }
    
}