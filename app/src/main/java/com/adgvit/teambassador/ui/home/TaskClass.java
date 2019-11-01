package com.adgvit.teambassador.ui.home;

public class TaskClass
{
    private String Description;
    private String Status;
    private String DaysLeft;

    public TaskClass(String description, String status, String daysLeft)
    {
        Description = description;
        Status = status;
        DaysLeft = daysLeft;
    }

    public TaskClass()
    {

    }
    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public String getStatus()
    {
        return Status;
    }

    public void setStatus(String status)
    {
        Status = status;
    }

    public String getDaysLeft() {
        return DaysLeft;
    }

    public void setDaysLeft(String daysLeft) {
        DaysLeft = daysLeft;
    }
}
