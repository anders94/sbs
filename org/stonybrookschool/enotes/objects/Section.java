package org.stonybrookschool.enotes.objects;

import java.sql.*;

public class Section
{
    private int sectionId;
    private String sectionName;
    private int yearId;
    private int periodId;
    private String periodName;
    private int teacherUserId;
    private String teacherName;
    private String teacherFirst;
    private String teacherLast;
    private String teacherTitle;
    private String sectionNumber;
    private int departmentId;
    private String departmentName;
    private boolean honors;

    public Section(
        int sectionId,
        String sectionName,
        int yearId,
        int periodId,
        String periodName,
        int teacherUserId,
        String teacherName,
	String teacherFirst,
	String teacherLast,
	String teacherTitle,
        String sectionNumber,
        int departmentId,
        String departmentName,
        boolean honors
    ){
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.yearId = yearId;
        this.periodId = periodId;
        this.periodName = periodName;
        this.teacherUserId = teacherUserId;
        this.teacherName = teacherName;
        this.sectionNumber = sectionNumber;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.honors = honors;
    }
    public static Section getDefault() {
        return new Section(
                   0, // int sectionId,
                   "", // String sectionName,
                   0, // int yearId,
                   0, // int periodId,
                   "", // String periodName,
                   0, // int teacherUserId,
                   "", // String teacherName,
		   "", // String teacherFirst,
		   "", // String teacherLast,
		   "", // String teacherTitle,
                   "", // String sectionNumber,
                   0, // int departmentId,
                   "", // String departmentName,
                   false // boolean honors
               );
    } // getDefault()
    public int getSectionId() { return sectionId; }
    public String getSectionName() { return sectionName; }
    public int getYearId() { return yearId; }
    public int getPeriodId() { return periodId; }
    public String getPeriodName() { return periodName; }
    public int getTeacherUserId() { return teacherUserId; }
    public String getTeacherName() { return teacherName; }
    public String getTeacherFirst()  { return teacherFirst; }
    public String getTeacherLast()  { return teacherLast; }
    public String getTeacherTitle() { return teacherTitle; }
    public String getSectionNumber() { return sectionNumber; }
    public int getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public boolean getHonors() { return honors; }
}
