package org.stonybrookschool.enotes.data;

import java.util.Vector;

public class MailParameters {
    String messageFrom;
    String mailServer;
    Vector feedbackRecipients;
    String disciplineStandardText;
    String quicknoteStandardText;
    String commentStandardText;
    String disciplineSubject;
    String quicknoteSubject;
    String commentSubject;

    MailParameters(
        String mf,
        String ms,
        String dst,
        String qst,
        String cst,
        String ds,
        String qs,
        String cs
    ) {
        messageFrom = mf;
        mailServer = ms;
        disciplineStandardText = dst;
        quicknoteStandardText = qst;
        commentStandardText = cst;
        disciplineSubject = ds;
        quicknoteSubject = qs;
        commentSubject = cs;

        feedbackRecipients = new Vector();
    } // MailParameters

    public void addFeedbackRecipient( String adr ) {
        feedbackRecipients.add( adr );
    } // addFeedbackRecipient()

    public String getDisciplineSubject() { return disciplineSubject; }
    public String getQuicknoteSubject() { return quicknoteSubject; }
    public String getCommentSubject() { return commentSubject; }
    public Vector getFeedbackRecipients() { return feedbackRecipients; }
    public String getMessageFrom() { return messageFrom; }
    public String getMailServer() { return mailServer; }
    public String getDisciplineStandardText() { return disciplineStandardText; }
    public String getQuicknoteStandardText() { return quicknoteStandardText; }
    public String getCommentStandardText() { return commentStandardText; }
}
