package gitlet;

import java.io.*;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;
import static gitlet.MyUtils.*;
public class AddStage implements Serializable {
    // <File , blob>
    private final Map<File,Blob> addedMap;
    // <filename>
    private final Set<String> removeSet;

    public AddStage(){
        this.addedMap = new TreeMap<>();
        this.removeSet = new TreeSet<>();
    }

    /*
    * 1. Staging an already-staged file overwrites the previous entry in the staging
    *  area with the new contents.
    * 2. If the current working version of the file is identical to the version in the current
    * commit, do not stage it to be added, and remove it from the staging area if it is already there
    * 3. The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
    * */
    static void add(File file){
        Blob blob = null;
        AddStage addStage = readStage();

        // stage There are similarities in the blob
        if(addStage.addedMap.get(file) != null){
            blob = addStage.addedMap.get(file);
            deleteBlobInFile(blob);
            // 重写
            Blob newblob = new Blob(file);

            File headBranch = getHeadBranch();
            String commitId = readContentsAsString(headBranch);
            Commit commit = getCommitById(commitId);
            assert commit != null;
            String commitblobId = commit.getCommitBlobId(file.getName());
            // There are similarities in the blob
            if(commitblobId.equals(newblob.getId())){
                addStage.addedMap.remove(file);
            }
            // Whether the source file is deleted or not, create another one
            File blobfile = join(objects_DIR,newblob.getId());
            writeObject(blobfile,newblob);
        }else{
            blob = new Blob(file);
            addStage.addedMap.put(file,blob);
            File blobfile = join(objects_DIR,blob.getId());
            writeObject(blobfile,blob);
            writeObject(addstage_File,addStage);
        }

    }



    // create file
    static void createFile(File file){
        try {
            file.createNewFile();
        }catch (IOException e){
            exit("opp ! create file Failed.");
        }
    }

    // delete blob in File
    static void deleteBlobInFile(Blob blob){
        String blobId = blob.getId();
        File file = join(objects_DIR,blobId);
        restrictedDelete(file);
    }

    // return fileMapBlob
    Map<File,Blob> getAdd(){
        return addedMap;
    }

    // return removeSet
    Set<String> getRemove(){
        return removeSet;
    }

    // return if add-stage has file
    boolean hasAdded(File file){
        return addedMap.get(file) != null;
    }

    // check if add-stage or remove stage not empty
    boolean anyChanged(){
        return (!addedMap.isEmpty() || !removeSet.isEmpty());
    }


}
