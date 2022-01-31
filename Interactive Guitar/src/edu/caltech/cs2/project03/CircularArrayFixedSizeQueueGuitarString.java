package edu.caltech.cs2.project03;

import edu.caltech.cs2.datastructures.CircularArrayFixedSizeQueue;
import edu.caltech.cs2.interfaces.IFixedSizeQueue;
import edu.caltech.cs2.interfaces.IQueue;

import java.util.Random;


public class CircularArrayFixedSizeQueueGuitarString {
    private IFixedSizeQueue<Double> string;
    private static Random random = new Random();
    private static final double KS = 0.996;
    private static final double SR = 44100;

    public CircularArrayFixedSizeQueueGuitarString(double frequency) {
        int capacity = (int)(SR / frequency) + 1;
        this.string = new CircularArrayFixedSizeQueue<>(capacity);
        for(int i = 0; i <= this.string.capacity(); i++) {
            this.string.enqueue(0.0);
        }
    }

    public int length() {
        return this.string.size();
    }

    public void pluck() {
        for(int i = 0; i <= this.string.capacity(); i++) {
            this.string.dequeue();
        }
        for(int j = 0; j <= this.string.capacity(); j++) {
            this.string.enqueue(-0.5 + random.nextDouble());
        }
    }

    public void tic() {
        double d = this.string.peek();
        this.string.dequeue();
        this.string.enqueue((d + this.string.peek())*0.5*KS);
    }

    public double sample() {
        return this.string.peek();
    }
}
