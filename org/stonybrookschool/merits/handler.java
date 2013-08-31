package org.stonybrookschool.merits;

import java.io.*;
import java.util.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.stonybrookschool.merits.data.*;
import org.stonybrookschool.merits.objects.*;


/* Handler is the traffic cop for the merits system. It handles
 * all get and post requests farming out all real logic to other
 * classes in the system. Handler is the main servlet in the system.
 */

public class handler extends HttpServlet
{
    private boolean debug       = true;
    private String templateBase = "/merits/templates/";

    public void doGet (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        handle(req, res);
    }

    public void doPost (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        handle(req, res);
    }

    public void handle (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        String template = null;

	res.setContentType( "text/html" );

	if ( req.getParameter( "page" ) == null ) {
	    template = templateBase + "login.jsp";
	}
	else {
	    if ( req.getParameter( "page" ).equals( "processLogin" ) )
		template = doProcessLogin( req, res );
	    else {
		User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
		if ( u != null ) { // if the session contains a user object (ie: is logged in)
		    if ( "t".equals( u.getFaculty( ) ) ) {
			if ( req.getParameter( "page" ).equals( "tools" ) )
			    template = doAdminTools( req, res );
			if ( req.getParameter( "page" ).equals( "adminShowUsers" ) )
			    template = doAdminShowUsers( req, res );
			if ( req.getParameter( "page" ).equals( "adminShowHistory" ) )
			    template = doAdminShowHistory( req, res );
			if ( req.getParameter( "page" ).equals( "adminShowEvents" ) )
			    template = doAdminShowEvents( req, res );
			if ( req.getParameter( "page" ).equals( "adminShowEvent" ) )
			    template = doAdminShowEvent( req, res );
			if ( req.getParameter( "page" ).equals( "adminEditUsers" ) )
			    template = doAdminEditUsers( req, res );
			if ( req.getParameter( "page" ).equals( "adminEditUser" ) )
			    template = doAdminEditUser( req, res );
			if ( req.getParameter( "page" ).equals( "adminUsers" ) )
			    template = doAdminUsers( req, res );
			if ( req.getParameter( "page" ).equals( "adminPostEditUser" ) )
			    template = doAdminPostEditUser( req, res );
			if ( req.getParameter( "page" ).equals( "adminEvents" ) )
			    template = doAdminEvents( req, res );
			if ( req.getParameter( "page" ).equals( "adminEventDelete" ) )
			    template = doAdminEventDelete( req, res );
			if ( req.getParameter( "page" ).equals( "adminEventEditor" ) )
			    template = doAdminEventEditor( req, res );
			if ( req.getParameter( "page" ).equals( "adminEventEditorPost" ) )
			    template = doAdminEventEditorPost( req, res );
			if ( req.getParameter( "page" ).equals( "adminEventBulkEditor" ) )
			    template = doAdminEventBulkEditor( req, res );
			if ( req.getParameter( "page" ).equals( "adminEventBulkEditorPost" ) )
			    template = doAdminEventBulkEditorPost( req, res );
			if ( req.getParameter( "page" ).equals( "adminDebug" ) )
			    template = doAdminDebug( req, res );
			if ( req.getParameter( "page" ).equals( "logout" ) )
			    template = doLogout( req, res );
		    }
		    else {
			if ( req.getParameter( "page" ).equals( "view" ) )
			    template = doView( req, res );
			if ( req.getParameter( "page" ).equals( "history" ) )
			    template = doHistory( req, res );
			if ( req.getParameter( "page" ).equals( "purchaseOptions" ) )
			    template = doPurchaseOptions( req, res );
			if ( req.getParameter( "page" ).equals( "purchaseDetails" ) )
			    template = doPurchaseDetails( req, res );
			if ( req.getParameter( "page" ).equals( "purchase" ) )
			    template = doPurchase( req, res );
			if ( req.getParameter( "page" ).equals( "sell" ) )
			    template = doSell( req, res );
			if ( req.getParameter( "page" ).equals( "viewPasses" ) )
			    template = doViewPasses( req, res );
			if ( req.getParameter( "page" ).equals( "logout" ) )
			    template = doLogout( req, res );
		    }
		}
		else {
		    System.err.println( "\nhandler.java: invalid session\n" );
		    template = templateBase + "login.jsp";
		}
	    }
	}

	// execute the jsp page
	RequestDispatcher rd = req.getRequestDispatcher( template );

	if ( rd == null ) {
	    rd = req.getRequestDispatcher( templateBase + "error.jsp" );

	    if (rd == null) {
		System.err.println( "\nhandler.java: no default error template!\n" );
		throw new ServletException( "default error template not found!" );
	    }
	}

	rd.include( req, res );

    }

    private String doLogin( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "login.jsp";

	return( template );
    }

    private String doProcessLogin( HttpServletRequest req, HttpServletResponse res )
    {
	String username = "";
	String password = "";
	String template;

	if ( req.getParameter( "username" ) != null )
	    username = req.getParameter( "username" );
	if ( req.getParameter( "password" ) != null )
	    password = req.getParameter( "password" );

	if ( DBRoutines.checkPassword( username, password ) ) {
	    User u = DBRoutines.getUser( username );
	    req.getSession( ).setAttribute( "org.stonybrookschool.merits.objects.user", u );

	    if ( "t".equals( u.getFaculty( ) ) )
		template = doAdminTools( req, res );
	    else
		template = doView( req, res );
	}
	else {
	    req.getSession( ).setAttribute( "org.stonybrookschool.merits.objects.user", null );
	    req.setAttribute( "error", "incorrect username or password" );
	    template = templateBase + "login.jsp";
	}

	return( template );
    }

    private String doView( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "view.jsp";
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	Vector pv = DBRoutines.getPasses( u.getId( ) );
	Vector etv = DBRoutines.getEventTypes( );

	req.setAttribute( "passesVector", pv );
	req.setAttribute( "eventTypesVector", etv );

	return( template );
    }

    private String doViewPasses( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "viewPasses.jsp";
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	int eventId = Integer.parseInt( req.getParameter( "eventId" ) );
	Vector pv = DBRoutines.getPassesByEventId( eventId );
        Event e = DBRoutines.getEvent( eventId );
	EventType et = DBRoutines.getEventType( e.getEventTypeId( ) );

	req.setAttribute( "event", e );
	req.setAttribute( "passesVector", pv );
	req.setAttribute( "eventType", et );

	return( template );
    }

    private String doHistory( HttpServletRequest req, HttpServletResponse res )
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	Vector hv = DBRoutines.getHistory( u.getId( ) );
	String template = templateBase + "history.jsp";

	req.setAttribute( "historyVector", hv );

	return( template );
    }

    private String doPurchaseOptions( HttpServletRequest req, HttpServletResponse res )
    {
	int eventTypeId = Integer.parseInt( req.getParameter( "eventTypeId" ) );
	Vector ev = DBRoutines.getEvents( eventTypeId );
	EventType e = DBRoutines.getEventType( eventTypeId );
	String template = templateBase + "purchaseOptions.jsp";

	req.setAttribute( "eventVector", ev );
	req.setAttribute( "eventType", e );

	return( template );
    }

    private String doPurchaseDetails( HttpServletRequest req, HttpServletResponse res )
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	String template = templateBase + "purchaseDetails.jsp";
	int eventId = Integer.parseInt( req.getParameter( "eventId" ) );
	Event e = DBRoutines.getEvent( eventId );
	EventType et = DBRoutines.getEventType( e.getEventTypeId( ) );
	String textString = DBRoutines.getPreviousPurchaseDate( u.getId( ), e.getId( ), 1209623 ); // 2 weeks & 23 hours
	if ( "dinner pass".equals( et.getName( ) ) && ( textString != "" ) ) { // changes here should be made in doPurchase also
	    String message = "you are not allowed to purchase more than one dinner pass " +
		"within a given 2 week period.<br>you already own a " + textString;
	    req.setAttribute( "message", message );
	    template = templateBase + "purchaseDeny.jsp";

	}
	else if ( DBRoutines.checkUserOwnsEvent( u.getId( ), e.getId( ) ) ) {
	    req.setAttribute( "message", "you already own a pass to this event" );
	    template = templateBase + "purchaseDeny.jsp";

	}
	else {
	    Vector tv = DBRoutines.getTransportations( );

	    req.setAttribute( "event", e );
	    req.setAttribute( "transportationsVector", tv );

	}
	return( template );

    }

    private String doPurchase( HttpServletRequest req, HttpServletResponse res )
    {
	String message;
	String template = templateBase + "purchase.jsp";
	int transportationId = 0;
	String whereGoing = "";
	String whenLeaving = "";
	String whenReturning = "";

	User uOld = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	User u = DBRoutines.getUser( uOld.getUsername( ) );
	req.getSession( ).setAttribute( "org.stonybrookschool.merits.objects.user", u );

	int eventId = Integer.parseInt( req.getParameter( "eventId" ) );
	Event e = DBRoutines.getEvent( eventId );
	EventType et = DBRoutines.getEventType( e.getEventTypeId( ) );

	if ( "t".equals( et.getTransportation( ) ) ) {
	    transportationId = Integer.parseInt( req.getParameter( "transportationId" ) );
	    whereGoing = (String) req.getParameter( "whereGoing" );
	    whenLeaving = req.getParameter( "leavingYear" ) + "-" +
		req.getParameter( "leavingMonth" ) + "-" +
		req.getParameter( "leavingDay" ) + " " +
		req.getParameter( "leavingHour" ) + ":" +
		req.getParameter( "leavingMinute" ) + ":00";
	    whenReturning = req.getParameter( "returningYear" ) + "-" +
		req.getParameter( "returningMonth" ) + "-" +
		req.getParameter( "returningDay" ) + " " +
		req.getParameter( "returningHour" ) + ":" +
		req.getParameter( "returningMinute" ) + ":00";

	}

	if ( e.getId( ) < 1 )
	    message = "invalid event id";
	else
	    if ( e.getSecondsTill( ) < 0 )
		message = "you are too late to purchase this event";
	    else
		if ( u.getMerits( ) < et.getPrice( e.getPurchases( ) ) )
		    message = "you can't afford to purchase this event";
		else
		    if ( DBRoutines.checkUserOwnsEvent( u.getId( ), e.getId( ) ) )
			message = "you already own this event";
		    else {
			if ( "t".equals( et.getMeal( ) ) 
			     && ! "brunch pass".equals( et.getName( ) )
			     && "middleschool".equals( u.getYear( ) ) )
			    message = "middle school students aren't allowed to purchase this type of meal passes";
			else {
			    String textString = DBRoutines.getPreviousPurchaseDate( u.getId( ), e.getId( ), 1209623 ); // 2 weeks & 23 hours
			    if ( "dinner pass".equals( et.getName( ) ) && ( textString != "" ) )
				message = "you are not allowed to purchase more than one dinner pass " +
				    "within a given 2 week period.<br>you already own a " + textString;
			    else {
				u.setMerits( u.getMerits( ) - et.getPrice( e.getPurchases( ) ) );
				req.getSession( ).setAttribute( "org.stonybrookschool.merits.objects.user", u );
				DBRoutines.setUserMerits( u.getId( ), u.getMerits( ) );
				DBRoutines.newHistory( 1, u.getId( ), 
						       et.getPrice( e.getPurchases( ) ) * -1, 0,
						       e.getId( ), "purchased through system" );
				DBRoutines.newPass( u.getId( ), e.getId( ), et.getPrice( e.getPurchases( ) ), 
						    transportationId, whereGoing, whenLeaving, whenReturning );
				message = "event has been purchased";

			    }

			}

		    }

	req.setAttribute( "message", message );
	req.setAttribute( "event", e );
	req.setAttribute( "eventType", et );

	return( template );
    }

    private String doSell( HttpServletRequest req, HttpServletResponse res )
    {
	String message;

	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	int passId = Integer.parseInt( req.getParameter( "passId" ) );
	Pass p = DBRoutines.getPass( passId );
        Event e = DBRoutines.getEvent( p.getEventId( ) );
        EventType et = DBRoutines.getEventType( e.getEventTypeId( ) );

	// todo: chech for valid passId
	if ( e.getSecondsTill( ) < 0 )
	    message = "you are too late to sell this event";
	else
	    if ( DBRoutines.checkUserOwnsEvent( u.getId( ), e.getId( ) ) ) {
		u.setMerits( u.getMerits( ) + p.getPurchasePrice( ) );
		req.getSession( ).setAttribute( "org.stonybrookschool.merits.objects.user", u );
		DBRoutines.setUserMerits( u.getId( ), u.getMerits( ) );
		DBRoutines.newHistory( u.getId( ), 1, 0,
				       p.getPurchasePrice( ),
				       e.getId( ), "sold through system" );
		DBRoutines.deletePass( p.getId( ) );

		message = "event has been sold";
	    }
	    else 
		message = "you don't own this event";

	req.setAttribute( "event", e );
	req.setAttribute( "eventType", et );
	req.setAttribute( "pass", p );
	req.setAttribute( "message", message );

	String template = templateBase + "sell.jsp";

	return( template );
    }

    private String doAdminTools( HttpServletRequest req, HttpServletResponse res )
    {
	Vector gv = (Vector) DBRoutines.getGroups( );
	Vector etv = (Vector) DBRoutines.getEventTypes( );
	Vector uv = (Vector) DBRoutines.getUsers( );

	req.setAttribute( "groupsVector", gv );
	req.setAttribute( "eventTypesVector", etv );
	req.setAttribute( "usersVector", uv );

	String template = templateBase + "adminTools.jsp";

	return( template );
    }

    private String doAdminShowUsers( HttpServletRequest req, HttpServletResponse res )
    {
	String tmp = (String) req.getParameter( "groupId" );
	String inGroup = "f";

	if ( tmp == null ) {
	    System.err.println( "bad post: no groupId" );
	    req.setAttribute( "error", "bad post: no groupId" );
	    return( templateBase + "error.jsp" );
	}
	else {
	    int groupId = Integer.parseInt( tmp );
	    Vector uv = (Vector) DBRoutines.getUsers( groupId );
	    req.setAttribute( "usersVector", uv );
	    User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	    if ( DBRoutines.isUserInGroup( u.getId( ), groupId ) )
		inGroup = "t";
	    req.setAttribute( "userIsInGroup", inGroup );

	    String template = templateBase + "adminShowUsers.jsp";

	    return( template );
	}

    }

    private String doAdminUsers( HttpServletRequest req, HttpServletResponse res )
    {
	Vector uv = (Vector) DBRoutines.getUsers( );
	req.setAttribute( "usersVector", uv );

	String template = templateBase + "adminUsers.jsp";

	return( template );

    }

    private String doAdminShowHistory( HttpServletRequest req, HttpServletResponse res )
    {
	String template = "";
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
        if ( "t".equals( u.getFaculty( ) ) ) {
	    int userId = Integer.parseInt( req.getParameter( "userId" ) );
	    if ( req.getParameter( "userId" ) != null ) {
		User user = DBRoutines.getUser( userId );
		Vector hv = DBRoutines.getHistory( userId );
		req.setAttribute( "user", user );
		req.setAttribute( "historyVector", hv );
		req.setAttribute( "totalMeritsAwarded", DBRoutines.getTotalMeritsAwarded( userId ) );
		req.setAttribute( "totalDemeritsAwarded", DBRoutines.getTotalDemeritsAwarded( userId ) );
		template = templateBase + "adminHistory.jsp";
	    }
	    else {
		req.setAttribute( "message", "no userId supplied" );
		template = "error.jsp";
	    }
	}
	else {
	    req.setAttribute( "message", "you are not allowed to view history" );
	    template = "error.jsp";
	}

	return( template );
    }

    private String doAdminShowEvents( HttpServletRequest req, HttpServletResponse res )
    {
	String tmp = (String) req.getParameter( "eventTypeId" );

	if ( tmp == null ) {
	    System.err.println( "bad post: no eventTypeId" );
	    req.setAttribute( "error", "bad post: no eventTypeId" );
	    return( templateBase + "error.jsp" );
	}
	else {
	    int eventTypeId = Integer.parseInt( tmp );
	    Vector ev;
	    if ( "unexpired".equals( req.getParameter( "type" ) ) )
		ev = DBRoutines.getEvents( eventTypeId );
	    else
		ev = DBRoutines.getLatestEvents( eventTypeId );
	    req.setAttribute( "eventsVector", ev );

	    String template = templateBase + "adminShowEvents.jsp";

	    return( template );
	}

    }

    private String doAdminShowEvent( HttpServletRequest req, HttpServletResponse res )
    {
	String tmp = (String) req.getParameter( "eventId" );

	if ( tmp == null ) {
	    System.err.println( "bad post: no eventId" );
	    req.setAttribute( "error", "bad post: no eventId" );
	    return( templateBase + "error.jsp" );
	}
	else {
	    int eventId = Integer.parseInt( tmp );
	    Event e = (Event) DBRoutines.getEvent( eventId );
	    req.setAttribute( "event", e );
	    EventType et = (EventType) DBRoutines.getEventType( e.getEventTypeId( ) );
	    req.setAttribute( "eventType", et );
	    Vector pv = (Vector) DBRoutines.getPassesByEventId( eventId );
	    req.setAttribute( "passVector", pv );

	    String template = templateBase + "adminShowEvent.jsp";

	    return( template );
	}

    }

    private String doAdminEditUsers( HttpServletRequest req, HttpServletResponse res )
    {
	int userId, deltaMerit, deltaDemerit;
	String message;
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	Vector modifiedUsersVector = new Vector( );
	int eventId = DBRoutines.getEventIdByDate( "0000-00-00 00:00:00" ); // default event (magic number)

	for ( int x = 1; req.getParameter( "userId" + x ) != null; x ++ ) {
	    boolean userModified = false;
	    userId = Integer.parseInt( req.getParameter( "userId" + x ) );
	    User user = DBRoutines.getUser( userId );
	    if ( ! "0".equals( req.getParameter( "deltaMerit" + x ) ) ) {
		deltaMerit = Integer.parseInt( req.getParameter( "deltaMerit" + x ) );
		user.setMerits( user.getMerits( ) + deltaMerit );
		DBRoutines.setUserMerits( userId, user.getMerits( ) );
		DBRoutines.newHistory( u.getId( ), userId, deltaMerit, 0,
				       eventId, req.getParameter( "reason" ) );
		userModified = true;
		if ( debug ) System.err.println( "\nadding " + deltaMerit + " merits to userId " + userId + "\n" );
	    }
	    if ( ! "0".equals( req.getParameter( "deltaDemerit" + x ) ) ) {
		deltaDemerit = Integer.parseInt( req.getParameter( "deltaDemerit" + x ) );
		user.setDemerits( user.getDemerits( ) + deltaDemerit );
		DBRoutines.setUserDemerits( userId, user.getDemerits( ) );
		DBRoutines.newHistory( u.getId( ), userId, 0, deltaDemerit,
				       eventId, req.getParameter( "reason" ) );
		userModified = true;
		if ( debug ) System.err.println( "\nadding " + deltaDemerit + " demerits to userId " + userId + "\n" );
	    }
	    if ( userModified )
		modifiedUsersVector.addElement( user );

	}

	req.setAttribute( "modifiedUsersVector", modifiedUsersVector );

	String template = templateBase + "adminEditUsers.jsp";

	return( template );
    }

    private String doAdminEditUser( HttpServletRequest req, HttpServletResponse res )
    {
	String template = "";

	if ( req.getParameter( "userId" ) != null ) {
	    User u = new User( );
	    Vector gbtv = new Vector( );
	    int userId = Integer.parseInt( req.getParameter( "userId" ) );
	    if ( userId != 0 ) {
		u = DBRoutines.getUser( userId );
		gbtv = DBRoutines.getGroups( userId );
	    }
	    u.setId( userId );
	    Vector y = DBRoutines.getYears( );
	    Vector g = DBRoutines.getGroups( );
	    req.setAttribute( "user", u );
	    req.setAttribute( "years", y );
	    req.setAttribute( "groups", g );
	    req.setAttribute( "groupsBelongedTo", gbtv );

	    template = templateBase + "adminEditUser.jsp";
	}
	else {
	    req.setAttribute( "message", "no userId specified" );
	    template = templateBase + "error.jsp";
	}

	return( template );
    }

    private String doAdminPostEditUser( HttpServletRequest req, HttpServletResponse res )
    {
	User u = new User( );
	User user = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );
	String message = "";

	if ( "t".equals( user.getSuperuser( ) ) ) {
	    if ( req.getParameter( "userId" ) != null ) {
		if ( req.getParameter( "password1" ).equals( req.getParameter( "password2" ) ) ) {
		    if ( ! "".equals( req.getParameter( "username" ) ) ) {
			u.setId( Integer.parseInt( req.getParameter( "userId" ) ) );
			u.setTitle( (String) req.getParameter( "title" ) );
			u.setFirst( (String) req.getParameter( "first" ) );
			u.setLast( (String) req.getParameter( "last" ) );
			u.setUsername( (String) req.getParameter( "username" ) );
			u.setPassword( (String) req.getParameter( "password1" ) );
			u.setEmail( (String) req.getParameter( "email" ) );
			if ( "on".equals( (String) req.getParameter( "faculty" ) ) )
			    u.setFaculty( "t" );
			else
			    u.setFaculty( "f" );
			if ( "on".equals( (String) req.getParameter( "superuser" ) ) )
			    u.setSuperuser( "t" );
			else
			    u.setSuperuser( "f" );
			u.setMerits( Float.valueOf( req.getParameter( "merits" ) ).floatValue( ) );
			u.setYearId( Integer.parseInt( req.getParameter( "yearId" ) ) );
			if ( "on".equals( (String) req.getParameter( "studentCarPermission" ) ) )
			    u.setStudentCarPermission( "t" );
			else
			    u.setStudentCarPermission( "f" );
			if ( "on".equals( (String) req.getParameter( "adultCarPermission" ) ) )
                            u.setAdultCarPermission( "t" );
                        else
                            u.setAdultCarPermission( "f" );
			if ( Integer.parseInt( req.getParameter( "userId" ) ) == 0 ) {
			    User u2 = DBRoutines.getUser( u.getUsername( ) );
			    if ( u2.getId( ) > 0 )
				message = "the username '" + u.getUsername( ) + "' already exists. no changes made.";
			    else {
				DBRoutines.newUser( u );
				message = "created new user";
			    }
			}
			else {
			    DBRoutines.updateUser( u );
			    message = "user updated";
			}
			u = DBRoutines.getUser( (String) req.getParameter( "username" ) );
			DBRoutines.deleteUsersGroups( u.getId( ) );
			String[] groupIds = req.getParameterValues( "groupId" );
			if ( groupIds != null )
			    for ( int x = 0; x < groupIds.length; x ++ )
				DBRoutines.setUsersGroups( u.getId( ), Integer.parseInt( groupIds[x] ) );
		    }
		    else
			message = "sorry, you need to specify a username.";

		}
		else 
		    message = "sorry, those passwords didn't match. no changes have been made.";
	    }
	}
	else {
	    message = "you aren't allowed to edit users";

	}

	req.setAttribute( "message", message );
	String template = templateBase + "adminPostEditUser.jsp";

	return( template );
    }

    private String doAdminEvents( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "adminEvents.jsp";
	Vector ev = DBRoutines.getEvents( );

	req.setAttribute( "eventsVector", ev );

	return( template );

    }

    private String doAdminEventDelete( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "adminEventDelete.jsp";
	String message = "";
	int eventId = Integer.parseInt( req.getParameter( "eventId" ) );
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );

        if ( "t".equals( u.getFaculty( ) ) ) {
	    Event e = DBRoutines.getEvent( eventId );
	    if ( e.getPurchases( ) > 0 ) {
		message = "can't delete event with existing purchases.";
	    }
	    else {
		DBRoutines.deleteEvent( eventId );
		message = "event deleted";
	    }
	}
	else {
	    message = "you aren't authorized to do that";
	}

	req.setAttribute( "message", message );

	return( template );

    }

    private String doAdminEventEditor( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "adminEventEditor.jsp";
	int eventId = Integer.parseInt( req.getParameter( "eventId" ) );
	Event e = new Event( );
	EventType et = new EventType( );
        if ( eventId != 0 ) {
	    e = DBRoutines.getEvent( eventId );
	    et = DBRoutines.getEventType( e.getEventTypeId( ) );
	    if ( debug ) System.err.println( "\neventId = " + eventId + "\nraw = " + e.getRawDate( ) );
	}
	Vector etv = DBRoutines.getEventTypes( );

	req.setAttribute( "eventTypesVector", etv );
	req.setAttribute( "event", e );
	req.setAttribute( "eventType", et );

	return( template );

    }

    private String doAdminEventEditorPost( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "adminEventEditorPost.jsp";
	String message = "";
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );

        if ( "t".equals( u.getFaculty( ) ) ) {
	    int eventId = Integer.parseInt( req.getParameter( "eventId" ) );
	    String date = req.getParameter( "year" ) + "-" +
		req.getParameter( "month" ) + "-" +
		req.getParameter( "day" ) + " " +
		req.getParameter( "hour" ) + ":" +
		req.getParameter( "minute" ) + ":00";
	    int eventTypeId = Integer.parseInt( req.getParameter( "eventTypeId" ) );
	    if ( eventId == 0 ) {
		DBRoutines.newEvent( date, eventTypeId );
		message = "created new event";
	    }
	    else {
		DBRoutines.updateEvent( eventId, date, eventTypeId );
		message = "modified event";
	    }
	}
	else {
	    message = "you don't have the rights to edit events";
	}

	req.setAttribute( "message", message );

	return( template );

    }

    private String doAdminEventBulkEditor( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "adminEventBulkEditor.jsp";
	Vector etv = DBRoutines.getEventTypes( );
	req.setAttribute( "eventTypesVector", etv );

	return( template );

    }

    private String doAdminEventBulkEditorPost( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "adminEventBulkEditorPost.jsp";
	String message = "";
	int eventTypeId = Integer.parseInt( req.getParameter( "eventTypeId" ) );
        User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.merits.objects.user" );

	try {
	    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh:mm:00" );
	    Date d = sdf.parse( req.getParameter( "year" ) + "-" +
				req.getParameter( "month" ) + "-01 " +
				req.getParameter( "hour" ) + ":" +
				req.getParameter( "minute" ) + ":00" );
	    Calendar c = Calendar.getInstance( );
	    c.setTime( d );

	    if ( "t".equals( u.getFaculty( ) ) ) {
		for ( int x = 0; x < c.getMaximum( Calendar.DAY_OF_MONTH ); x ++ ) {
		    if ( c.get( Calendar.DAY_OF_WEEK ) == 1 ) {
			if ( "sunday".equals( req.getParameter( "key" ) ) ) {
			    String date = req.getParameter( "year" ) + "-" +
				req.getParameter( "month" ) + "-" +
				c.get( Calendar.DAY_OF_MONTH ) + " " +
				req.getParameter( "hour" ) + ":" +
				req.getParameter( "minute" ) + ":00";
			    DBRoutines.newEvent( date, eventTypeId );
			    message = message + "added event on " + date + "<br>";
			}
		    }
		    else {
			if ( c.get( Calendar.DAY_OF_WEEK ) == 7 ) {
			    if ( "saturday".equals( req.getParameter( "key" ) ) ) {
				String date = req.getParameter( "year" ) + "-" +
				    req.getParameter( "month" ) + "-" +
				    c.get( Calendar.DAY_OF_MONTH ) + " " +
				    req.getParameter( "hour" ) + ":" +
				    req.getParameter( "minute" ) + ":00";
				DBRoutines.newEvent( date, eventTypeId );
				message = message + "added event on " + date + "<br>";
			    }
			}
			else {
			    if ( "weekday".equals( req.getParameter( "key" ) ) ) {
				String date = req.getParameter( "year" ) + "-" +
				    req.getParameter( "month" ) + "-" +
				    c.get( Calendar.DAY_OF_MONTH ) + " " +
				    req.getParameter( "hour" ) + ":" +
				    req.getParameter( "minute" ) + ":00";
				DBRoutines.newEvent( date, eventTypeId );
				message = message + "added event on " + date + "<br>";
			    }
			}
		    }
		    c.add( Calendar.HOUR, 24 );
		}
	    }
	    else {
		message = "you don't have the rights to edit events";
	    }

	    req.setAttribute( "message", message );

	}
	catch ( ParseException pe ) {
	    System.err.println( "doAdminEventBulkEditorPost: parse exception: " + pe );
	}

	return( template );

    }

    private String doAdminDebug( HttpServletRequest req, HttpServletResponse res )
    {
	String template = templateBase + "adminDebug.jsp";

	Locale l = Locale.US;
	SimpleDateFormat sdf = new SimpleDateFormat( "EEEE, MMMM d, yyyy h:mm:ss a", l );
	String srunDate = sdf.format( new Date( ) ).toLowerCase( );
	String dbDate = DBRoutines.getDatabaseDate( );
	Vector tsv = DBRoutines.getTableSizeVector( );

	req.setAttribute( "servletRunnerDate", srunDate );
	req.setAttribute( "databaseDate", dbDate.toLowerCase( ) );
	req.setAttribute( "tableSizeVector", tsv );

	return( template );

    }

    private String doLogout( HttpServletRequest req, HttpServletResponse res )
    {
	req.getSession( ).setAttribute( "org.stonybrookschool.merits.objects.user", null );
	req.setAttribute( "error", "you have successfully logged out" );

	String template = templateBase + "login.jsp";

	return( template );
    }

}


