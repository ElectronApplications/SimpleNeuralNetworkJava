package examples.Digits;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import NeuralNetwork.NeuralNetwork;

public class Train {
    static NeuralNetwork network;

    public static void main(String[] args) {
        try {
            Scanner networkScanner = new Scanner(new FileReader("./src/examples/Digits/network.json"));
            String networkJson = "";
            while (networkScanner.hasNext()) {
                networkJson += networkScanner.nextLine();
            }
            network = NeuralNetwork.deserialize(networkJson);
        } catch (FileNotFoundException e) {
            network = new NeuralNetwork(28 * 28, 256, 256, 10);
            network.setLearningRate(0.01);
        }

        try {
            Scanner trainDataset = new Scanner(new FileReader("./src/examples/Digits/dataset/mnist_train.csv"));
            Scanner testDataset = new Scanner(new FileReader("./src/examples/Digits/dataset/mnist_test.csv"));

            int line = 0;
            while (trainDataset.hasNext()) {
                line++;
                
                trainNetwork(trainDataset);

                if(line % 500 == 0)
                    testNetwork(testDataset);
                    
                if(line % 2000 == 0)
                    saveNetwork();
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void trainNetwork(Scanner dataset) {
        String[] data = dataset.next().split(",");

        int outputDigit = Integer.parseInt(data[0]);
        double output[] = new double[10];
        output[outputDigit] = 1;

        double[] pixels = new double[28*28];
        for(int i = 1; i <= 28*28; i++) {
            pixels[i-1] = Double.parseDouble(data[i]) / 256;
        }

        network.train(pixels, output);
    }

    public static void testNetwork(Scanner dataset) {
        String[] data = dataset.next().split(",");
        int outputDigit = Integer.parseInt(data[0]);
        double[] output = new double[10];
        output[outputDigit] = 1;

        double[] pixels = new double[28*28];
        for(int i = 1; i <= 28*28; i++) {
            pixels[i-1] = Double.parseDouble(data[i]) / 256;
        }

        double[][] errors = network.perform(output, network.predict(pixels));
        double error = 0;
        for(double i : errors[3])
            error += Math.abs(i);

        System.out.println("Testing Error: " + error);
    }

    public static void saveNetwork() throws IOException {
        FileWriter fw = new FileWriter("./src/examples/Digits/network.json", false);
        fw.write(network.serialize());
        fw.close();
    }

}
