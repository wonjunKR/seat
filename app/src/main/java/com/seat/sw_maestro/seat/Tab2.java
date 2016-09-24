package com.seat.sw_maestro.seat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;

public class Tab2 extends Fragment {

    private static final String TAG = "Tab2";
    String graphType;   // 그래프 표현 방식

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = this.getActivity().getSharedPreferences("SettingStatus", Context.MODE_PRIVATE);
        graphType = prefs.getString("prefGraphList","0");   // 그래프의 표현 방식 / 0 - 꺾은선 그래프 / 1 - 막대 그래프 / 디폴트는 꺾은선 그래프로 표시
        Log.d(TAG, "그래프 표현 방식 : " + graphType);

        View v;
        if(graphType.equals("0")) { // 꺾은선 그래프
            v = inflater.inflate(R.layout.tab_2_line, container, false);
        }
        else if(graphType.equals("1")) { // 막대 그래프
            v = inflater.inflate(R.layout.tab_2_bar, container, false);
        }
        else{ // 디폴트는 꺾은선 그래프
            v = inflater.inflate(R.layout.tab_2_line, container, false);
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        GraphManager graphManager = new GraphManager(); // 그래프를 그리기 위한 그래프 매니저

        // **** 나중에 요 아래 데이터들만 가져오는 함수 구현해서 값만 바꿔주면 되겠지?? ****
        // 라벨 데이터 만들기
        String[] labels = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11","12","13","14","15","16","17","18","19","20","21","22","23"};
        // 인덱스
        int[] index = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
        // 앉은 시간 데이터
        float[] timeDatas = {43,60,0,0,24,0,60,53,20,0,0,0,0,0,0,0,0,0,0,0,4,60,56,43};
        // 정확도 데이터
        float[] accuracyDatas = {67,77,0,0,87,0,78,86,86,0,0,0,0,0,0,0,0,0,0,0,76,56,86,78};


        switch(graphType){
            case "0" :  // 꺾은선 그래프
                LineChart lineChart_time = (LineChart) getView().findViewById(R.id.lineChart_time);
                LineChart lineChart_accuracy = (LineChart) getView().findViewById(R.id.lineChart_accuracy);

                // 앉은 시간 그래프
                // 그래프 매니저를 통해서 쉽게 그래프를 위한 데이터 셋을 만들 수 있다.
                LineData lineChart_time_data = graphManager.makeLineData(timeDatas,index,labels,"time"); // 만든 데이터 셋
                lineChart_time.setData(lineChart_time_data); // 만든 데이터 셋을 차트에만 연결해주면 됨.
                lineChart_time.animateY(5000);

                // 정확도 그래프
                LineData lineChart_accuracy_data = graphManager.makeLineData(accuracyDatas,index,labels,"accuracy"); // 만든 데이터 셋
                lineChart_accuracy.setData(lineChart_accuracy_data);
                lineChart_accuracy.animateY(5000);
                break;

            case "1" :
                BarChart barChart_time = (BarChart) getView().findViewById(R.id.barChart_time);
                BarChart barChart_accuracy = (BarChart) getView().findViewById(R.id.barChart_accuracy);

                // 앉은 시간 그래프
                // 그래프 매니저를 통해서 쉽게 그래프를 위한 데이터 셋을 만들 수 있다.
                BarData barChart_time_data = graphManager.makeBarData(timeDatas,index,labels,"time"); // 만든 데이터 셋
                barChart_time.setData(barChart_time_data);
                barChart_time.animateY(5000);

                // 정확도 그래프
                BarData barChart_accuracy_data = graphManager.makeBarData(accuracyDatas,index,labels,"accuracy"); // 만든 데이터 셋
                barChart_accuracy.setData(barChart_accuracy_data);
                barChart_accuracy.animateY(5000);
                break;
        }
        super.onActivityCreated(savedInstanceState);
        // 뷰에 데이터를 넣는 작업 등을 할 추가할 수 있음
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        // 이때 세팅 이후 그래프의 모양과 관련된 것을 바꾸면 되겠지?
        super.onStart();
    }
}