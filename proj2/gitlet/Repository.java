package gitlet;

//import org.apache.commons.math3.analysis.function.Add;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.nio.file.Paths;
import static gitlet.Utils.*;
import static gitlet.MyUtils.exit;
// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File HEADfile = join(GITLET_DIR,"HEAD");
    public static final File refs_DIR = join(GITLET_DIR,"refs");
    public static final File objects_DIR = join(GITLET_DIR,"objects");
    public static final File heads_DIR = join(refs_DIR,"heads");
    public static final File addstage_File = join(GITLET_DIR,"addstage");
    public static final File COMMIT_DIR = join(objects_DIR,"COMMIT");
    public static final String DEFAULT_BRANCH = "master";
    // Map branch with commit
    public static Map<String,String > strToIdMap = new HashMap<>();
    // instance addStage
//    public static AddStage addStage = readStage();
//    public static final Lazy<File[]> currentFiles = lazy(() -> CWD.listFiles(File::isFile)) TODO
    public static final String prefix_HEAD = "ref: refs/heads/";
    /*
     *   .gitlet
     *      |--objects
     *      |     |--commit and blob
     *      |--refs
     *      |    |--heads
     *      |         |--master
     *      |--HEAD
     *      |--addstage
     *      |--removestage
     */
    static void init() {
        if(GITLET_DIR.exists()){
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        refs_DIR.mkdir();
        objects_DIR.mkdir();
        heads_DIR.mkdir();
        COMMIT_DIR.mkdir();
        writeObject(addstage_File,new AddStage());
        createBranch(DEFAULT_BRANCH);
        setHEADToBranch(DEFAULT_BRANCH);
        createInitCommit();
    }

    // log operation
    static void log(){
        Commit commit = getHead();
        while(commit != null){
            printLog(commit);
            if(commit.getParents().size() == 0)
                break;
            commit = commit.getParents().get(0);
        }
    }

    // log and global-log helper
    static private void printLog(Commit commit){
        System.out.println("===");
        System.out.println("commit "+commit.getId());
        if(commit.getParents().size() > 1){
            String fir = commit.getParents().get(0).getId();
            String se  = commit.getParents().get(1).getId();
            System.out.println("Merge: "+fir.substring(0,8)+" "+se.substring(0,8));
        }
        System.out.println("Date: "+commit.getDate());
        System.out.println(commit.getMessage());
        System.out.println();
    }
    static void global_log(){
        List<String> files = plainFilenamesIn(COMMIT_DIR);
        for(String fileName : files){
            Commit commit = getCommitFromId(fileName);
            printLog(commit);
        }
    }

    static boolean find(String msg){
        List<String> files = plainFilenamesIn(COMMIT_DIR);
        for(String fileName : files){
            Commit commit = getCommitFromId(fileName);
            if(commit ==null)
                exit("opp ! <find> commit is null.");
            if(commit.getMessage().equals(msg)){
                System.out.println(commit.getId());
                return true;
            }
        }
        return false;
    }

    static void status(){
        // Lazy
        AddStage addStage = readStage();
        StringBuffer bf = new StringBuffer();
        bf.append("=== Branches ===\n");
        String headBranchName = readContentsAsString(HEADfile).replace(prefix_HEAD,"");
        List<String> branchesName = plainFilenamesIn(heads_DIR);
        for(String branch : branchesName){
            if(branch.equals(headBranchName)){
                bf.append("*");
            }
            bf.append(branch+"\n");
        }
        bf.append("\n");
        bf.append("=== Staged Files ===\n");
        List<String> keys_sort_cole = new ArrayList<String>();
        for(Map.Entry<File,Blob> file_blob : addStage.getAdd().entrySet()){
            String filename = file_blob.getKey().getName();
            keys_sort_cole.add(filename);
        }
        Collections.sort(keys_sort_cole);
        for(String key : keys_sort_cole){
            bf.append(key+"\n");
        }
        bf.append("\n");
        bf.append("=== Removed Files ===\n");
//        String[] array = new String[addStage.getRemove().size()];
//        addStage.getRemove().toArray(array);
        // unsorted list   TODO
        for(String filename : addStage.getRemove()){
            bf.append(filename+"\n");
        }
        bf.append("\n");
        bf.append("=== Modifications Not Staged For Commit ===\n\n");
        bf.append("=== Untracked Files ===\n\n");
        // redundancy \n ?
        System.out.print(bf);
    }

    // get commit from Id
    static Commit getCommitFromId(String id){
        File file = join(COMMIT_DIR,id);
        if(!file.exists())
            return null;
        return readObject(file,Commit.class);
    }

    // read Stage from file
    static AddStage readStage(){
        return readObject(addstage_File,AddStage.class);
    }

    /*
    * check the can use rm operation.( neither staged nor tracked by the head commit) if not , exit.
    * */
    static void checkCanRemove(String fileName){
        // Lazy
        AddStage addStage = readStage();
        File file = nameToFile(fileName);   // TODO
        Commit headCommit = getHead();
        if(!addStage.hasAdded(file) && headCommit.getCommitBlobId(fileName) == null)
            exit("No reason to remove the file.");
    }
    static void rm(String fileName){
        AddStage addStage = readStage();
        String fileId = getFileId(fileName);
        File file = join(CWD,fileName);
        // remove file from addstage
        if(addStage.hasAdded(file)) {
            addStage.getAdd().remove(file);
        }else{
            // add file to removeStage
            addStage.getRemove().add(file.getName());
        }

        String headCommitBlobId = getHead().getBLobs().get(fileName);
        Blob blob = new Blob(file);
        if(blob.getId().equals(headCommitBlobId)){
            restrictedDelete(file);
        }
        writeObject(addstage_File,addStage);
    }


    // clean Stage file after commit
    static void cleanStage(){
        // 清空 stage file
//        restrictedDelete(addstage_File);
        File file = join(GITLET_DIR,"addstage");
        writeObject(file,new AddStage());
    }

    // commit entry
    static void commit(String msg){
        // Lazy
        AddStage addStage = readStage();
        if(!addStage.anyChanged())
            exit("No changes added to the commit.");
        // List.of -->Create an immutable list

        List<Commit> parentsCommit = new ArrayList<>();
        parentsCommit.add(getHead());
        Commit commit = new Commit(msg,parentsCommit,addStage);
        String commitId = commit.getId();
        writeCommitToFile(commit);
        File file = getHeadBranch();
        writeContents(file,commitId);
    }

    /*
    * return whether head-point Commit has the file(blob)
    * */
    static boolean hasCommit(File file){
        Commit commit = getHead();
        String fileId = sha1(file.getPath(),readContents(file));
        return commit.checkWithinBlobs(file);
    }

    static String getFileId(String fileName){
        File file = nameToFile(fileName);
        byte[] content = readContents(file);
        return sha1(file.getPath(),content);
    }

    // get head-point branch
    static File getHeadBranch(){
        String branchname = readContentsAsString(HEADfile).replace(prefix_HEAD,"");
        return join(heads_DIR,branchname);
    }

    // get head-point commit
    static Commit getHead(){
        File branch = getHeadBranch();
        String commitId = readContentsAsString(branch);
        return getCommitById(commitId);
    }

    // get Commit by sha1 id
    static Commit getCommitById(String commitId){
        File file = join(COMMIT_DIR,commitId);
        if(!file.exists() || commitId == null)
            return null;
        Commit commit = readObject(file,Commit.class);
        return commit;
    }

    /*
    * TODO remove file add*/
    static void add(String fileName){
        AddStage addStage = readStage();
        File file = nameToFile(fileName);
        if(file.exists()){
            addStage.add(file);
        }else{
            exit("File does not exist.");
        }
    }


    // create branch by BranchName
    static void createBranch(String branchName) {
        File branch = join(heads_DIR,branchName);
        try {
            branch.createNewFile();
        }catch (IOException e){
            exit("opp! create file FAILED");
        }
    }

    /*use filename find file
    * TODO clarify why absolute*/
    private static File nameToFile(String filename){
        return Paths.get(filename).isAbsolute() ? new File(filename) : join(CWD,filename);
    }
    static void setHEADToBranch(String branchName){
        writeContents(HEADfile,prefix_HEAD+branchName);
    }
    //
    static void ConnectBranchToCommitId(String branch ,Commit commit){
        strToIdMap.put(branch, commit.getId(commit));
    }

    //check if git init before
    static boolean hasInit(){
        return GITLET_DIR.exists();
    }

    // set Branch To current commit
    static void updateBranch(String branchName,Commit commit){
        String commitId = commit.generate_SHA1();
        File branch = join(heads_DIR,branchName);
        if(branch.exists()){
            writeContents(branch,commitId);
        }else{
            exit("opp! branch not exits");
        }
    }

    /*
    * write Commit to file, filename is SHA1 id of file content*/
    static void writeCommitToFile(Commit commit){
        File file = join(COMMIT_DIR,commit.getId());
        // writeobj use Serialize element
        writeObject(file,commit);
    }

    static void createInitCommit(){
        Commit initCommit = new Commit();
        String commitId = initCommit.getId();
        writeCommitToFile(initCommit);

        updateBranch(DEFAULT_BRANCH,initCommit);
        ConnectBranchToCommitId(DEFAULT_BRANCH,initCommit);
    }

    // check has Init before
    public static void checkInit(){
        if(!hasInit()){
            exit("Not in an initialized Gitlet directory.");
        }
    }
}
