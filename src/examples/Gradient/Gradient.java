package examples.Gradient;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import NeuralNetwork.NeuralNetwork;

public class Gradient extends PApplet {
    static NeuralNetwork network;
    static List<Point> points = new ArrayList<>();

    static int width = 1024;
    static int height = 512;
    static int pixelSize = 8;

    public static void main(String[] args) {
        network = new NeuralNetwork(2, 16, 3);
        network.setLearningRate(0.01);

        new Thread(() -> {
            while(true) {
                if(points.size() != 0) {
                    for (int i = 0; i < 10000; i++) {
                        final Point point = points.get((int) (Math.random()*points.size()));
                        final double input[] = {(double) point.x/width, (double) point.y/height};
                        double output[];
                        if(point.type == 0)
                            output = new double[]{1, 0, 0};
                        else if(point.type == 1)
                            output = new double[]{0, 1, 0};
                        else
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
        fill(255);
        rect(0, 0, width, height);

        for (int i = 0; i < width/pixelSize; i++) {
            for (int j = 0; j < height/pixelSize; j++) {
                final double[] answer = network.predict(new double[]{(double) i/width*pixelSize, (double) j/height*pixelSize});
                fill((float) answer[0]*255, (float) answer[1]*255, (float) answer[2]*255);
                stroke((float) answer[0]*255, (float) answer[1]*255, (float) answer[2]*255);
                rectMode(1);
                rect(i*pixelSize, j*pixelSize, i*pixelSize+pixelSize, j*pixelSize+pixelSize);
            }
        }

        for (final Point point : points) {
            if(point.type == 0)
                fill(255, 0, 0);
            else if(point.type == 1)
                fill(0, 255, 0);
            else
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
            network = new NeuralNetwork(2, 16, 3);
    }

}
