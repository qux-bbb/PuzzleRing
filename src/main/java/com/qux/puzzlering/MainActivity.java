package com.qux.puzzlering;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Button resetButton;
    Button autoButton;
    Button showNumButton;
    Button ruleButton;
    ArrayList<TextView> rings = new ArrayList<TextView>();

    int[] flags = {1, 1, 1, 1, 1, 1, 1, 1, 1};  // 标志位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetButton = findViewById(R.id.reset);
        autoButton = findViewById(R.id.auto);
        showNumButton = findViewById(R.id.show_num);
        ruleButton = findViewById(R.id.rule);

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


        // 隐藏属性，哈哈
        resetButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                for (int i=0; i<9; i++) {
                    rings.get(i).setBackgroundColor(getColor(R.color.colorLightGreen));
                    flags[i] = 0;
                }
                return true;
            }
        });


        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButton.setClickable(false);
                resetButton.setLongClickable(false);
                autoButton.setClickable(false);
                for(int i=0; i<9; i++)
                    rings.get(i).setClickable(false);

                new AutoSolveTask().execute();
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
                    showNumButton.setText(R.string.hide_num);
                }else{
                    for (int i=0; i<9; i++) {
                        rings.get(i).setText("");
                    }
                    showNumButton.setText(R.string.show_num);
                }
            }
        });



        ruleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.rule)
                        .setMessage(R.string.rule_detail);
                builder.create().show();
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


    private class AutoSolveTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            for(int i=8; i>=0; i--)
                autoSetFlag(i, 0);
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if(values[1]==1){
                rings.get(values[0]).setBackgroundColor(getColor(R.color.colorRed));
            }else{
                rings.get(values[0]).setBackgroundColor(getColor(R.color.colorLightGreen));
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(MainActivity.this, R.string.end, Toast.LENGTH_LONG).show();

            resetButton.setClickable(true);
            resetButton.setLongClickable(true);
            autoButton.setClickable(true);
            for(int i=0; i<9; i++)
                rings.get(i).setClickable(true);
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

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            flags[i] = flag;
            publishProgress(i, flag);

            return;
        }
    }
}
