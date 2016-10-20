package com.example.vshurygin.timesupervisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.validation.Validator;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private Button mCalculateTimeButton;
    private Button mClearAllRecordsButton;
    private EditText mTheBeginingOfTheDayEditText;
    private EditText mTheEndingOfTheDayEditText;
    private EditText mTodaysDateEditText;
    private TextView mCalculatedTimeTextView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        mCalculateTimeButton = (Button)findViewById(R.id.TheButtonOfCalculateTime);
        mCalculateTimeButton.setOnClickListener(new CalculateTimeButtomClickListener());
        mClearAllRecordsButton = (Button)findViewById(R.id.ClearAllRecordsButton);
        mClearAllRecordsButton.setOnClickListener(new ClearAllRecordsButtonClickListener());

        mTheBeginingOfTheDayEditText = (EditText)findViewById(R.id.TheBeginingOfTheDay);
        mTheEndingOfTheDayEditText = (EditText)findViewById(R.id.TheEndingOfTheDay);
        mTodaysDateEditText = (EditText)findViewById(R.id.TodaysDate);
        mCalculatedTimeTextView = (TextView)findViewById(R.id.CalculatedTime);
    }

    @Override
    protected void onDestroy()
    {
        realm.close();
    }

    private void addRecord(final String _theBeginingOfTheDay, final String _theEndingOfTheDay, final String _date)
    {
        try
        {
            realm.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    DateRecord record = realm.createObject(DateRecord.class);
                    record.setBeginingOfDayTime(_theBeginingOfTheDay);
                    record.setEndingOfTheDayTime(_theEndingOfTheDay);
                    record.setDate(_date);
                }
            });
            Log.d("addRecords","Records Added");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("addRecords","Records are not Added");
        }

    }

    private void deleteAllRecords()
    {
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                try
                {
                    realm.deleteAll();
                    Log.d("DelRecords","Records Delete");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("DelRecords","Records not Deleted");
                }
            }
        });
    }

    class CalculateTimeButtomClickListener implements View.OnClickListener
    {
        private boolean checkTime(String _value)
        {
            Pattern p = Pattern.compile("^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$");
            Matcher m = p.matcher(_value);
            return m.matches();
        }
        private boolean checkDate(String _value)
        {
            Pattern p = Pattern.compile("(0[1-9]|1[0-9]|2[0-9]|3[01])-(0[1-9]|1[012])-[0-9]{4}");
            Matcher m = p.matcher(_value);
            return m.matches();
        }

        @Override
        public void onClick(View v)
        {
            String beginingOfTheDay = mTheBeginingOfTheDayEditText.getText().toString();
            String endingOfTheDay = mTheEndingOfTheDayEditText.getText().toString();
            String date = mTodaysDateEditText.getText().toString();

            if((checkDate(date))&&(checkTime(beginingOfTheDay))&&(checkTime(endingOfTheDay)))
            {
                addRecord(beginingOfTheDay,endingOfTheDay,date);
            }
            else
            {
                Log.d("Validate",String.valueOf(checkDate(date)));
                Log.d("Validate",String.valueOf(checkTime(beginingOfTheDay)));
                Log.d("Validate",String.valueOf(checkTime(endingOfTheDay)));
                Log.d("Validate","Input data are not valid");
            }

            /*for (DateRecord record: realm.where(DateRecord.class).findAll())
            {
                Log.d("Record",record.getBeginingOfDayTime());
                Log.d("Record",record.getEndingOfDayTime());
                Log.d("Record",record.getDate());
            }*/
        }
    }
    class ClearAllRecordsButtonClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            deleteAllRecords();
        }
    }
}
