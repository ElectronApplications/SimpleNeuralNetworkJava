package examples.Digits;

import java.io.FileReader;
import java.util.Scanner;

import NeuralNetwork.NeuralNetwork;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Test extends PApplet {
    final int size = 28; // Size of drawable area in pixels
    final int pixelSize = 24; // Size of 1 pixel

    NeuralNetwork network;
    int[][] pixels = new int[size][size];

    public static void main(String[] args) {
        PApplet.main(Test.class);
    }

    public void settings() {
        try(FileReader networkFile = new FileReader("digits/network.json")) {
            Scanner networkScanner = new Scanner(networkFile);
            String networkJson = "";
            while (networkScanner.hasNext()) {
                networkJson += networkScanner.nextLine();
            }
            network = NeuralNetwork.deserialize(networkJson);
            networkScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        size(size*pixelSize + pixelSize*8, size*pixelSize);
    }

    public void setup() {
        clear();
        loadRandom();
    }

    public void loadRandom() {
        try(FileReader testFile = new FileReader("digits/mnist_test.csv")) {
            Scanner testScanner = new Scanner(testFile);
            for(int i = 0; i < Math.random()*10000; i++)
                testScanner.nextLine();
            String[] pixelsStr = testScanner.nextLine().split(",");

            for(int i = 0; i < size; i++) {
                for(int j = 0; j < size; j++) {
                    pixels[i][j] = Integer.parseInt(pixelsStr[j*size+i+1]);
                    noStroke();
                    fill(pixels[i][j]);
                    rect(i*pixelSize, j*pixelSize, pixelSize, pixelSize);
                }
            }
            
            testScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        guessDigit();
    }

    @Override
    public void draw() {

    }

    public void guessDigit() {
        double[] inputs = new double[size*size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                inputs[j*size + i] = (double) pixels[i][j] / 255;
            }
        }

        double[] outputs = network.predict(inputs);
        
        fill(0);
        rect(pixelSize*size, 0, pixelSize*size + 128, pixelSize*size);
        
        strokeWeight(3);
        stroke(255, 255, 255);
        line(pixelSize*size+3, 0, pixelSize*size+3, pixelSize*size);
        
        noStroke();
        textSize(pixelSize);
        for(int i = 0; i < 10; i++) {
            fill(255, 255, 255);
            text(i + ":", pixelSize*size+10, i*pixelSize*2+pixelSize);
            fill(255, 0, 0);
            rect(pixelSize*size+pixelSize*2, i*pixelSize*2, (float) (outputs[i] * 100), pixelSize);
        }
    }

    public void mouseDragged(MouseEvent event) {
        int x = event.getX() / pixelSize;
        int y = event.getY() / pixelSize;

        for(int i = x-1; i < x+1; i++) {
            for(int j = y-1; j < y+1; j++) {
                if(i >= 0 && j >= 0 && i < size && j < size) {
                    pixels[i][j] += 96;
                    if(pixels[i][j] >= 256)
                        pixels[i][j] = 255;
                    noStroke();
                    fill(pixels[i][j]);
                    rect(i*pixelSize, j*pixelSize, pixelSize, pixelSize);
                }
            }
        }
        guessDigit();
    }

    public void keyPressed(KeyEvent event) {
        if(event.getKey() == 'r') {
            pixels = new int[size][size];
            clear();
        }
        else if(event.getKey() == 'l') {
            loadRandom();
        }
        guessDigit();
    }

}
