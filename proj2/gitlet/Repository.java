package gitlet;

//import org.apache.commons.math3.analysis.function.Add;

import java.io.File;
import java.io.FilenameFilter;
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
            System.out.println("Merge: "+fir.substring(0,7)+" "+se.substring(0,7));
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

    // checkout java gitlet.Main checkout -- [file name]
    static void checkout_headCommitFile(String fileName){
        String commitId = getHead().getId();
        checkout_anyCommitIdFile(commitId,fileName);
    }

    // checkout java gitlet.Main checkout [commit id] -- [file name]
    static void checkout_anyCommitIdFile(String commitId ,String fileName){
        Commit commit = getCommitFromId(commitId);
        if(commit == null)
            exit("No commit with that id exists.");
        String blobId = commit.getCommitBlobId(fileName);
        if(blobId == null)
            exit("File does not exist in that commit.");

        Blob blob = getBlobfromId(blobId);
        File cwd_file  = join(CWD,fileName);
        writeContents(cwd_file,blob.getContent());
    }

    static void rmBranch(String branchName){
        String nowBranchName = readContentsAsString(HEADfile).replace(prefix_HEAD,"");
        if(nowBranchName.equals(branchName)){
            exit("Cannot remove the current branch.");
        }
        File file = join(heads_DIR,branchName);
        if(!file.exists())
            exit("A branch with that name does not exist.");
        file.delete();
    }

    static Blob getBlobfromId(String blobId){
        File file = join(objects_DIR,blobId);
        return readObject(file,Blob.class);
    }

    // checkout java gitlet.Main checkout [branch name]
    /*
    * tmp : If a working file is untracked in the current branch and would be overwritten by
    * the checkout, print There is an untracked file in the way; delete it, or add and commit it first.
    * */
    static void checkoutBranch(String branchName){
        checkBranchExist(branchName);
        String headBranchName = readContentsAsString(HEADfile).replace(prefix_HEAD,"");
        if(headBranchName.equals(branchName))
            exit("No need to checkout the current branch.");

        // get target branch commit
        File file = join(heads_DIR,branchName);
        String commitId = readContentsAsString(file);
        Commit commit = getCommitFromId(commitId);

        // check untracked
        checkUntracked(commit.getBLobs());
        replaceCWD(commit.getBLobs());
        cleanStage();
        writeContents(HEADfile,prefix_HEAD+branchName);
    }

    static void reset(String commitId){
        // like checkout branch
        Commit commit = getCommitFromId(commitId);
        if(commit == null)
            exit("No commit with that id exists.");
        checkUntracked(commit.getBLobs());
        replaceCWD(commit.getBLobs());
        cleanStage();
//        writeContents(readContents());
        String branchName = readContentsAsString(HEADfile).replace(prefix_HEAD,"");
        File file = join(heads_DIR,branchName);
        writeContents(file,commitId);
    }

    // replace cwd with target branch
    // TODO all cwd is deleted and, rewrite . what if untracked by both branches?
    static void replaceCWD(Map<String,String> tarBlobs){
        // clean cwd
        cleanCWD();

        for(Map.Entry<String,String> item : tarBlobs.entrySet()){
            String fileName = item.getKey();
            String blobId = item.getValue();
            File file = join(CWD,fileName);
            Blob blob = readObject(join(objects_DIR,blobId),Blob.class);
            writeContents(file,blob.getContent());
        }
    }


    // delete all the files in cwd
    static void cleanCWD(){
        // FilenameFilter : when clean cwd , don't delete .gitlet files
        FilenameFilter gitletFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals(".gitlet");
            }
        };

        File[] files = CWD.listFiles(gitletFilter);
        for(File file : files){
            delFile(file);
        }
    }

    // delete file , recursive
    static void delFile(File file){
        if(file.isDirectory()){
            for(File inFile : file.listFiles()){
                inFile.delete();
            }
        }else{
            file.delete();
        }
    }

    // check if previous branch untracked and current branch tracked
    static void checkUntracked(Map<String,String> tarBlobs){
        List<String> untrackedNames = getUntrackedNames();
        if(untrackedNames.size()==0)
            return;

        // TODO not check file same to tarblob
        for(String fileName : untrackedNames){
            if(tarBlobs.get(fileName) != null)
                exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
    }

    // get previous untracked files
    static List<String> getUntrackedNames(){
        List<String> res = new LinkedList<>();
        List<String> addStageTracked = readStage().getAddedfileNames();
        Set<String> commitTracked = getHead().getBLobs().keySet();
        for(String fileName : plainFilenamesIn(CWD)){
            if(! addStageTracked.contains(fileName) && ! commitTracked.contains(fileName)){
                res.add(fileName);
            }
        }
        Collections.sort(res);
        return res;
    }

    // check if a file has been tracked
    static void checkTrackedHead(File file){
        Commit commit = getHead();
        if(commit.getBLobs().get(file.getName()) == null)
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
    }

    static void checkBranchExist(String branchName){
        File file = join(heads_DIR,branchName);
        if(!file.exists())
            exit("No such branch exists.");
    }

    static void branch(String branchName){
        File file = join(heads_DIR,branchName);
        if(file.exists()){
            exit("A branch with that name already exists.");
        }
        Commit commit = getHead();
        writeContents(file,commit.getId());
    }

    // get LCA of two commits
    static Commit getSplit(Commit othCommit , Commit curCommit){
        Map<String,Boolean> upTick = new TreeMap<>();
        while(othCommit.getParents().size() > 0){
            upTick.put(othCommit.getId(),true);
            othCommit = othCommit.getParents().get(0);
        }
        while(upTick.get(curCommit.getId())==null){
            curCommit = curCommit.getParents().get(0);
        }
        return curCommit;
    }


    static void merge(String branchName){
        File file = join(heads_DIR,branchName);
        if(!file.exists()){
            exit("A branch with that name does not exist.");
        }
        Commit otherCommit = getCommitFromId(readContentsAsString(file));
        Commit curCommit = getHead();
        Commit splitCommit = getSplit(otherCommit,curCommit);
        AddStage addStage = readStage();

        // check
        if(!addStage.isEmpty()){
            exit("You have uncommitted changes.");
        }
        if(otherCommit.getId().equals(curCommit.getId()))
            exit("Cannot merge a branch with itself.");
        if(otherCommit.getId().equals(splitCommit.getId())){
            message("Given branch is an ancestor of the current branch.");
            return;
        }
        if(curCommit.getId().equals(splitCommit.getId())){
            message("Current branch fast-forwarded.");
            return;
        }

        mergeLCA(splitCommit,curCommit,otherCommit);
        String msg = "Merged "+ branchName +" into "+ readContentsAsString(HEADfile).replace(prefix_HEAD,"") +".";
        List<Commit> pars = new ArrayList<>();
        pars.add(curCommit); pars.add(otherCommit);
        commitWithpars(msg,pars);
    }

    static Set<String> getAllFileNames(Commit lcaCommit,Commit curCommit ,Commit otherCommit){
//        Set<String> lcaFileNames = lcaCommit.getBLobs().keySet();
//        Set<String> curFileNames = curCommit.getBLobs().keySet();
//        Set<String> othFileNames = otherCommit.getBLobs().keySet();
//        Set<String> res = new TreeSet<>();
//        for(String lcaFileName : lcaFileNames){
//            if(!res.contains(lcaFileName))
//                res.add(lcaFileName);
//        }
//        for(String curFileName : curFileNames){
//            if(!res.contains(curFileName))
//                res.add(curFileName);
//        }
//        for(String othFileName : othFileNames){
//            if(!res.contains(othFileName))
//                res.add(othFileName);
//        }
//        return res;
        Set<String> res = new TreeSet<>();
        res.addAll(lcaCommit.getBLobs().keySet());
        res.addAll(curCommit.getBLobs().keySet());
        res.addAll(otherCommit.getBLobs().keySet());
        return res;
    }

    static void mergeLCA(Commit lcaCommit,Commit curCommit ,Commit otherCommit){
        Set<String> fileNames = getAllFileNames(lcaCommit,curCommit,otherCommit);
        Map<String,String> lcaMap = lcaCommit.getBLobs();
        Map<String,String> curMap = curCommit.getBLobs();
        Map<String,String> othMap = otherCommit.getBLobs();

        for(String fileName : fileNames){
            String lcaBlobId = lcaMap.getOrDefault(fileName,"");
            String curBlobId = curMap.getOrDefault(fileName,"");
            String othBlobId = othMap.getOrDefault(fileName,"");
            // 1
            if(!othBlobId.equals(lcaBlobId) && curBlobId.equals(lcaBlobId)){
                // delete
                if(othBlobId.equals("")){
                    rm(fileName);
                }

                // modify but not delete
                else {
                    checkout_anyCommitIdFile(otherCommit.getId(), fileName);
                    add(fileName);
                }
            }
            // 2
            else if(othBlobId.equals(lcaBlobId) && !curBlobId.equals(lcaBlobId)){
                // cur deleted
                if(curBlobId.equals("")){
                    // Do nothing
                }
                // modified but not deleted
                else {
                    add(fileName);
                }
            }
            // 3
            if(!lcaBlobId.equals(curBlobId) && !lcaBlobId.equals(othBlobId)){
                // 3.1
                if(curBlobId.equals(othBlobId)){
                    // both delete
                        // Do nothing
                    // other case
                    if(!curBlobId.equals(""))
                        add(fileName);
                }
                // 3.2
                else{
                    // TODO conflict
                    conflict(curBlobId,othBlobId,fileName);
                }
            }


            if(lcaBlobId.equals("")){
                // 4
                if(othBlobId.equals("") && !curBlobId.equals("")){
                    add(fileName);
                }
                // 5
                else{
                    checkout_anyCommitIdFile(otherCommit.getId(),fileName);
                    add(fileName);
                }
            }

        }
    }

    // change conflict file and add msg
    static void conflict(String curBlobId , String othBlobId, String fileName){
        String curContent = getContentAsStringFromBlobId(curBlobId);
        String othContent = getContentAsStringFromBlobId(othBlobId);
        String content = getConflictFileContent(curContent.split("\n"),othContent.split("\n"));
        rewrite(fileName,content);
        System.out.println("Encountered a merge conflict.");
    }

    // rewrite cwd by content
    static void rewrite(String fileName , String content){
        File file = join(CWD,fileName);
        writeContents(file,content);
    }


    // conflict helper
    static String getConflictFileContent(String[] curContent,String[] othContent){
        StringBuffer sb = new StringBuffer();
        int len1 = curContent.length, len2 = othContent.length;
        int i = 0, j = 0;
        while(i < len1 && j < len2){
            if(curContent[i].equals(othContent[j])){
                sb.append(curContent[i]+"\n");
            }else{
                sb.append(getConflictContent(curContent[i],othContent[j]));
            }
            i++ ; j ++;
        }
        while(i < len1){
            sb.append(getConflictContent(curContent[i],""));
            i++;
        }
        while(j < len2){
            sb.append(getConflictContent("",othContent[j]));
            j++;
        }
        return sb.toString();
    }

    // getConflictFileContent  : add string on conflict splited string
    static String getConflictContent(String cur,String oth){
        StringBuffer sb = new StringBuffer();
        sb.append("<<<<<<< HEAD\n");
        sb.append(cur.equals("") ? cur : cur+"\n");
        sb.append("=======\n");
        sb.append(oth.equals("") ? oth : oth + "\n");
        sb.append(">>>>>>>\n");
        return sb.toString();
    }

    static String getContentAsStringFromBlobId(String blobId){
        if(blobId.equals(""))
            return "";
        Blob blob = getBlobfromId(blobId);
        return blob.getContentAsString();
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
        Blob blob = new Blob(fileName,CWD);
        if(blob.exists() && blob.getId().equals(headCommitBlobId)){
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

    static void commitWithpars(String msg , List<Commit> pars){
        AddStage addStage = readStage();
        if(!addStage.anyChanged())
            exit("No changes added to the commit.");
        // List.of -->Create an immutable list

        Commit commit = new Commit(msg,pars,addStage);
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
        if(!file.exists())
            return "";
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
    /*
     * 1. Staging an already-staged file overwrites the previous entry in the staging
     *  area with the new contents.
     * 2. If the current working version of the file is identical to the version in the current
     * commit, do not stage it to be added, and remove it from the staging area if it is already there
     * 3. The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     *
     * */
    // TODO mystery code
    static void add(String fileName){
        AddStage addStage = readStage();
        File file = join(CWD,fileName);
        if(!file.exists()){
            exit("File does not exist.");
        }
        Commit headCommit = getHead();

        String commitblobId = headCommit.getBLobs().getOrDefault(fileName,"");
        // why addblobId not same to blob's id
        Blob addBlob = addStage.getAdd().getOrDefault(file,null);
        String addBlobId = addBlob != null ? addBlob.getId() : "";
        Blob blob = new Blob(file);
        String blobId = blob.getId();

        // commit blob eq
        if(commitblobId.equals(blobId)){
            // addStage blob !eq
            if(!blob.equals(addBlobId)){
                // addblobId is temp, only use once.
                join(objects_DIR,addBlobId).delete();
                addStage.getAdd().remove(file);
                addStage.getRemove().remove(fileName);
                writeObject(addstage_File,addStage);
            }
        }else if(!blob.equals(addBlobId)){
            // addblobId is temp, only use once.
            if(!addBlobId.equals(""))
                join(objects_DIR,addBlobId).delete();
            addStage.getAdd().put(file,blob);
            writeObject(join(objects_DIR,blobId),blob);
            writeObject(addstage_File,addStage);
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
