package com.qux.puzzlering;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    Button resetButton;
    Button autoButton;
    Button showNumButton;
    ArrayList<TextView> rings = new ArrayList<TextView>();

    int[] flags = {1, 1, 1, 1, 1, 1, 1, 1, 1};  // 标志位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetButton = findViewById(R.id.reset);
        autoButton = findViewById(R.id.auto);
        showNumButton = findViewById(R.id.show_num);

        rings.add((TextView) findViewById(R.id.ring1));
        rings.add((TextView) findViewById(R.id.ring2));
        rings.add((TextView) findViewById(R.id.ring3));
        rings.add((TextView) findViewById(R.id.ring4));
        rings.add((TextView) findViewById(R.id.ring5));
        rings.add((TextView) findViewById(R.id.ring6));
        rings.add((TextView) findViewById(R.id.ring7));
        rings.add((TextView) findViewById(R.id.ring8));
        rings.add((TextView) findViewById(R.id.ring9));




        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0; i<9; i++) {
                    rings.get(i).setBackgroundColor(getColor(R.color.colorRed));
                    flags[i] = 1;
                }
            }
        });


        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=8; i>=0; i--)
                    autoSetFlag(i, 0);
            }
        });


        // 显示或隐藏数字，名字起得不好
        showNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tmp = rings.get(0);
                if(tmp.getText() == ""){
                    for (int i=0; i<9; i++) {
                        rings.get(i).setText(String.format("%d", i+1));
                    }
                }else{
                    for (int i=0; i<9; i++) {
                        rings.get(i).setText("");
                    }
                }
            }
        });



        for(int i=0; i<9; i++){
            rings.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = rings.indexOf(view);
                    if(canChange(i)){
                        if(flags[i]==1){
                            rings.get(i).setBackgroundColor(getColor(R.color.colorLightGreen));
                            flags[i] = 0;
                        }else{
                            rings.get(i).setBackgroundColor(getColor(R.color.colorRed));
                            flags[i] = 1;
                        }

                        for(int j=0; j<9; j++)
                            if(flags[j]==1)
                                return;
                        Toast.makeText(MainActivity.this, R.string.congratulation, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, R.string.invalid_op, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    // i的取值必定为[0,8]，整数
    protected void autoSetFlag(int i, int flag){
        if(flags[i]==flag)
            return;

        if(i!=0){
            autoSetFlag(i-1, 1);
            if(i>=2){
                for(int j=i-2; j>=0; j--){
                    autoSetFlag(j, 0);
                }
            }
        }

        flags[i] = flag;

        new AutoSolveTask(i, flag).execute();

        return;
    }


    protected boolean canChange(int i){
        if(i==0)
            return true;

        if(flags[i-1]!=1)
            return false;

        if(i>=2)
            for(int j=i-2; j>=0; j--)
                if(flags[j]!=0)
                    return false;

        return true;
    }


    // 用于暂停和修改界面的AsyncTask
    // 主要是因为暂停不能在主线程实现,而代码的主要逻辑是迭代，也不知道怎么放进这里，
    private class AutoSolveTask extends AsyncTask<String, String, String> {

        private int i;
        private int flag;
        private Lock lock = new ReentrantLock();

        public AutoSolveTask(int i, int flag) {
            super();
            this.i = i;
            this.flag = flag;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(flag==1){
                rings.get(i).setBackgroundColor(getColor(R.color.colorRed));
            }else{
                rings.get(i).setBackgroundColor(getColor(R.color.colorLightGreen));
            }

//            // TODO 结束时输出提示，由于线程执行时间的不可控性，无法准确得出什么时候结束
//            boolean tmp = true;
//            for(int j=0; j<9; j++){
//                if(flags[j]==1)
//                    tmp = false;
//            }
//            if(tmp)
//                Toast.makeText(MainActivity.this, R.string.end, Toast.LENGTH_LONG).show();

        }
    }
}
