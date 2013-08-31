package org.stonybrookschool.enotes.objects;

import java.sql.*;
import org.stonybrookschool.enotes.data.*;

public class Enote implements Comparable {
    protected Timestamp datetime;
    protected int sectionId;
    protected int teacherUserId;

    // These are only here temporarily.
    // In future versions, the teacher name info
    // will be pulled from the database before the Enote
    // object is constructed because we already have an
    // open db connection and a prepared statement that
    // gets that.  I put it here because it gets the
    // project out the door faster.

    // That is all true regarding db connections, but
    // it's ok (good even) for Enote to hold any field that is common
    // to all Enotes, like teacher name, teacher userId, etc.
    private String teacherTitle = null;
    private String teacherFirst = null;
    private String teacherLast = null;

    public Enote( Timestamp datetime, int sectionId ) {
        this.datetime = datetime;
        this.sectionId = sectionId;

        getTeacherNames();
    }

    // We might get into performance troubles by doing these
    // lookups here.  If so, we'll change it.
    public String getPeriodName() {
        Section s = DBRoutines.getSection( sectionId );
        return s.getPeriodName();
    }

    private void getTeacherNames() {
        Section s = DBRoutines.getSection( sectionId );
        teacherUserId = s.getTeacherUserId();
        User teacher = DBRoutines.getUser( teacherUserId );

        teacherTitle = teacher.getTitle();
        teacherFirst = teacher.getFirst();
        teacherLast  = teacher.getLast();

    } // getTeacherNames

    public String getTeacherTitle() {
        if (teacherTitle == null) getTeacherNames();
        return teacherTitle;
    } // getTeacherName

    public String getTeacherFirst() {
        if (teacherFirst == null) getTeacherNames();
        return teacherFirst;
    } // getTeacherName

    public String getTeacherLast() {
        if (teacherLast == null) getTeacherNames();
        return teacherLast;
    } // getTeacherName

    public int getTeacherUserId() {
        return teacherUserId;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Enote))
            return false;
        return ((Enote)o).datetime.equals(datetime);
    }

    public int compareTo(Object o) {
        if (!(o instanceof Enote)) return 0;
        Enote c = (Enote)o;
        // the minus (-) gives descending order.
        return -(datetime.compareTo(c.datetime));
    }




}
