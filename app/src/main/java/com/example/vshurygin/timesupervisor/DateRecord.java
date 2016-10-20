package com.example.vshurygin.timesupervisor;

import io.realm.RealmObject;

/**
 * Created by vshurygin on 20.10.2016.
 */

public class DateRecord extends RealmObject
{
    private String mTheBeginingOfDayTime;
    private String mTheEndingOfDayTime;
    private String mDate;

    public void setBeginingOfDayTime(String _value)
    {
        mTheBeginingOfDayTime = _value;
    }
    public void setEndingOfTheDayTime(String _value)
    {
        mTheEndingOfDayTime = _value;
    }
    public void setDate(String _value)
    {
        mDate = _value;
    }

    public String getBeginingOfDayTime()
    {
        return mTheBeginingOfDayTime;
    }
    public String getEndingOfDayTime()
    {
        return mTheEndingOfDayTime;
    }
    public String getDate()
    {
        return mDate;
    }
}
