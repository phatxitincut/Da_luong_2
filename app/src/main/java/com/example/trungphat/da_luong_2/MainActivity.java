package com.example.trungphat.da_luong_2;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    ProgressBar myBar;
    TextView tv_status,tv_display;
    Button btnRedo;

    boolean isRunning = false;

    int MAX_sec=60;//(giây) chu kỳ cho background thread
    String strTest="Global value seen by all threads";
    int intTest=0;

    Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            String returnedvalue = (String)msg.obj;
            //sau khi nhận được giá trị từ background thread ta sẽ hiển thị giá trị ra textview display
            tv_display.setText("Returned by background thread: \n\n"+returnedvalue);
            //tăng giá trị progressbar lên 2 đơn vị
            myBar.incrementProgressBy(2);
            //kt xem đến điểm kết thúc luồng chưa?
            if (myBar.getProgress()==MAX_sec){
                tv_display.setText("Done \nback thread has been stopped");
                isRunning=false;
            }
            if (myBar.getProgress()==myBar.getMax()){
                tv_status.setText("Done");
                myBar.setVisibility(View.INVISIBLE);
                btnRedo.setVisibility(View.VISIBLE);
            }
            else {
                tv_status.setText("Working..."+myBar.getProgress());
            }//kết thúc handler



        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myBar=(ProgressBar)findViewById(R.id.myBar);
        myBar.setMax(MAX_sec);
        myBar.setVisibility(View.INVISIBLE);

        tv_status=(TextView)findViewById(R.id.tv_status);
        tv_display=(TextView)findViewById(R.id.tv_display);

        btnRedo=(Button)findViewById(R.id.button);
        btnRedo.setVisibility(View.VISIBLE);

        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setVisibility(View.VISIBLE);
                myBar.setProgress(0);
                strTest +="-01";
                intTest = 1;

                final Thread backgroundThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i=0; i<MAX_sec && isRunning;i++){
                                //ở đây ta ko thể dùng phương thức TOast để thay đổi UI được

                                Thread.sleep(1000);
                                Random randomValue=new Random();
                                String data="Thread Value: "+(int) randomValue.nextInt(101);
                                data +="\n" + strTest + " " + intTest;
                                intTest++;

                                Message msg=myhandler.obtainMessage(1,(String)data);

                                if (isRunning){
                                    myhandler.sendMessage(msg);
                                }


                            }
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }); //background

                isRunning=true;
                backgroundThread.start();
                myBar.setVisibility(View.VISIBLE);
                btnRedo.setVisibility(View.INVISIBLE);

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning=false;
    }
}