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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;

public class Tab2 extends Fragment {

    private static final String TAG = "Tab2";
    String graphType;   // 그래프 표현 방식
    Spinner spinnerGraphInterval;

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
        final GraphManager graphManager = new GraphManager(); // 그래프를 그리기 위한 그래프 매니저

        //스피너 관련
        spinnerGraphInterval = (Spinner)this.getActivity().findViewById(R.id.spinnerGraphInterval);
        ArrayAdapter adapterGraphInterval = ArrayAdapter.createFromResource(this.getActivity(), R.array.graphInterval, R.layout.spinner_layout2);
        spinnerGraphInterval.setAdapter(adapterGraphInterval);
        spinnerGraphInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //그래프를 일, 월, 연 중에서 어떤 것으로 보여줄지..
                String graphInterval = spinnerGraphInterval.getSelectedItem().toString();
                Log.d(TAG, "graphInterval : " + graphInterval);

                String[] labels;
                int[] index;
                float[] timeDatas;
                float[] accuracyDatas;

                // 디비에서 정보를 가져오기 위해서!
                DatabaseManager databaseManager = new DatabaseManager(getActivity());
                String date = databaseManager.getCurrentDay();  // 현재의 날짜 예)20160926

                switch(graphInterval){
                    case "일":
                        // 라벨 데이터 만들기
                        labels = new String[] {"0시", "", "", "3시", "", "", "6시", "", "", "9시", "",
                                "","12시","","","15시","","","18시","","","21시","",""};
                        // 인덱스
                        index = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
                        // 앉은 시간 데이터
                        timeDatas = databaseManager.makeTimeDatas_OneDay(date);
                        // 정확도 데이터
                        accuracyDatas = databaseManager.makeAccuracyDatas_OneDay(date);

                        break;

                    case "월":
                        // 라벨 데이터 만들기
                        labels = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                                "11","12","13","14","15","16","17","18","19","20","21","22","23",
                                "24","25","26","27","28","29","30","31"};
                        // 인덱스
                        index = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
                        // 앉은 시간 데이터
                        timeDatas = databaseManager.makeTimeDatas_Month();
                        // 정확도 데이터
                        accuracyDatas = databaseManager.makeAccuracyDatas_Month();
                        break;

                    case "연":
                        // 라벨 데이터 만들기
                        labels = new String[] {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월",
                                "11월","12월"};
                        // 인덱스
                        index = new int[] {0,1,2,3,4,5,6,7,8,9,10,11};
                        // 앉은 시간 데이터
                        timeDatas = new float[] {-55,60,0,0,24,0,60,53,20,0,0,0};
                        // 정확도 데이터
                        accuracyDatas = new float[] {67,77,0,0,87,0,78,86,86,0,0,0,0};
                        break;

                    default :
                        // 라벨 데이터 만들기
                        labels = new String[] {"0"};
                        // 인덱스
                        index = new int[] {0};
                        // 앉은 시간 데이터
                        timeDatas = new float[] {0};
                        // 정확도 데이터
                        accuracyDatas = new float[] {0};
                        break;
                }

                // 설정의 그래프 타입에 맞게 그리기
                switch(graphType){
                    case "0" :  // 꺾은선 그래프
                        LineChart lineChart_time = (LineChart) getView().findViewById(R.id.lineChart_time);
                        LineChart lineChart_accuracy = (LineChart) getView().findViewById(R.id.lineChart_accuracy);

                        // 앉은 시간 그래프
                        // 그래프 매니저를 통해서 쉽게 그래프를 위한 데이터 셋을 만들 수 있다.
                        LineData lineChart_time_data = graphManager.makeLineData(timeDatas,index,labels,"time", getContext()); // 만든 데이터 셋
                        lineChart_time.setData(lineChart_time_data); // 만든 데이터 셋을 차트에만 연결해주면 됨.
                        lineChart_time.animateY(5000);

                        // 정확도 그래프
                        LineData lineChart_accuracy_data = graphManager.makeLineData(accuracyDatas,index,labels,"accuracy", getContext()); // 만든 데이터 셋
                        lineChart_accuracy.setData(lineChart_accuracy_data);
                        lineChart_accuracy.animateY(5000);
                        break;

                    case "1" :  // 막대 그래프
                        BarChart barChart_time = (BarChart) getView().findViewById(R.id.barChart_time);
                        BarChart barChart_accuracy = (BarChart) getView().findViewById(R.id.barChart_accuracy);

                        // 앉은 시간 그래프
                        // 그래프 매니저를 통해서 쉽게 그래프를 위한 데이터 셋을 만들 수 있다.
                        BarData barChart_time_data = graphManager.makeBarData(timeDatas,index,labels,"time", getContext()); // 만든 데이터 셋
                        barChart_time.setData(barChart_time_data);
                        barChart_time.animateY(5000);

                        // 정확도 그래프
                        BarData barChart_accuracy_data = graphManager.makeBarData(accuracyDatas,index,labels,"accuracy", getContext()); // 만든 데이터 셋
                        barChart_accuracy.setData(barChart_accuracy_data);
                        barChart_accuracy.animateY(5000);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d(TAG, "아무것도 고르지 않음");
            }
        });
        super.onActivityCreated(savedInstanceState);
        // 뷰에 데이터를 넣는 작업 등을 할 추가할 수 있음
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
    }
}