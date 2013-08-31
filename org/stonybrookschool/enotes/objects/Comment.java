package org.stonybrookschool.enotes.objects;

import java.sql.*;

public class Comment extends Enote
{
    private int commentId;
    private boolean obsolete;
    // datetime is an Enote member
    private int studentUserId;
    private String studentFirst;
    private String studentLast;
    // sectionId is an Enote member
    private String sectionName;
    private String periodName;
    private int eventId;
    private String eventName;
    private int markId;
    private String markName;
    private String commentText;
    private String status;     // shows if email has been sent or not.

    public Comment(
        int commentId,
        boolean obsolete,
        Timestamp datetime,
        int studentUserId,
        String studentFirst,
        String studentLast,
        int sectionId,
        String sectionName,
        String periodName,
        int eventId,
        String eventName,
        int markId,
        String markName,
        String commentText,
        String status
    ) {
        super(datetime,sectionId);
        this.commentId = commentId;
        this.obsolete = obsolete;
        this.studentUserId = studentUserId;
        this.studentFirst = studentFirst;
        this.studentLast = studentLast;
        this.sectionName = sectionName;
        this.periodName = periodName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.markId = markId;
        this.markName = markName;
        this.commentText = commentText;
        this.status = status;
    }

    public static Comment getDefault() {
        return new Comment(
                   0,//    int commentId,
                   false,// boolean obsolete,
                   new Timestamp(new java.util.Date().getTime()),//   Timestamp datetime,
                   0,//    int studentUserId,
                   "",//   String studentFirst,
                   "",//   String studentLast,
                   0,//    int sectionId,
                   "",//   String sectionName,
                   "",//   String periodName,
                   0,//    int eventId,
                   "",//   String eventName,
                   0,//    int markId,
                   "",//   String markName,
                   "",//   String commentText,
                   "" //   String status
               );
    }

    public int getCommentId() { return commentId; }
    public boolean getObsolete() { return obsolete; }
    public Timestamp getDatetime() { return datetime; }
    public int getStudentUserId() { return studentUserId; }
    public String getStudentFirst() { return studentFirst; }
    public String getStudentLast() { return studentLast; }
    public int getSectionId() { return sectionId; }
    public String getSectionName() { return sectionName; }
    // public String getPeriodName() { return periodName; } // to be moved here from Enote.
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public int getMarkId() { return markId; }
    public String getMarkName() { return markName; }
    public String getCommentText() { return commentText; }
    public String getStatus() { return status; }

}
