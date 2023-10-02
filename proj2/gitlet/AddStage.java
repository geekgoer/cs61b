package gitlet;

import java.io.*;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;
public class AddStage implements Serializable {
    // File -> Blob
    private final Map<File,Blob> fileMapBlob = new TreeMap<>();
    // FileID
    private final Set<String> removeSet = new HashSet<>();
    void add(File file){
        if(fileMapBlob.get(file) != null){
            Blob blob = fileMapBlob.get(file);
            blob.setContent(file);
            File headBranch = getHeadBranch();
            String commitId = readContentsAsString(headBranch);
            if(commitId.equals(blob.getId())){
                fileMapBlob.remove(file);
            }
        }else{
            Blob blob = new Blob(file);
            fileMapBlob.put(file,blob);
        }
    }

    // return fileMapBlob
    Map<File,Blob> getAdd(){
        return fileMapBlob;
    }

    // return removeSet
    Set<String> getRemove(){
        return removeSet;
    }

    // return if add-stage has file
    boolean hasAdded(File file){
        return fileMapBlob.get(file) != null;
    }

    // check if add-stage or remove stage not empty
    boolean anyChanged(){
        return (!fileMapBlob.isEmpty() || !removeSet.isEmpty());
    }
}
