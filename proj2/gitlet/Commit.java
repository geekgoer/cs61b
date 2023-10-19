package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

import static gitlet.Repository.cleanStage;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String id;
    private String timeStamp;
    private List<Commit> parents;
//    private List<String> blobs;
    // <filename , blobId>
    private final HashMap<String,String> blobs;
    public static String getId(Commit commit){
        return commit.generate_SHA1();
    }
    public Commit(){
        this.message = "initial commit";
        this.timeStamp = StdDateToTimeStamp(new Date(0));
        this.parents = new LinkedList<>();
        // blobid
        this.blobs = new HashMap<>();
        this.id = generate_SHA1();
    }

    public Commit(String msg , List<Commit> parents,AddStage addStage){
        this.message = msg;
        this.timeStamp = StdDateToTimeStamp(new Date());
        this.parents = parents;
        Commit parent = parents.get(0);
        this.blobs = new HashMap<String,String>();
        this.blobs.putAll(parent.getBLobs());
        Map<File,Blob> AddMap = addStage.getAdd();
        Set<String > removeSet = addStage.getRemove();
        for(Map.Entry<File,Blob> item : AddMap.entrySet()){
            String key = item.getKey().getName();
            String blobId = item.getValue().getId();
            blobs.put(key,blobId);
        }
        for(String removeFileName : removeSet){
            blobs.remove(removeFileName);
        }
        this.id = generate_SHA1();
        cleanStage();
    }
    public String getCommitBlobId(String fileName){
        return blobs.get(fileName);
    }

    // deep copy
    public HashMap<String, String> getBLobs(){
        return this.blobs;
    }
    public String getId(){
        return this.id;
    }
    public String getDate(){
        return this.timeStamp;
    }
    public String getMessage(){
        return this.message;
    }
    public List<Commit> getParents(){
        return this.parents;
    }
    /* check the file equal to any blobs of Blobs*/
    public boolean checkWithinBlobs(File file){
        return blobs.get(file.getName()) != null;
    }

    private static String StdDateToTimeStamp(Date d){
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",Locale.US);
        return dateFormat.format(d);
    }
    String generate_SHA1(){
//        System.out.println(this.message+this.timeStamp.toString());
        return sha1(this.blobs.toString(),this.parents.toString(),this.message,this.timeStamp);
    }
}
