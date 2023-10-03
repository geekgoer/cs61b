package gitlet;

import java.io.*;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;
import static gitlet.MyUtils.*;
public class AddStage implements Serializable {
    // <File , blob>
    private final Map<File,Blob> addedMap = new TreeMap<>();
    // <filename>
    private final Set<String> removeSet = new HashSet<>();
    void add(File file){
        Blob blob = null;
        AddStage addStage1 = readStage();
        if(addedMap.get(file) != null){
            blob = addedMap.get(file);
            deleteBlobInFile(blob);
            blob.setContent(file);
            File headBranch = getHeadBranch();
            String commitId = readContentsAsString(headBranch);
            if(commitId.equals(blob.getId())){
                addedMap.remove(file);
            }

        }else{
            blob = new Blob(file);
            addedMap.put(file,blob);
        }

        // add blob to objfile to store
        File tarFile = join(objects_DIR,blob.getId());
        if(!tarFile.exists())
            createFile(tarFile);
        writeObject(tarFile,blob);
    }

    // read stage from file
    AddStage readStage(){
        File file = join(addstage_DIR,)
        return readObject()
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

    // after commit , clean all add-stage and remove-stage
    void clean(){
        // TODO
    }
}
