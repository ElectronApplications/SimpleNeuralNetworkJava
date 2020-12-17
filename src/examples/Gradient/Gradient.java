package examples.Gradient;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import NeuralNetwork.NeuralNetwork;

public class Gradient extends PApplet {
    final static int width = 1024;
    final static int height = 512;
    final static int pixelSize = 4;

    static NeuralNetwork network;
    static List<Point> points = new ArrayList<>();

    public static void main(String[] args) {
        network = new NeuralNetwork(2, 16, 3);
        network.setLearningRate(0.01);

        //Start new thread that will train the neural network
        new Thread(() -> {
            while(true) {
                if(points.size() != 0) {
                    for (int i = 0; i < 500; i++) {
                        final Point point = points.get((int) (Math.random()*points.size()));
                        final double input[] = {(double) point.x/width, (double) point.y/height};
                        double output[];
                        if(point.type == 0) //Red
                            output = new double[]{1, 0, 0};
                        else if(point.type == 1) //Green
                            output = new double[]{0, 1, 0};
                        else //Blue
                            output = new double[]{0, 0, 1};    
                        network.train(input, output);
                    }
                }
                try {
                    Thread.sleep(1);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        PApplet.main(Gradient.class);
    }

    public void settings() {
        size(width, height);
    }

    public void draw() {
        clear();

        PImage frame = createImage(width/pixelSize, height/pixelSize, RGB);
        frame.loadPixels();

        for(int i = 0; i < width/pixelSize; i++) {
            for(int j = 0; j < height/pixelSize; j++) {
                final double[] answer = network.predict(new double[]{(double) i/width*pixelSize, (double) j/height*pixelSize});
                frame.pixels[j*(width/pixelSize) + i] = color((int) (answer[0]*255), (int) (answer[1]*255), (int) (answer[2]*255));
            }
        }

        frame.resize(width, height);
        frame.updatePixels();
        image(frame, 0, 0);

        for (final Point point : points) {
            if(point.type == 0) //Red
                fill(255, 0, 0);
            else if(point.type == 1) //Green
                fill(0, 255, 0);
            else //Blue
                fill(0, 0, 255);

            stroke(0);
            ellipseMode(CENTER);
            ellipse(point.x, point.y, 10, 10);
        }

        System.gc();
    }

    public void mousePressed(final MouseEvent event) {
        int type = 0; //RED
        if(event.getButton() == CENTER)
            type = 1; //GREEN
        else if(event.getButton() == RIGHT)
            type = 2; //BLUE

        points.add(new Point(event.getX(), event.getY(), type));
    }

    public void keyPressed(final KeyEvent event) {
        if(event.getKey() == 'r')
            network = new NeuralNetwork(2, 16, 3); //Recreate the neural network
    }

}
