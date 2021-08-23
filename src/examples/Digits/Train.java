package examples.Digits;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import NeuralNetwork.NeuralNetwork;

public class Train {
    final static int size = 28;

    static NeuralNetwork network;
    static double avgError = 1;
    static double prevAvgError = 2;
    static int tests;

    public static void main(String[] args) {
        try(FileReader networkFile = new FileReader("./src/examples/Digits/network.json")) {
            Scanner networkScanner = new Scanner(networkFile);
            String networkJson = "";
            while (networkScanner.hasNext()) {
                networkJson += networkScanner.nextLine();
            }
            network = NeuralNetwork.deserialize(networkJson);
            networkScanner.close();
        } catch (Exception e) {
            network = new NeuralNetwork(size*size, 256, 256, 10);
            network.setLearningRate(0.001);
        }

        while(avgError <= prevAvgError) {
            avgError = 0;
            tests = 0;
            trainDataset();
            avgError /= tests;
            prevAvgError = avgError;
        }
    }

    public static void trainDataset() {
        try(FileReader trainFile = new FileReader("./src/examples/Digits/dataset/mnist_train.csv");
            FileReader testFile = new FileReader("./src/examples/Digits/dataset/mnist_test.csv")) {
            
            Scanner trainDataset = new Scanner(trainFile);
            Scanner testDataset = new Scanner(testFile);

            int line = 0;
            while (trainDataset.hasNext()) {
                line++;
                
                trainNetwork(trainDataset);

                if(line % 6 == 0)
                    testNetwork(testDataset);
                    
                if(line % 5000 == 0)
                    saveNetwork();

                if(line % 500 == 0)
                    System.out.print("\r[" + "*".repeat(line/600) + " ".repeat((60000-line)/600) + "]. Average testing error: " + avgError/tests);
            
            }
            System.out.println();

            trainDataset.close();
            testDataset.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trainNetwork(Scanner dataset) {
        String[] data = dataset.next().split(",");

        int outputDigit = Integer.parseInt(data[0]);
        double output[] = new double[10];
        output[outputDigit] = 1;

        double[] pixels = new double[size*size];
        for(int i = 1; i <= size*size; i++) {
            pixels[i-1] = Double.parseDouble(data[i]) / 255;
        }

        network.train(pixels, output);
    }

    public static void testNetwork(Scanner dataset) {
        String[] data = dataset.next().split(",");
        int outputDigit = Integer.parseInt(data[0]);
        double[] output = new double[10];
        output[outputDigit] = 1;

        double[] pixels = new double[size*size];
        for(int i = 1; i <= size*size; i++) {
            pixels[i-1] = Double.parseDouble(data[i]) / 255;
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
