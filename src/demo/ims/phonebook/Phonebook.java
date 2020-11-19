package demo.ims.phonebook;

import com.ibm.ims.dli.DLIException;
import com.ibm.ims.dli.tm.Application;
import com.ibm.ims.dli.tm.ApplicationFactory;
import com.ibm.ims.dli.tm.IOMessage;
import com.ibm.ims.dli.tm.MessageQueue;
import com.ibm.ims.dli.tm.Transaction;
import com.ibm.ims.jdbc.IMSDataSource;

import demo.ims.phonebook.PhonebookService;

import java.sql.*;

// This Java program will access the IMS IVP Sample DFSIVP37 (database name IVPDB2)
// used by the phonebook IMS application.
//
// The database record format of DFSIVD1
//
// Offset    Length     Fieldname       Description
// ================================================
// 0	     10	        A1111111	    Last Name
// 10	     10	        N/A	            First Name
// 20	     10	        N/A	            Extension Number
// 30	      7	        N/A	            Internal Zip Code
// 37	      3      	N/A	            Reserved
//
// The input to transaction is
//
// IMSPBOOK <ACT:4><LASTNAME:10><FIRSTNAME:10><EXTENSION:10><ZIPCODE:7>
//
// Where <ACT> can be DIS, ADD, DEL, and UPD
//
// For DIS and DEL, you only need to specify <LASTNAME>
// For ADD and UPD, you need to specify <LASTNAME>, <FIRSTNAME>, <EXTENSION>
// and <ZIPCODE>
//
// For example:
//
// IMSPBOOK ADD DOE       JOHN      5551234567B2A1A1
// IMSPBOOK UPD DOE       JANE      5559876543A1A2B2
// IMSPBOOK DIS DOE
// IMSPBOOK DEL DOE

public class Phonebook {
	
	private static final String Version = "2.0.3";
	private static final double FIVE_MINUTES = 300.0;
	private static final String ACTION_DISPLAY = "DIS";
	private static final String ACTION_ADD = "ADD";
	private static final String ACTION_DELETE = "DEL";
	private static final String ACTION_UPDATE = "UPD";
	private static boolean getNewConnection = false;
	private static long timeLastCall = 0L;
	private static long timeCurrentCall = 0L;
	private static long elapsedTime = 0L;
	private static double elapsedInSec = 0.0d;
	private static Connection imsConnection = null;
	
	public static int callCount = 0;
	
    // This application uses the IVPDB2 DB and DFSIVP37 PSB
	//
	// PHONEAP  PCB TYPE=DB,DBDNAME=IVPDB2,PROCOPT=A,KEYLEN=10
	//          SENSEG NAME=A1111111,PARENT=0,PROCOPT=AP      
	//	        PSBGEN LANG=JAVA,PSBNAME=DFSIVP37             
	//	        END  
	//
	
	private static final String IMS_DATASTORE = "IMSD";
	private static final String IMS_DFSIVP37_METADATA = "class://demo.ims.phonebook.metadata.DFSIVP37DatabaseView";
	private static final String IMS_INPUT_MSG = "class://demo.ims.phonebook.message.ContactInfoInput";
	private static final String IMS_OUTPUT_MSG = "class://demo.ims.phonebook.message.ContactInfoOutput";
	
	public static void main (String[] args) {

		callCount++;
		
		// Write to standard output in JMP 
		writeToJoblog("Phonebook DFSIVP37 (Version " + Version + ") application called, count = " + callCount);		

		// Application is used to get a Transaction object
		Application app = ApplicationFactory.createApplication();

		// Transaction is primarily used for commit or roll back calls
		Transaction tran = app.getTransaction();

		// Get a handle to the MessageQueue object for sending and receiving
		// messages to and from the IMS message queue
		MessageQueue messageQueue = app.getMessageQueue();

		IOMessage inputMessage = null;
		IOMessage outputMessage = null;
		String errorMessage = "";
		Boolean foundError = false;
		String action = "";
		String lastName = "";
		String firstName = "";
		String phoneNum = "";
		String zipCode = "";

		// Set the initial time for first call, keep persistent connection
		// for 5 minutes, release connection if no activity after 5 minutes.
		if (callCount == 1) {
			timeLastCall = System.nanoTime();
		}
		else {
			timeCurrentCall = System.nanoTime();
			elapsedTime = timeCurrentCall - timeLastCall;
			elapsedInSec = (double) elapsedTime / 1_000_000_000.0;
			
			// Set previous call to the current call so we can
			// refer to it later when calculating the elapsed time
			timeLastCall = timeCurrentCall;
			
			if (elapsedInSec > FIVE_MINUTES) {
				getNewConnection = true;
			}
		}		

		writeToJoblog("Elapsed time from previous call (in seconds) : " + elapsedInSec);		

		// Create a connection the first time, and reuse connection for future
		// calls, establish a new connection after elapsed time in between
		// calls has exceeded 5 minutes or 300 seconds
		if (imsConnection == null || getNewConnection) {
			
			getNewConnection = false;
			
			try {
				
				if (getNewConnection) {
					// Close the old connection
					imsConnection.close();
					writeToJoblog("Get new database connection");
				}
				
				IMSDataSource imsDB = new IMSDataSource();
				imsDB.setDriverType(IMSDataSource.DRIVER_TYPE_2);
				imsDB.setDatastoreName(IMS_DATASTORE);
				
				// If you are using the IMS catalog, the IMS Universal drivers
				// application program can obtain the necessary metadata directly 
				// from the catalog database without a Java metadata class file.
				//
				// The setDatabaseName can contain the following:
				// * class://... class generated from E4D, basic segment metadata mapping
			    // * xml://...  xml taken from E4D projects, advanced metadata mapping
			    // * PSBNAME goes to catalog - advanced metadata mapping 
				//
				// NOTE: Ensure you have the latest driver installed.
				imsDB.setDatabaseName(IMS_DFSIVP37_METADATA);
				imsConnection = imsDB.getConnection();
			}
			catch (SQLException sqle) {

				writeToJoblog("1:SQLException encountered");
				writeToJoblog(sqle.getMessage());
				imsConnection = null;
				
				foundError = true;
				if (sqle.getMessage().length() > 500) {
					errorMessage = sqle.getMessage().substring(0, 500);
				} else {
					errorMessage = sqle.getMessage();
				}					
			}
		}

		try {

			// Initialize the input and output messages to the IOMessage object
			inputMessage = app.getIOMessage(IMS_INPUT_MSG);
			outputMessage = app.getIOMessage(IMS_OUTPUT_MSG);

			if (!foundError) {

				// Get unique message from the message queue, if there is one
				while (messageQueue.getUnique(inputMessage)) {	
					
					action = inputMessage.getString("IN_COMMAND");
					lastName = inputMessage.getString("IN_LASTNAME");

					if (action.trim().equalsIgnoreCase(ACTION_ADD) ||
						action.trim().equalsIgnoreCase(ACTION_UPDATE)) {
						
						firstName = inputMessage.getString("IN_FIRSTNAME");
						phoneNum = inputMessage.getString("IN_PHONENUM");
						zipCode = inputMessage.getString("IN_ZIPCODE");
					}
					
					ContactInformation contact = new ContactInformation();
					contact.setLastName(lastName);
					contact.setFirstName(firstName);
					contact.setPhoneNumber(phoneNum);
					contact.setZipCode(zipCode);
					
					PhonebookService pbService = new PhonebookService(imsConnection);
					
					if (pbService != null) {
						
						String returnMessage = "";
						
						if (action.trim().equalsIgnoreCase(ACTION_ADD)) {
							
							returnMessage = pbService.addContact(contact);
						} else if (action.trim().equalsIgnoreCase(ACTION_UPDATE)) {
							
							returnMessage = pbService.updateContact(contact);
						} else if (action.trim().equalsIgnoreCase(ACTION_DELETE)) {
							
							returnMessage = pbService.deleteContact(contact.getLastName());
						} else if (action.trim().equalsIgnoreCase(ACTION_DISPLAY)) {
							
							returnMessage = pbService.getContact(contact.getLastName());
						} else {
							
							// An invalid command was specified in the input terminal.
							// Return an error.
							returnMessage = "ERROR: INVALID COMMAND WAS SPECIFIED";
						}
						
						outputMessage.setString("OUT_MESSAGE", returnMessage);

						// Insert the populated OutputMessage into the message queue
						messageQueue.insert(outputMessage, MessageQueue.DEFAULT_DESTINATION);

						tran.commit();
					}
				}
			}
			else {
				// Return error encountered when setting up the Connection
				// to the IMS Database
				try {
					
					outputMessage.setString("ERROR_MSG", errorMessage);
					messageQueue.insert(outputMessage, MessageQueue.DEFAULT_DESTINATION);
					
				} catch (DLIException e1) {
					writeToJoblog("2:DLIException encountered");
					writeToJoblog(e1.getMessage());					
				}
			}	
		} catch (SQLException e) {			

			writeToJoblog("3:SQLException encountered");
			writeToJoblog(e.getMessage());			
			
			try {

				if (e.getMessage().length() > 500) {
					outputMessage.setString("ERROR_MSG", e.getMessage().substring(0, 500));
				} else {
					outputMessage.setString("ERROR_MSG", e.getMessage());
				}
					
				messageQueue.insert(outputMessage, MessageQueue.DEFAULT_DESTINATION);

			} catch (DLIException e1) {
				writeToJoblog("4:DLIException encountered");
				writeToJoblog(e1.getMessage());
			}			
		
		} catch (Exception e) {

			writeToJoblog("5:Exception encountered");
			writeToJoblog(e.getMessage());

			try {

				if (e.getMessage().length() > 500) {
					outputMessage.setString("ERROR_MSG", e.getMessage().substring(0, 500));
				} else {
					outputMessage.setString("ERROR_MSG", e.getMessage());
				}
					messageQueue.insert(outputMessage, MessageQueue.DEFAULT_DESTINATION);

			} catch (DLIException e1) {
				writeToJoblog("6:DLIException encountered");
				writeToJoblog(e1.getMessage());
			}

		} finally {

			app.end();
		}
	
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
}
