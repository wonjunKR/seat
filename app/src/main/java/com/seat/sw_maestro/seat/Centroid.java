package com.seat.sw_maestro.seat;

/**
 * Created by JiYun on 2016. 10. 29..
 */
public class Centroid {
        private double mX = 0.0;
        private double mY = 0.0;

        public Centroid() {
            return;
        }

        public Centroid(double newX, double newY) {
            this.mX = newX;
            this.mY = newY;
            return;
        }

    public void X(double newX) {
        this.mX = newX;
        return;
    }

    public double X() {
        return this.mX;
    }

    public void Y(double newY) {
        this.mY = newY;
        return;
    }

    public double Y() {
        return this.mY;
    }

    public double getDistance(double x, double y) {
        return Math.sqrt(Math.pow((this.X() - x), 2) + Math.pow((this.Y() - y), 2));
    }
}
