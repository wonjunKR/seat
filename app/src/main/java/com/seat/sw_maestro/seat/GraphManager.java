package com.seat.sw_maestro.seat;

import android.content.Context;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

public class GraphManager {
    final static String TAG = "GraphManager";
    public GraphManager() { // 생성자

    }

    /*
    String형의 labels 배열을 넣으면 공용 어레이 리스트를 만들어서 리턴해주는 함수
     */
    public ArrayList<String> makeChartLabel(String[] labels){
        ArrayList<String> entries = new ArrayList<>();
        for(int i = 0; i < labels.length; i++){
            entries.add(labels[i]);
        }
        return entries;
    }

    /*
    float형의 데이터와 int형의 인덱스 배열을 넣으면 *라인차트용* 어레이 리스트를 만들어서 리턴해주는 함수
     */
    public ArrayList<Entry> makeLineChartEntry(float[] datas, int[] index){
        ArrayList<Entry> lineChart_entries = new ArrayList<>();
        for(int i = 0; i < index.length; i++){
            lineChart_entries.add(new Entry(datas[i], index[i]));
        }
        return lineChart_entries;
    }

    /*
    위의 두 과정(데이터와 라벨을 만드는 것)을 바탕으로 이후에 최종 데이터 셋을 만드는 함수(라인차트용)/ 인자는 데이터, 인덱스, 라벨, 표 아래에 보이는 글씨..
     */
    public LineData makeLineData(float[] datas, int[] index, String[] labels, String description, Context context){
        ArrayList<Entry> lineChart_entries = makeLineChartEntry(datas,index);
        ArrayList<String> lineChart_labels = makeChartLabel(labels);
        LineDataSet lineChart_dataSet = new LineDataSet(lineChart_entries, description);    // description -> 표 아래에 보이는 글씨
        LineData lineChart_data = new LineData(lineChart_labels, lineChart_dataSet);
        lineChart_dataSet.setColors(new int[] {context.getResources().getColor(R.color.mainColor)});    // 색상
        lineChart_dataSet.setDrawFilled(true);

        // 그래프에 표시될 값을 커스터마이징   예) 324 -> 5시간 24분
        if(description.equals("time")) {  // 시간 전용
            lineChart_data.setValueFormatter(new valueFormatter_time());
        } else if(description.equals("accuracy")){
            lineChart_data.setValueFormatter(new valueFormatter_accuracy());
        }

        return lineChart_data;
    }

    /*
   float형의 데이터와 int형의 인덱스 배열을 넣으면 *바차트용* 어레이 리스트를 만들어서 리턴해주는 함수
    */
    public ArrayList<BarEntry> makeBarChartEntry(float[] datas, int[] index){
        ArrayList<BarEntry> barChart_entries = new ArrayList<>();
        for(int i = 0; i < index.length; i++){
            barChart_entries.add(new BarEntry(datas[i], index[i]));
        }
        return barChart_entries;
    }

    /*
    위의 두 과정(데이터와 라벨을 만드는 것)을 바탕으로 이후에 최종 데이터 셋을 만드는 함수(바차트용)/ 인자는 데이터, 인덱스, 라벨, 표 아래에 보이는 글씨..
     */
    public BarData makeBarData(float[] datas, int[] index, String[] labels, String description, Context context){
        ArrayList<BarEntry> barChart_entries = makeBarChartEntry(datas,index);
        ArrayList<String> barChart_labels = makeChartLabel(labels);
        BarDataSet barChart_dataSet = new BarDataSet(barChart_entries, description);    // description -> 표 아래에 보이는 글씨
        barChart_dataSet.setColors(new int[] {context.getResources().getColor(R.color.mainColor)}); // 막대 그래프 색상
        BarData barChart_data = new BarData(barChart_labels, barChart_dataSet);

        // 그래프에 표시될 값을 커스터마이징   예) 324 -> 5시간 24분
        if(description.equals("time")) {  // 시간 전용
            barChart_data.setValueFormatter(new valueFormatter_time());
        } else if(description.equals("accuracy")){
            barChart_data.setValueFormatter(new valueFormatter_accuracy());
        }


        return barChart_data;
    }

    // 그래프의 값을 소수점이 아닌 정수로 보여주기 위함. 시간은 몇 시 몇 분으로 변환해서 보여주기.
    public class valueFormatter_time implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            //Log.d(TAG,"value : " + value);
            if(value == 0.0){   // 0인 경우에는 그냥 보여주지 않음.
                return "";
            }
            int[] hourAndMinyute = new int[2];
            hourAndMinyute = getHourMinute((int)value); // 소수점은 버리고 변환시킴

            if(hourAndMinyute[0] == 0){ // 1시간 넘지않는경우
                return hourAndMinyute[1] + "분";
            } else{
                return hourAndMinyute[0] + "시간" + hourAndMinyute[1] + "분";
            }
        }
    }

    // 그래프의 값을 소수점이 아닌 정수로 보여주기 위함.
    public class valueFormatter_accuracy implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            //Log.d(TAG,"value : " + value);
            if(value == 0.0){   // 0인 경우에는 그냥 보여주지 않음.
                return "";
            }
            return Math.round(value) + "%";
        }
    }

    public int[] getHourMinute(int sittingTime){    // 분 단위의 sittingTime을 넣으면 몇 시,몇 분으로 바꾸어서 리턴
        int[] hourAndMinute = new int[2];
        hourAndMinute[0] = sittingTime / 60;  // 시간은 60으로 나눈 몫
        hourAndMinute[1] = sittingTime % 60;  // 분은 60으로 나눈 나머지
        return hourAndMinute;
    }
}