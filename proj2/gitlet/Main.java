package gitlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static gitlet.MyUtils.exit;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if(args.length == 0){
            exit("Please enter a command.");
        }
        switch(args[0]) {
            case "init":
                validArgsNum(args,1);
                Repository.init();
                break;
            case "add":
                Repository.checkInit();
                // TODO: handle the `add [filename]` command
                validArgsNum(args,2);
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.checkInit();
                validArgsNum(args,2);
                if(args[1] == null || args[1].equals(""))
                    exit("Please enter a commit message.");
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.checkInit();
                validArgsNum(args,2);
                Repository.checkCanRemove(args[1]);
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.checkInit();
                validArgsNum(args,1);
                Repository.log();
                break;
            case "global-log":
                Repository.checkInit();
                validArgsNum(args,1);
                Repository.global_log();
                break;
            case "find":
                Repository.checkInit();
                validArgsNum(args,2);
                if(!Repository.find(args[1]))
                    exit("Found no commit with that message.");
                break;
            case "status":
                Repository.checkInit();
                validArgsNum(args,1);
                Repository.status();
                break;
            case "branch":
                Repository.checkInit();
                validArgsNum(args,2);
                Repository.branch(args[1]);
                break;
            case "checkout":
                Repository.checkInit();
                if(args.length<2 || args.length > 4){
                    exit("Incorrect operands.");
                }else if(args.length == 2){
                    //java gitlet.Main checkout [branch name]
                    Repository.checkoutBranch(args[1]);
                }else if(args.length == 3){
                    //java gitlet.Main checkout -- [file name]
                    if(!args[1].equals("--"))
                        exit("Incorrect operands.");
                    Repository.checkout_headCommitFile(args[2]);
                }else if(args.length == 4){
                    //java gitlet.Main checkout [commit id] -- [file name]
                    if(!args[2].equals("--")){
                        exit("Incorrect operands.");
                    }
                    Repository.checkout_anyCommitIdFile(args[1],args[3]);
                }
                break;
            case "rm-branch":
                Repository.checkInit();
                validArgsNum(args,2);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                Repository.checkInit();
                validArgsNum(args,2);
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.checkInit();
                validArgsNum(args,2);
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
    private static void validArgsNum(String []args, int n){
        if( args.length != n)
            exit("Incorrect operands.");
    }
}
