/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package goose.mainManager;

import org.garret.perst.Persistent;
import org.garret.perst.Index;
import org.garret.perst.IInputStream;
import org.garret.perst.IOutputStream;
import org.garret.perst.Storage;
import org.garret.perst.Types;
//import org.garret.perst.FieldIndex;

/**
 * This class acts as the database root for all the tables. It provides persistent
 * reference to the application. An storage can only have a root object.
 *
 * Once the root object has been registered, the Storage.getRoot method will always return a reference to this object.
 *
 * @author Narseo
 */
public class Root extends Persistent{

    public Index userId;
    public Index surname;
    public Index name;
    public Index phoneNumber;
    public Index btAddress;
    
    public Index messageId;
    public Index senderName;
    public Index senderSurname;
    public Index senderPhone;
    public Index senderBTAddr;
    public Index recipient;
    public Index txMode;
    public Index GUID;
    public Index folder;
    
    /*
     * A default constructor is needed for Perst to be able to instantiate instances of loaded objects
     */
    public Root(){}
    
    /*
     * 
     */
    public Root (Storage db){
        super(db);
        //Create unique index for userId
        userId = db.createIndex(Types.Int, true);
        //Create non-unique index for last name of the contacts
        surname = db.createIndex(Types.String, false);
        //Create non-unique index for the contacts name
        name = db.createIndex(Types.String, false);
        //We do not consider that phone number and bt mac address are unique. There can be more than a user that share them (what about sharing a handset?)
        phoneNumber = db.createIndex(Types.String, false);
        btAddress = db.createIndex(Types.String, false);
        
        
        
        //Create unique index for messageId
        messageId = db.createIndex(Types.Int, false);        
        senderName = db.createIndex(Types.String, false);
        senderSurname = db.createIndex(Types.String, false);
        senderPhone = db.createIndex(Types.String, false);
        senderBTAddr = db.createIndex(Types.String, false);
        recipient = db.createIndex(Types.String, false);
        txMode = db.createIndex(Types.String, false);
        //GUID must be also unique
        GUID = db.createIndex(Types.String, true);
        folder = db.createIndex(Types.String, false);        
    }
    
    public void readObject(IInputStream in){
        userId = (Index) in.readObject();
        surname = (Index) in.readObject();
        name = (Index) in.readObject();
        phoneNumber = (Index) in.readObject();
        btAddress = (Index) in.readObject();
        messageId = (Index) in.readObject();
        senderName = (Index) in.readObject();
        senderSurname = (Index)in.readObject();
        senderPhone = (Index) in.readObject();
        senderBTAddr = (Index) in.readObject();
        recipient = (Index) in.readObject();
        txMode = (Index) in.readObject();
        GUID = (Index) in.readObject();
        folder = (Index) in.readObject();
    }

    public void writeObject (IOutputStream out){
        out.writeObject(userId);
        out.writeObject(surname);
        out.writeObject(name);
        out.writeObject(phoneNumber);
        out.writeObject(btAddress);
        out.writeObject(messageId);
        out.writeObject(senderName);
        out.writeObject(senderPhone);
        out.writeObject(senderSurname);
        out.writeObject(senderBTAddr);
        out.writeObject(recipient);
        out.writeObject(txMode);
        out.writeObject(GUID);
        out.writeObject(folder);
    }    
}
