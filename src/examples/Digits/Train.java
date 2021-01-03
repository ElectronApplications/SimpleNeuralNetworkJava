package examples.Digits;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import NeuralNetwork.IActivationFunction;
import NeuralNetwork.NeuralNetwork;

public class Train {
    static NeuralNetwork network;
    static double avgError = 1;
    static double prevAvgError = 2;
    static int tests;

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
            network.setLearningRate(0.00005);
        }

        //ReLU
        network.setActivationFunction(new IActivationFunction(){
            public double activation(double x) {
                return x < 0 ? 0 : x;
            }
            public double derivative(double y) {
                return y < 0 ? 0 : 1;
            }
        });

        while(avgError > 0.1 || avgError <= prevAvgError) {
            avgError = 0;
            tests = 0;
            trainDataset();
            avgError /= tests;
            prevAvgError = avgError;
            System.out.println("Trained! Average testing error: " + avgError);
        }
    }

    public static void trainDataset() {
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

                if(line % 1000 == 0)
                    System.out.print("\r[" + "*".repeat(line/600) + " ".repeat((60000-line)/600) + "]. Average testing error: " + avgError/tests);
            
            }
            System.out.println();

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
        avgError += error;
        tests++;
    }

    public static void saveNetwork() throws IOException {
        FileWriter fw = new FileWriter("./src/examples/Digits/network.json", false);
        fw.write(network.serialize());
        fw.close();
    }

}
