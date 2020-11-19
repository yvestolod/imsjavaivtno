# Sample IMS Java application (JMP)

This is a sample IMS JMP program using SQL to access the database IVPDB2. 

This application uses the IVPDB2 DB and DFSIVP37 PSB.

```
//
// PHONEAP  PCB TYPE=DB,DBDNAME=IVPDB2,PROCOPT=A,KEYLEN=10
//          SENSEG NAME=A1111111,PARENT=0,PROCOPT=AP      
//          PSBGEN LANG=JAVA,PSBNAME=DFSIVP37             
//          END  
//
```

The input to transaction is

```
IMSPBOOK <ACT:4><LASTNAME:10><FIRSTNAME:10><EXTENSION:10><ZIPCODE:7>
```

Where `<ACT>` can be DIS, ADD, DEL, and UPD.

For *DIS* and *DEL*, you only need to specify `<LASTNAME>`
For *ADD* and *UPD*, you need to specify `<LASTNAME>`, `<FIRSTNAME>`, `<EXTENSION>`and `<ZIPCODE>`

For example:

```
IMSPBOOK ADD DOE       JOHN      5551234567B2A1A1
IMSPBOOK UPD DOE       JANE      5559876543A1A2B2
IMSPBOOK DIS DOE
IMSPBOOK DEL DOE
```

It issues the following SQL:

For the *DIS* action, it uses:
```
SELECT * FROM PHONEBOOK.PERSON WHERE LASTNAME = ?
```

For the *DEL* action, it uses:
```
DELETE FROM PHONEBOOK.PERSON 
 WHERE LASTNAME = ?
```

For the *ADD* action, it uses:
```
INSERT INTO PHONEBOOK.PERSON
       (LASTNAME, FIRSTNAME, EXTENSION, ZIPCODE)
  VALUES (?, ?, ?, ?)
```

For the *UPD* action, it uses:
```
UPDATE PHONEBOOK.PERSON
       SET FIRSTNAME = ?,
       EXTENSION = ?,
       ZIPCODE =  ?
 WHERE LASTNAME = ?
```

