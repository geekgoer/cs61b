package gitlet;

import org.apache.commons.math3.analysis.function.Add;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public static final File addstage_DIR = join(GITLET_DIR,"addstage");
    public static final File removestage = join(GITLET_DIR,"removestage");
    public static final String DEFAULT_BRANCH = "master";
    // Map branch with commit
    public static Map<String,String > strToIdMap = new HashMap<>();
    // instance addStage
    public static AddStage addStage = new AddStage();
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
        removestage.mkdir();
        addstage_DIR.mkdir();
        createBranch(DEFAULT_BRANCH);
        setHEADToBranch(DEFAULT_BRANCH);
        createInitCommit();
    }

    /*
    * check the can use rm operation. if not , exit.
    * */
    static void checkCanRemove(String fileName){
        File file = nameToFile(fileName);
        if(!addStage.hasAdded(file) && !hasCommit(file))
            exit("No reason to remove the file.");
    }
    static void rm(String fileName){
        String fileId = getFileId(fileName);
        File file = join(CWD,fileName);
        // remove from add
        if(addStage.hasAdded(file))
            addStage.getAdd().remove(file);
        Commit commit = getCommitById(fileId);
        if(commit != null && commit.checkWithinBlobs(file)){
            // rm-stage add
            addStage.getRemove().add(fileId);
            restrictedDelete(file);
        }
    }

    // commit entry
    static void commit(String msg){
        if(!addStage.anyChanged())
            exit("No changes added to the commit.");
        // List.of -->Create an immutable list
        List<Commit> parentsCommit = List.of(getHead());
//        String parentCommitId = parentCommit.getId();
        Commit commit = new Commit(msg,parentsCommit,addStage);
        String commitId = commit.getId();
//        addStage.commit(commit);
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
        File file = join(heads_DIR,commitId);
        if(!file.exists() || commitId == null)
            return null;
        Commit commit = readObject(file,Commit.class);
        return commit;
    }

    /*
    * TODO remove file add*/
    static void add(String fileName){
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
        File file = join(objects_DIR,commit.getId());
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
