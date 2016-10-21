package com.example.vshurygin.timesupervisor;

import io.realm.RealmObject;

/**
 * Created by vshurygin on 20.10.2016.
 */

public class DateRecord extends RealmObject
{
    private String mTheBeginningOfDayTime;
    private String mTheEndingOfDayTime;
    private String mDate;
    private int mWeekNumber;

    public void setBeginningOfDayTime(String _value)
    {
        mTheBeginningOfDayTime = _value;
    }
    public void setEndingOfTheDayTime(String _value)
    {
        mTheEndingOfDayTime = _value;
    }
    public void setDate(String _value)
    {
        mDate = _value;
    }
    public void setWeekNumber(int _value)
    {
        mWeekNumber = _value;
    }

    public String getBeginningOfDayTime()
    {
        return mTheBeginningOfDayTime;
    }
    public String getEndingOfDayTime()
    {
        return mTheEndingOfDayTime;
    }
    public String getDate()
    {
        return mDate;
    }
    public int getWeekNumber()
    {
        return mWeekNumber;
    }
}
