package demo.ims.phonebook.metadata;

import com.ibm.ims.db.*;
import com.ibm.ims.base.*;

public class DFSIVP37DatabaseView extends DLIDatabaseView {

	// This class describes the data view of PSB: DFSIVP37
	// PSB DFSIVP37 has database PCBs with 8-char PCBNAME or label: PHONEAP
	//
	// PHONEAP  PCB TYPE=DB,DBDNAME=IVPDB2,PROCOPT=A,KEYLEN=10
	//          SENSEG NAME=A1111111,PARENT=0,PROCOPT=AP      
	//	        PSBGEN LANG=JAVA,PSBNAME=DFSIVP37             
	//	        END  
	//
	// The following describes Segment: A1111111 ("Person") in PCB: 
	// PHONEAP ("PhoneBook")
	static DLITypeInfo[] PHONEAPA1111111Array= {
			new DLITypeInfo("LastName", DLITypeInfo.CHAR, 1, 10, "A1111111", DLITypeInfo.UNIQUE_KEY),
			new DLITypeInfo("FirstName", DLITypeInfo.CHAR, 11, 10),
			new DLITypeInfo("Extension", DLITypeInfo.CHAR, 21, 10),
			new DLITypeInfo("ZipCode", DLITypeInfo.CHAR, 31, 7)
	};
 
	// 'Person' is the alias for Segment name 'A1111111'.
	// This is equivalent to the table name. Example: <schema-name>.<table-name>.
	static DLISegment PHONEAPA1111111Segment= new DLISegment
			("Person", "A1111111", PHONEAPA1111111Array, 40);

	// An array of DLISegmentInfo objects follows to describe the view 
	// for PCB: PHONEAP ("PhoneBook")
	static DLISegmentInfo[] PHONEAParray = {
			new DLISegmentInfo(PHONEAPA1111111Segment, DLIDatabaseView.ROOT)
	};

	// Constructor
	// 'PhoneBook' is the alias for PCB name or Label PHONEAP.
	// This is equivalent to Schema name. Example: <schema-name>.<table-name>.
	public DFSIVP37DatabaseView() {
		super("2.0", "DFSIVP37", "PhoneBook", "PHONEAP", PHONEAParray);
	} 

}