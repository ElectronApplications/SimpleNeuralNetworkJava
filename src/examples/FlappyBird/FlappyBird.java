package examples.FlappyBird;

import NeuralNetwork.NeuralNetwork;
import processing.core.PApplet;
import processing.core.PImage;

public class FlappyBird extends PApplet {
    final int width = 750;
    final int height = 500;
    final int birdSize = 30;
    final int pipeGap = birdSize * 4;
    final int pipesDistance = 300;
    final int pipesAmount = (int) Math.ceil((double) width / pipesDistance);
    final int startX = 30;

    final int birdSpeed = 1;
    final int birdStrength = 15;

    final int minVelocity = -4;
    final int maxVelocity = 5;
    final double gravity = 0.15;

    PImage birdUp;
    PImage birdMid;
    PImage birdDown;

    PImage background;
    PImage ground;
    PImage pipeUp;
    PImage pipeDown;

    float backgroundTranslation = 0;
    int groundTranslation = 0;

    Bird[] birds = new Bird[100];
    Pipe[] pipes = new Pipe[pipesAmount];
    int generation = 0;
    int maxScore = 0;
    int distance = 0;

    public static void main(String[] args) {
        PApplet.main(FlappyBird.class);
    }

    public void settings() {
        size(width, height);

        birdUp = loadImage("src/examples/FlappyBird/assets/redbird-upflap.png");
        birdMid = loadImage("src/examples/FlappyBird/assets/redbird-midflap.png");
        birdDown = loadImage("src/examples/FlappyBird/assets/redbird-downflap.png");
        background = loadImage("src/examples/FlappyBird/assets/background-day.png");
        ground = loadImage("src/examples/FlappyBird/assets/base.png");
        pipeUp = loadImage("src/examples/FlappyBird/assets/pipe-green-up.png");
        pipeDown = loadImage("src/examples/FlappyBird/assets/pipe-green-down.png");

        for (int i = 0; i < birds.length; i++) {
            birds[i] = new Bird(startX, height/2, new NeuralNetwork(2, 6, 1)); // inputs: 0 - distance to a pipe horizontally, 1 - distance to a gap of a pipe vertically
        }

        for (int i = 0; i < pipesAmount; i++) {
            pipes[i] = new Pipe(pipesDistance * (i + 1), (int) (Math.random() * (height - ground.height - pipeGap)));
        }
    }

    public void draw() {
        clear();
        distance += birdSpeed;
        translate(-distance, 0);

        backgroundTranslation += 0.35 * birdSpeed;
        groundTranslation += birdSpeed;

        // Rendering background
        for (int i = 0; i <= Math.ceil((double) width / background.width); i++) {
            if (backgroundTranslation > background.width)
                backgroundTranslation = 0;
            image(background, distance + i * background.width - backgroundTranslation, 0);
        }

        // Rendering pipes
        for (int i = 0; i < pipesAmount; i++) {
            if (pipes[i].pipeX < distance - pipeUp.width - startX)
                pipes[i] = new Pipe(pipes[i - 1 < 0 ? pipes.length - 1 : i - 1].pipeX + pipesDistance,
                        (int) (Math.random() * (height - ground.height - pipeGap)));

            image(pipeUp, pipes[i].pipeX, pipes[i].gapY - pipeUp.height);
            image(pipeDown, pipes[i].pipeX, pipes[i].gapY + pipeGap);
        }

        // Rendering ground
        for (int i = 0; i <= Math.ceil((double) width / ground.width); i++) {
            if (groundTranslation > ground.width)
                groundTranslation = 0;
            image(ground, distance + i * ground.width - groundTranslation, height - ground.height);
        }

        int alive = 0;
        for (int i = 0; i < birds.length; i++) {
            if (birds[i].isAlive) {
                alive++;

                int closestPipe = 0;
                for (int j = 1; j < pipes.length; j++) {
                    if (pipes[j].pipeX < pipes[closestPipe].pipeX)
                        closestPipe = j;
                }

                double horizontalDistance = (double) (pipes[closestPipe].pipeX - birds[i].x + birdSize) / pipesDistance;
                double verticalDistance = (pipes[closestPipe].gapY + pipeGap/2 - birds[i].y) / (height - ground.height);

                double output[] = birds[i].network.predict(new double[]{horizontalDistance, verticalDistance});
                if (output[0] > 0.5 && birds[i].velocity > minVelocity)
                    birds[i].velocity -= birdStrength;

                if (birds[i].velocity < maxVelocity)
                    birds[i].velocity += gravity;
                if (birds[i].velocity < minVelocity)
                    birds[i].velocity = minVelocity;

                birds[i].y += birds[i].velocity;

                if (birds[i].y < 0 && birds[i].velocity < 0) {
                    birds[i].y = 0;
                    birds[i].velocity = 0;
                }

                birds[i].x += birdSpeed;

                if (birds[i].y + birdSize - 4 >= height - ground.height || touchPipe(i))
                    birds[i].isAlive = false;
            }

            // Rendering birds
            if (birds[i].velocity > 1)
                image(birdDown, birds[i].x, birds[i].y);
            else if (birds[i].velocity > -1 && birds[i].velocity < 1)
                image(birdMid, birds[i].x, birds[i].y);
            else
                image(birdUp, birds[i].x, birds[i].y);
        }

        text("Generation: " + generation, 10+distance, 10);
        text("Birds alive: " + alive, 10+distance, 30);
        text("Score: " + distance/pipesDistance, 10+distance, 50);
        text("Max Score: " + maxScore, 10+distance, 70);

        if(alive == 0)
            newGeneration();

    }

    public void newGeneration() {
        if(distance/pipesDistance > maxScore)
            maxScore = distance/pipesDistance;

        generation++;
        distance = 0;

        int closestPipe = 0;
        for (int j = 1; j < pipes.length; j++) {
            if (pipes[j].pipeX < pipes[closestPipe].pipeX)
                closestPipe = j;
        }

        int maxDistance = 0;
        for (int i = 1; i < birds.length; i++) {
            if (Math.sqrt(Math.pow(birds[i].x - pipes[closestPipe].pipeX, 2) + Math.pow(birds[i].y - pipes[closestPipe].gapY + pipeGap, 2)) < Math.sqrt(Math.pow(birds[maxDistance].x - pipes[closestPipe].pipeX, 2) + Math.pow(birds[maxDistance].y - pipes[closestPipe].gapY + pipeGap, 2)))
                maxDistance = i;
        }

        for (int i = 0; i < birds.length; i++) {
            if (i != maxDistance) {
                birds[i].network = birds[maxDistance].network.copy();
                birds[i].network.mutate(1);
            }     

            birds[i].x = startX;
            birds[i].y = height/2;
            birds[i].isAlive = true;
            birds[i].velocity = 0;              
        }

        for (int i = 0; i < pipesAmount; i++) {
            pipes[i] = new Pipe(pipesDistance*(i+1), (int) (Math.random()*(height-ground.height-pipeGap)));
        }
    }

    public boolean touchPipe(int bird) {
        boolean touch = false;
        for (int i = 0; i < pipes.length; i++) {
            if(birds[bird].x+birdSize >= pipes[i].pipeX && birds[bird].x <= pipes[i].pipeX+pipeUp.width && (birds[bird].y <= pipes[i].gapY || birds[bird].y+birdSize >= pipes[i].gapY+pipeGap))
                touch = true;
        }
        return touch;
    }

}