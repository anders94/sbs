package org.stonybrookschool.dailyBulletin.objects;

public class PollOption
{
    private int    id, pollId, ordinal, votes;
    private String answer;

    public PollOption ( )
    {

    }

    public PollOption ( int id, int pollId, String answer, int ordinal, int votes )
    {
	this.id      = id;
	this.pollId  = pollId;
	this.answer  = answer;
	this.ordinal = ordinal;
	this.votes   = votes;
    }

    public int getId( ) { return( id ); }
    public int getPollId( ) { return( pollId ); }
    public String getAnswer( ) { return( answer ); }
    public int getOrdinal( ) { return( ordinal ); }
    public int getVotes( ) { return( votes ); }

    public void setId( int id ) { this.id = id; }
    public void setPollId( int pollId ) { this.pollId = pollId; }
    public void setAnswer( String answer ) { this.answer = answer; }
    public void setOrdinal( int ordinal ) { this.ordinal = ordinal; }
    public void setVotes( int votes ) { this.votes = votes; }

}












