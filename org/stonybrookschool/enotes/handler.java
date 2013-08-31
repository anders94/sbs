package org.stonybrookschool.enotes;

import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.*;

import javax.mail.*;
import javax.mail.internet.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.stonybrookschool.enotes.data.*;
import org.stonybrookschool.enotes.objects.*;

/* handler is the traffic cop for the enotes system. It handles
 * all get and post requests farming out all real logic to other
 * classes in the system. handler is the main servlet in the system.
 *
 * --------------------------------------------------------------- *
 * There are a few things we should do to this as follows.
 *
 * doAdminUpdateThisComment() calls doEditCommentConfirm()
 * there are corresponding calls for Quicknotes and Disciplines
 * those doEdit calls pass a pile of attributes to the .jsp
 * page that the .jsp page doesn't use.  That's an inefficiency
 * that should be cleaned up.
 *
 * We should merge the two setEditEnoteOneAttributes() routines.
 *
 * Is there anything we can do about having 30 routines that
 * are all almost identical?  Can we make one routine that accepts
 * commands or something?  Can we move all the parameter checking
 * to one place?
 *
 * --------------------------------------------------------------- *
 */

public class handler extends HttpServlet
{
    private SimpleDateFormat logsdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd" );
    private SimpleDateFormat emailsdf = new SimpleDateFormat( "EEEE, MMMM, d, yyyy" );

    private final static String ALL        = "all";
    private final static String DISCIPLINE = "discipline";
    private final static String COMMENT    = "comment";
    private final static String QUICKNOTE  = "quicknote";

    private final static String FACULTY	 = "faculty";
    private final static String PARENT   = "parent";
    private final static String STUDENT  = "student";

    private boolean debug       = true;
    private String templateBase = "/enotes/templates/";

    // Returns the value of the string s.
    private Integer parseInt( String s ) {
        Integer i = null;

        try {
            i = new Integer( Integer.parseInt( s ) );
        } catch (java.lang.NumberFormatException e) {
        }

        return i;
    } // parseInt

    private Timestamp parseTimestamp( String s ) {
        Timestamp t = null;

        try {
            t = new Timestamp( df.parse( s ).getTime() );
        } catch (java.text.ParseException e) {
        }
        return t;
    } // parseTimestamp

    private String capitalize( String s ) {
        if ( null == s ) return "Null";
        if ( s.length() <= 1 ) return s.toUpperCase();
        String first = s.substring( 0, 1 );
        String rest = s.substring( 1, s.length());
        return first.toUpperCase() + rest;
    } // capitalize()

    // compares enoteType to predefined possibilities
    // not case sensitive.
    private boolean validEnoteType( String enoteType ) {
        String cmp = enoteType.toLowerCase();

        if ( DISCIPLINE.equals( cmp ) ) return true;
        else if ( COMMENT.equals( cmp ) ) return true;
        else if ( QUICKNOTE.equals( cmp ) ) return true;
        else return false;
    } // validEnoteType()

    public void doGet (HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
    {
        handle(req, res);
    }

    public void doPost (HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
    {
        handle(req, res);
    }

    public void handle (HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
    {
        String template = null;
        String page = req.getParameter( "page" );
        res.setContentType( "text/html" );

        if ( page == null ) {
            template = templateBase + "login.jsp";
        } else {
            if ( page.equals( "processLogin" ) )
                template = doProcessLogin( req );
            else {
                User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );
                if ( u != null ) { // if the session contains a user object (ie: is logged in)

                    if ( page.equals( "logout" ) ) {
                        template = doLogout( req );
                    } else if ( page.equals( "sendFeedbackStart" ) ) {
                        template = doSendFeedbackStart( req );
                    } else if ( page.equals( "sendFeedback" ) ) {
                        template = doSendFeedback( req );
                    } else if (FACULTY.equals( u.getGroupName() )) {
                        if ("t".equals( u.getSuperuser() )) {
                            if ( page.equals( "adminUserList" ) )
                                template = doAdminUserList( req );
                            else if ( page.equals( "adminUserOne" ) )
                                template = doAdminUserOne( req );
                            else if ( page.equals( "adminUserSubmit" ) )
                                template = doAdminUserSubmit( req );
                            else if ( page.equals( "adminUserEditLink" ) )
                                template = doAdminUserEditLink( req );
                            else if ( page.equals( "adminUserLinkDelete" ) )
                                template = doAdminUserLinkDelete( req );
                            else if ( page.equals( "adminUserEditConfirm" ) )
                                template = doAdminUserEditConfirm( req );

                            else if ( page.equals( "adminQuicknoteStart" ) )
                                template = doAdminQuicknoteStart( req );
                            else if ( page.equals( "adminSendThisQuicknote" ) )
                                template = doAdminSendThisQuicknote( req );
                            else if ( page.equals( "adminDeleteThisQuicknote" ) )
                                template = doAdminDeleteThisQuicknote( req );
                            else if ( page.equals( "adminEditThisQuicknote" ) )
                                template = doAdminEditThisQuicknote( req );
                            else if ( page.equals( "adminUpdateThisQuicknote" ) )
                                template = doAdminUpdateThisQuicknote( req );

                            else if ( page.equals( "adminDisciplineStart" ) )
                                template = doAdminDisciplineStart( req );
                            else if ( page.equals( "adminSendThisDiscipline" ) )
                                template = doAdminSendThisDiscipline( req );
                            else if ( page.equals( "adminDeleteThisDiscipline" ) )
                                template = doAdminDeleteThisDiscipline( req );
                            else if ( page.equals( "adminEditThisDiscipline" ) )
                                template = doAdminEditThisDiscipline( req );
                            else if ( page.equals( "adminUpdateThisDiscipline" ) )
                                template = doAdminUpdateThisDiscipline( req );

                            else if ( page.equals( "adminCommentStart" ) )
                                template = doAdminCommentStart( req );
                            else if ( page.equals( "adminCommentPerGrade" ) )
                                template = doAdminCommentPerGrade( req );
                            else if ( page.equals( "adminMarkThisComment" ) )
                                template = doAdminMarkThisComment( req );
                            else if ( page.equals( "adminEditThisComment" ) )
                                template = doAdminEditThisComment( req );
                            else if ( page.equals( "adminUpdateThisComment" ) )
                                template = doAdminUpdateThisComment( req );

                            else if ( page.equals( "adminAnnounceComments" ) )
                                template = doAdminAnnounceComments( req );
                            else if ( page.equals( "adminAnnounceCommentsConfirm" ) )
                                template = doAdminAnnounceCommentsConfirm( req );

                            else if ( page.equals( "adminConfigureStart" ) )
                                template = doAdminConfigureStart( req );
                            else if ( page.equals( "adminConfigureConfirm" ) )
                                template = doAdminConfigureConfirm( req );
			    else if ( page.equals( "adminCommentEventStart" ) )
				template = doAdminCommentEventStart( req );
			    else if ( page.equals( "adminCommentEventConfirm" ) )
				template = doAdminCommentEventConfirm( req );

                            else if ( page.equals( "adminSectionList" ) )
                                template = doAdminSectionList( req );
                            else if ( page.equals( "adminSectionEdit" ) )
                                template = doAdminSectionEdit( req );
                            else if ( page.equals( "adminSectionUpdate" ) )
                                template = doAdminSectionUpdate( req );
                            else if ( page.equals( "adminSectionDelete" ) )
                                template = doAdminSectionDelete( req );

                            else if ( page.equals( "adminError" ) )
                                template = doAdminError( req );
                            else if ( page.equals( "adminEmailTest" ) )
                                template = doAdminTest( req );
                            else if ( page.equals( "adminGeneralTest" ) )
                                template = doAdminGeneralTest( req );

                        } // if superuser

                        if ( null == template) {
                            // This user may or may not be a superuser. Whoever they are,
                            // they didn't make any of the superuser requests.
                            // Test for the general faculty requests.

                            if ( page.equals( "editSectionStart" ) )
                                template = doEditSectionStart( req );
                            else if ( page.equals( "editSectionDelete" ) )
                                template = doEditSectionDelete( req );
                            else if ( page.equals( "editSectionAddStudent" ) )
                                template = doEditSectionAddStudent( req );
                            else if ( page.equals( "editSectionConfirm" ) )
                                template = doEditSectionConfirm( req );

                            else if ( page.equals( "editDisciplineStart" ) )
                                template = doEditDisciplineStart( req );
                            else if ( page.equals( "editQuicknoteStart" ) )
                                template = doEditQuicknoteStart( req );
                            else if ( page.equals( "editCommentStart" ) )
                                template = doEditCommentStart( req );

                            else if ( page.equals( "editEnoteSection" ) )
                                template = doEditEnoteSection( req );
                            else if ( page.equals( "editEnoteOne") )
                                template = doEditEnoteOne( req );
                            else if ( page.equals( "editEnoteConfirm" ) )
                                template = doEditEnoteConfirm( req );

                            else if ( page.equals( "editCommentOne") )
                                template = doEditCommentOne( req );
                            else if ( page.equals( "editDisciplineOne") )
                                template = doEditDisciplineOne( req );
                            else if ( page.equals( "editQuicknoteOne") )
                                template = doEditQuicknoteOne( req );

                            else if ( page.equals( "editDeleteThisDiscipline") )
                                template = doEditDeleteThisDiscipline( req );
                            else if ( page.equals( "editDeleteThisQuicknote") )
                                template = doEditDeleteThisQuicknote( req );
                            else if ( page.equals( "editDeleteThisComment") )
                                template = doEditDeleteThisComment( req );

                            else if ( page.equals( "viewfaculty" ) )
                                template = doViewFacultyStart( req );
                            else if ( page.equals( "viewFacultyList" ) )
                                template = doViewFacultyList( req );
                            else if ( page.equals( "viewFacultyOne" ) )
                                template = doViewFacultyOne( req );
                            else if ( page.equals( "viewFacultyCounselees" ) )
                                template = doViewFacultyCounselees( req );
                            else if ( page.equals( "viewFacultyGradechair" ) )
                                template = doViewFacultyGradechair( req );
                            else
                                template = doTbd( req );
                        } // if template == null
                    } else if ( PARENT.equals( u.getGroupName() )) {
                        if ( page.equals( "viewparent" ) )
                            template = doViewParentList( req );
                        else if ( page.equals( "viewParentOne" ) )
                            template = doViewParentList( req );
                        else
                            template = doTbd( req );
                    } else if ( STUDENT.equals( u.getGroupName() )) {
                        if ( page.equals( "viewstudent" ) )
                            template = doViewStudent( req );
                        else
                            template = doTbd( req );
                    } else {
                        template = doTbd( req );
                    }
                } else {
                    System.err.println(
                        logsdf.format(  new java.util.Date() ) +
                        " eNotes: handler.java: handle(): invalid session" );
                    template = templateBase + "login.jsp";
                }
            }
        }

        // execute the jsp page
        RequestDispatcher rd = req.getRequestDispatcher( template );

        if ( rd == null ) {
            rd = req.getRequestDispatcher( templateBase + "error.jsp" );

            if (rd == null) {
                System.err.println( "handler.java: no default error template!" );
                throw new ServletException( "default error template not found!" );
            }
        }

        rd.include( req, res );

    } // handle()


    private String doTbd( HttpServletRequest req ) {
        String template = templateBase + "tbd.jsp";
        String feature = req.getParameter( "page" );

        if ( feature == null) feature = "desired feature.";
        req.setAttribute( "tbd", feature );
        return( template );
    }


    private String doLogin( HttpServletRequest req ) {
        String template = templateBase + "login.jsp";

        return( template );
    } // doLogin()


    private String doProcessLogin( HttpServletRequest req ) {
        String username = "";
        String password = "";
        String template;

/*      // This is an experiment, to learn what we have:
	// System.out.println( "debug: req.getAuthType(): " + req.getAuthType() );
	// System.out.println( "debug: req.getContextPath(): " + req.getContextPath());
	// System.out.println( "debug: req.getHeaderNames(): " + req.getHeaderNames().toString() );
	for (Enumeration e = req.getHeaderNames() ; e.hasMoreElements() ;) {
	    System.out.println("debug: req.getHeaderNames(): " + e.nextElement().toString());
        }
	// System.out.println( "debug: req.getMethod(): " + req.getMethod() );
	// System.out.println( "debug: req.getPathInfo(): " + req.getPathInfo() );
	// System.out.println( "debug: req.getPathTranslated(): " + req.getPathTranslated() );
	// System.out.println( "debug: req.getQueryString(): " + req.getQueryString() );
	// System.out.println( "debug: req.getRemoteUser(): " + req.getRemoteUser() );
	System.out.println( "debug: req.getRequestedSessionId(): " + req.getRequestedSessionId() );
	System.out.println( "debug: req.getRequestURI(): " + req.getRequestURI() );
	// System.out.println( "debug: req.getRequestURL(): " + req.getRequestURL().toString() );
	// System.out.println( "debug: req.getSession(): " + req.getSession().toString() );
*/

        if ( req.getParameter( "username" ) != null )
            username = req.getParameter( "username" );
        if ( req.getParameter( "password" ) != null )
            password = req.getParameter( "password" );

        if ( DBRoutines.checkPassword( username, password ) ) {
            User u = DBRoutines.getUser( username );
            req.getSession( ).setAttribute( "org.stonybrookschool.enotes.objects.user", u );
            StringBuffer log = new StringBuffer( logsdf.format( new java.util.Date() ) );
            log.append( " eNotes login: " );
            log.append( u.getFirst() );
            log.append( " " );
            log.append( u.getLast() );
            log.append( " (" );
            log.append( u.getGroupName() );
            log.append( ")" );

            System.out.println( log.toString() );

            // This is the main entry point for all users.
            if ( FACULTY.equals( u.getGroupName() ) ) {

                template = doViewFacultyStart( req );

            } else if ( PARENT.equals( u.getGroupName() ) ) {

                template = doViewParentList( req );

            } else if ( STUDENT.equals( u.getGroupName() ) ) {

                template = doViewStudent( req );

            } else {
                req.setAttribute( "error", "your groupName is invalid" );
                template = templateBase + "error.jsp";
            }
        }
        else {
            req.getSession( ).setAttribute( "org.stonybrookschool.enotes.objects.user", null );
            req.setAttribute( "error", "incorrect username or password" );
            template = templateBase + "login.jsp";
        }

        return( template );
    } // doProcessLogin()


    private String makeEnoteType( String type ) {
        if ( null == type ) return ALL;

        if ( !validEnoteType( type )) return ALL;

        return type;
    } // doEnoteType()


    private String doViewStudent( HttpServletRequest req ) {
        String template;
        String enoteType = makeEnoteType( req.getParameter( "enoteType" ) );
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );

        req.setAttribute( "enoteType", capitalize( enoteType ) );
        req.setAttribute( "enoteList",
                          DBRoutines.getPubEnotesStudent(
                              u.getUserId(),
                              getEnoteSelectionMask( enoteType ) ));

        template = templateBase + "viewStudent.jsp";
        return( template );
    } // doViewStudent()


    private String doViewFacultyStart( HttpServletRequest req ) {
        String template;
        String enoteType = makeEnoteType( req.getParameter( "enoteType" ) );

        req.setAttribute( "enoteType", capitalize( enoteType ) );
        req.setAttribute( "dormList", DBRoutines.getHomes() );
        template = templateBase + "viewFacultyStart.jsp";

        return( template );
    } // doViewFacultyStart()


    private String doViewFacultyCounselees( HttpServletRequest req ) {
        String template;
        String enoteType = makeEnoteType( req.getParameter( "enoteType" ) );
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );

        req.setAttribute( "enoteList", 
	    DBRoutines.getPubEnotesFaculty( u, DBRoutines.COUNSELOR, getEnoteSelectionMask( enoteType )) );

        template = templateBase + "viewFacultyEnoteList.jsp";
        return( template );
    } // doViewFacultyCounselees()


    private String doViewFacultyGradechair( HttpServletRequest req ) {
        String template;
        String enoteType = makeEnoteType( req.getParameter( "enoteType" ) );
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );

        req.setAttribute( "enoteList", 
	    DBRoutines.getPubEnotesFaculty( u, DBRoutines.GRADE_CHAIR, getEnoteSelectionMask( enoteType )) );

        template = templateBase + "viewFacultyEnoteList.jsp";
        return( template );
    } // doViewFacultyGradechair()


    private String doAdminError ( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        req.setAttribute( "error", "debug: test error page" );
        return( template );
    } // doAdminError()


    private String doViewFacultyOne( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String enoteType = makeEnoteType( req.getParameter( "enoteType" ) );
        String homeId = req.getParameter( "homeId" );
        String yearId = req.getParameter( "yearId" );
        String studentId = req.getParameter( "studentUserId" );

        Integer iHomeId = null;
        Integer iYearId = null;
        Integer iStudentId = null;

        if ( null == homeId ) req.setAttribute( "error", "no homeId specified" );
        else if ( null == yearId ) req.setAttribute( "error", "no yearId specified" );
        else if ( null == studentId ) req.setAttribute( "error", "no studentUserId specified" );
        else {
            iHomeId = parseInt( homeId );
            iYearId = parseInt( yearId );
            iStudentId = parseInt( studentId );

            if ( null == iHomeId ) req.setAttribute( "error", "invalid homeId" );
            else if ( null == iYearId ) req.setAttribute( "error", "invalid yearId" );
            else if ( null == iStudentId ) req.setAttribute( "error", "invalid studentUserId" );
            else {
                // set attributes here.

                req.setAttribute( "enoteType", capitalize ( enoteType ) );
                req.setAttribute( "dormList", DBRoutines.getHomes());
                req.setAttribute( "studentList",
                                  DBRoutines.getStudentsHomeYear( iHomeId.intValue(), iYearId.intValue()));
                req.setAttribute( "homeId", iHomeId );
                req.setAttribute( "yearId", iYearId );
                req.setAttribute( "student", DBRoutines.getUser( iStudentId.intValue() ) );
                req.setAttribute( "enoteList",
                                  DBRoutines.getPubEnotesStudent(
                                      iStudentId.intValue(),
                                      getEnoteSelectionMask(enoteType)));

                template = templateBase + "viewFacultyOne.jsp";
            }
        }

        return( template );

    } // doViewFacultyOne()


    private String doViewFacultyList( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String enoteType = makeEnoteType( req.getParameter( "enoteType" ) );
        String homeId = req.getParameter( "homeId" );
        String yearId = req.getParameter( "yearId" );

        Integer iHomeId = null;
        Integer iYearId = null;

        if ( null == homeId ) req.setAttribute( "error", "no homeId specified" );
        else if ( null == yearId ) req.setAttribute( "error", "no yearId specified" );
        else {
            iHomeId = parseInt( homeId );
            iYearId = parseInt( yearId );

            if ( null == iHomeId ) req.setAttribute( "error", "invalid homeId" );
            else if ( null == iYearId ) req.setAttribute( "error", "invalid yearId" );
            else {
                // set attributes here.
                req.setAttribute( "enoteType", capitalize( enoteType ) );
                req.setAttribute( "dormList", DBRoutines.getHomes());
                req.setAttribute( "studentList",
                                  DBRoutines.getStudentsHomeYear( iHomeId.intValue(), iYearId.intValue()) );
                req.setAttribute( "homeId", iHomeId );
                req.setAttribute( "yearId", iYearId );
                template = templateBase + "viewFacultyList.jsp";
            }
        }

        return( template );
    } // doViewFacultyList()


    private String doViewParentList(  HttpServletRequest req ) {
        String template;
        String enoteType = makeEnoteType( req.getParameter( "enoteType" ) );
        String studentId = req.getParameter( "studentId" );
        User student;
        User u;
        Integer iStudentId = null;

        if ( null != studentId ) iStudentId = parseInt( studentId );
        if ( null == iStudentId ) iStudentId = new Integer(0);

        u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );

        // Get the children of this parent.
        Vector v = DBRoutines.getAssociations( u.getUserId() );

        req.setAttribute( "enoteType", capitalize( enoteType ) );
        req.setAttribute( "studentList", v );

        if ( (v.size() > 1) && (0 == iStudentId.intValue()) ) {

            template = templateBase + "viewParentList.jsp";

        } else {
            if ( 0 != iStudentId.intValue()) {
                student = DBRoutines.getUser( iStudentId.intValue() );
                req.setAttribute( "enoteList",
                                  DBRoutines.getPubEnotesStudent(
                                      student.getUserId(),
                                      getEnoteSelectionMask( enoteType ) ));
            } else {
                if ( v.size() > 0 ) {
                    student = (User)v.elementAt(0);
                    req.setAttribute( "enoteList",
                                      DBRoutines.getPubEnotesStudent(
                                          student.getUserId(),
                                          getEnoteSelectionMask( enoteType ) ));

                } else {
                    student = User.getDefaultUser();
                    req.setAttribute( "enoteList", new Vector() );
                }
            }
            req.setAttribute( "student", student );

            template = templateBase + "viewParentOne.jsp";
        }

        return( template );
    } // doViewParentOne()


    private int getEnoteSelectionMask( String e ) {
        String cmp = e.toLowerCase();
        if ( ALL.equals( cmp ) )             return DBRoutines.ALL;
        else if ( DISCIPLINE.equals( cmp ) ) return DBRoutines.DISCIPLINE;
        else if ( COMMENT.equals( cmp ) )    return DBRoutines.COMMENT;
        else if ( QUICKNOTE.equals( cmp ) )  return DBRoutines.QUICKNOTE;
        else                               return DBRoutines.ALL;
    } // getEnoteSectionMask()


    private String doLogout( HttpServletRequest req )
    {
        req.getSession( ).setAttribute( "org.stonybrookschool.enotes.objects.user", null );
        // TODO: check to see if this does anything:
        req.setAttribute( "error", "you have successfully logged out" );

        String template = "/dailyBulletin";

        return( template );
    } // doLogout()

    private String doAdminUserList( HttpServletRequest req ) {
        Vector us = DBRoutines.getUsersNotSuperUsers();

        req.setAttribute( "users", us );

        return( templateBase + "adminUserList.jsp" );
    } // doAdminUserList()

    private String doAdminUserOne( HttpServletRequest req ) {
        String template = "";
        String userId = req.getParameter( "userId" );

        if ( userId != null ) {
            User u;
            Vector associations; // it's a list of parents of a student or
            // students of a parent.
            Integer iUserId = null;

            iUserId = parseInt( userId );

            // probably should flag an error here if iUserId == null,
            // but it's ok too to set it to 0.
            if (iUserId == null) iUserId = new Integer(0);

            if ( 0 == iUserId.intValue() ) {
                u = User.getDefaultUser();
                associations = new Vector();
            } else {
                u = DBRoutines.getUser( iUserId.intValue() );
                associations = DBRoutines.getAssociations( iUserId.intValue() );
            }

            req.setAttribute( "user", u);
            req.setAttribute( "parentStudents", associations );
            req.setAttribute( "teacherList", DBRoutines.getByGroupName( FACULTY ) );
            req.setAttribute( "homes", DBRoutines.getHomes() );
            req.setAttribute( "groups", DBRoutines.getGroups() );
            template = templateBase + "adminUserOne.jsp";
        }
        else {
            req.setAttribute( "error", "no userId specified" );
            template = templateBase + "error.jsp";
        }

        return( template );
    } // doAdminUserOne()

    private String doAdminUserSubmit( HttpServletRequest req ) {
        String template; // gets set just before leaving.
        String message = "unspecified error";
        StringBuffer dbmessage;     // message passed down to DBRoutines.
        User u = User.getDefaultUser();

        Integer userId = null;
        String strObsolete;
        Timestamp datetime;
        String first;
        String last;
        String title;
        String username;
        String passwrd1;
        String passwrd2;
        String email;
        Integer groupId = null;
        // groupName is not used here.
        Integer academicId = null;
        Integer yearId = null;
        Integer schoolId = null;
        Integer counselorId = null;
        // counselorName is not used here.
        Integer homeId = null;
        // homeName is not used here.
        // superuser is not here because that gets set directly
        // from a mysql prompt.

        String strUserId;
        String strAcademicId;
        String strYearId;
        String strSchoolId;
        String strCounselorId;
        String strGroupId;
        String strHomeId;

        // check everything out, if it's all good, call
        // u = DBRoutines.insertUser( new User(all that stuff), message );
        // you know that worked if u.getUserId() is not zero.

        strUserId = req.getParameter( "userId" );
        first = req.getParameter( "first" );
        last = req.getParameter( "last" );
        title = req.getParameter( "title" );
        username = req.getParameter( "username" );
        passwrd1 = req.getParameter( "password1" );
        passwrd2 = req.getParameter( "password2" );
        email = req.getParameter( "email" );
        strGroupId = req.getParameter( "groupId" );
        strAcademicId = req.getParameter( "academicId" );
        strYearId = req.getParameter( "yearId" );
        strSchoolId = req.getParameter( "schoolId" );
        strCounselorId = req.getParameter( "counselorId" );
        strHomeId = req.getParameter( "homeId" );

        if ( strUserId == null ) message = "userId is missing";
        else if ( first == null ) message = "first is missing";
        else if ( last == null ) message = "last is missing";
        else if ( title == null ) message = "title is missing";
        else if ( username == null ) message = "username is missing";
        else if ( passwrd1 == null ) message = "password1 is missing";
        else if ( passwrd2 == null ) message = "password2 is missing";
        else if ( email == null ) message = "email is missing";
        else if ( strGroupId == null ) message = "groupId is missing";
        else if ( strAcademicId == null ) message = "academicId is missing";
        else if ( strYearId == null ) message = "yearId is missing";
        else if ( strSchoolId == null ) message = "schoolId is missing";
        else if ( strCounselorId==null ) message = "counselorId is missing";
        else if ( strHomeId == null ) message = "homeId is missing";
        else if ( !passwrd2.equals(passwrd1) ) message="passwords don't match";
        else {
            userId = parseInt( strUserId );
            academicId = parseInt( strAcademicId );
            yearId = parseInt( strYearId );
            schoolId = parseInt( strSchoolId );
            counselorId = parseInt( strCounselorId );
            groupId = parseInt( strGroupId );
            homeId = parseInt( strHomeId );

            if ( null == userId ) message = "invalid userId";
            else if ( null == academicId ) message = "invalid academicId";
            else if ( null == yearId ) message = "invalid yearId";
            else if ( null == schoolId ) message = "invalid schoolId";
            else if ( null == counselorId ) message = "invalid counselorId";
            else if ( null == groupId ) message = "invalid groupId";
            else if ( null == homeId ) message = "invalid homeId";
            else {
                dbmessage = new StringBuffer( message );
                u = DBRoutines.insertUser(
                        new User(
                            userId.intValue(),       // userId
                            false,                   // obsolete
                            new Timestamp( new java.util.Date().getTime() ),  // datetime
                            first,                   // first
                            last,                    // last
                            title,                   // title
                            username,                // username
                            passwrd1,                // password
                            email,                   // email
                            groupId.intValue(),      // groupId
                            "",                      // groupName
                            academicId.intValue(),   // academicId
                            yearId.intValue(),       // yearId
                            schoolId.intValue(),     // schoolId
                            counselorId.intValue(),  // counselorId
                            "",                      // counselorUserName
                            homeId.intValue(),       // homeId
                            "",                      // homeName
                            ""                       // superuser (not used by insertUser())
                        ),
                        dbmessage
                    );
                message = dbmessage.toString();
            } // int parse checks
        } // param presence checks

        req.setAttribute( "user", u );
        req.setAttribute( "message", message );
        template = templateBase + "adminUserConfirm.jsp";
        return( template );
    } // doAdminUserSubmit()

    private String doAdminUserEditConfirm( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String userId = req.getParameter( "userId" );
        String associationId = req.getParameter( "associationId" );
        String oldAssociationId = req.getParameter( "oldAssociationId" );
        Integer iUserId = null;
        Integer iAssociationId = null;
        Integer iOldAssociationId = null;
        User u;

        if ( null == userId ) req.setAttribute( "error", "no userId specified" );
        else if ( null == associationId ) req.setAttribute( "error", "no associationId specified" );
        else if ( null == oldAssociationId ) req.setAttribute( "error", "no oldAssociationId specified" );
        else {
            iUserId = parseInt( userId );
            iAssociationId = parseInt( associationId );
            iOldAssociationId = parseInt( oldAssociationId );

            if (null == iUserId) {
                req.setAttribute( "error", "invalid userId" );
            } else if ( null == iAssociationId ) {
                req.setAttribute( "error", "invalid associationId" );
            } else if ( null == iOldAssociationId ) {
                req.setAttribute( "error", "invalid oldAssociationId" );
            } else {
                u = DBRoutines.getUser( iUserId.intValue() );

                if ( 0 == iOldAssociationId.intValue() ) {
                    // there is no old association, so this is an add operation.
                    if ( PARENT.equals( u.getGroupName() ) ) {
                        DBRoutines.insertAssoc(
                            iUserId.intValue(),
                            iAssociationId.intValue()
                        );
                    } else if ( STUDENT.equals( u.getGroupName() ) ) {
                        DBRoutines.insertAssoc(
                            iAssociationId.intValue(),
                            iUserId.intValue()
                        );
                    }
                } else {
                    if ( PARENT.equals(u.getGroupName())) {
                        // Association him with a different student.
                        DBRoutines.delAddAssoc(
                            iUserId.intValue(),
                            iOldAssociationId.intValue(),
                            iUserId.intValue(),
                            iAssociationId.intValue()
                        );
                    } else if ( STUDENT.equals(u.getGroupName())) {
                        // Association him with a different parent.
                        DBRoutines.delAddAssoc(
                            iOldAssociationId.intValue(),
                            iUserId.intValue(),
                            iAssociationId.intValue(),
                            iUserId.intValue()
                        );
                    }
                }

                req.setAttribute( "user", u );
                req.setAttribute( "parentStudents", DBRoutines.getAssociations( iUserId.intValue() ) );
                req.setAttribute( "teacherList", DBRoutines.getByGroupName( FACULTY ) );
                req.setAttribute( "homes", DBRoutines.getHomes() );
                req.setAttribute( "groups", DBRoutines.getGroups() );
                template = templateBase + "adminUserOne.jsp";
            } // parseInt tests
        } // passed param tests.

        return( template );
    } // doAdminUserEditConfirm()

    private String doAdminUserEditLink( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String parentId = req.getParameter( "parentId" );
        String studentId = req.getParameter( "studentId" );
        String userId = req.getParameter( "userId" );
        Integer iStudentId = null;
        Integer iParentId = null;
        Integer iUserId = null;

        if ( null == parentId ) {
            req.setAttribute( "error", "no parentId specified" );
        } else if ( null == studentId ) {
            req.setAttribute( "error", "no studentId specified" );
        } else if ( null == userId ) {
            req.setAttribute( "error", "no userId specified" );
        } else {
            iUserId = parseInt( userId );
            iParentId = parseInt( parentId );
            iStudentId = parseInt( studentId );

            if ( null == iUserId ) {
                req.setAttribute( "error", "invalid userId" );
            } else if ( null == iParentId ) {
                req.setAttribute( "error", "invalid parentId" );
            } else if ( null == iStudentId ) {
                req.setAttribute( "error", "invalid studentId" );
            } else {
                User u = DBRoutines.getUser( iUserId.intValue() );
                String attribName = "oldAssociationId";

                // Get a list of students that maybe should be
                // associated with this parent or visa versa.
                req.setAttribute( "associations", DBRoutines.getOthers( iUserId.intValue() ) );
                req.setAttribute( "user" , u );

                if ( PARENT.equals( u.getGroupName() ) ) {
                    req.setAttribute( attribName, iStudentId );
                } else if ( STUDENT.equals( u.getGroupName() ) ) {
                    req.setAttribute( attribName, iParentId );
                } else {
                    req.setAttribute( attribName, new Integer(0) );
                }

                template = templateBase + "adminUserEditLink.jsp";
            } // parse checks
        } // presence checks

        return( template );
    } // doAdminUserEditLink()

    private String doAdminUserLinkDelete( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String userId = req.getParameter( "userId" );
        String parentId = req.getParameter( "parentId" );
        String studentId = req.getParameter( "studentId" );
        Integer iStudentId = null;
        Integer iParentId = null;
        Integer iUserId = null;

        if ( null == parentId ) {
            req.setAttribute( "error", "no parentId specified" );
        } else if ( null == studentId ) {
            req.setAttribute( "error", "no studentId specified" );
        } else if ( null == userId ) {
            req.setAttribute( "error", "no userId specified" );
        } else {
            iUserId = parseInt( userId );
            iParentId = parseInt( parentId );
            iStudentId = parseInt( studentId );

            if ( null == iUserId ) {
                req.setAttribute( "error", "invalid userId" );
            } else if ( null == iParentId ) {
                req.setAttribute( "error", "invalid parentId" );
            } else if ( null == iStudentId ) {
                req.setAttribute( "error", "invalid studentId" );
            } else {

                DBRoutines.delAssoc( iParentId.intValue(), iStudentId.intValue() );

                req.setAttribute( "user", DBRoutines.getUser( iUserId.intValue() ) );
                req.setAttribute( "parentStudents", DBRoutines.getAssociations( iUserId.intValue() ) );
                req.setAttribute( "teacherList", DBRoutines.getByGroupName( FACULTY ) );
                req.setAttribute( "homes", DBRoutines.getHomes() );
                req.setAttribute( "groups", DBRoutines.getGroups() );
                template = templateBase + "adminUserOne.jsp";
            }
        }

        return( template );
    } // doAdminUserLinkDelete

    // main entry for editting section lists (when a teacher is editting the list of students
    // in his section).
    private String doEditSectionStart( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String sectionId = req.getParameter( "sectionId" );
        Integer iSectionId;

        if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
        else {
            iSectionId = parseInt( sectionId );

            if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
            else {

                req.setAttribute( "students",
                                  DBRoutines.getStudentsInThisSection( iSectionId.intValue() ) );

                req.setAttribute( "section",
                                  DBRoutines.getSection( iSectionId.intValue() ) );

                template = templateBase + "editSectionStart.jsp";
            }
        }

        return( template );
    } // doEditSectionStart()

    // Processes a request to delete a student from a section.
    private String doEditSectionDelete( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String sectionId = req.getParameter( "sectionId" );
        String studentId = req.getParameter( "studentId" );
        Integer iSectionId;
        Integer iStudentId;

        if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
        else if ( null == studentId ) req.setAttribute( "error", "no studentId specified" );
        else {
            iSectionId = parseInt( sectionId );
            iStudentId = parseInt( studentId );

            if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
            else if ( null == iStudentId ) req.setAttribute( "error", "invalid studentId" );
            else {

                DBRoutines.delSectionStudent(
                    iSectionId.intValue(),
                    iStudentId.intValue()
                );

                req.setAttribute( "students",
                                  DBRoutines.getStudentsInThisSection( iSectionId.intValue() ) );
                req.setAttribute( "section",
                                  DBRoutines.getSection( iSectionId.intValue() ));

                template = templateBase + "editSectionStart.jsp";
            }
        }

        return( template );
    } // doEditSectionDelete()

    private String doEditSectionAddStudent( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String sectionId = req.getParameter( "sectionId" );
        Integer iSectionId;

        if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
        else {
            iSectionId = parseInt( sectionId );

            if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
            else {

                req.setAttribute( "students",
                                  DBRoutines.getByGroupName( "student" ) );

                req.setAttribute( "section",
                                  DBRoutines.getSection( iSectionId.intValue() ));

                req.setAttribute( "sectionStudents",
                                  DBRoutines.getStudentsInThisSection( iSectionId.intValue() ));

                req.setAttribute( "message", " " );

                template = templateBase + "editSectionAddStudent.jsp";

            }
        }

        return( template );
    } // doEditSectionAdd()

    private String doEditSectionConfirm( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String sectionId = req.getParameter( "sectionId" );
        String studentId = req.getParameter( "studentId" );
        Integer iSectionId;
        Integer iStudentId;

        if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
        else if ( null == studentId ) req.setAttribute( "error", "no studentId specified" );
        else {
            iSectionId = parseInt( sectionId );
            iStudentId = parseInt( studentId );

            if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
            else if ( null == iStudentId ) req.setAttribute( "error", "invalid studentId" );
            else if ( 0 == iStudentId.intValue() ) req.setAttribute( "error", "Please choose a student.");
            else if ( 0 == iSectionId.intValue() ) req.setAttribute( "error", "Please choose a section. ");
            else {
                DBRoutines.insertStudentSection( iStudentId.intValue(), iSectionId.intValue() );

                req.setAttribute( "students",
                                  DBRoutines.getByGroupName( "student" ) );

                req.setAttribute( "section",
                                  DBRoutines.getSection( iSectionId.intValue() ));

                req.setAttribute( "sectionStudents",
                                  DBRoutines.getStudentsInThisSection( iSectionId.intValue() ));

                req.setAttribute( "message", " " );

                template = templateBase + "editSectionAddStudent.jsp";

            }
        }

        return( template );

    } // doEditSectionConfirm()

    private String doEditDisciplineStart( HttpServletRequest req ) {
        String oldEnoteType = (String) req.getSession( ).getAttribute(
                                  "org.stonybrookschool.enotes.objects.enoteType" );

        req.getSession( ).setAttribute(
            "org.stonybrookschool.enotes.objects.enoteType",
            capitalize(DISCIPLINE)
        );
        return doCommonEnoteStart( req );
    } // doEditDisciplineStart()

    private String doEditQuicknoteStart( HttpServletRequest req ) {
        String oldEnoteType = (String) req.getSession( ).getAttribute(
                                  "org.stonybrookschool.enotes.objects.enoteType" );

        req.getSession( ).setAttribute(
            "org.stonybrookschool.enotes.objects.enoteType",
            capitalize(QUICKNOTE)
        );
        return doCommonEnoteStart( req );

    } // doEditQuicknoteStart()

    private String doEditCommentStart( HttpServletRequest req ) {
        String oldEnoteType = (String) req.getSession( ).getAttribute(
                                  "org.stonybrookschool.enotes.objects.enoteType" );

        req.getSession( ).setAttribute(
            "org.stonybrookschool.enotes.objects.enoteType",
            capitalize(COMMENT)
        );
        return doCommonEnoteStart( req );

    } // doEditCommentStart()

    private String doCommonEnoteStart( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );

        if ( null == u ) req.setAttribute( "error", "no logged in user" );
        else {

            req.setAttribute( "sections",
                              DBRoutines.getSections( u.getUserId() ) );

            template = templateBase + "editEnoteStart.jsp";
        }

        return( template );

    } // doCommonEnoteStart()

    private String doEditEnoteSection( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String sectionId = req.getParameter( "sectionId" );
        Integer iSectionId;

        if ( null == u ) req.setAttribute( "error", "no logged in user" );
        else if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
        else {
            iSectionId = parseInt( sectionId );

            if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
            else {
                req.setAttribute( "sections",
                                  DBRoutines.getSections( u.getUserId() ) );

                req.setAttribute( "students",
                                  DBRoutines.getStudentsInThisSection( iSectionId.intValue() ) );

                req.setAttribute( "section",
                                  DBRoutines.getSection( iSectionId.intValue() ) );

                template = templateBase + "editEnoteSection.jsp";
            }
        }

        return( template );

    } // doEditEnoteSection()

    // existing enotes--author matched with enote.
    private void setEditEnoteOneAttributes(
        HttpServletRequest req,
        Integer iStudentId,
        Integer iSectionId,
        User u,
        Enote e,
        String message )
    {
        String enoteType = (String) req.getSession()
                           .getAttribute( "org.stonybrookschool.enotes.objects.enoteType" );
        String cmp = enoteType.toLowerCase();

        User student = DBRoutines.getUser( iStudentId.intValue() );
        Section section = DBRoutines.getSection( iSectionId.intValue() );

        req.setAttribute( "sections",
                          DBRoutines.getSections( u.getUserId() ) );

        req.setAttribute( "students",
                          DBRoutines.getStudentsInThisSection( iSectionId.intValue() ) );

        // get all enotes regardless of status jsp takes care of preventing
        // them from editing proofed or published enotes.
        req.setAttribute( "enotelist",
                          DBRoutines.getEnotesForStudentSection(
                              iStudentId.intValue(),
                              iSectionId.intValue()
                          )
                        );

        req.setAttribute( "student", student );

        req.setAttribute( "thisSection", section );

        req.setAttribute( "enote", e );

        if ( DISCIPLINE.equals( cmp ) ) {

            req.setAttribute( "capName" , DBRoutines.getCap() );

        } else if ( COMMENT.equals( cmp ) ) {

            req.setAttribute( "events" , DBRoutines.getEvents() );
            req.setAttribute( "marksName" , DBRoutines.getMarks() );

        } else {

            req.setAttribute( "quicknoteTypeName" , DBRoutines.getQuicknoteTypes() );

        }

        req.setAttribute( "message", message );

    } // setEditEnoteOneAttributes

    private void setEditEnoteOneAttributes(
        HttpServletRequest req,
        Integer iStudentId,
        Integer iSectionId,
        User u,
        Integer iEnoteId,
        String message )
    {
        String enoteType = (String) req.getSession()
                           .getAttribute( "org.stonybrookschool.enotes.objects.enoteType" );
        String cmp = enoteType.toLowerCase();

        User student = DBRoutines.getUser( iStudentId.intValue() );
        Section section = DBRoutines.getSection( iSectionId.intValue() );

        req.setAttribute( "sections",
                          DBRoutines.getSections( u.getUserId() ) );

        req.setAttribute( "students",
                          DBRoutines.getStudentsInThisSection( iSectionId.intValue() ) );

        // get all enotes regardless of status jsp takes care of preventing
        // them from editing proofed or published enotes.
        req.setAttribute( "enotelist",
                          DBRoutines.getEnotesForStudentSection(
                              iStudentId.intValue(),
                              iSectionId.intValue()
                          )
                        );

        req.setAttribute( "student", student );

        req.setAttribute( "thisSection", section );

        if ( DISCIPLINE.equals( cmp ) ) {
            if ( iEnoteId.intValue() == 0 )
                req.setAttribute( "enote",
                                  new Discipline(
                                      0,                       // disciplineId
                                      false,                   // obsolete
                                      new Timestamp(0),        // datetime
                                      student.getUserId(),     // studentUserId
                                      student.getFirst(),      // studentFirst
                                      student.getLast(),       // studentLast
                                      iSectionId.intValue(),   // sectionId
                                      section.getSectionName(),// sectionName
                                      section.getPeriodName(), // periodName
                                      0,                       // capId
                                      "",                      // offenseName
                                      "",                      // punishementName
                                      "",                      // commentText
                                      "pending"                // status
                                  )
                                );
            else
                req.setAttribute( "enote", DBRoutines.getDiscipline( iEnoteId.intValue() ));

            req.setAttribute( "capName" , DBRoutines.getCap() );

        } else if ( COMMENT.equals( cmp ) ) {
	    Event cce = DBRoutines.getConfiguredEvent(); // current configured event
            if ( iEnoteId.intValue() == 0 )
                req.setAttribute( "enote",
                                  new Comment(
                                      0,                                   // commentId
                                      false,                               // obsolete
                                      new Timestamp(new java.util.Date().getTime()), // datetime
                                      student.getUserId(),                 // studentUserId
                                      student.getFirst(),                  // studentFirst
                                      student.getLast(),                   // studentLast
                                      iSectionId.intValue(),               // sectionId
                                      section.getSectionName(),            // sectionName
                                      section.getPeriodName(),             // periodName
                                      cce.getEventId(), cce.getEventName(),// eventId, eventName
                                      0, "",                               // markId, markName
                                      "",                                  // commentText
                                      "pending"                            // status
                                  )
                                );
            else
                req.setAttribute( "enote", DBRoutines.getComment( iEnoteId.intValue()));

            req.setAttribute( "events" , DBRoutines.getEvents() );

            req.setAttribute( "marksName" , DBRoutines.getMarks() );

        } else {
            if ( iEnoteId.intValue() == 0 )
                req.setAttribute( "enote",
                                  new Quicknote(
                                      0,                                    // quicknoteId
                                      false,                                // obsolete
                                      new Timestamp( new java.util.Date().getTime()), // datetime
                                      student.getUserId(),                  // studentUserId
                                      student.getFirst(),                   // studentFirst
                                      student.getLast(),                    // studentLast
                                      iSectionId.intValue(),                // sectionId
                                      section.getSectionName(),
                                      section.getPeriodName(),
                                      0,                                    // quicknoteTypeId
                                      "",                                   // quicknoteTypeName
                                      "",                                   // commentText
                                      false,                                // published
                                      "pending"                             // status
                                  )
                                );
            else
                req.setAttribute( "enote", DBRoutines.getQuicknote( iEnoteId.intValue()));

            req.setAttribute( "quicknoteTypeName" , DBRoutines.getQuicknoteTypes() );

        } // "case" of enoteTypes

        req.setAttribute( "message", message );

    } // setEditEnoteOneAttributes


    // get teacher id from any enote, any status,
    private int getEnoteTeacher( String type, int id ) {
        int tid = -1;
        Enote e = null;
        String cmp = type.toLowerCase();

        if (COMMENT.equals( cmp )) {
            e = (Enote)DBRoutines.getComment( id );
        } else if (DISCIPLINE.equals( cmp )) {
            e = (Enote)DBRoutines.getDiscipline( id );
        } else {
            e = (Enote)DBRoutines.getQuicknote( id );
        }

        if ( e != null ) tid = e.getTeacherUserId();
        return( tid );
    }

    private String doEditEnoteOne( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( )
                 .getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String enoteType = (String) req.getSession()
                           .getAttribute( "org.stonybrookschool.enotes.objects.enoteType" );
        String studentId = req.getParameter( "studentId" );
        String sectionId = req.getParameter( "sectionId" );
        String enoteId = req.getParameter( "enoteId" );
        Integer iStudentId;
        Integer iSectionId;
        Integer iEnoteId;
        int    enoteTeacher;

        if ( null == u ) req.setAttribute( "error", "no logged in user" );
        else if ( null == enoteType ) req.setAttribute( "error", "no enoteType" );
        else if ( !validEnoteType(enoteType) ) req.setAttribute( "error", "invalid enoteType: "+enoteType );
        else if ( null == studentId ) req.setAttribute( "error", "no studentId specified" );
        else if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
        else if ( null == enoteId ) req.setAttribute( "error", "no enoteId specified" );
        else {
            iStudentId = parseInt( studentId );
            iSectionId = parseInt( sectionId );
            iEnoteId = parseInt( enoteId );

            if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
            else if ( null == iStudentId ) req.setAttribute( "error", "invalid studentId" );
            else if ( null == iEnoteId ) req.setAttribute( "error", "invalid enoteId" );
            else {

                if (    ( iEnoteId.intValue() == 0 )
                        || ( u.getUserId() == getEnoteTeacher( enoteType, iEnoteId.intValue() ) ) ) {
                    // Suppose a teacher uses the browser to edit a quicknote, logs out and leaves.
                    // Later another teacher comes.  The parameters to edit that quicknote appear
                    // in the IE pulldown.  Make sure we don't let the wrong person edit an enote.

                    setEditEnoteOneAttributes( req, iStudentId, iSectionId, u, iEnoteId, " " );
                    template = templateBase + "editEnoteOne.jsp";
                } else {
                    System.err.println("handler: doEditEnoteOne(): userId did not match");
                    req.setAttribute( "error", "This enote does not seem to have originated from your section." );
                }
            }
        }

        return( template );

    } // doEditEnoteOne()


    private String doEditDisciplineConfirm( HttpServletRequest req ) {
        String template;
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String message = "unspecified error";
        StringBuffer dbmessage;
        String disciplineId = req.getParameter( "disciplineId" );
        String enoteDate = req.getParameter( "timestamp" );
        String studentId = req.getParameter( "studentUserId" );
        String sectionId = req.getParameter( "sectionId" );
        String capId = req.getParameter( "capId" );
        String comment = req.getParameter( "commentText" );
        String commentTE;  // comment with tics escaped.
        // If Admin mode, enoteType is not set.
        req.getSession( ).setAttribute(
            "org.stonybrookschool.enotes.objects.enoteType",
            capitalize(DISCIPLINE)
        );

        Integer iDisciplineId;
        Timestamp tEnoteDate;
        Integer iStudentId = new Integer(0);
        Integer iSectionId = new Integer(0);
        Integer iCapId;

        if (null == u) {
            message = "no logged in user";
            u = User.getDefaultUser(); }
        else if (null == disciplineId) message = "no disciplineId";
        else if (null == enoteDate) message = "no timestamp";
        else if (null == studentId) message = "no studentUserId";
        else if (null == sectionId) message = "no sectionId";
        else if (null == capId) message = "no capId";
        else if (null == comment) message = "no commentText";
        else {
            iDisciplineId = parseInt( disciplineId );
            // for now the date is the current date and time
            // we might want to change the way it works later though.
            tEnoteDate = parseTimestamp( enoteDate );
            iStudentId = parseInt( studentId );
            iSectionId = parseInt( sectionId );
            iCapId = parseInt( capId );
            commentTE = DBRoutines.escapeTics( comment );

            if (null == iDisciplineId) message = "invalid disciplineId";
            else if (null == tEnoteDate) message = "invalid timestamp";
            else if (null == iStudentId) message = "invalid studentUserId";
            else if (null == iSectionId) message = "invalid sectionId";
            else if (null == iCapId) message = "invalid capId";
            else if (0 == iCapId.intValue()) message = "Please select an offense.";
            else {
                dbmessage = new StringBuffer( "" );
                DBRoutines.insertDiscipline(
                    new Discipline(
                        iDisciplineId.intValue(),
                        false,
                        tEnoteDate,
                        iStudentId.intValue(), "", "",
                        iSectionId.intValue(), "", "",
                        iCapId.intValue(), "", "",
                        commentTE,
                        "pending"
                    ),
                    dbmessage
                );

                message = dbmessage.toString();
            } // else everything looks good.

        } // presence checks

        setEditEnoteOneAttributes( req, iStudentId, iSectionId, u, new Integer(0), message );
        template = templateBase + "editEnoteOne.jsp";

        return( template );

    } // doEditDisciplineConfirm()

    private String doEditCommentConfirm( HttpServletRequest req ){
        String template;
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String message = "unspecified error";
        String commentId = req.getParameter( "commentId" );
        String enoteDate = req.getParameter( "timestamp" );
        String studentId = req.getParameter( "studentUserId" );
        String sectionId = req.getParameter( "sectionId" );
        String eventId = req.getParameter( "eventId" );
        String markId = req.getParameter( "markId" );
        String comment = req.getParameter( "commentText" );
        String commentTE;  // comment with tics escaped.
        StringBuffer dbmessage;
        // If Admin mode, enoteType is not set.
        req.getSession( ).setAttribute(
            "org.stonybrookschool.enotes.objects.enoteType",
            capitalize(COMMENT)
        );

        Integer iCommentId;
        Timestamp tEnoteDate;
        Integer iStudentId = new Integer(0);
        Integer iSectionId = new Integer(0);
        Integer iEventId;
        Integer iMarkId;

        if (null == u) {
            message = "no logged in user";
            u = User.getDefaultUser(); }
        else if (null == commentId) message = "no commentId";
        else if (null == enoteDate) message = "no timestamp";
        else if (null == studentId) message = "no studentUserId";
        else if (null == sectionId) message = "no sectionId";
        else if (null == eventId) message = "no eventId";
        else if (null == markId) message = "no markId";
        else if (null == comment) message = "no commentText";
        else {
            iCommentId = parseInt( commentId );
            tEnoteDate = parseTimestamp( enoteDate );
            iStudentId = parseInt( studentId );
            iSectionId = parseInt( sectionId );
            iEventId = parseInt( eventId );
            iMarkId = parseInt( markId );
            commentTE = DBRoutines.escapeTics( comment );

            if (null == iCommentId) message = "invalid commentId";
            else if (null == tEnoteDate) message = "invalid timestamp";
            else if (null == iStudentId) message = "invalid studentUserId";
            else if (null == iSectionId) message = "invalid sectionId";
            else if (null == iEventId) message = "invalid eventId";
            else if (null == iMarkId) message = "invalid markId";
            else if (0 == iMarkId.intValue()) message = "Please select a mark.";
            else if (0 == iEventId.intValue()) message = "Please select a comment type.";
            else {
                dbmessage = new StringBuffer( "" );
                DBRoutines.insertComment(
                    new Comment(
                        iCommentId.intValue(),
                        false,
                        new Timestamp( new java.util.Date().getTime() ),
                        iStudentId.intValue(), "", "",
                        iSectionId.intValue(), "", "",
                        iEventId.intValue(), "",
                        iMarkId.intValue(), "",
                        commentTE,
                        "pending"
                    ),
                    dbmessage
                );

                message = dbmessage.toString();
            }

        } // presence checks

        setEditEnoteOneAttributes( req, iStudentId, iSectionId, u, new Integer(0), message );
        template = templateBase + "editEnoteOne.jsp";

        return( template );

    } // doEditCommentConfirm()


    private String doEditQuicknoteConfirm( HttpServletRequest req ) {
        String template;
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String message = "unspecified error";
        String quicknoteId = req.getParameter( "quicknoteId" );
        String enoteDate = req.getParameter( "timestamp" );
        String studentId = req.getParameter( "studentUserId" );
        String sectionId = req.getParameter( "sectionId" );
        String typeId = req.getParameter( "typeId" );
        String comment = req.getParameter( "commentText" );
        String commentTE;  // comment with tics escaped.
        StringBuffer dbmessage;
        // If Admin mode, enoteType is not set.
        req.getSession( ).setAttribute(
            "org.stonybrookschool.enotes.objects.enoteType",
            capitalize(QUICKNOTE)
        );

        Integer iQuicknoteId;
        Timestamp tEnoteDate;
        Integer iStudentId = new Integer(0);
        Integer iSectionId = new Integer(0);
        Integer iTypeId;

        if (null == u) {
            message = "no logged in user";
            u = User.getDefaultUser(); }
        else if (null == quicknoteId) message = "no quicknoteId";
        else if (null == enoteDate) message = "no timestamp";
        else if (null == studentId) message = "no studentUserId";
        else if (null == sectionId) message = "no sectionId";
        else if (null == typeId) message = "no typeId";
        else if (null == comment) message = "no commentText";
        else {
            iQuicknoteId = parseInt( quicknoteId );
            tEnoteDate = parseTimestamp( enoteDate );
            iStudentId = parseInt( studentId );
            iSectionId = parseInt( sectionId );
            iTypeId = parseInt( typeId );
            commentTE = DBRoutines.escapeTics( comment );

            if (null == iQuicknoteId) message = "invalid quicknoteId";
            else if (null == tEnoteDate) message = "invalid timestamp";
            else if (null == iStudentId) message = "invalid studentUserId";
            else if (null == iSectionId) message = "invalid sectionId";
            else if (null == iTypeId) message = "invalid typeId";
            else if (0 == iTypeId.intValue()) message = "Please select an item.";
            else {
                dbmessage = new StringBuffer( "" );
                DBRoutines.insertQuicknote(
                    new Quicknote (
                        iQuicknoteId.intValue(),
                        false,
                        tEnoteDate,
                        iStudentId.intValue(), "", "",
                        iSectionId.intValue(), "", "",
                        iTypeId.intValue(), "",
                        commentTE,
                        false,
                        "pending"
                    ),
                    dbmessage
                );

                message = dbmessage.toString();
            }

        } // presence checks

        setEditEnoteOneAttributes( req, iStudentId, iSectionId, u, new Integer(0), message );
        template = templateBase + "editEnoteOne.jsp";

        return( template );

    } // doEditQuicknoteConfirm()

    private String doEditEnoteConfirm( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String enoteType = (String) req.getSession()
                           .getAttribute( "org.stonybrookschool.enotes.objects.enoteType" );

        if (null == enoteType) req.setAttribute( "error", "no enoteType" );
        else if ( !validEnoteType(enoteType) ) req.setAttribute( "error", "invalid enoteType: "+enoteType );
        else {

            String cmp = enoteType.toLowerCase();
            if ( DISCIPLINE.equals( cmp ) )   return ( doEditDisciplineConfirm( req ));
            else if ( COMMENT.equals( cmp ) ) return ( doEditCommentConfirm( req ));
            else                              return ( doEditQuicknoteConfirm( req ));

        } // presence checks

        return( template );

    } // doEditEnoteConfirm()


    private String doAdminTest( HttpServletRequest req ) {
        String template;
        String mailServer     = "10.10.10.70";
        String messageFrom    = "steve.sides@stonybrookschool.org";
        Vector recipients = new Vector();
        String messageTo;
        String messageSubject = "eNotes e-mail test";
        String tbdmsg;
        StringBuffer messageBody    = new StringBuffer();

	messageBody.append( "<html>\n" );
	messageBody.append( "<head>\n" );
	messageBody.append( "</head>\n" );
	messageBody.append( "<body>\n" );
	messageBody.append( "<font face=\"arial\" size=3>" );
        messageBody.append(    "This came to you from the eNotes servlet, handler.  "  );
        messageBody.append(    "usage is enotes?page=adminEmailTest&adr=email@address "  );
        messageBody.append(    "<font color=\"red\">for users with superuser='t'</font><br><br>"  );
        messageBody.append(    "accepts a second or third address named adr2, adr3"  );
	messageBody.append( "</font>" );
	messageBody.append( "</body>\n" );
	messageBody.append( "</html>\n" );

        messageTo = req.getParameter( "adr" );
        if ( null != messageTo ) recipients.add( messageTo );
        messageTo = req.getParameter( "adr2" );
        if ( null != messageTo ) recipients.add( messageTo );
        messageTo = req.getParameter( "adr3" );
        if ( null != messageTo ) recipients.add( messageTo );

        if ( recipients.size() != 0) {
            try {

                Properties props = System.getProperties();
                props.put( "mail.smtp.host", mailServer );

                Session session = Session.getDefaultInstance( props, null );

                MimeMessage message = new MimeMessage( session );
                message.setFrom( new InternetAddress( messageFrom ) );
		message.setSentDate( new java.util.Date() );

                for (int i = 0, j = recipients.size(); i < j; i++ ) {
                    message.addRecipient(
                        Message.RecipientType.TO,
                        new InternetAddress( (String)recipients.elementAt(i) ) );
                }

                message.setSubject( messageSubject );
                // message.setContent( messageBody, "text/plain" );
		message.setContent( messageBody.toString(), "text/html" );

                Transport.send( message );

                if (recipients.size() == 1) {
                    tbdmsg = "mail sent to: " + (String)recipients.elementAt(0);
                } else {
                    tbdmsg = "mail sent to: " + recipients.size() + " recipients.";
                }
            } catch ( Exception e ) {
                System.err.println( "handler.java: doAdminTest(): " + e.getMessage() );
                tbdmsg = e.getMessage();
            }
        } else {
            tbdmsg = "email to nobody?  No adr.";
        }
        req.setAttribute( "message", tbdmsg );
        template = templateBase + "adminTest.jsp";
        return( template );
    } // doAdminTest()

    private String doSendFeedbackStart( HttpServletRequest req ) {
        String template;
        template = templateBase + "sendFeedbackStart.jsp";
        return( template );
    } // doSendFeedbackStart()

    private String doSendFeedback( HttpServletRequest req ) {
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String template;
        MailParameters mp = DBRoutines.getMailParameters();
        Vector recipients     = mp.getFeedbackRecipients();
        String messageSubject = "eNotes feedback";
        StringBuffer messageBody = new StringBuffer();
        String messageParam    = req.getParameter( "message" );
        String msg = "";
	StringBuffer from = new StringBuffer();

	from.append( u.getFirst() );
	from.append( " " );
	from.append( u.getLast() );
	from.append( " (" );
	from.append( u.getGroupName() );
	from.append( ") <" );
	from.append( u.getEmail() );
	from.append( ">" );

        if ( ( recipients.size() != 0 ) && ( messageParam != null ) ) {
            try {
                messageBody.append( messageParam );

                Properties props = System.getProperties();
                props.put( "mail.smtp.host", mp.getMailServer() );

                Session session = Session.getDefaultInstance( props, null );

                MimeMessage message = new MimeMessage( session );
                // message.setFrom( new InternetAddress( mp.getMessageFrom() ) );
		message.setFrom( new InternetAddress( from.toString() ) );
		message.setSentDate( new java.util.Date() );

                for (int i = 0, j = recipients.size(); i < j; i++ ) {
                    message.addRecipient(
                        Message.RecipientType.TO,
                        new InternetAddress( (String)recipients.elementAt(i) ) );
                }

                message.setSubject( messageSubject );
                message.setContent( messageBody.toString(), "text/plain" );

                Transport.send( message );

                msg = "mail sent to: " + recipients.size() + " recipient(s).";
            } catch ( Exception e ) {
                System.err.println( "handler.java: doSendFeedback(): " + e.getMessage() );
                msg = e.getMessage();
            }
        } // if recipients.size() != 0

        req.setAttribute( "message", msg );
        template = templateBase + "feedback.jsp";
        return( template );
    } // doSendFeedback()


    private String doEditCommentOne( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( )
                 .getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String enoteId = req.getParameter( "enoteId" );
        Integer iEnoteId;

        if ( null == u ) req.setAttribute( "error", "no logged in user" );
        else if ( null == enoteId ) req.setAttribute( "error", "no enoteId specified" );
        else {
            iEnoteId = parseInt( enoteId );

            if ( null == iEnoteId ) req.setAttribute( "error", "invalid enoteId" );
            else {
                Comment e = DBRoutines.getComment( iEnoteId.intValue() );
                if ( e == null)
                    req.setAttribute( "error", "could not retrieve comment, id: "+ iEnoteId.toString() );
                else if ( u.getUserId() != e.getTeacherUserId() )
                    req.setAttribute( "error", "This enote did not originate from your section.");
                else {
                    setEditEnoteOneAttributes(
                        req,
                        new Integer( e.getStudentUserId() ),
                        new Integer( e.getSectionId() ),
                        u,
                        e,
                        " " );
                    template = templateBase + "editEnoteOne.jsp";
                }
            }
        }

        return( template );
    } // doEditCommentOne()


    private String doEditDisciplineOne( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( )
                 .getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String enoteId = req.getParameter( "enoteId" );
        Integer iEnoteId;

        if ( null == u ) req.setAttribute( "error", "no logged in user" );
        else if ( null == enoteId ) req.setAttribute( "error", "no enoteId specified" );
        else {
            iEnoteId = parseInt( enoteId );

            if ( null == iEnoteId ) req.setAttribute( "error", "invalid enoteId" );
            else {
                Discipline e = DBRoutines.getDiscipline( iEnoteId.intValue() );
                if ( e == null)
                    req.setAttribute( "error", "could not retrieve discipline, id: "+ iEnoteId.toString() );
                else if ( u.getUserId() != e.getTeacherUserId() )
                    req.setAttribute( "error", "This enote did not originate from your section.");
                else {
                    setEditEnoteOneAttributes(
                        req,
                        new Integer( e.getStudentUserId() ),
                        new Integer( e.getSectionId() ),
                        u,
                        e,
                        " " );
                    template = templateBase + "editEnoteOne.jsp";
                }
            }
        }

        return( template );

    } // doEditDisciplineOne()


    private String doEditQuicknoteOne( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( )
                 .getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String enoteId = req.getParameter( "enoteId" );
        Integer iEnoteId;

        if ( null == u ) req.setAttribute( "error", "no logged in user" );
        else if ( null == enoteId ) req.setAttribute( "error", "no enoteId specified" );
        else {
            iEnoteId = parseInt( enoteId );

            if ( null == iEnoteId ) req.setAttribute( "error", "invalid enoteId" );
            else {
                Quicknote e = DBRoutines.getQuicknote( iEnoteId.intValue() );
                if ( e == null)
                    req.setAttribute( "error", "could not retrieve quicknote, id: "+ iEnoteId.toString() );
                else if ( u.getUserId() != e.getTeacherUserId() )
                    req.setAttribute( "error", "This enote did not originate from your section.");
                else {
                    setEditEnoteOneAttributes(
                        req,
                        new Integer( e.getStudentUserId() ),
                        new Integer( e.getSectionId() ),
                        u,
                        e,
                        " " );
                    template = templateBase + "editEnoteOne.jsp";
                }
            }
        }

        return( template );

    } // doEditQuicknoteOne()

    // returns 0 if no error.
    private int sendQuicknote( int quicknoteId ) {
        Quicknote e = DBRoutines.getQuicknote( quicknoteId );
	StringBuffer from = new StringBuffer();
	StringBuffer subject = new StringBuffer();

        if ( e == null) return -1;

        MailParameters mp = DBRoutines.getMailParameters();
        Vector recipients = MailDistribution.getQuicknoteRecipients( e );
        StringBuffer messageBody = new StringBuffer();
        int error = 0;

        messageBody.append( "Date: " );
        messageBody.append( emailsdf.format( e.getDatetime() ) );
        messageBody.append( "\r\n\r\nSubject: " );
        messageBody.append( e.getSectionName() );
        messageBody.append( " (" );
        messageBody.append( e.getPeriodName() );
        messageBody.append( ")" );
        messageBody.append( "\r\n\r\nTeacher: ");
        messageBody.append( e.getTeacherTitle() );
        messageBody.append( " " );
        messageBody.append( e.getTeacherFirst() );
        messageBody.append( " " );
        messageBody.append( e.getTeacherLast() );
        messageBody.append( "\r\n\r\nStudent: " );
        messageBody.append( e.getStudentFirst() );
        messageBody.append( " " );
        messageBody.append( e.getStudentLast() );
        messageBody.append( "\r\n\r\nItem: " );
        messageBody.append( e.getQuicknoteTypeName() );
        messageBody.append( "\r\n\r\nTeacher Comment: " );
        messageBody.append( e.getCommentText() );
        messageBody.append( "\r\n\r\n" );
        messageBody.append( mp.getQuicknoteStandardText() );

	from.append( e.getTeacherTitle() );
	from.append( " " );
	from.append( e.getTeacherFirst() );
	from.append( " " );
	from.append( e.getTeacherLast() );
	from.append( "<" );
	from.append( DBRoutines.getUser(e.getTeacherUserId()).getEmail() );
	from.append( ">" );

	subject.append( "Regarding " );
	subject.append( e.getStudentFirst() );
	subject.append( " " );
	subject.append( e.getStudentLast() );
	subject.append( ": " );
	subject.append( mp.getQuicknoteSubject() );

        if ( ( recipients.size() != 0 ) && ( messageBody != null ) ) {
            try {
                Properties props = System.getProperties();
                props.put( "mail.smtp.host", mp.getMailServer() );

                Session session = Session.getDefaultInstance( props, null );

                MimeMessage message = new MimeMessage( session );

                // message.setFrom( new InternetAddress( mp.getMessageFrom() ) );
		message.setFrom( new InternetAddress( from.toString() ));
		message.setSentDate( new java.util.Date() );

                for (int i = 0, j = recipients.size(); i < j; i++ ) {

                    // System.err.println( "debug: " + (String)recipients.elementAt(i) );

                    message.addRecipient(
                        Message.RecipientType.TO,
                        new InternetAddress( (String)recipients.elementAt(i) ) );
                }

                // message.setSubject( mp.getQuicknoteSubject() );
		message.setSubject( subject.toString() );
                message.setContent( messageBody.toString(), "text/plain" );

                Transport.send( message );

            } catch ( Exception ex ) {
                System.err.println( "handler.java sendQuicknote(): " + ex.getMessage() );
                error = -1;
            }

        } else { // if recipients.size() != 0
            error = -1;
        }

        return( error );

    } // sendQuicknote()


    // returns 0 if no error.
    private int sendDiscipline( int disciplineId ) {
        Discipline d = DBRoutines.getDiscipline( disciplineId );
	StringBuffer from = new StringBuffer();
	StringBuffer subject = new StringBuffer();

        if ( d == null) return -1;

        MailParameters mp = DBRoutines.getMailParameters();

        Vector recipients = MailDistribution.getDisciplineRecipients( d );
        StringBuffer messageBody = new StringBuffer();
        int error = 0;
        messageBody.append( "Date: " );
        messageBody.append( emailsdf.format( d.getDatetime() ) );
        messageBody.append( "\r\n\r\nSubject: " );
        messageBody.append( d.getSectionName() );
        messageBody.append( "\r\n\r\nTeacher: ");
        messageBody.append( d.getTeacherTitle() );
        messageBody.append( " " );
        messageBody.append( d.getTeacherFirst() );
        messageBody.append( " " );
        messageBody.append( d.getTeacherLast() );
        messageBody.append( "\r\n\r\nStudent: " );
        messageBody.append( d.getStudentFirst() );
        messageBody.append( " " );
        messageBody.append( d.getStudentLast() );
        messageBody.append( "\r\n\r\nAction: " );
        messageBody.append( d.getOffenseName() );
        messageBody.append( "\r\n\r\nConsequence: " );
        messageBody.append( d.getPunishmentName() );
        messageBody.append( "\r\n\r\nTeacher Comment: " );
        messageBody.append( d.getCommentText() );
        messageBody.append( "\r\n\r\n" );
        messageBody.append( mp.getDisciplineStandardText() );

        from.append( d.getTeacherTitle() );
        from.append( " " );
        from.append( d.getTeacherFirst() );
        from.append( " " );
        from.append( d.getTeacherLast() );
        from.append( "<" );
        from.append( DBRoutines.getUser(d.getTeacherUserId()).getEmail() );
        from.append( ">" );

	subject.append( "Regarding " );
	subject.append( d.getStudentFirst() );
	subject.append( " " );
	subject.append( d.getStudentLast() );
	subject.append( ": " );
	subject.append( mp.getDisciplineSubject() );

        if ( ( recipients.size() != 0 ) && ( messageBody != null ) ) {
            try {
                Properties props = System.getProperties();
                props.put( "mail.smtp.host", mp.getMailServer() );

                Session session = Session.getDefaultInstance( props, null );

                MimeMessage message = new MimeMessage( session );

                // message.setFrom( new InternetAddress( mp.getMessageFrom() ) );
		message.setFrom( new InternetAddress( from.toString() ));
		message.setSentDate( new java.util.Date() );

                for (int i = 0, j = recipients.size(); i < j; i++ ) {

                    // System.err.println( "debug: " + (String)recipients.elementAt(i) );

                    message.addRecipient(
                        Message.RecipientType.TO,
                        new InternetAddress( (String)recipients.elementAt(i) ) );
                }

		// message.setSubject( mp.getDisciplineSubject() );
                message.setSubject( subject.toString() );
                message.setContent( messageBody.toString(), "text/plain" );

                Transport.send( message );

            } catch ( Exception e ) {
                System.err.println( "handler.java: sendDiscipline(): " + e.getMessage() );
                error = -1;
            }

        } else { // if recipients.size() != 0
            error = -1;
        }

        return( error );

    } // sendDiscipline()


    private String doAdminSendThisDiscipline( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String disciplineId = req.getParameter( "disciplineId" );
        Integer iDisciplineId;

        if ( null == disciplineId ) req.setAttribute( "error", "no disciplineId specified" );
        else {
            iDisciplineId = parseInt( disciplineId );

            if ( null == iDisciplineId ) req.setAttribute( "error", "invalid disciplineId" );
            else {
                if ( 0 == sendDiscipline( iDisciplineId.intValue() ) ) {
                    DBRoutines.setEnoteStatus( 
			"disciplines", "published", "disciplineId", iDisciplineId.intValue() );
                }
                template = doAdminDisciplineStart( req );
            }
        }

        return( template );
    } // doAdminSendThisDiscipline()


    private String doAdminSendThisQuicknote( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String quicknoteId = req.getParameter( "quicknoteId" );
        Integer iQuicknoteId;

        if ( null == quicknoteId ) req.setAttribute( "error", "no quicknoteId specified" );
        else {
            iQuicknoteId = parseInt( quicknoteId );

            if ( null == iQuicknoteId ) req.setAttribute( "error", "invalid quicknoteId" );
            else {
                if ( 0 == sendQuicknote( iQuicknoteId.intValue() ) ) {
                    DBRoutines.setEnoteStatus( 
			"quicknotes", "published", "quicknoteId", iQuicknoteId.intValue() );
                }
                template = doAdminQuicknoteStart( req );
            }
        }

        return( template );
    } // doAdminSendThisQuicknote()


    private String doAdminMarkThisComment( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String commentId = req.getParameter( "commentId" );
	String gradYear = req.getParameter( "gradYear" );
	String status = req.getParameter( "status" );
	String pageStatus = req.getParameter( "pageStatus" );
        Integer iCommentId;
	Integer iGradYear;

        if ( null == commentId ) req.setAttribute( "error", "no commentId specified" );
	else if ( null == gradYear ) req.setAttribute( "error", "no gradYear specified" );
	else if ( null == status ) req.setAttribute( "error", "no status specified" );
	else if ( null == pageStatus ) req.setAttribute( "error", "no pageStatus specified" );
        else {
            iCommentId = parseInt( commentId );
	    iGradYear = parseInt( gradYear );

            if ( null == iCommentId ) req.setAttribute( "error", "invalid commentId" );
	    else if ( null == iGradYear ) req.setAttribute( "error", "invalid gradYear" );
            else {
                int err = DBRoutines.setEnoteStatus( "comments", status, "commentId", iCommentId.intValue() );
		if (0 == err) {
		    template = templateBase + "adminCommentClose.jsp";
		} else {
		    req.setAttribute( "message", "Could not mark comment as " + status );
		    template = templateBase + "adminCommentMessage.jsp";
		}

		/*
                * req.setAttribute( "gradYear", iGradYear );
                * req.setAttribute( "commentList", 
		*       DBRoutines.getSendComments( iGradYear.intValue(), pageStatus ) );
		* req.setAttribute( "status", pageStatus );
                * template = templateBase + "adminCommentPerGrade.jsp";
		*/
            }
        }

        return( template );
    } // doAdminMarkThisComment()


    private String doAdminDisciplineStart( HttpServletRequest req ) {
        String template;

        req.setAttribute( "disciplineList", DBRoutines.getSendDisciplines() );
        template = templateBase + "adminDisciplineStart.jsp";
        return( template );
    } // doAdminDisciplineStart()


    private String doAdminQuicknoteStart( HttpServletRequest req ) {
        String template;

        req.setAttribute( "quicknoteList", DBRoutines.getSendQuicknotes() );
        template = templateBase + "adminQuicknoteStart.jsp";
        return( template );
    } // doAdminQuicknoteStart()


    private String doAdminCommentStart( HttpServletRequest req ) {
        String template;

        // req.setAttribute( "commentList", DBRoutines.getSendComments() );
        template = templateBase + "adminCommentStart.jsp";
        return( template );
    } // doAdminCommentStart()


    private String doAdminCommentPerGrade( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
	String gradYear = req.getParameter( "gradYear" );
	String status = req.getParameter( "status" );
	String editprint = req.getParameter( "editprint" );
	Integer iGradYear;

	if ( null == editprint ) editprint = "PerGrade";

	if ( null == gradYear ) req.setAttribute( "error", "no gradYear specified" );
	else if ( null == status ) req.setAttribute( "error", "no status specified" );
	else {
	    iGradYear = parseInt( gradYear );

	    if ( null == iGradYear ) req.setAttribute( "error", "invalid gradYear" );
	    else {
		req.setAttribute( "gradYear", iGradYear );
		req.setAttribute( "commentList", DBRoutines.getSendComments( iGradYear.intValue(), status ) );
		req.setAttribute( "status", status );
		// template = templateBase + "adminCommentPerGrade.jsp";
		template = templateBase + "adminComment" + editprint + ".jsp";
	    }
	}

        return( template );
    } // doAdminCommentPerGrade()


    private String doAdminEditThisDiscipline( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String disciplineId = req.getParameter( "disciplineId" );
        Integer iDisciplineId;

        if ( null == disciplineId ) req.setAttribute( "error", "no disciplineId specified" );
        else {
            iDisciplineId = parseInt( disciplineId );

            if ( null == iDisciplineId ) req.setAttribute( "error", "invalid disciplineId" );
            else {
                Discipline e = DBRoutines.getDiscipline( iDisciplineId.intValue() );

                req.setAttribute( "enote", e );
                req.setAttribute( "capName" , DBRoutines.getCap() );

                template = templateBase + "adminEditThisDiscipline.jsp";
            }
        }

        return( template );

    } // doAdminEditThisDiscipline()


    private String doAdminEditThisQuicknote( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String quicknoteId = req.getParameter( "quicknoteId" );
        Integer iQuicknoteId;

        if ( null == quicknoteId ) req.setAttribute( "error", "no quicknoteId specified" );
        else {
            iQuicknoteId = parseInt( quicknoteId );

            if ( null == iQuicknoteId ) req.setAttribute( "error", "invalid quicknoteId" );
            else {
                Quicknote e = DBRoutines.getQuicknote( iQuicknoteId.intValue() );

                req.setAttribute( "enote", e );
                req.setAttribute( "quicknoteTypeName" , DBRoutines.getQuicknoteTypes() );

                template = templateBase + "adminEditThisQuicknote.jsp";
            }
        }

        return( template );

    } // doAdminEditThisQuicknote()


    private String doAdminEditThisComment( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String commentId = req.getParameter( "commentId" );
	String gradYear = req.getParameter( "gradYear" );
        Integer iCommentId;
	Integer iGradYear;

        if ( null == commentId ) req.setAttribute( "error", "no commentId specified" );
	else if ( null == gradYear ) req.setAttribute( "error", "no gradYear specified" );
        else {
            iCommentId = parseInt( commentId );
	    iGradYear = parseInt( gradYear );

            if ( null == iCommentId ) req.setAttribute( "error", "invalid commentId" );
	    else if ( null == iGradYear ) req.setAttribute( "error", "invalid gradYear" );
            else {
                Comment e = DBRoutines.getComment( iCommentId.intValue() );

                req.setAttribute( "enote", e );
                req.setAttribute( "events" , DBRoutines.getEvents() );
                req.setAttribute( "marksName" , DBRoutines.getMarks() );
		req.setAttribute( "gradYear", iGradYear );

                template = templateBase + "adminEditThisComment.jsp";
            }
        }

        return( template );

    } // doAdminEditThisComment()


    private String doAdminUpdateThisQuicknote( HttpServletRequest req ) {
        doEditQuicknoteConfirm( req ) ;
        return( doAdminQuicknoteStart( req ) );
    } // doAdminUpdateThisQuicknote()


    private String doAdminUpdateThisDiscipline( HttpServletRequest req ) {
        doEditDisciplineConfirm( req );
        return( doAdminDisciplineStart( req ) );
    } // doAdminUpdateThisDiscipline()


    private String doAdminUpdateThisComment( HttpServletRequest req ) {
	// this is almost like doEditCommentConfirm( ).  The differences here are that
	// enoteType is not set, and the return value of insertComment() is tested.
	// If OK, go to adminCommentClose.jsp, otherwise go to adminCommentMessage.jsp

        String template;
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String message = "general error";
        String commentId = req.getParameter( "commentId" );
        String enoteDate = req.getParameter( "timestamp" );
        String studentId = req.getParameter( "studentUserId" );
        String sectionId = req.getParameter( "sectionId" );
        String eventId = req.getParameter( "eventId" );
        String markId = req.getParameter( "markId" );
        String comment = req.getParameter( "commentText" );
        String commentTE;  // comment with tics escaped.
        StringBuffer dbmessage;
        Integer iCommentId;
        Timestamp tEnoteDate;
        Integer iStudentId = new Integer(0);
        Integer iSectionId = new Integer(0);
        Integer iEventId;
        Integer iMarkId;
	int err = -1;

        if (null == u) {
            message = "no logged in user";
            u = User.getDefaultUser(); }
        else if (null == commentId) message = "no commentId";
        else if (null == enoteDate) message = "no timestamp";
        else if (null == studentId) message = "no studentUserId";
        else if (null == sectionId) message = "no sectionId";
        else if (null == eventId) message = "no eventId";
        else if (null == markId) message = "no markId";
        else if (null == comment) message = "no commentText";
        else {            
	    iCommentId = parseInt( commentId );
            tEnoteDate = parseTimestamp( enoteDate );
            iStudentId = parseInt( studentId );
            iSectionId = parseInt( sectionId );
            iEventId = parseInt( eventId );
            iMarkId = parseInt( markId );
            commentTE = DBRoutines.escapeTics( comment );

            if (null == iCommentId) message = "invalid commentId";
            else if (null == tEnoteDate) message = "invalid timestamp";
            else if (null == iStudentId) message = "invalid studentUserId";
            else if (null == iSectionId) message = "invalid sectionId";
            else if (null == iEventId) message = "invalid eventId";
            else if (null == iMarkId) message = "invalid markId";
            else if (0 == iMarkId.intValue()) message = "Please select a mark.";
            else if (0 == iEventId.intValue()) message = "Please select a comment type.";
            else {
                dbmessage = new StringBuffer( "" );
                err =  DBRoutines.insertComment(
                    new Comment(
                        iCommentId.intValue(),
                        false,
                        new Timestamp( new java.util.Date().getTime() ),
                        iStudentId.intValue(), "", "",
                        iSectionId.intValue(), "", "",
                        iEventId.intValue(), "",
                        iMarkId.intValue(), "",
                        commentTE,
                        "pending"
                    ),
                    dbmessage
                );

                message = dbmessage.toString();
            }

        } // presence checks        

	if (0 == err) {
	    template = templateBase + "adminCommentClose.jsp";
	} else {
	    req.setAttribute( "message", message );
	    template = templateBase + "adminCommentMessage.jsp";
	}

        return( template );

    } // doAdminUpdateThisComment()


    private String doAdminAnnounceComments( HttpServletRequest req ) {
        // Ask the user if he is sure he wants to send email to everyone in the user list.
        // If he selects yes, I'll get the adminAnnounceCommentsConfirm request here in handler.
        return( templateBase + "adminAnnounceComments.jsp" );
    } // doAdminAnnounceComments()


    private String doAdminAnnounceCommentsConfirm( HttpServletRequest req ) {
        String errorMessage = "unspecified error";

	// set status of all proofed comments to 'published'.
	DBRoutines.publishComments();

        MailParameters mp = DBRoutines.getMailParameters();

	// Get list of recipients including parents of students
	// who have published comments for this event as of now.
        Vector recipients = MailDistribution.getCommentRecipients();

        String messageBody = mp.getCommentStandardText();
	String commentSubject = mp.getCommentSubject();
	String mailTo;
	StringBuffer log = new StringBuffer();

        if ( recipients.size() == 0 ) errorMessage = "no recipients";
        else if ( messageBody == null ) errorMessage = "no \"commentStandardText\" in database.";
        else if ( commentSubject == null ) errorMessage = "no \"commentSubject\" in database.";
        else {
	    
	    try {
                Properties props = System.getProperties();
                props.put( "mail.smtp.host", mp.getMailServer() );

                Session session = Session.getDefaultInstance( props, null );

                MimeMessage message = new MimeMessage( session );
                message.setFrom( new InternetAddress( mp.getMessageFrom() ) );
		message.setSentDate( new java.util.Date() );

                for (int i = 0, j = recipients.size(); i < j; i++ ) {
		    mailTo = (String)recipients.elementAt(i);

		    log.delete(0, log.length());
		    log.append( logsdf.format( new java.util.Date() ) );
		    log.append( " comment announcement addressee: " );
		    log.append( mailTo );
		    System.err.println( log );

		    message.addRecipient(
			Message.RecipientType.BCC,
			new InternetAddress( mailTo ) );
                }

                message.setSubject( commentSubject );
                message.setContent( messageBody, "text/plain" );

                Transport.send( message );

                errorMessage = "sent announcement to " + recipients.size() + " recipients.";

            } catch ( Exception e ) {
                System.err.println( "handler.java: doAdminAnnounceCommentsConfirm(): " + e.getMessage() );
                errorMessage = e.getMessage();
            }
        }

        req.setAttribute( "message", errorMessage );

        return( templateBase + "adminAnnounceCommentsConfirm.jsp" );
    } // doAdminAnnounceCommentsConfirm()


    private String doAdminGeneralTest( HttpServletRequest req ) {
        return( templateBase + "adminGeneralTest.jsp" );
    } // doAdminGeneralTest()


    private String doAdminDeleteThisQuicknote( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String quicknoteId = req.getParameter( "quicknoteId" );
        Integer iQuicknoteId;

        if ( null == quicknoteId ) req.setAttribute( "error", "no quicknoteId specified" );
        else {
            iQuicknoteId = parseInt( quicknoteId );

            if ( null == iQuicknoteId ) req.setAttribute( "error", "invalid quicknoteId" );
            else {
                DBRoutines.setEnoteStatus( "quicknotes", "deleted", "quicknoteId", iQuicknoteId.intValue() );

                template = doAdminQuicknoteStart( req );
            }
        }

        return( template );
    } // doAdminDeleteThisQuicknote()


    private String doAdminDeleteThisDiscipline( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String disciplineId = req.getParameter( "disciplineId" );
        Integer iDisciplineId;

        if ( null == disciplineId ) req.setAttribute( "error", "no disciplineId specified" );
        else {
            iDisciplineId = parseInt( disciplineId );

            if ( null == iDisciplineId ) req.setAttribute( "error", "invalid disciplineId" );
            else {
                DBRoutines.setEnoteStatus( "disciplines", "deleted", "disciplineId", iDisciplineId.intValue() );

                template = doAdminDisciplineStart( req );
            }
        }

        return( template );
    } // doAdminDeleteThisDiscipline()


    private String doAdminConfigureStart( HttpServletRequest req ) {
        req.setAttribute( "propertyList", DBRoutines.getProperties() );
        return( templateBase + "adminConfigureStart.jsp" );
    } // doAdminConfigureStart()


    private String doAdminConfigureConfirm( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String name = req.getParameter( "name" );
        String value = req.getParameter( "value" );

        if ( null == name ) req.setAttribute( "error", "no name specified" );
        if ( null == value ) req.setAttribute( "error", "no value specified" );
        else {
            String valueET = DBRoutines.escapeTics( value );
            DBRoutines.setProperty( name, valueET );
            template = doAdminConfigureStart( req );
        }

        return( template );
    } // doAdminConfigureConfirm()


    private String doAdminCommentEventStart( HttpServletRequest req ) {
	Vector events = DBRoutines.getEvents();
	Event e = DBRoutines.getConfiguredEvent();
	Integer eventId = new Integer( e.getEventId() );

	req.setAttribute( "events" , events ); 
	req.setAttribute( "eventId", eventId );
        return( templateBase + "adminCommentEventStart.jsp" );
    } // doAdminCommentEventStart


    private String doAdminCommentEventConfirm( HttpServletRequest req ) {
	String template = templateBase + "error.jsp";
	String eventId = req.getParameter( "eventId" );
	Integer iEventId;

	if ( null == eventId ) req.setAttribute( "error", "no eventId specified" );
	else {
	    iEventId = parseInt( eventId );

	    if ( null == iEventId ) req.setAttribute( "error", "invalid eventId" );
	    else {
		Vector events = DBRoutines.getEvents();

		for (int i=0, j = events.size(); i < j; i++) {
		    if ( iEventId.intValue() == ((Event)events.elementAt(i)).getEventId() ) {
			DBRoutines.setProperty( "commentEvent", ((Event)events.elementAt(i)).getEventName() );
		    }
		}
		return( doAdminConfigureStart( req ) );
	    }
	}

	return( template );
    } // doAdminCommentEventConfirm()


    private String doEditDeleteThisDiscipline( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( )
                 .getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String disciplineId = req.getParameter( "disciplineId" );
        Integer iDisciplineId;

        if ( null == disciplineId ) req.setAttribute( "error", "no disciplineId specified" );
        else {
            iDisciplineId = parseInt( disciplineId );

            if ( null == iDisciplineId ) req.setAttribute( "error", "invalid disciplineId" );
            else {
                Discipline e = DBRoutines.getDiscipline( iDisciplineId.intValue() );
                if ( e == null)
                    req.setAttribute( "error", "could not retrieve discipline, id: "+ iDisciplineId.toString() );
                else if ( u.getUserId() != e.getTeacherUserId() )
                    req.setAttribute( "error", "This enote did not originate from your section.");
                else {

                    DBRoutines.setEnoteStatus( 
			"disciplines", "deleted", "disciplineId", iDisciplineId.intValue() );

                    setEditEnoteOneAttributes(
                        req,
                        new Integer( e.getStudentUserId() ),
                        new Integer( e.getSectionId() ),
                        u,
                        e,
                        " " );

                    template = templateBase + "editEnoteOne.jsp";

                }
            }
        }

        return( template );

    } // doEditDeleteThisDiscipline()

    private String doEditDeleteThisQuicknote( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( )
                 .getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String quicknoteId = req.getParameter( "quicknoteId" );
        Integer iQuicknoteId;

        if ( null == quicknoteId ) req.setAttribute( "error", "no quicknoteId specified" );
        else {
            iQuicknoteId = parseInt( quicknoteId );

            if ( null == iQuicknoteId ) req.setAttribute( "error", "invalid quicknoteId" );
            else {
                Quicknote e = DBRoutines.getQuicknote( iQuicknoteId.intValue() );
                if ( e == null)
                    req.setAttribute( "error", "could not retrieve quicknote, id: "+ iQuicknoteId.toString() );
                else if ( u.getUserId() != e.getTeacherUserId() )
                    req.setAttribute( "error", "This enote did not originate from your section.");
                else {

                    DBRoutines.setEnoteStatus(
			"quicknotes", "deleted", "quicknoteId", iQuicknoteId.intValue() );

                    setEditEnoteOneAttributes(
                        req,
                        new Integer( e.getStudentUserId() ),
                        new Integer( e.getSectionId() ),
                        u,
                        e,
                        " " );

                    template = templateBase + "editEnoteOne.jsp";

                }
            }
        }

        return( template );

    } // doEditDeleteThisQuicknote()

    private String doEditDeleteThisComment( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        User u = (User) req.getSession( )
                 .getAttribute( "org.stonybrookschool.enotes.objects.user" );
        String commentId = req.getParameter( "commentId" );
        Integer iCommentId;

        if ( null == commentId ) req.setAttribute( "error", "no commentId specified" );
        else {
            iCommentId = parseInt( commentId );

            if ( null == iCommentId ) req.setAttribute( "error", "invalid commentId" );
            else {
                Comment e = DBRoutines.getComment( iCommentId.intValue() );
                if ( e == null)
                    req.setAttribute( "error", "could not retrieve comment, id: "+ iCommentId.toString() );
                else if ( u.getUserId() != e.getTeacherUserId() )
                    req.setAttribute( "error", "This enote did not originate from your section.");
                else {

                    DBRoutines.setEnoteStatus( 
			"comments", "deleted", "commentId", iCommentId.intValue() );

                    setEditEnoteOneAttributes(
                        req,
                        new Integer( e.getStudentUserId() ),
                        new Integer( e.getSectionId() ),
                        u,
                        e,
                        " " );

                    template = templateBase + "editEnoteOne.jsp";

                }
            }
        }

        return( template );

    } // doEditDeleteThisComment()


    private String doAdminSectionList( HttpServletRequest req ) {
        String template = templateBase + "adminSectionList.jsp";

	req.setAttribute( "sections", DBRoutines.getAllSections( ) );

        return( template );
    } // doAdminSectionList()


    private String doAdminSectionEdit( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
	String sectionId = req.getParameter( "sectionId" );
	Integer iSectionId = null;

	if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
	else {
	    iSectionId = parseInt( sectionId );

	    if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
	    else {
		if ( 0 == iSectionId.intValue() )
		    req.setAttribute( "section", Section.getDefault() );
		else
		    req.setAttribute( "section", DBRoutines.getSection( iSectionId.intValue() ) );

		req.setAttribute( "teachers", DBRoutines.getByGroupName( FACULTY ) );
		req.setAttribute( "departments", DBRoutines.getAllDepartments() );
		req.setAttribute( "periods", DBRoutines.getAllPeriods() );

		template =  templateBase + "adminSectionEdit.jsp";
	    }
	}

        return( template );
    } // doAdminSectionEdit()


    private String doAdminSectionUpdate( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
	String sectionId = req.getParameter( "sectionId" );
	String teacherId = req.getParameter( "teacherId" );
	String sectionNumber = req.getParameter( "sectionNumber" );
	String periodId = req.getParameter( "periodId" );
	String sectionName = req.getParameter( "sectionName" );
	String departmentId = req.getParameter( "departmentId" );
	String honors = req.getParameter( "honors" );

	Integer iSectionId = null;
	Integer iTeacherId = null;
	Integer iPeriodId = null;
	Integer iDepartmentId = null;
	Integer iHonors = null;

        if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
        else if ( null == teacherId ) req.setAttribute( "error", "no teacherId specified" );
        else if ( null == sectionNumber ) req.setAttribute( "error", "no sectionNumber specified" );
        else if ( null == periodId ) req.setAttribute( "error", "no periodId specified" );
        else if ( null == sectionName ) req.setAttribute( "error", "no sectionName specified" );
        else if ( null == departmentId ) req.setAttribute( "error", "no departmentId specified" );
        else if ( null == honors ) req.setAttribute( "error", "no honors specified" );
        else {
	    iSectionId = parseInt( sectionId );
	    iTeacherId = parseInt( teacherId );
	    iPeriodId = parseInt( periodId );
	    iDepartmentId = parseInt( departmentId );
	    iHonors = parseInt( honors );

	    if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
	    else if ( null == iTeacherId ) req.setAttribute( "error", "invalid teacherId" );
	    else if ( null == iPeriodId ) req.setAttribute( "error", "invalid periodId" );
	    else if ( null == iDepartmentId ) req.setAttribute( "error", "invalid departmentId" );
	    else if ( null == iHonors ) req.setAttribute( "error", "invalid honors" );
	    else {
		// insertSection passes back a message indicating the result
		// of the insert.  The JSP doesn't use it at this time.

		DBRoutines.insertSection( 
		    new Section(
			iSectionId.intValue(),
			sectionName,
			User.getCurrentYear(),
			iPeriodId.intValue(),
			"", // String periodName,
			iTeacherId.intValue(),
			"", // String teacherName,
			"", // String teacherFirst,
			"", // String teacherLast,
			"", // String teacherTitle,
			sectionNumber,
			iDepartmentId.intValue(),
			"", // String departmentName,
			(1 == iHonors.intValue()) ) 
		);

		template = doAdminSectionList( req );

	    }
	}

        return( template );
    } // doAdminSectionUpdate()


    private String doAdminSectionDelete( HttpServletRequest req ) {
        String template = templateBase + "error.jsp";
        String sectionId = req.getParameter( "sectionId" );
        Integer iSectionId = null;

        if ( null == sectionId ) req.setAttribute( "error", "no sectionId specified" );
        else {
            iSectionId = parseInt( sectionId );

            if ( null == iSectionId ) req.setAttribute( "error", "invalid sectionId" );
            else {
                DBRoutines.delSection( iSectionId.intValue() );

                template = doAdminSectionList( req );
            }
        }

        return( template );

    } // doAdminSectionDelete()

} // handler
