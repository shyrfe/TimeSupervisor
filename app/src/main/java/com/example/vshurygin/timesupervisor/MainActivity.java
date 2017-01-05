package com.example.vshurygin.timesupervisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.xml.validation.Validator;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
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
    private ListView mRecordsList;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        RealmResults<DateRecord> results = realm.where(DateRecord.class).findAll();
        ArrayList<DateRecord> allRecords = new ArrayList<>();
        for (DateRecord item:results) {
            allRecords.add(item);
        }

        mRecordsList = (ListView)findViewById(R.id.RecordsList);
        String[] testResult = {"Android","iOS","WindowsPhone"};
        ResultListAdapter adapter = new ResultListAdapter(this,allRecords);
        mRecordsList.setAdapter(adapter);

    }

    @Override
    protected void onDestroy()
    {
        try{
            super.onDestroy();
            realm.close();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void addRecord(final String _theBeginningOfTheDay, final String _theEndingOfTheDay, final String _date, final int _weeknumber) {
        try {
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
        catch (Exception e) {
            e.printStackTrace();
            Log.d("addRecords","Records are not Added");
        }
    }

    private void deleteAllRecords() {
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

    private int calculateHoursForWeek(int _week) {
        int TotalHours = 0;
        int TotalMinute = 0;

        int TotalTime;

        for (DateRecord record : realm.where(DateRecord.class).findAll()) {
            if (record.getWeekNumber() == _week) {
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


                    if (Tminute < 0) {
                        Thours--;
                        Tminute = Math.abs(60 + Tminute);
                    }
                    Log.d("TH",String.valueOf(Thours) + " TM: "+ String.valueOf(Tminute));


                    int WorkHour = Thours-6;
                    int WorkMinute = Tminute;

                    if((WorkHour < 0) && (WorkMinute != 0)) {
                        WorkMinute = 60 - WorkMinute;
                        WorkHour++;
                    }

                    if (WorkHour >= 0){
                        if(TotalHours >= 0){
                            if(TotalMinute + WorkMinute >= 60){
                                TotalHours = TotalHours + (WorkHour+1);
                                TotalMinute = (TotalMinute + WorkMinute) - 60;
                            }
                            else {
                                TotalHours = TotalHours + WorkHour;
                                TotalMinute = TotalMinute + WorkMinute;
                            }
                        }
                        else{
                            if((Math.abs(TotalHours) < WorkHour)) {
                                if((TotalMinute < WorkMinute)) {
                                    TotalHours = TotalHours + WorkHour;
                                    TotalMinute = WorkMinute - TotalMinute;
                                }
                                else{
                                    WorkHour--;
                                    TotalHours = TotalHours + WorkHour;
                                    TotalMinute = (60 + WorkMinute) - TotalMinute;
                                }
                            }
                            else{
                                if((TotalMinute >= WorkMinute)) {
                                    TotalHours = TotalHours + WorkHour;
                                    TotalMinute = TotalMinute - WorkMinute;
                                }
                                else{
                                    WorkHour++;
                                    TotalHours = TotalHours + WorkHour;
                                    TotalMinute = (60 + TotalMinute) - WorkMinute;
                                }
                            }
                            /*if(TotalMinute - WorkMinute >= 0){
                                //TotalHours = TotalHours + (WorkHour + 1);
                                TotalHours = TotalHours + WorkHour;
                                //TotalHours--;
                                TotalMinute = TotalMinute - WorkMinute;
                            }
                            else{
                                //TotalHours = TotalHours + WorkHour;
                                TotalHours = TotalHours + (WorkHour + 1);
                                TotalMinute = 60 + (TotalMinute - WorkMinute);
                            }*/
                        }
                    }
                    else {
                        if(TotalHours >= 0){
                            if(TotalMinute - WorkMinute >= 0){
                                TotalHours = TotalHours - Math.abs(WorkHour);
                                TotalMinute = TotalMinute - WorkMinute;
                            }
                            else {
                                TotalHours = TotalHours - (Math.abs(WorkHour) + 1);
                                TotalMinute = 60 + (TotalMinute - WorkMinute);
                            }
                        }
                        else{
                            if(TotalMinute + WorkMinute >= 60){
                                TotalHours = TotalHours - (Math.abs(WorkHour) + 1);
                                TotalMinute = 60 - (TotalMinute + WorkMinute);
                            }
                            else{
                                TotalHours = TotalHours - Math.abs(WorkHour);
                                TotalMinute = TotalMinute + WorkMinute;
                            }
                        }
                    }
                    Log.d("H",String.valueOf(TotalHours) + " M: "+ String.valueOf(TotalMinute));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /*TotalHours += TotalMinute / 60;
        TotalMinute = TotalMinute - ((TotalMinute/60)*60);*/

        if (TotalHours > 0) {
            TotalTime = (Math.abs(TotalHours)*100+Math.abs(TotalMinute));
        }
        else {
            TotalTime = (Math.abs(TotalHours)*100+Math.abs(TotalMinute)) * -1;
        }

        return TotalTime;
    }

    class AddRecordButtomClickListener implements View.OnClickListener {
        private boolean checkTime(String _value) {
            Pattern p = Pattern.compile("^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$");
            Matcher m = p.matcher(_value);
            return m.matches();
        }
        private boolean checkDate(String _value) {
            Pattern p = Pattern.compile("(0[1-9]|1[0-9]|2[0-9]|3[01])-(0[1-9]|1[012])-[0-9]{4}");
            Matcher m = p.matcher(_value);
            return m.matches();
        }

        @Override
        public void onClick(View v) {
            String beginningOfTheDay = mTheBeginningOfTheDayEditText.getText().toString();
            String endingOfTheDay = mTheEndingOfTheDayEditText.getText().toString();
            String date = mTodaysDateEditText.getText().toString();

            String sWeekNumber = mWeekNumberEditText.getText().toString();
            int weeknumber;

            if((checkDate(date))&&(checkTime(beginningOfTheDay))&&(checkTime(endingOfTheDay))&&(sWeekNumber != "")) {
                weeknumber = Integer.parseInt(sWeekNumber);
                addRecord(beginningOfTheDay,endingOfTheDay,date,weeknumber);
            }
            else {
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

    class ClearAllRecordsButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            deleteAllRecords();
        }
    }
    class CalculateOfTimeButtonListener implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!mWeekNumberEditText.getText().toString().equals("")) {
                String sWeekNumber = mWeekNumberEditText.getText().toString();
                int weeknumber = Integer.parseInt(sWeekNumber);

                int totalHours = calculateHoursForWeek(weeknumber);
                Log.d("Hours",String.valueOf(totalHours));
                int hours = totalHours / 100;
                int minute = totalHours % 100;
                if(minute < 0) {
                    minute *= -1;
                }


                mCalculatedTimeTextView.setText(String.valueOf(hours)+":"+String.valueOf(minute));
            }
            else {
                Toast.makeText(mLocalContext,R.string.EmptyWeek,Toast.LENGTH_SHORT).show();
            }
        }
    }
    class ResultListAdapter extends BaseAdapter{
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private ArrayList<DateRecord> mRecords;

        ResultListAdapter(Context _context, ArrayList<DateRecord> _records){
            mContext = _context;
            mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mRecords = _records;
        }

        @Override
        public int getCount(){
            return mRecords.size();
        }

        @Override
        public DateRecord getItem(int _position){
            return mRecords.get(_position);
        }

        @Override
        public long getItemId(int _position){
            return _position;
        }

        @Override
        public View getView(int _position, View _convertView, ViewGroup _parent){
            View view = _convertView;
            if(view == null){
                view = mLayoutInflater.inflate(R.layout.activity_listview,_parent,false);
            }
            DateRecord record = getItem(_position);

            ((TextView)view.findViewById(R.id.listItemWeekNumber)).setText(String.valueOf(record.getWeekNumber()));
            ((TextView)view.findViewById(R.id.listItemBeginningOfDayTime)).setText(record.getBeginningOfDayTime());
            ((TextView)view.findViewById(R.id.listItemEndingOfDayTime)).setText(record.getEndingOfDayTime());
            ((TextView)view.findViewById(R.id.listItemDate)).setText(record.getDate());

            return view;
        }
    }
}
