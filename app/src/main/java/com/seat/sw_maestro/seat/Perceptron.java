package com.seat.sw_maestro.seat;

import android.util.Log;

import java.util.Random;

/**
 * Created by JiYun on 2016. 10. 1..
 * http://natureofcode.com/book/chapter-10-neural-networks/ 참고
 * https://github.com/jlmd/SimpleNeuralNetwork 참고
 */

public class Perceptron {
    private static final String TAG = "Perceptron";
    float[] weights;
    double c = 0.1;    // learning constant

    Perceptron(int n) { // 생성자에서는 가중치를 랜덤으로 해서 생성한다
        Random generator = new Random();

        weights = new float[n]; // Weights start off random.


        for (int i = 0; i < weights.length; i++) {
            weights[i] = generator.nextFloat()*2 - 1;   // -1~0.9999 까지의 난수를 생성
            Log.d(TAG, "weight : " + weights[i]);
        }
    }

    /*
    int feedforward(float[] inputs) {   // Return an output based on inputs.
        float sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += inputs[i]*weights[i];    // 인풋 값과 그 값에 해당하는 가중치를 곱하고, 그 값들을 다 더한다
        }
        Log.d(TAG, "transfer : " + transfer(sum));
        return activate(sum);   // 다 더한 값을 활성화함수에 던져준다.
    }
    */
    float feedforward(float[] inputs) {   // Return an output based on inputs.
        float sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += inputs[i]*weights[i];    // 인풋 값과 그 값에 해당하는 가중치를 곱하고, 그 값들을 다 더한다
        }
        return transfer(sum);   // 다 더한 값을 활성화함수에 던져준다.
    }

    int activate(float sum) {   // Output is a +1 or -1.
        if (sum > 0) return 1;
        else return -1;
    }

    /*
    void train(float[] inputs, int desired) {   // Train the network against known data.
        int guess = feedforward(inputs);    // 내 모델에서 예상해서 나온 값
        float error = desired - guess;      // 실제 결과와의 차이를 구한다.
        for (int i = 0; i < weights.length; i++) {
            weights[i] += c * error * inputs[i];    // 결과와의 차이를 이용해서 가중치를 새롭게 갱신한다.
        }
    }
    */
    void train(float[] inputs, int desired) {   // Train the network against known data.
        float guess = feedforward(inputs);    // 내 모델에서 예상해서 나온 값
        float error = desired - guess;      // 실제 결과와의 차이를 구한다.

        for (int i = 0; i < weights.length; i++) {
            weights[i] += c * error * inputs[i];    // 결과와의 차이를 이용해서 가중치를 새롭게 갱신한다.
            //Log.d(TAG, i + " : " + weights[i]);
        }
    }

    public float transfer(float value) {    // 시그모이드 함수, 학습에 사용할 함수이다. 활성화함수
        return (float)(1/(1+Math.exp(-value)));
    }
}

