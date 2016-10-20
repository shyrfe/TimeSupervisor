package com.example.vshurygin.timesupervisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button mCalculateTimeButton;
    private EditText mTheBeginingOfTheDayEditText;
    private EditText mTheEndingOfTheDayEditText;
    private EditText mTodaysDateEditText;
    private TextView mCalculatedTimeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalculateTimeButton = (Button)findViewById(R.id.TheButtonOfCalculateTime);
        mCalculateTimeButton.setOnClickListener(new CalculateTimeButtomClickListener());

        mTheBeginingOfTheDayEditText = (EditText)findViewById(R.id.TheBeginingOfTheDay);
        mTheEndingOfTheDayEditText = (EditText)findViewById(R.id.TheEndingOfTheDay);
        mTodaysDateEditText = (EditText)findViewById(R.id.TodaysDate);
        mCalculatedTimeTextView = (TextView)findViewById(R.id.CalculatedTime);

    }

    class CalculateTimeButtomClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {

        }
    }
}
