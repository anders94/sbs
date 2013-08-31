package org.stonybrookschool.enotes.objects;

public class QuicknoteType
{
    private int quicknoteTypeId;
    private String quicknoteTypeName;

    public QuicknoteType( int quicknoteTypeId, String quicknoteTypeName  )
    {
        this.quicknoteTypeId = quicknoteTypeId;
        this.quicknoteTypeName = quicknoteTypeName;
    }

    public int getQuicknoteTypeId( ) { return( quicknoteTypeId ); }
    public String getQuicknoteTypeName( ) { return( quicknoteTypeName ); }

}
