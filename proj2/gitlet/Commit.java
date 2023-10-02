package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

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
    private Date timeStamp;
    private List<Commit> parent;
//    private List<String> blobs;
    private final Map<File,Blob> blobs;
    public static String getId(Commit commit){
        return commit.generate_SHA1();
    }
    public Commit(){
        this.message = "initial commit";
        this.timeStamp = new Date(0);
        this.parent = new LinkedList<>();
        // blobid
        this.blobs = new TreeMap<>();
        this.id = generate_SHA1();
    }

    public Commit(String msg , List<Commit> parents,AddStage addStage){
        this.message = msg;
        this.timeStamp = new Date();
        this.parent = parent;
        Commit parent = parents.get(0);
        this.blobs = parent.blobs;
        Map<File,Blob> AddMap = addStage.getAdd();
        Set<String > removeSet = addStage.getRemove();
        for(Map.Entry<File,Blob> item : AddMap.entrySet()){
            File key = item.getKey();
            Blob blob = item.getValue();
            blobs.put(key,blob);
        }
        for(String removeFileName : removeSet){

        }
    }
    public Map<File, Blob> getBLobs(){
        return blobs;
    }
    public String getId(){
        return this.id;
    }

    /* check the file equal to any blobs of Blobs*/
    public boolean checkWithinBlobs(File file){
        return blobs.get(file) != null;
    }

    private static String StdDateToTimeStamp(Date d){
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z",Locale.US);
        return dateFormat.format(d);
    }
    String generate_SHA1(){
//        System.out.println(this.message+this.timeStamp.toString());
        return sha1(this.blobs.toString(),this.parent.toString(),this.message,StdDateToTimeStamp(this.timeStamp));
    }
}
