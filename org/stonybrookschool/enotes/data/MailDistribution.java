package org.stonybrookschool.enotes.data;

import java.util.*;

import org.stonybrookschool.enotes.objects.*;

public class MailDistribution
{
    public static String formatEmailAddress( String title, String first, String last, String email ) {
        StringBuffer b = new StringBuffer();
        b.append( title );
        b.append( " " );
        b.append( first );
        b.append( " " );
        b.append( last );
        b.append( "<" );
        b.append( email );
        b.append( ">" );
        return( b.toString() );
    } // formatEmailAddress()

    private static Vector getCommonRecipients( int studentId, Enote e ) {
        Vector list = new Vector();
        User u;

        // get a list of parents of this student.
        Vector buffer = DBRoutines.getAssociations( studentId );
        for (int i = 0, j = buffer.size(); i < j; i++) {
            u = (User)buffer.elementAt(i);
            list.add( formatEmailAddress( "", u.getFirst(), u.getLast(), u.getEmail() ) );
        }

        User student = DBRoutines.getUser( studentId );
        if ( !("Day Student".equals( student.getHomeName() )) ) {
            buffer = DBRoutines.getFacultyHome( student.getHomeId() );
            for (int i = 0, j = buffer.size(); i < j; i++) {
                u = (User)buffer.elementAt(i);
                list.add( formatEmailAddress( "", u.getFirst(), u.getLast(), u.getEmail() ));
            }
        }

        User counselor = DBRoutines.getUser( student.getCounselorId() );
        list.add( formatEmailAddress( "", counselor.getFirst(), counselor.getLast(), counselor.getEmail()));
        list.add( formatEmailAddress( "", student.getFirst(), student.getLast(), student.getEmail()));

        User teacher = DBRoutines.getUser( e.getTeacherUserId() );
        list.add( formatEmailAddress( teacher.getTitle(), teacher.getFirst(), teacher.getLast(), teacher.getEmail()));

        return( list );
    } // getCommonRecipients()


    public static Vector getQuicknoteRecipients( Quicknote q ) {
        Vector list = getCommonRecipients( q.getStudentUserId(), q );

        return( list );
    } // getQuicknoteRecipients()


    public static Vector getDisciplineRecipients( Discipline d ) {
        Vector list = getCommonRecipients( d.getStudentUserId(), d );

        // list.add( DBRoutines.getDisciplinarian() );

        return( list );
    } // getDisciplineRecipients()


    public static Vector getCommentRecipients( ) {
	Event event = DBRoutines.getConfiguredEvent();
	Vector students = DBRoutines.getStudentsWhoRcvdComment( event.getEventId() );
	Vector parents = new Vector();
	Vector teachers = DBRoutines.getByGroupName( "faculty" ); 
	Vector buffer;
	User u;
	String previous;
        String[] emails;

	for ( int i = 0, j = students.size(); i < j; i++ ) {
	     buffer = DBRoutines.getAssociations( ((User)students.elementAt(i)).getUserId() );
	     for (int k = 0, l = buffer.size(); k < l; k++) {
		 parents.add( (User)buffer.elementAt(k) );
	     }
	} // for

	buffer = new Vector();
	for (int i = 0, j = students.size(); i < j; i++) {
	    u = (User)students.elementAt(i);
	    buffer.add( formatEmailAddress( "", u.getFirst(), u.getLast(), u.getEmail() ));
	}

	for (int i = 0, j = parents.size(); i < j; i++) {
	    u = (User)parents.elementAt(i);
	    buffer.add( formatEmailAddress( "", u.getFirst(), u.getLast(), u.getEmail() ));
	}

	for (int i = 0, j = teachers.size(); i < j; i++) {
	    u = (User)teachers.elementAt(i);
	    buffer.add( formatEmailAddress( "", u.getFirst(), u.getLast(), u.getEmail() ));
	}

        // sort them so that you can make a buffer without duplicate email addresses.
        emails = (String[])(buffer.toArray(new String[0]));
        java.util.Arrays.sort( emails );

        previous = "";
	buffer = new Vector();
	for (int i = 0, j = emails.length; i < j; i++ ) {
	    if ( !previous.equals(emails[i]) )
		buffer.add( emails[i] );
	    previous = emails[i];
	} // for

	return buffer;
    } // getCommentRecipients()
} // class MailDistribution
