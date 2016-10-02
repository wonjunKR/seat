package com.seat.sw_maestro.seat;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Utils for read external data and save it in memory
 * @author jlmd
 */
public class DataUtils {
    private static final String TAG = "DataUtils";

    public static float[][] readInputsFromFile(Context context) {
        float[][] fArray = new float[0][];
        int lineCount = 0;

        Resources res = context.getResources();

        // 텍스트 파일의 라인 카운트를 얻기 위함.
        LineNumberReader lineNumberReader = null;
        try {
            InputStream inputStream = res.openRawResource(R.raw.data_input);  // res -> raw -> data_input.txt 파일 읽을 것임.
            lineNumberReader = new LineNumberReader(new InputStreamReader(inputStream));
            while ((lineNumberReader.readLine()) != null);
            lineCount = lineNumberReader.getLineNumber();   // 텍스트 파일에서 라인의 수를 리턴
        } catch (Exception ex) {
            lineCount = -1; // 에러면 -1 리턴
        }
        Log.d(TAG,"lineCount : " + lineCount);

        // 이제 하나씩 읽어와서 배열로 만들기 위함.
        fArray = new float[lineCount][];    // 새롭게 float 배열을 만들어준다. 라인 수 만큼 할당..
        try {
            InputStream inputStream = res.openRawResource(R.raw.data_input);  // res -> raw -> data.txt 파일 읽을 것임.
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            for(int i = 0; i<lineCount; i++){   // 읽어온 파일 한줄씩 내려가면서
                String line = reader.readLine();
                fArray[i] = convertStringArrayToFloatArray(line.split(","));    // , 으로 짤라서 float Array를 만듬.
                //Log.d(TAG, line);   // 일단은 로그에 찍고
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return fArray;
    }

    public static int[] readOutputsFromFile(Context context){
        int[] iArray = new int[0];
        int lineCount = 0;

        Resources res = context.getResources();

        // 텍스트 파일의 라인 카운트를 얻기 위함.
        LineNumberReader lineNumberReader = null;
        try {
            InputStream inputStream = res.openRawResource(R.raw.data_output);  // res -> raw -> data_output.txt 파일 읽을 것임.
            lineNumberReader = new LineNumberReader(new InputStreamReader(inputStream));
            while ((lineNumberReader.readLine()) != null);
            lineCount = lineNumberReader.getLineNumber();   // 텍스트 파일에서 라인의 수를 리턴
        } catch (Exception ex) {
            lineCount = -1; // 에러면 -1 리턴
        }
        Log.d(TAG,"lineCount : " + lineCount);

        // 하나씩 읽어와 배열로
        iArray = new int[lineCount];    // 새롭게 int 배열을 할당
        try {
            InputStream inputStream = res.openRawResource(R.raw.data_output);  // res -> raw -> data.txt 파일 읽을 것임.
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            for(int i = 0; i<lineCount; i++){   // 읽어온 파일 한줄씩 내려가면서
                String line = reader.readLine();
                iArray[i] = Integer.parseInt(line);
                //Log.d(TAG, line);   // 일단은 로그에 찍고
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return iArray;
    }

    private static float[] convertStringArrayToFloatArray(String[] num){ // String Array를 Float Array로 변환. ',' 로 짜름
        if (num != null) {
            float fArray[] = new float[num.length];
            for (int i = 0; i <num.length; i++) {
                fArray[i] = Float.parseFloat(num[i]);
            }
            return fArray;
        }
        return null;
    }
}