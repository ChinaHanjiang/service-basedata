package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.show.ShowType;

import java.util.Date;

public class SchedulingWithShow {

    private int id;

    private int weekDay;

    private int hourFrom;

    private int minFrom;

    private int duration;

    private Date validFrom;

    private Date validTo;

    private Date base;

    private int weekType;

    private String text;

    private int overlap = 0;

    private String showName;

    private ShowType showType;

    public ShowType getShowType() {
        return showType;
    }

    public void setShowType(ShowType showType) {
        this.showType = showType;
    }

    public String getShowName() {
        return showName;
    }



    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getHourFrom() {
        return hourFrom;
    }

    public void setHourFrom(int hourFrom) {
        this.hourFrom = hourFrom;
    }

    public int getMinFrom() {
        return minFrom;
    }

    public void setMinFrom(int minFrom) {
        this.minFrom = minFrom;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getBase() {
        return base;
    }

    public void setBase(Date base) {
        this.base = base;
    }

    public int getWeekType() {
        return weekType;
    }

    public void setWeekType(int weekType) {
        this.weekType = weekType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOverlap() {
        return overlap;
    }

    public void setOverlap(int overlap) {
        this.overlap = overlap;
    }

    public void incrementOverlap() {
        this.overlap++;
    }

    protected Integer getWeekMinFrom(){
        return (getWeekDay() * 24 + getHourFrom()) * 60 + getMinFrom();
    }

    protected Integer getWeekMinTo(){
        return (getWeekDay() * 24 + getHourFrom()) * 60 + getMinFrom() + getDuration();
    }
}

