package org.stonybrookschool.dailyBulletin.objects;

import java.util.*;

public class Poll
{
    private int    id, userId, priorityId, voteOptionId, totalVotes;
    private String date, publishDate, unpublishDate, username, question, state, resultsPublic;
    private Vector options;

    public Poll ( )
    {

    }

    public Poll ( int id, String date, String publishDate, String unpublishDate,
		  String question, int userId, String username, String state, 
		  int priorityId, Vector options, int totalVotes, String resultsPublic )
    {
	this.id            = id;
	this.date          = date;
	this.publishDate   = publishDate;
	this.unpublishDate = unpublishDate;
	this.question      = question;
	this.userId        = userId;
	this.username      = username;
	this.state         = state;
	this.priorityId    = priorityId;
	this.options       = options;
	this.totalVotes    = totalVotes;
	this.resultsPublic = resultsPublic;
    }

    public int getId( ) { return( id ); }
    public String getDate( ) { return( date ); }
    public String getPublishDate( ) { return( publishDate ); }
    public String getUnpublishDate( ) { return( unpublishDate ); }
    public String getQuestion( ) { return( question ); }
    public int getUserId( ) { return( userId ); }
    public String getUsername( ) { return( username ); }
    public String getState( ) { return( state ); }
    public int getPriorityId( ) { return( priorityId ); }
    public Vector getOptions( ) { return( options ); }
    public int getVoteOptionId( ) { return( voteOptionId ); }
    public int getTotalVotes( ) { return( totalVotes ); }
    public String getResultsPublic( ) { return( resultsPublic ); }

    public void setId( int id ) { this.id = id; }
    public void setDate( String date ) { this.date = date; }
    public void setPublishDate( String publishDate ) { this.publishDate = publishDate; }
    public void setUnpublishDate( String unpublishDate ) { this.unpublishDate = unpublishDate; }
    public void setQuestion( String question ) { this.question = question; }
    public void setUserId( int userId ) { this.userId = userId; }
    public void setUsername( String username ) { this.username = username; }
    public void setState( String state ) { this.state = state; }
    public void setPriorityId( int priorityId ) { this.priorityId = priorityId; }
    public void setOptions( Vector options ) { this.options = options; }
    public void setVoteOptionId( int voteOptionId ) { this.voteOptionId = voteOptionId; }
    public void setTotalVotes( int totalVotes ) { this.totalVotes = totalVotes; }
    public void setResultsPublic( String resultsPublic ) { this.resultsPublic = resultsPublic; };

}
