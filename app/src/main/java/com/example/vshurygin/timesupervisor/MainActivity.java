package com.example.vshurygin.timesupervisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.xml.validation.Validator;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private final int MINIMUM_WORK_TIME = 6;

    private Context mLocalContext;
    private Button mCalculateTimeButton;
    private Button mAddRecordTimeButton;
    private Button mClearAllRecordsButton;
    private EditText mTheBeginningOfTheDayEditText;
    private EditText mTheEndingOfTheDayEditText;
    private EditText mTodaysDateEditText;
    private EditText mWeekNumberEditText;
    private TextView mCalculatedTimeTextView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocalContext = this;

        Realm.init(this);

        /*RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.deleteRealm(config);*/

        realm = Realm.getDefaultInstance();

        mAddRecordTimeButton = (Button)findViewById(R.id.AddRecordButton);
        mAddRecordTimeButton.setOnClickListener(new AddRecordButtomClickListener());
        mClearAllRecordsButton = (Button)findViewById(R.id.ClearAllRecordsButton);
        mClearAllRecordsButton.setOnClickListener(new ClearAllRecordsButtonClickListener());
        mCalculateTimeButton = (Button)findViewById(R.id.CalculateOfTimeButton);
        mCalculateTimeButton.setOnClickListener(new CalculateOfTimeButtonListener());

        mTheBeginningOfTheDayEditText = (EditText)findViewById(R.id.TheBeginningOfTheDay);
        mTheEndingOfTheDayEditText = (EditText)findViewById(R.id.TheEndingOfTheDay);
        mTodaysDateEditText = (EditText)findViewById(R.id.TodaysDate);
        mWeekNumberEditText = (EditText)findViewById(R.id.WeekNumber);
        mCalculatedTimeTextView = (TextView)findViewById(R.id.CalculatedTime);
    }

    @Override
    protected void onDestroy()
    {
        realm.close();
    }

    private void addRecord(final String _theBeginningOfTheDay, final String _theEndingOfTheDay, final String _date, final int _weeknumber)
    {
        try
        {
            realm.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    DateRecord record = realm.createObject(DateRecord.class);
                    record.setBeginningOfDayTime(_theBeginningOfTheDay);
                    record.setEndingOfTheDayTime(_theEndingOfTheDay);
                    record.setDate(_date);
                    record.setWeekNumber(_weeknumber);
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

    private int calculateHoursForWeek(int _week)
    {
        int TotalHours = 0;

        int TotalMinute = 0;
        int TotalTime;

        for (DateRecord record : realm.where(DateRecord.class).findAll())
        {
            if (record.getWeekNumber() == _week)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                try {
                    Date Bdate = sdf.parse(record.getBeginningOfDayTime());
                    Calendar Bcalendar = GregorianCalendar.getInstance();
                    Bcalendar.setTime(Bdate);

                    int Bhour = Bcalendar.get(Calendar.HOUR_OF_DAY);
                    int Bminute = Bcalendar.get(Calendar.MINUTE);

                    Date Edate = sdf.parse(record.getEndingOfDayTime());
                    Calendar Ecalendar = GregorianCalendar.getInstance();
                    Ecalendar.setTime(Edate);

                    int Ehour = Ecalendar.get(Calendar.HOUR_OF_DAY);
                    int Eminute = Ecalendar.get(Calendar.MINUTE);

                    int Thours = Ehour - Bhour;
                    int Tminute = Eminute - Bminute;

                    if (Tminute < 0)
                    {
                        Thours--;
                        Tminute = Math.abs(60 + Tminute);
                    }
                    Log.d("TH",String.valueOf(Thours) + " TM: "+ String.valueOf(Tminute));

                    if (Thours >= MINIMUM_WORK_TIME)
                    {
                        TotalHours += Thours - MINIMUM_WORK_TIME;

                        if(TotalMinute + Tminute >= 60)
                        {
                            TotalHours++;
                            TotalMinute = Math.abs(60 - (TotalMinute + Tminute));
                        }
                        else
                        {
                            TotalHours += Tminute;
                        }
                    }
                    else
                    {


                        if (Tminute > 0)
                        {
                            TotalHours -= (MINIMUM_WORK_TIME - (Thours+1));
                            if (TotalMinute - (60 - Tminute) < 0)
                            {
                                //TotalHours -= 1;
                                TotalMinute = 60 - Math.abs(TotalMinute - (60 - Tminute));
                            }
                            else
                            {
                                TotalMinute -= (60 - Tminute);
                            }

                        }
                        else
                        {
                            TotalHours -= (MINIMUM_WORK_TIME - Thours);
                        }
                    }
                   /* if(Thours >= MINIMUM_WORK_TIME)
                    {
                        TotalHours += Thours - MINIMUM_WORK_TIME;

                        if ((TotalMinute + Tminute) >= 60)
                        {
                            TotalHours++;
                            TotalMinute = Math.abs(60 - (TotalMinute + Tminute));
                        }
                        else
                        {
                            TotalMinute += Tminute;
                        }
                    }
                    else
                    {
                        TotalHours -= Math.abs(Thours - MINIMUM_WORK_TIME);

                        if((TotalMinute - Tminute) < 0)//-50
                        {
                            TotalHours++;
                            TotalMinute = 60 - Math.abs(TotalMinute - Tminute);
                        }
                        else
                        {
                            TotalMinute -= Tminute;
                        }

                    }*/
                    Log.d("H",String.valueOf(TotalHours) + " M: "+ String.valueOf(TotalMinute));
                    /*TotalHours += Thours;
                    if ((TotalMinute + Tminute) >= 60)
                    {
                        TotalHours++;
                        TotalMinute = Math.abs(60 - (TotalMinute + Tminute));
                    }
                    else
                    {
                        TotalMinute += Tminute;
                    }*/
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        if (TotalHours > 0)
        {
            TotalTime = (Math.abs(TotalHours)*100+Math.abs(TotalMinute));
        }
        else
        {
            TotalTime = (Math.abs(TotalHours)*100+Math.abs(TotalMinute)) * -1;
        }

        return TotalTime;
    }

    class AddRecordButtomClickListener implements View.OnClickListener
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
            String beginningOfTheDay = mTheBeginningOfTheDayEditText.getText().toString();
            String endingOfTheDay = mTheEndingOfTheDayEditText.getText().toString();
            String date = mTodaysDateEditText.getText().toString();

            String sWeekNumber = mWeekNumberEditText.getText().toString();
            int weeknumber;

            if((checkDate(date))&&(checkTime(beginningOfTheDay))&&(checkTime(endingOfTheDay))&&(sWeekNumber != ""))
            {
                weeknumber = Integer.parseInt(sWeekNumber);
                addRecord(beginningOfTheDay,endingOfTheDay,date,weeknumber);
            }
            else
            {
                Log.d("Validate",String.valueOf(checkDate(date)));
                Log.d("Validate",String.valueOf(checkTime(beginningOfTheDay)));
                Log.d("Validate",String.valueOf(checkTime(endingOfTheDay)));
                Log.d("Validate","Input data are not valid");
            }
            //calculateHoursForLastWeek();
            /*for (DateRecord record: realm.where(DateRecord.class).findAll())
            {
                Log.d("Record",record.getBeginningOfDayTime());
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
    class CalculateOfTimeButtonListener implements  View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if(!mWeekNumberEditText.getText().toString().equals(""))
            {
                String sWeekNumber = mWeekNumberEditText.getText().toString();
                int weeknumber = Integer.parseInt(sWeekNumber);

                int totalHours = calculateHoursForWeek(weeknumber);
                Log.d("Hours",String.valueOf(totalHours));
                int hours = totalHours / 100;
                int minute = totalHours % 100;
                if(minute < 0)
                {
                    minute *= -1;
                }


                mCalculatedTimeTextView.setText(String.valueOf(hours)+":"+String.valueOf(minute));
            }
            else
            {
                Toast.makeText(mLocalContext,R.string.EmptyWeek,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
