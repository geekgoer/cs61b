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

    public Map<File, Blob> getAddedMap() {
        return addedMap;
    }

    public Set<String> getRemoveSet() {
        return removeSet;
    }

    public boolean isEmpty(){
        return  addedMap.isEmpty() && removeSet.isEmpty();
    }

    // get all fileNames from files
    List<String> getAddedfileNames(){
        List<String > res = new ArrayList<>();
        for(Map.Entry<File,Blob> item:addedMap.entrySet()){
            String fileName = item.getKey().getName();
            res.add(fileName);
        }
        return res;
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
