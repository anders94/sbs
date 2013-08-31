package org.stonybrookschool.dailyBulletin;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.stonybrookschool.dailyBulletin.*;
import org.stonybrookschool.dailyBulletin.data.*;
import org.stonybrookschool.dailyBulletin.objects.*;

// a "pqa" is a palm query application for a palm pilot. this is somewhat dated these days

public class viewer extends HttpServlet
{

    String templateBase = "/dailyBulletin/templates/";
    String template = "";

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
	res.setContentType( "text/html" );

	String page = req.getParameter( "page" );

	if ( ( page != null ) && ( ! "".equals( page ) ) ) {
	    if ( page.equals( "frontPage" ) )
		template = doFrontPage( req, res );
	    else if ( page.equals( "login" ) )
		template = doProcessLogin( req, res );
	    else if ( page.equals( "logout" ) )
		template = doProcessLogout( req, res );
	    else if ( page.equals( "story" ) )
                template = doStory( req, res );
            else if ( page.equals( "edit" ) )
                template = doEditList( req, res );
 	    else if ( page.equals( "editStory" ) )
		template = doEditStory( req, res );
	    else if ( page.equals( "postEdit" ) )
		template = doPostEdit( req, res );
            else if ( page.equals( "admin" ) )
                template = doAdminEditList( req, res );
	    else if ( page.equals( "adminEditStory" ) )
		template = doAdminEditStory( req, res );
	    else if ( page.equals( "adminPostEdit" ) )
		template = doAdminPostEdit( req, res );
	    else if ( page.equals( "poll" ) )
		template = doPoll( req, res );
	    else if ( page.equals( "pollAdmin" ) )
		template = doPollAdmin( req, res );
	    else if ( page.equals( "pollEdit" ) )
		template = doPollEdit( req, res );
	    else if ( page.equals( "pollPost" ) )
		template = doPollPost( req, res );
	    else if ( page.equals( "pollVote" ) )
		template = doPollVote( req, res );
	    else if ( page.equals( "showPolls" ) )
		template = doShowPolls( req, res );
	    // if ( page.equals( "adminUserEdit" ) )
	    //   template = doAdminUserEdit( req, res );
	    // if ( page.equals( "adminUserPost" ) )
	    //   template = doAdminUserPost( req, res );

	}
	else
	    template = doFrontPage( req, res );

	if ( req.getParameter( "template" ) != null )
	    template = templateBase + req.getParameter( "template" ) + ".jsp";

	if ( ( templateBase + "rss.jsp" ).equals( template ) ) {
	    res.setContentType( "application/xml" );
	    RequestDispatcher rd = req.getRequestDispatcher( template );
	    rd.include( req, res );

	}
	else {
	    // execute the jsp page
	    RequestDispatcher rd = req.getRequestDispatcher( template );
	    rd.include( req, res );

	}

    }

    public String doFrontPage( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	Vector sv = new Vector( );
	String edition;
	String dateString = "";
	int pollId = 0;
	Poll p = new Poll( );
	Vector pollVector = new Vector( );
	Vector piv = DBRoutines.getLatestPollIds( (String) req.getSession( ).getAttribute( "edition" ) );

	if ( req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" ) != null ) {
	    User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	    if ( piv.size( ) > 0 ) {
		p = DBRoutines.getPoll( Integer.parseInt( (String) piv.elementAt( 0 ) ) );
		p.setVoteOptionId( DBRoutines.getVoteOptionId( p.getId( ), u.getId( ) ) );
		pollVector.addElement( p );
		p = new Poll( );
	    }

	    if ( piv.size( ) > 1 ) {
		p = DBRoutines.getPoll( Integer.parseInt( (String) piv.elementAt( 1 ) ) );
		p.setVoteOptionId( DBRoutines.getVoteOptionId( p.getId( ), u.getId( ) ) );
		pollVector.addElement( p );
	    }

	    if ( "t".equals( u.getStaff( ) ) ) {
		if ( req.getParameter( "edition" ) != null ) {
		    if ( "student".equals( req.getParameter( "edition" ) ) )
			edition = "student";
		    else 
			if ( "parent".equals( req.getParameter( "edition" ) ) )
			    edition = "parent";
			else
			    edition = "staff";
		    req.getSession( ).setAttribute( "edition", edition );
		}
	    }
	}
	else { // not logged in
	    if ( piv.size( ) > 0 ) {
		p = DBRoutines.getPoll( Integer.parseInt( (String) piv.elementAt( 0 ) ) );
		p.setVoteOptionId( 0 );
		pollVector.addElement( p );
		p = new Poll( );
	    }

	    if ( piv.size( ) > 1 ) {
		p = DBRoutines.getPoll( Integer.parseInt( (String) piv.elementAt( 1 ) ) );
		p.setVoteOptionId( 0 );
		pollVector.addElement( p );
	    }
	    if ( "pqa".equals( req.getParameter( "template" ) ) ) // allow the pqa version to set an edition
		req.getSession( ).setAttribute( "edition", req.getParameter( "edition" ) );
	    else
		req.getSession( ).setAttribute( "edition", CommonRoutines.getEdition( req.getRemoteAddr( ) ) );
	}

	if ( req.getParameter( "date" ) != null ) {
	    dateString = (String) req.getParameter( "date" );
	    sv = DBRoutines.getStories( (String) req.getSession( ).getAttribute( "edition" ), dateString );

	}
	else {
	    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
	    dateString = sdf.format( new java.util.Date( ) );
	    if ( req.getParameter( "query" ) != null ) {
		sv = DBRoutines.getStoriesBySearchTerm( (String) req.getSession( ).getAttribute( "edition" ), (String) req.getParameter( "query" ) );

	    }
	    else {
		sv = DBRoutines.getStories( (String) req.getSession( ).getAttribute( "edition" ), dateString );

	    }

	}

	req.setAttribute( "date", dateString );
	req.setAttribute( "storyVector", sv );
	req.setAttribute( "pollVector", pollVector );

	return( templateBase + "frontPage.jsp" );
    }

    public String doProcessLogin( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	String username = req.getParameter( "username" );
	String password = req.getParameter( "password" );
	if ( username != null ) {
	    if ( password != null ) {
		if ( DBRoutines.checkPassword( username, password ) ) {
		    User u = DBRoutines.getUser( username );
		    req.getSession( ).setAttribute( "org.stonybrookschool.dailyBulletin.objects.user", u );
		    if ( "t".equals( u.getStaff( ) ) )
			req.getSession( ).setAttribute( "edition", "staff" );
		    else
			req.getSession( ).setAttribute( "edition", "student" );
		}
	    }
	}
	return( doFrontPage( req, res ) );
    }

    public String doProcessLogout( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	req.getSession( ).setAttribute( "org.stonybrookschool.dailyBulletin.objects.user", null );
	req.getSession( ).setAttribute( "edition", null );

	return( doFrontPage( req, res ) );
    }

    public String doEditList( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	Vector sv = DBRoutines.getUserOndeckStories( u.getId( ) );
	req.setAttribute( "storyVector", sv );

        return( templateBase + "editList.jsp" );
    }

    public String doAdminEditList( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	if ( "t".equals( u.getPublisher( ) ) ) {
	    Vector sv = DBRoutines.getOndeckStories( );
	    req.setAttribute( "storyVector", sv );

	    return( templateBase + "adminEditList.jsp" );
	}
	else {
	    return( templateBase + "frontPage.jsp" );
	}
    }

    public String doEditStory( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
	int storyId;
	Story s;
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );

	storyId = Integer.parseInt( req.getParameter( "storyId" ) );

	if ( storyId > 0 )
	    s = DBRoutines.getStory( storyId );
	else {
	    s = new Story( );
	    s.setUserId( u.getId( ) );
	}

	req.setAttribute( "story", s );

        return( templateBase + "edit.jsp" );
    }

    public String doAdminEditStory( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
	int storyId;
	Story s;
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );

	storyId = Integer.parseInt( req.getParameter( "storyId" ) );

	if ( storyId > 0 )
	    s = DBRoutines.getStory( storyId );
	else {
	    s = new Story( );
	    s.setUserId( u.getId( ) );
	}

	req.setAttribute( "story", s );

        return( templateBase + "adminEdit.jsp" );
    }

    public String doPostEdit( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
	boolean publisher = false;
	Story s = new Story( );
	Story p = new Story( );
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	int storyId = Integer.parseInt( req.getParameter ( "storyId" ) );

	if ( "t".equals( u.getPublisher( ) ) )
	    publisher = true;

	if ( ! "".equals( req.getParameter( "head" ) ) ) {
	    if ( storyId > 0 )
		p = DBRoutines.getStory( storyId );
	    s.setId( storyId );
	    s.setPublishDate( req.getParameter( "publishYear" ) + "-" +
			      req.getParameter( "publishMonth" ) + "-" +
			      req.getParameter( "publishDay" ) + " 00:00:00" );
	    s.setUnpublishDate( req.getParameter( "unpublishYear" ) + "-" +
				req.getParameter( "unpublishMonth" ) + "-" +
				req.getParameter( "unpublishDay" ) + " 00:00:00" );
	    if ( "on".equals( req.getParameter( "student" ) ) )
		s.setStudent( "t" );
	    else
		s.setStudent( "f" );
	    if ( "on".equals( req.getParameter( "staff" ) ) )
		s.setStaff( "t" );
	    else
		s.setStaff( "f" );
	    if ( "on".equals( req.getParameter( "parent" ) ) )
		s.setParent( "t" );
	    else
		s.setParent( "f" );
	    if ( publisher )
		s.setPriority( Integer.parseInt( req.getParameter( "priority" ) ) );
	    else
		if ( storyId > 0 )
		    s.setPriority( p.getPriority( ) );
		else 
		    s.setPriority( 10 );
	    s.setHead( CommonRoutines.escapeHTML( req.getParameter( "head" ) ) );
	    s.setText( CommonRoutines.escapeHTML( req.getParameter( "text" ) ) );
	    if ( storyId > 0 )
		s.setUserId( p.getUserId( ) );
	    else
		s.setUserId( u.getId( ) );
	    if ( publisher )
		s.setState( req.getParameter( "state" ) );
	    else
		s.setState( "pending" );
	    
	    if ( storyId == 0 )
		DBRoutines.insertStory( s );
	    else
		DBRoutines.updateStory( s );
	}
	else { // no headline
	    // need to deal with this error
	}

        return( doEditList( req, res ) ); 
    }

    public String doAdminPostEdit( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
	boolean publisher = false;
	Story s = new Story( );
	Story p = new Story( );
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	int storyId = Integer.parseInt( req.getParameter ( "storyId" ) );

	if ( "t".equals( u.getPublisher( ) ) )
	    publisher = true;

	if ( ! "".equals( req.getParameter( "head" ) ) ) {
	    if ( storyId > 0 )
		p = DBRoutines.getStory( storyId );
	    s.setId( storyId );
	    s.setPublishDate( req.getParameter( "publishYear" ) + "-" +
			      req.getParameter( "publishMonth" ) + "-" +
			      req.getParameter( "publishDay" ) + " 00:00:00" );
	    s.setUnpublishDate( req.getParameter( "unpublishYear" ) + "-" +
				req.getParameter( "unpublishMonth" ) + "-" +
				req.getParameter( "unpublishDay" ) + " 00:00:00" );
	    if ( "on".equals( req.getParameter( "student" ) ) )
		s.setStudent( "t" );
	    else
		s.setStudent( "f" );
	    if ( "on".equals( req.getParameter( "staff" ) ) )
		s.setStaff( "t" );
	    else
		s.setStaff( "f" );
	    if ( "on".equals( req.getParameter( "parent" ) ) )
		s.setParent( "t" );
	    else
		s.setParent( "f" );
	    if ( publisher )
		s.setPriority( Integer.parseInt( req.getParameter( "priority" ) ) );
	    else
		if ( storyId > 0 )
		    s.setPriority( p.getPriority( ) );
		else 
		    s.setPriority( 10 );
	    s.setHead( CommonRoutines.escapeHTML( req.getParameter( "head" ) ) );
	    s.setText( CommonRoutines.escapeHTML( req.getParameter( "text" ) ) );
	    if ( storyId > 0 )
		s.setUserId( p.getUserId( ) );
	    else
		s.setUserId( u.getId( ) );
	    if ( publisher )
		s.setState( req.getParameter( "state" ) );
	    else
		s.setState( "pending" );
	    
	    if ( storyId == 0 )
		DBRoutines.insertStory( s );
	    else
		DBRoutines.updateStory( s );
	}
	else { // no headline
	    // need to deal with this error
	}

        return( doAdminEditList( req, res ) ); 
    }

    public String doStory( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
	String template = "";

	if ( req.getParameter( "storyId" ) != null ) {
	    if ( ! "".equals( req.getParameter( "storyId" ) ) ) { 
		int storyId = Integer.parseInt( req.getParameter( "storyId" ) );
		Story s = DBRoutines.getStory( storyId );
		req.setAttribute( "story", s );
		template = "story.jsp";
	    }
	    else {
		req.setAttribute( "message", "empty story id" );
		template = "error.jsp";
	    }
	}
	else {
	    req.setAttribute( "message", "null story id" );
	    template = "error.jsp";
	}

	return ( templateBase + template );

    }

    public String doPoll( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	Vector pv = new Vector( );
	Vector piv;
	String template = "poll.jsp";

	if ( req.getParameter( "action" ) != null ) {
	    if ( ! "".equals( req.getParameter( "pollId" ) ) && ! "0".equals( req.getParameter( "pollId" ) ) ) {
		if ( "delete".equals( req.getParameter( "action" ) ) ) {
		    DBRoutines.setPollState( Integer.parseInt( req.getParameter( "pollId" ) ), "deleted" );
		}
		else if ( "unpublish".equals( req.getParameter( "action" ) ) ) {
		    DBRoutines.setPollState( Integer.parseInt( req.getParameter( "pollId" ) ), "unpublished" );
		}
	    }
	}

	piv = DBRoutines.getPollIdsByUserId( u.getId( ) );
	for ( int x = 0; x < piv.size( ); x ++ )
	    pv.addElement( DBRoutines.getPoll( Integer.parseInt( (String) piv.elementAt( x ) ) ) );
	req.setAttribute( "pollVector", pv );

	return( templateBase + template );
    }

    public String doPollAdmin( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	Vector pv = new Vector( );
	Vector piv;
	String template = "poll.jsp";

	piv = DBRoutines.getPollAdminIds( );
	for ( int x = 0; x < piv.size( ); x ++ )
	    pv.addElement( DBRoutines.getPoll( Integer.parseInt( (String) piv.elementAt( x ) ) ) );
	req.setAttribute( "pollVector", pv );

	return( templateBase + template );
    }

    public String doPollEdit( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	Poll p = new Poll( );

	if ( ! "".equals( req.getParameter( "pollId" ) ) && ! "0".equals( req.getParameter( "pollId" ) ) ) {
	    p = DBRoutines.getPoll( Integer.parseInt( req.getParameter( "pollId" ) ) );
	    if ( ! "".equals( req.getParameter( "action" ) ) ) {
		if ( "delete".equals( req.getParameter( "action" ) ) )
		    if ( "t".equals( u.getPublisher( ) ) || u.getId( ) == p.getUserId( ) ) { 
			// if this is a publisher or they own the poll, change state to deleted
			//		DBRoutines.deletePoll( p.getId( ) );
		    }
	    }

	}

	req.setAttribute( "poll", p );
	req.setAttribute( "prioritiesVector", DBRoutines.getPriorities( ) );

	return( templateBase + "pollEdit.jsp" );
    }

    public String doPollPost( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	boolean publisher = false;
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	Poll np = new Poll( ); // np = new poll
	Poll op = new Poll( ); // op = old poll
	int pollId = Integer.parseInt( req.getParameter ( "pollId" ) );
	Vector ov = new Vector( );

	if ( "t".equals( u.getPublisher( ) ) )
	    publisher = true;

	if ( ! "".equals( req.getParameter( "question" ) ) ) {
	    if ( pollId > 0 )
		op = DBRoutines.getPoll( pollId );
	    np.setId( pollId );
	    np.setPublishDate( req.getParameter( "publishYear" ) + "-" +
			       req.getParameter( "publishMonth" ) + "-" +
			       req.getParameter( "publishDay" ) + " 00:00:00" );
	    np.setUnpublishDate( req.getParameter( "unpublishYear" ) + "-" +
				 req.getParameter( "unpublishMonth" ) + "-" +
				 req.getParameter( "unpublishDay" ) + " 00:00:00" );

	    // !!! need to deal with editions here!

	    np.setQuestion( req.getParameter( "question" ) );
	    np.setResultsPublic( req.getParameter( "resultsPublic" ) );

	    if ( publisher )
		np.setPriorityId( Integer.parseInt( req.getParameter( "priorityId" ) ) );
	    else
		if ( pollId > 0 )
		    np.setPriorityId( op.getPriorityId( ) );
		else 
		    np.setPriorityId( 10 );

	    if ( pollId > 0 )
		np.setUserId( op.getUserId( ) );
	    else
		np.setUserId( u.getId( ) );
	    if ( publisher )
		np.setState( req.getParameter( "state" ) );
	    else
		np.setState( "pending" );

	    for ( int m = 1; req.getParameter( "option" + m ) != null; m ++ ) {
		ov.addElement( new PollOption( 0, pollId, (String) req.getParameter( "option" + m ), m, 0 ) );
	    }
	    np.setOptions( ov );

	    if ( pollId == 0 )
		DBRoutines.insertPoll( np );
	    else
		DBRoutines.updatePoll( np );
	}
	else { // no question
	    // need to deal with this error
	}

	return( doPoll( req, res ) );
    }

    public String doPollVote( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );

	if ( ! "".equals( req.getParameter( "pollId" ) ) ) {
	    int pollId = Integer.parseInt( req.getParameter( "pollId" ) );
	    if ( DBRoutines.getVoteOptionId( pollId, u.getId( ) ) == 0 )
		DBRoutines.setVoteOptionId( pollId, u.getId( ), Integer.parseInt( req.getParameter( "optionId" ) ) );
   
	}

	return( doFrontPage( req, res ) );
    }

    public String doShowPolls( HttpServletRequest req, HttpServletResponse res )
	throws ServletException, IOException
    {
	User u = (User) req.getSession( ).getAttribute( "org.stonybrookschool.dailyBulletin.objects.user" );
	Vector pv = new Vector( );
	Vector piv = DBRoutines.getLatestPollIds( (String) req.getSession( ).getAttribute( "edition" ) );
	String template = "showPolls.jsp";

	for ( int x = 0; x < piv.size( ); x ++ )
	    pv.addElement( DBRoutines.getPoll( Integer.parseInt( (String) piv.elementAt( x ) ) ) );

	req.setAttribute( "pollVector", pv );

	return( templateBase + template );

    }

}



