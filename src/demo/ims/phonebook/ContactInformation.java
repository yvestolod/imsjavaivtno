package demo.ims.phonebook;

public class ContactInformation {

	private String lastName;
	private String firstName;
	private String phoneNumber;
	private String zipCode;
	private String segmentNumber;
	private String outputMessage;
	
	public ContactInformation() {
		this.lastName = "";
		this.firstName = "";
		this.phoneNumber = "";
		this.zipCode = "";
		this.segmentNumber = "";
		this.outputMessage = "";
	}	
	
	public String getLastName() {
		return lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getZipCode() {
		return zipCode;
	}
	
	public String getSegmentNumber() {
		return segmentNumber;
	}
	
	public String getOutputMessage() {
		return outputMessage;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName.trim();
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName.trim();
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber.trim();
	}
	
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode.trim();
	}
	
	public void setSegmentNumber(String segmentNumber) {
		this.segmentNumber = segmentNumber.trim();
	}
	
}
