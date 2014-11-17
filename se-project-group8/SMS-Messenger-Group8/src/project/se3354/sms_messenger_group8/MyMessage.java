package project.se3354.sms_messenger_group8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class MyMessage {
	
	private Bitmap icon;
    private String contactName = null;
    private String phoneNumber = null;
    private String messageDate = null;
    private String messageBody = null;
    private String messageType = null;
    private String messageThreadId = null;
    private String messageId = null;
    private String messageDraft = null;
    
    public Bitmap getIcon() {return icon;}
    public String getContactName() {return contactName;}
    public String getPhoneNumber() {return phoneNumber;}
    public String getMessageDate() {return messageDate;}
    public String getMessageBody() {return messageBody;}
    public String getMessageType() {return messageType;}
    public String getMessageThreadId() {return messageThreadId;}
    public String getMessageId() {return messageId;}
    public String isDraft() {return messageDraft;}
    
    public void setIcon(Bitmap icon) {this.icon = icon;}
    public void setContactName(String contactName) {this.contactName = contactName;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
    public void setMessageDate(String messageDate) {this.messageDate = messageDate;}
    public void setMessageBody(String messageBody) {this.messageBody = messageBody;}
    public void setMessageType(String messageType) {this.messageType = messageType;}
    public void setMessageThreadId(String messageThreadId) {this.messageThreadId = messageThreadId;}
    public void setMessageId(String messageId) {this.messageId = messageId;}
    public void isDraft(boolean isDraft) {if (isDraft) {messageDraft = "Draft";} }
}
