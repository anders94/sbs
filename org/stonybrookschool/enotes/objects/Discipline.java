package org.stonybrookschool.enotes.objects;

import java.sql.*;

public class Discipline extends Enote
{
    private int disciplineId;
    private boolean obsolete;
    // private Timestamp datetime;  // this is an Enote member
    private int studentUserId;
    private String studentFirst;    // looked up prior to construction
    private String studentLast;     // ditto.
    // private int sectionId;       // this is an Enote member
    private String sectionName;     // ditto.
    private String periodName;      // ditto.
    private int capId;
    private String offenseName;     // ditto.
    private String punishementName; // ditto.
    private String commentText;
    private String status;

    public Discipline(
        int disciplineId,
        boolean obsolete,
        Timestamp datetime,
        int studentUserId,
        String studentFirst,
        String studentLast,
        int sectionId,
        String sectionName,
        String periodName,
        int capId,
        String offenseName,
        String punishementName,
        String commentText,
        String status
    ) {
        super(datetime,sectionId);
        this.disciplineId = disciplineId;
        this.obsolete = obsolete;
        this.datetime = datetime;
        this.studentUserId = studentUserId;
        this.studentFirst = studentFirst;
        this.studentLast = studentLast;
        this.sectionName = sectionName;
        this.periodName = periodName;
        this.capId = capId;
        this.offenseName = offenseName;
        this.punishementName = punishementName;
        this.commentText = commentText;
        this.status = status;
    }

    public static Discipline getDefault() {
        return new Discipline (
                   0,//    int disciplineId,
                   false,//    boolean obsolete,
                   new Timestamp(new java.util.Date().getTime()),//    Timestamp datetime,
                   0,//    int studentUserId,
                   "",//    String studentFirst,
                   "",//   String studentLast,
                   0,//   int sectionId,
                   "",//   String sectionName,
                   "",//   String periodName,
                   0,//   int capId,
                   "",//   String offenseName,
                   "",//   String punishementName,
                   "",//   String commentText,
                   "" //   String status
               );
    }

    public int getDisciplineId() { return disciplineId; }
    public boolean getObsolete() { return obsolete; }
    public Timestamp getDatetime() { return datetime; }
    public int getStudentUserId() { return studentUserId; }
    public String getStudentFirst() { return studentFirst; }
    public String getStudentLast() { return studentLast; }
    public int getSectionId() { return sectionId; }
    public String getSectionName() { return sectionName; }
    // public String getPeriodName() { return periodName; } // in Enotes. move it here.
    public int getCapId() { return capId; }
    public String getOffenseName() { return offenseName; }
    public String getPunishmentName() { return punishementName; }
    public String getCommentText() { return commentText; }
    public String getStatus() { return status; }

}
