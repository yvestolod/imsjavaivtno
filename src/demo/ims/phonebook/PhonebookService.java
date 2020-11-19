package demo.ims.phonebook;

import java.sql.*;

public class PhonebookService {

	private Connection imsConnection;
	private Statement statement;
	private ResultSet resultSet;
	private String outputMessage;
	private String sql;


	public PhonebookService(Connection imsConnection) {
		
		this.imsConnection = imsConnection;
		this.sql = "";
		this.outputMessage = "";
	}
	
	public String addContact(ContactInformation contact) throws SQLException {

		int insertCount = 0;

		statement = imsConnection.createStatement();
		sql = "INSERT INTO PHONEBOOK.PERSON " +
			    "(LASTNAME, FIRSTNAME, EXTENSION, ZIPCODE) VALUES " +
			    "('" + contact.getLastName() + "', '" + contact.getFirstName() +
			    "', '" + contact.getPhoneNumber() + "', '" + contact.getZipCode() + "')";
		insertCount = statement.executeUpdate(sql);
		
		writeSQLToJoblog(sql);
		
		if (insertCount > 0) {
			outputMessage = "INFO: CONTACT WITH LASTNAME " + contact.getLastName() +
					        " WAS ADDED SUCCESSFULLY, COUNT = " + insertCount;
		}
		else {
			outputMessage = "ERROR: CONTACT WITH LASTNAME " + contact.getLastName() +
					        " WAS NOT ADDED SUCCESSFULLY";
		}

		writeToJoblog(outputMessage);
		statement.close();
	
		return outputMessage;
	}
	
	public String deleteContact(String lastName) throws SQLException {

		int deleteCount = 0;

		statement = imsConnection.createStatement();
		sql = "DELETE FROM PHONEBOOK.PERSON WHERE " +
			    " LASTNAME = '" + lastName + "'";
		deleteCount = statement.executeUpdate(sql);
		
		writeSQLToJoblog(sql);
		
		if (deleteCount > 0) {
			outputMessage = "INFO: CONTACT WITH LASTNAME " + lastName +
					        " WAS DELETED SUCCESSFULLY, COUNT = " + deleteCount;
		}
		else {
			outputMessage = "ERROR: CONTACT WITH LASTNAME " + lastName +
					        " WAS NOT DELETED SUCCESSFULLY";
		}
		
		writeToJoblog(outputMessage);
		statement.close();
		
		return outputMessage;
	}
	
	public String updateContact(ContactInformation contact) throws SQLException {
		
		int updateCount = 0;

		statement = imsConnection.createStatement();
		sql = "UPDATE PHONEBOOK.PERSON " +
			    "SET FIRSTNAME = '" + contact.getFirstName() + "', " +
				"EXTENSION = '" + contact.getPhoneNumber() + "', " +
			    "ZIPCODE =  '" + contact.getZipCode() + "' " +
				"WHERE LASTNAME = '" + contact.getLastName() + "'";
		updateCount = statement.executeUpdate(sql);
		
		writeSQLToJoblog(sql);

		if (updateCount > 0) {
			outputMessage = "INFO: CONTACT WITH LASTNAME " + contact.getLastName() +
					        " WAS UPDATED SUCCESSFULLY, COUNT = " + updateCount;
		}
		else {
			outputMessage = "ERROR: CONTACT WITH LASTNAME " + contact.getLastName() +
					        " WAS NOT UPDATED SUCCESSFULLY";
		}
		
		writeToJoblog(outputMessage);
		statement.close();
	
		return outputMessage;
	}
	
	public String getContact(String lastName) throws SQLException {
		
		int rowCount = 0;
		
		statement = imsConnection.createStatement();
		sql = "SELECT * FROM PHONEBOOK.PERSON WHERE LASTNAME = '" + lastName + "'";
		resultSet = statement.executeQuery(sql);
		
		writeSQLToJoblog(sql);
		
		while (resultSet.next()) {
			rowCount ++;
			outputMessage = " LN:" + resultSet.getString("LASTNAME") + 
					        " FN:" + resultSet.getString("FIRSTNAME") + 
					        " PN:" + resultSet.getString("EXTENSION") + 
		                    " ZC:" + resultSet.getString("ZIPCODE");
		}
		
		if (rowCount == 0) {
			outputMessage = "ERROR: CONTACT WITH LASTNAME " + lastName +
					        " WAS NOT FOUND.";
		}
			
		writeToJoblog(outputMessage);
		resultSet.close();
		statement.close();
		
		return outputMessage;
	}

	
	private static void writeToJoblog(String text) {
		
		// If you have the following in the JMP job
		//
		// //STDOUT   DD  SYSOUT=* 
		//
		// then the output is written to th job log
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		System.out.println(" " + ts + " " + text);
	}
	
	
	private static void writeSQLToJoblog(String text) {
		
		// If you have the following in the JMP job
		//
		// //STDOUT   DD  SYSOUT=* 
		//
		// then the output is written to th job log
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		System.out.println(" " + ts + " SQL = " + text);
	}		
}
