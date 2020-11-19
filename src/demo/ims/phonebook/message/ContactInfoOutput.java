package demo.ims.phonebook.message;

import com.ibm.ims.application.IMSFieldMessage;
import com.ibm.ims.base.DLITypeInfo;

public class ContactInfoOutput extends IMSFieldMessage {
	private static final long serialVersionUID = 23432884;

	static DLITypeInfo[] fieldInfo = {

			new DLITypeInfo("OUT_MESSAGE", DLITypeInfo.CHAR, 1, 200),
			new DLITypeInfo("ERROR_MSG", DLITypeInfo.CHAR, 201, 500)
	};

	public ContactInfoOutput() {  
		super(fieldInfo, 700, false);                   
	}
}