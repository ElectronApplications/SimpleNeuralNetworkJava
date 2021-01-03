package examples.Digits;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import NeuralNetwork.IActivationFunction;
import NeuralNetwork.NeuralNetwork;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Test extends PApplet {
    final int pixelSize = 16;

    NeuralNetwork network;
    int[][] pixels = new int[28][28];

    public static void main(String[] args) {
        PApplet.main(Test.class);
    }

    public void settings() {
        try {
            Scanner networkScanner = new Scanner(new FileReader("./src/examples/Digits/network.json"));
            String networkJson = "";
            while (networkScanner.hasNext()) {
                networkJson += networkScanner.nextLine();
            }
            network = NeuralNetwork.deserialize(networkJson);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        size(28*pixelSize + 128, 28*pixelSize);

        //ReLU
        network.setActivationFunction(new IActivationFunction(){
            public double activation(double x) {
                return x < 0 ? 0 : x;
            }
            public double derivative(double y) {
                return y < 0 ? 0 : 1;
            }
        });
    }

    public void setup() {
        clear();
    }

    public void draw() {
        double[] inputs = new double[28*28];
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                inputs[j*28 + i] = (double) pixels[i][j] / 256;
            }
        }

        double[] outputs = network.predict(inputs);
        
        fill(0);
        rect(pixelSize*28, 0, pixelSize*28 + 128, pixelSize*28);
        for(int i = 0; i < 10; i++) {
            fill(255, 0, 0);
            text(i + ":", pixelSize*28+10, i*32+16);
            rect(pixelSize*28+24, i*32, (float) (outputs[i] * 100), 16);
        }
    }

    public void mouseDragged(MouseEvent event) {
        int x = event.getX() / pixelSize;
        int y = event.getY() / pixelSize;

        if(x >= 0 && y >= 0 && x <= 27 && y <= 27) {
            pixels[x][y] += 64;
            if(pixels[x][y] >= 256)
                pixels[x][y] = 255;
            noStroke();
            fill(pixels[x][y]);
            rect(x*pixelSize, y*pixelSize, pixelSize, pixelSize);
        }
    }

    public void keyPressed(KeyEvent event) {
        if(event.getKey() == 'r') {
            pixels = new int[28][28];
            clear();
        }
    }

}
