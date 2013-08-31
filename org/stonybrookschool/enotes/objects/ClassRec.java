package org.stonybrookschool.enotes.objects;

import java.sql.*;

public class ClassRec
{
  private int classId;
  private String className;
  private int yearId;
  private int periodId;
  private int teacherUserId;
  private String sectionNumber;
  private int departmentId;
  private String departmentName;

  public ClassRec(
    int classId,
    String className,
    int yearId,
    int periodId,
    int teacherUserId,
    String sectionNumber,
    int departmentId

  ){
    this.classId = classId; 
    this.className = className; 
    this.yearId = yearId;
    this.periodId = periodId;
    this.teacherUserId = teacherUserId;
    this.sectionNumber = sectionNumber;
    this.departmentId = departmentId;
  }

  public int getClassId() { return classId; }
  public String getClassName() { return className; }
  public int getYearId() { return yearId; }
  public int getPeriodId() { return periodId; }
  public int getTeacherUserId() { return teacherUserId; }
  public String getSectionNumber() { return sectionNumber; }
  public String getDepartmentName() { return departmentName; }
}
