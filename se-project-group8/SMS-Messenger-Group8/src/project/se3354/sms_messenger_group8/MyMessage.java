package project.se3354.sms_messenger_group8;

import android.graphics.Bitmap;

public class MyMessage {
	//private Bitmap icon;
    private String contactName = null;
    private String messageDate = null;
    private String messageBody = null;
    private String messageDraft = null;
    
    //public Bitmap getIcon() {return icon;}
    public String getContactName() {return contactName;}
    public String getMessageDate() {return messageDate;}
    public String getMessageBody() {return messageBody;}
    public String isDraft() {return messageDraft;}
    
    //public void setIcon(Bitmap icon) {this.icon = icon;}
    public void setContactName(String contactName) {this.contactName = contactName;}
    public void setMessageDate(String messageDate) {this.messageDate = messageDate;}
    public void setMessageBody(String messageBody) {this.messageBody = messageBody;}
    public void isDraft(boolean isDraft) {if (isDraft) {messageDraft = "Draft";} }
}
