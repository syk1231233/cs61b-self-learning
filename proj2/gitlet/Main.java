package gitlet;

import java.util.Arrays;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  <init></>
     *  <add><file name></>
     *  <commit><message></>
     *  <rm><file name></>
     *  <log></>
     *  <global-log></>
     *  <find><message></>
     *  <status></>
     *  <checkout><></></></>
     */
    public static void main(String[] args) {
        if(args.length == 0){
            Utils.message("You must input some order.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                if(args[1] == null) {
                    Utils.message("You must pass a file name.");
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            case "commit":
                if(args[1] == null){
                    Utils.message("You must pass a message.");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.rm(args[1]);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.global_log();
                break;
            case "find":
                if(args[1] == null){
                    Utils.message("You must pass a message.");
                    System.exit(0);
                }
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if(args[1] == "--") Repository.checkout1(args[2]);
                else if (args[2] == "--") Repository.checkout2(args[1],args[3]);
                else Repository.checkout3(args[1]);
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.rm_branch(args[1]);
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.merge(args[1]);
                break;
        }

    }
}
