package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

import static gitlet.Repository.*;
public class Blob implements Serializable {
    // store contents
    private byte[] contents ;
    private String BlobId;
    private String pathName;
    public Blob(File file){

        // pathName 不包括文件名
        pathName = file.getPath();
        contents = readContents(file);
        BlobId = getId();
    }

    public byte[] getContent(){
        return contents;
    }
    public void setContent(File file){
        contents = readContents(file);
    }
    public String getId(){
        return sha1(pathName,contents);
    }

}
