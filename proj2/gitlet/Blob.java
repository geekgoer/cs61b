package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.Utils.*;

import static gitlet.Repository.*;
public class Blob implements Serializable {
    // store contents
    private byte[] contents ;
    private String BlobId;
    private String fileName;
//    public Blob(File file){
//        // pathName 不包括文件名
//        pathName = file.getPath();
//        contents = readContents(file);
//        BlobId = getId();
//    }
public Blob(File file){
    // pathName 不包括文件名
    fileName = file.getName();
    contents = readContents(file);
    BlobId = getId();
}
    public Blob(String fileName,File dir){
        this.fileName = fileName;
        File file = join(CWD,fileName);
        if(file.exists()){
            contents = readContents(file);
            BlobId = getId();
        }else{
            contents = null;
            this.BlobId = sha1(fileName);
        }
    }
    public boolean exists(){
        return this.contents != null;
    }
    public byte[] getContent(){
        return contents;
    }
    public void setContent(File file){
        contents = readContents(file);
    }
    public String getId(){
        return sha1(fileName,contents);
    }
    public String getContentAsString(){
        return new String(contents, StandardCharsets.UTF_8);
    }
}
