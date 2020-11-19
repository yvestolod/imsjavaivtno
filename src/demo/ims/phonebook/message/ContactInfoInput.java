package demo.ims.phonebook.message;

import com.ibm.ims.application.IMSFieldMessage;
import com.ibm.ims.base.DLITypeInfo;

public class ContactInfoInput extends IMSFieldMessage {

	private static final long serialVersionUID = 1L;
	static DLITypeInfo[] fieldInfo = 
		{
			// MESSAGE_TYPE is MSG1, MSG2 or MSG3
			new DLITypeInfo("IN_COMMAND", DLITypeInfo.CHAR, 1, 4),
			new DLITypeInfo("IN_LASTNAME", DLITypeInfo.CHAR, 5, 10),
			new DLITypeInfo("IN_FIRSTNAME", DLITypeInfo.CHAR, 15, 10),
			new DLITypeInfo("IN_PHONENUM", DLITypeInfo.CHAR, 25, 10),
			new DLITypeInfo("IN_ZIPCODE", DLITypeInfo.CHAR, 35, 7)
		};  

	/**
	 * Required no arguments constructor
	 */
	public ContactInfoInput()
	{ 
		super(fieldInfo, 41, false); 
	}
}