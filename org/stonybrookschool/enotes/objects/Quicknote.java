package org.stonybrookschool.enotes.objects;

import java.sql.*;

public class Quicknote extends Enote
{
    private int quicknoteId;
    private boolean obsolete;
    // datetime is an Enote property
    private int studentUserId;
    private String studentFirst;
    private String studentLast;
    // sectionId is an Enote property
    private String sectionName;
    private String periodName;
    private int quicknoteTypeId;
    private String quicknoteTypeName;
    private String commentText;
    private boolean published;
    private String status;

    public Quicknote(
        int quicknoteId,
        boolean obsolete,
        Timestamp datetime, // this is an Enote property
        int studentUserId,
        String studentFirst,
        String studentLast,
        int sectionId,  // this is an Enote property
        String sectionName,
        String periodName,
        int quicknoteTypeId,
        String quicknoteTypeName,
        String commentText,
        boolean published,
        String status

    ) {
        super(datetime,sectionId);
        this.quicknoteId = quicknoteId;
        this.obsolete = obsolete;
        this.studentUserId = studentUserId;
        this.studentFirst = studentFirst;
        this.studentLast = studentLast;
        this.sectionName = sectionName;
        this.periodName = periodName;
        this.quicknoteTypeId = quicknoteTypeId;
        this.quicknoteTypeName = quicknoteTypeName;
        this.commentText = commentText;
        this.published = published;
        this.status = status;
    }

    public static Quicknote getDefault() {
        return new Quicknote(
                   0,//   int quicknoteId,
                   false,//   boolean obsolete,
                   new Timestamp(new java.util.Date().getTime()),//   Timestamp datetime,
                   0,//   int studentUserId,
                   "",//   String studentFirst,
                   "",//   String studentLast,
                   0,//   int sectionId,
                   "",//  String sectionName,
                   "",//  String periodName,
                   0,//   int quicknoteTypeId,
                   "",//   String quicknoteTypeName,
                   "",//   String commentText,
                   false,//   boolean published,
                   "" //   String status
               );
    }

    public int       getQuicknoteId() { return quicknoteId; }
    public boolean   getObsolete() { return obsolete; }
    public Timestamp getDatetime() { return datetime; }
    public int       getStudentUserId() { return studentUserId; }
    public String    getStudentFirst() { return studentFirst; }
    public String    getStudentLast() { return studentLast; }
    public int       getSectionId() { return sectionId; }
    public String    getSectionName(){ return sectionName; }
    // public String    getPeriodName() { return periodName; } // in Enote.  move it here.
    public int       getQuicknoteTypeId() { return quicknoteTypeId; }
    public String    getQuicknoteTypeName() { return quicknoteTypeName; }
    public String    getCommentText() { return commentText; }
    public boolean   getPublished() { return published; }
    public String    getStatus() { return status; }

}
