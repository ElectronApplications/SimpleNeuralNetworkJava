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
            network = new NeuralNetwork(28 * 28, 512, 10);
        }

        try {
            Scanner dataset = new Scanner(new FileReader("./src/examples/Digits/digit-recognizer/train.csv"));

            int line = 0;
            while (dataset.hasNext()) {
                line++;
                String[] data = dataset.next().split(",");

                int outputDigit = Integer.parseInt(data[0]);
                double output[] = new double[10];
                output[outputDigit] = 1;

                double[] pixels = new double[28*28];
                for(int i = 1; i <= 28*28; i++) {
                    pixels[i-1] = Double.parseDouble(data[i]) / 256;
                }

                if(line % 1000 == 0) {
                    saveNetwork();
                    double[][] errors = network.perform(output, network.predict(pixels));
                    System.out.println("Line: " + line);
                    System.out.println("Errors:");
                    for(int i = 0; i < 10; i++) {
                        System.out.println(i + " - " + errors[2][i]);
                    }
                }

                network.train(pixels, output);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveNetwork() throws IOException {
        FileWriter fw = new FileWriter("./src/examples/Digits/network.json", false);
        fw.write(network.serialize());
        fw.close();
    }

}
