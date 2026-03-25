package gitlet;

//import net.sf.saxon.trans.SymbolicName;
//import org.checkerframework.checker.units.qual.C;

import afu.org.checkerframework.checker.oigj.qual.O;
import afu.org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

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
    public static File HEAD = join(GITLET_DIR,"HEAD");
    public static Commit init = new Commit();
    public static File initCommit = join(Objects.objects,init.getId());

    public static class Stage implements Serializable {
        public static final File stage = join(GITLET_DIR,"stage");
        private Map<String,String> addition;
        private Set<String> removals;

        public Stage(){
            addition = new HashMap<>();
            removals = new HashSet<>();
        }

        public Map<String,String> getAddition(){
            return addition;
        }

        public Set<String> getRemovals(){
            return removals;
        }
    }

    public static class Refs{
        public static final File refs = join(GITLET_DIR, "refs");
        public static final File heads = join(refs,"heads");
        private static Map<String,File> branches = new HashMap<>();

        public static Commit getLastestCommit(){
            if(!isInit()) throw new GitletException("Gitlet does not init.");
            return readObject(join(Objects.objects,readContentsAsString(join(heads,readContentsAsString(HEAD)))),Commit.class);
        }

        public static void refreshLatestCommit(Commit latest){
            if(!isInit()) throw new GitletException("Gitlet does not init.");
            writeContents(join(heads,readContentsAsString(HEAD)),latest.getId());
        }

        public static void createNewBranch(String branchName){
            File branch = join(Refs.heads,branchName);
            if(branch.exists()) throw new GitletException("This branch already exists.");
            else{
                try{
                    branch.createNewFile();
                    writeContents(branch,init.getId());
                } catch (IOException e){
                    throw error("Create file fails.");
                }
            }
        }

        public static Map<String,File> getBranches(){
            return branches;
        }
    }

    public static class Objects{
        public static File objects = join(GITLET_DIR,"objects");
        public static File createNewFileInObjects(String file_name){
            File fileName = Utils.join(objects,file_name);
            try{
                fileName.createNewFile();
                return fileName;
            } catch (IOException e){
                throw error("Create file fails.",e);
            }
        }
    }

    public static boolean isInit(){
        if(!(Stage.stage.exists() && Refs.heads.exists())) throw new GitletException("Gitlet does not init.");
        return true;
    }
    /**
     * Init .gitlet folder and complete the first commit.
     * The first commit only has a message "initial commit" and a timestamp " 00:00:00 UTC, Thursday, 1 January 1970"
     * Here are its structure.
     *.gitlet/
     * ├── stage(缓冲区，只存储哈希映射)
     * ├── HEAD(指向refs/heads/分支名,使得本次提交的commit哈希值覆盖上次提交)
     * ├── refs（存储所有版本分支的指针）/
     * │   └── heads(分支文件夹存储所有的分支指针)/
     * │       └── master（一个实例，分支名命名，内部存储该分支最新一次commit哈希值）
     * └── objects（存储历史所有提交的blob和commit）/
     * ├── blob1（一个实例，存储一个文件的内容和其哈希值）
     * └── commit（一个实例，存储一次提交，包含日志文件（修改了什么），时间戳，对应文件的哈希值，父节点1，父节点2（不合并时为null））
     * @throws GitletException when .gitlet exists.
     */
    public static void init(){
        if(Stage.stage.exists() && Refs.heads.exists()){
            throw error("A gitlet version-control system exists in the current directory.");
        }
        else{
           Refs.heads.mkdirs();
           Objects.objects.mkdirs();
           try {
                Stage.stage.createNewFile();
                initCommit.createNewFile();
                Refs.createNewBranch("master");
                HEAD.createNewFile();
           }catch (IOException e){
               throw Utils.error("Create file fails.",e);
           }
           Utils.writeObject(initCommit,init);
           writeContents(HEAD,"master");
        }
    }

    /**
     * 判断文件相对于当前版本有无更改，有更改则加入到缓存区中，无则取消
     * usage <add><[file name]></> 只处理一个文件
     * 1.检查文件是否存在——>不存在输出File does not exist. 并退出.
     * 2.抵消rm标记——>从暂存区的removals中移除该文件名.
     * 3.计算当前文件的blob哈希——>读取文件内容得到哈希值
     * 4.获取当前提交中该文件的版本,从上次提交中获取该文件的id,查看是否一致
     * 5.版本对比,如果id一致,从addition中移除该文件,如果不一致,覆盖写入addition中
     * 6.将更新后的stage序列化写入stage中
     * @param file_name 传入的文件名
     */
    public static void add(String file_name){
        isInit();
        Stage stage = new Stage();
        stage = readObject(Stage.stage,Stage.class);
        File aimFile = Utils.join(CWD,file_name);
        if(!aimFile.exists()) throw new GitletException("File does not exist.");
        if(stage.getRemovals().contains(file_name)) stage.getRemovals().remove(file_name);
        String id = sha1(readContentsAsString(aimFile));
        if(!(Refs.getLastestCommit().getMap().get(file_name).equals(id))) stage.getAddition().put(file_name,id);
        writeObject(Stage.stage,stage);
    }

    /**
     * 将缓冲区文件全部保存至本地仓库,同时记录本次commit信息,同步存入本地仓库
     * usage <commit><message></>
     * 1. 检查提交信息是否为空,为空则报错 Please enter a commit message.
     * 2. 暂存区必须有变更 -> addition和removals不为空,为空则 No changes added to the commit.
     * 3. 获取父提交的map,获取上次提交的map,根据addition和removals删减文件
     * 4. 清空暂存区,覆盖写入一次空文档
     * 5. 计算本次commit的哈希值
     * 6. 更新当前分支指针
     * @param message Commit message.
     */
    public static void commit(String message){
        isInit();
        if(message.isEmpty()) throw new GitletException("Please enter a commit message.");
        Stage curStage = readObject(Stage.stage,Stage.class);
        if(curStage.getAddition().isEmpty() && curStage.getRemovals().isEmpty()) throw new GitletException("No changes added to the commit.");
        Commit curCommit = Commit.creatNewCommit(curStage);
        Refs.refreshLatestCommit(curCommit);
        File fileName = Objects.createNewFileInObjects(curCommit.getId());
        writeObject(fileName,curCommit);
        writeObject(Stage.stage,new Stage());
    }

    /**
     * 不再追踪该文件的版本
     * usage <rm><file name></>
     * 1. 如果该文件在缓冲区addition中,从缓冲区中删除;
     * 2. 如果该文件不在addition中,而是在父提交的commit中的map,那么将其加入removals,删除工作区的该文件,因为已经在本地仓库中存储;
     * 3. 如果都不在,报错 No reason to remove the file.
     * @param file_name 传入的文件名
     */
    public static void rm(String file_name){
        isInit();
        Stage curStage = readObject(Stage.stage,Stage.class);
        if(curStage.addition.containsKey(file_name)) curStage.addition.remove(file_name);
        else {
            Commit parent = Refs.getLastestCommit();
            if (parent.getMap().containsKey(file_name)) {
                curStage.removals.add(file_name);
                File aimFile = join(CWD, file_name);
                if (aimFile.exists()) aimFile.delete();
                else throw new GitletException("This file doex not exist.");
            } else throw new GitletException("No reason to remove the file.");
        }
    }

    /**
     * 显示当前分支的提交历史,从最新到初始提交 提交格式如下
     * Commit [完整哈希值]
     * Date: [时间戳，如 Wed Dec 31 16:00:00 1969 -0800]
     * [提交消息]
     * 1. 前往HEAD指向文件处,读出文件存储的commit对象信息,输出,向父节点递归输出
     * 2. 输出时按上述格式,其中时间戳的格式输出方式为Utilis.getTimestamp(this.timestamp)
     * 3. 若为合并提交（2 个父节点），额外输出：Merge: [父1哈希前7位] [父2哈希前7位]
     */
    public static void log(){
        isInit();
        Commit curCommit = Refs.getLastestCommit();
        while(curCommit != null){
            System.out.println(curCommit);
            curCommit = readObject(Utils.join(Objects.objects,curCommit.getParent().get(0)),Commit.class);
        }
    }

    /**
     * 显示所有提交信息
     * 1. 遍历.gitlet/objetcs中所有文件,readObject(,Commit) 来读取文件,只要成功读取的
     * 2. 每个 Commit 输出与 log 命令完全相同的格式。
     */
    public static void global_log(){
        isInit();
        List<String> allFile = Utils.plainFilenamesIn(Objects.objects);
        for(String fileName : allFile){
            try{
                Commit commit = readObject(Utils.join(Objects.objects,fileName),Commit.class);
                System.out.println(commit);
            } catch (GitletException excp){
                continue;
            }
        }
    }

    /**
     * 查找特定提交
     * 查询所有的满足message = 传入参数的Commit,输出他们的哈希值
     * 1. 前往.gitlet/objects下遍历所有Commit文件
     * 2. 逐行输出每个匹配提交的完整哈希值
     * @param message
     */
    public static void find(String message){
        isInit();
        List<String> allFile = Utils.plainFilenamesIn(Objects.objects);
        for(String fileName : allFile){
            try{
                Commit commit = readObject(Utils.join(Objects.objects,fileName),Commit.class);
                if(commit.getMessage().equals(message)) System.out.println(commit.getId());
            } catch (GitletException excp){
                continue;
            }
        }
    }

    /**
     * 显示当前仓库状态,即分支(HEAD指针指向即当前分支前加*),缓冲区情况,修改但未add的文件,未追踪文件
     * ```
     * === Branches ===
     * *master
     * other-branch
     *
     * === Staged Files ===
     * wug.txt
     * wug2.txt
     *
     * === Removed Files ===
     * goodbye.txt
     *
     * === Modifications Not Staged For Commit ===
     * junk.txt (deleted)
     * wug3.txt (modified)
     *
     * === Untracked Files ===
     * random.stuff
     * ```
     * 1.读取当前工作区所有文件,建立一个List<String>,创建多个数组stagedFiles,removedFiles,ModificationNotStagedForCommit,Untrack
     * 2.逐个读取文件,哈希计算id,进行如下比对,读取stage,复制上次Commit的Map
     * 2.1 文件名检索在stage的addition中且id一致,加入stagedFiles
     * 2.2 文件名在stage的removals中,加入RemovedFiles,并删除复制Map的对应键值
     * 2.3 文件名在上次Commit中但id已不同 文件名后缀加" (modified)"加入Modi数组,并删除复制Map的对应键值
     * 2.4 文件名在上次Commit中且id相同,直接删除复制Map的对应键值
     * 2.5 上述均不满足的文件加入Untrack数组 .
     * 3. 遍历结束Commit中剩下的文件后缀加" (deleted)"加入modi数组
     * 4. 遍历heads文件夹内所有分支名,如果和HEAD存储名一致则前加*,加入branches数组
     * 5. 所有数组.sort(strs)排序,按照格式输出
     */
    public static void status(){
        isInit();
        List<String> allFileInWork = Utils.plainFilenamesIn(CWD);
        List<String> allBranches = Utils.plainFilenamesIn(Refs.heads);
        List<String> stagedFiles = new ArrayList<>();
        List<String> removedFiles = new ArrayList<>();
        List<String> modificationNotStage = new ArrayList<>();
        List<String> untrack = new ArrayList<>();
        List<String> branches = new ArrayList<>();
        Stage curStage = readObject(Stage.stage,Stage.class);
        Commit parentCommit = Refs.getLastestCommit();

        for(String fileName : allFileInWork){
            String id = sha1(readContentsAsString(Utils.join(CWD,fileName)));
            if (curStage.addition.containsKey(fileName) && curStage.addition.get(fileName).equals(id)){
                stagedFiles.add(fileName);
                continue;
            }
            if (curStage.removals.contains(fileName)){
                removedFiles.add(fileName);
                if(parentCommit.getMap().containsKey(fileName)) parentCommit.getMap().remove(fileName);
                continue;
            }
            if (parentCommit.getMap().containsKey(fileName)){
                if (!parentCommit.getMap().get(fileName).equals(id)) {
                    StringBuilder re = new StringBuilder(fileName);
                    modificationNotStage.add(re.append(" (modified)").toString());
                }
                parentCommit.getMap().remove(fileName);
            }
            untrack.add(fileName);
        }
        if(!parentCommit.getMap().isEmpty()){
            for(String fileName : parentCommit.getMap().keySet()){
                StringBuilder re = new StringBuilder(fileName);
                modificationNotStage.add(re.append(" (deleted)").toString());
            }
        }

        String curBranch = readContentsAsString(HEAD);
        for(String branchName : allBranches) branches.add(branchName);
        Collections.sort(branches);
        Collections.sort(stagedFiles);
        Collections.sort(removedFiles);
        Collections.sort(modificationNotStage);
        Collections.sort(untrack);

        System.out.println("=== Branches ===");
        for(String a : branches){
            if (a.equals(curBranch)) System.out.println("*"+a);
            else System.out.println(a);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for(String a : stagedFiles) System.out.println(a);
        System.out.println();

        System.out.println("=== Removed Files ===");
        for(String a : removedFiles) System.out.println(a);
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for(String a : modificationNotStage) System.out.println(a);
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for(String a : untrack) System.out.println(a);
        System.out.println();
    }

    /**
     * 匹配id
     * @param id 用户传入id
     * @return 匹配到的Commit.
     */
    public static Commit getCommit(String id) {
        List<String> allId = Utils.plainFilenamesIn(Objects.objects);
        int count = 0;
        String aimCommit = null;
        for(String idFIle : allId){
            if(idFIle.startsWith(id)){
                count += 1;
                aimCommit = idFIle;
            }
        }
        if(count == 0) throw new GitletException("No commit with that id exists.");
        else if (count == 1) return readObject(Utils.join(Objects.objects,aimCommit),Commit.class);
        else throw new GitletException("Ambiguous commit id.");
    }

    /**
     * 检查工作目录中的未追踪文件是否会被检出操作覆盖
     * @param targetCommit 将要覆盖写入的提交
     */
    public static void checkUntrackedConflict(Commit curcommit,Commit targetCommit){
        List<String> allFileInWork = Utils.plainFilenamesIn(CWD);
        for(String fileName : allFileInWork){
            if(curcommit.getMap().containsKey(fileName)) continue;
            if(targetCommit.getMap().containsKey(fileName)) throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
        }
    }

    /**
     * ### checkout
     * 取出制定版本文件
     * Usage1：checkout -- [fileName]
     * 1.获取当前 HEAD 提交
     * 2.检查文件是否在提交中 → 不存在则报错
     * 3.从提交中取出文件，覆盖写入工作目录
     * 4.不暂存、不清空暂存区
     * @param fileName 要取出的文件。
     */
    public static void checkout1(String fileName){
        isInit();
        Commit commit = Refs.getLastestCommit();
        if(!commit.getMap().containsKey(fileName)) throw new GitletException("File does not exsit in that commit.");
        writeContents(Utils.join(CWD,fileName),readContents(Utils.join(Objects.objects,commit.getMap().get(fileName))));
    }

    /**
     *  * Usage2：checkout [commitId] -- [fileName]
     *      * 1.用 getCommit(commitId) 获取提交 → 不存在则报错
     *      * 2.检查文件是否在该提交中 → 不存在则报错
     *      * 3.从提交中取出文件，覆盖写入工作目录
     *      * 4.不暂存、不清空暂存区
     * @param commitId
     * @param fileName
     */
    public static void checkout2(String commitId, String fileName){
        isInit();
        Commit commit = getCommit(commitId);
        if(!commit.getMap().containsKey(fileName)) throw new GitletException("File does not exsit in that commit.");
        writeContents(Utils.join(CWD,fileName),readContents(Utils.join(Objects.objects,commit.getMap().get(fileName))));
    }

    /**
     * * Usage3：checkout [branchName]
     *      * 1.检查分支是否存在 → 不存在→报错
     *      * 2.检查是否为当前分支 → 是→打印提示并退出
     *      * 3.安全检查：未追踪文件会被覆盖→报错
     *      * 4.获取目标分支的头提交
     *      * 5.覆盖工作目录：用目标分支所有文件替换
     *      * 6.删除文件：当前追踪、但目标分支不存在的文件
     *      * 7.切换 HEAD：指向目标分支
     *      * 8.清空暂存区
     * @param branchName
     */
    public static void checkout3(String branchName){
        isInit();
        if(!Utils.join(Refs.heads,branchName).exists()) throw new GitletException("No such branch exists.");
        if(readContentsAsString(HEAD).equals(branchName)) throw new GitletException("No need to checkout the current branch.");
        Commit targetCommit = readObject(Utils.join(Objects.objects,readContentsAsString(Utils.join(Objects.objects,branchName))), Commit.class);
        Commit curCommit = Refs.getLastestCommit();
        checkUntrackedConflict(curCommit,targetCommit);
        for(String fileName : curCommit.getMap().keySet()) if(!targetCommit.getMap().containsKey(fileName)) Utils.join(CWD,fileName).delete();
        for(Map.Entry<String,String> file : targetCommit.getMap().entrySet()) writeContents(Utils.join(CWD,file.getKey()),readContents(Utils.join(Objects.objects,file.getValue())));
        writeContents(HEAD,branchName);
        writeContents(Stage.stage,"");
    }

    /**
     * 创建一个指定名称的新分支,将新分支指向当前HEAD所在的提交节点
     * usage branch [branch name]
     * 1. 判断分支是否存在
     * 2. 在heads中创建一个新文件branch name
     * 3. 将HEAD指针指向文件的内容写入新文件中
     * @param branchName 分支名称。
     */
    public static void branch(String branchName){
        File newBranch = Utils.join(Refs.heads,branchName);
        if(newBranch.exists()) throw new GitletException("A branch with that name already exists.");
        try {
            newBranch.createNewFile();
            writeContents(newBranch,Refs.getLastestCommit().getId());
        } catch (IOException e) {
            throw error("Create file fails.", e);
        }
    }

    /**
     * 删除指定名称的分支
     * usage rm-branch [branch name]
     * 1.检查该分支是否存在,不存在报错 A branch with that name does not exist.
     * 2.检查该分支是否为HEAD指针指向的分支,如果是报错 Connot remove the current branch.
     * 3.删除该文件
     * @param branchName
     */
    public static void rm_branch(String branchName){
        File aimBranch = Utils.join(Refs.heads,branchName);
        if(!aimBranch.exists()) throw new GitletException("A branch with that name does not exist.");
        if(readContentsAsString(HEAD).equals(branchName)) throw new GitletException("Connot remove the current branch.");
        aimBranch.delete();
    }

    /**
     * 回档某个特定版本文件
     * usage reset [commit id]
     * 1.读取该id的Commit,将其中所有文件导入当前工作目录
     * 2.未追踪覆盖写入检查
     * 3.删除当前已追踪,但该Commit中未追踪的文件
     * 4.将HEAD指针指向文件重写,写入该Commit
     * 5.清空缓存区
     * @param commitId 要回档的commitID
     */
    public static void reset(String commitId){
        isInit();
        Commit targetCommit = getCommit(commitId);
        Commit curCommit = Refs.getLastestCommit();
        checkUntrackedConflict(curCommit,targetCommit);
        for(String fileName : curCommit.getMap().keySet()) if(!targetCommit.getMap().containsKey(fileName)) Utils.join(CWD,fileName).delete();
        for(Map.Entry<String,String> file : targetCommit.getMap().entrySet()) writeContents(Utils.join(CWD,file.getKey()),readContents(Utils.join(Objects.objects,file.getValue())));
        writeContents(Utils.join(Refs.heads,readContentsAsString(HEAD)),targetCommit.getId());
        writeContents(Stage.stage,"");
    }

    public static Commit getLatestCommonParent(Commit commit1, Commit commit2){
        int counts = 0;
        while(commit1 != null && commit2 != null){
            if (commit1.getId().equals(commit2.getId())) return commit1;
            if(counts % 2 == 0 && commit1.getParent() != null){
                commit1 = readObject(Utils.join(Objects.objects,commit1.getParent().get(0)),Commit.class);
            } else if (counts % 2 != 0 && commit2.getParent() != null) {
                commit2 = readObject(Utils.join(Objects.objects,commit1.getParent().get(0)),Commit.class);
            }
            counts += 1;
        }
        return null;
    }

    /**
     *
     * @param parent
     * @param message
     */
    public static void mergeCommit(List<String> parent,String message){
        isInit();
        Stage curStage = readObject(Stage.stage,Stage.class);
        if(curStage.getAddition().isEmpty() && curStage.getRemovals().isEmpty()) throw new GitletException("No changes added to the commit.");
        Commit curCommit = Commit.creatNewCommit(curStage);
        curCommit.setMessage(message);
        curCommit.setParent(parent);
        curCommit.setTimestamp();
        curCommit.setId();
        Refs.refreshLatestCommit(curCommit);
        File fileName = Objects.createNewFileInObjects(curCommit.getId());
        writeObject(fileName,curCommit);
    }

    public static void dealMerge(Stage stage,String filename,String id1,String id2){
        File newFile = Utils.join(CWD,filename);
        if(!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                message("Create file fails.", e);
            }
        }
        byte[] b1 = null;
        byte[] b2 = null;
        if(id1 != null) b1 = readContents(Utils.join(Objects.objects,id1));
        if(id2 != null) b2 = readContents(Utils.join(Objects.objects,id2));
        writeContents(newFile,"<<<<<<< HEAD",'\n',b1,'\n',"======",'\n',b2,'\n',">>>>>>>");

        stage.addition.put(filename,sha1(filename,readContentsAsString(newFile)));
    }
    /**
     * 将指定分支合并到当前分支
     * usage merge [branch name]
     * 1.检查是否存在未提交的变更 -> 检查stage是否为空 You have uncommitted changes.
     * 2.检查目标分支是否存在 A branch with that name does not exist.
     * 3.尝试合并自己 ——> 检查HEAD指针是否指向自己  Cannot merge a branch with itself.
     * 4.未追踪文件被合并覆盖 untrackCheck .
     * 5.获取当前分支和目标分支的最新公共祖先,即分割点
     * 6.如果分割点 == 目标分支, 即当前分支为目标分支的孩子,报错 Given branch is an ancestor of the current branch.
     * 7.如果分割点 == 当前分支, 即目标分支为当前分支的孩子,直接输出目标分支, Current branch fast-forwarded.
     * 8.开始文件合并,获取分割点,当前分支,目标分支的所有文件去重集合,分别获取三者的哈希id,没有则为null
     *     8.1 目标分支修改id不同,当前分支未修改id相同 -> 用目标分支版本覆盖,自动暂存 将目标分支的文件加入addition
     *     8.2 当前分支修改id不同,目标分支未修改id相同 -> 保持当前版本不动
     *     8.3 两分支id相同 -> 不改动
     *     8.4 分割点无该文件,仅当前分支有该文件 -> 不改动
     *     8.5 分割点没有,仅目标分支有该文件 -> 加入至addition
     *     8.6 分割点有该文件,当前分支也有该文件且id相同,目标分支没有该文件 -> 加入removals
     *     8.7 分割点有该文件,当前分支没有该文件,但目标分支有该文件且id相同 -> 不改动
     *     8.8 分割点没有该文件,但目标分支和当前分支有该文件且id不同 -> 合并冲突
     *     8.9 分割点有该文件,但目标分支或者当前分支有该文件且id不同,另一分支无该文件 -> 合并冲突
     *     8.10 目标分支,当前分支与分割点文件id互不相同 -> 合并冲突
     *         8.11.合并冲突
     *             拼成如下格式后重新哈希加入addition并输出 Encountered a merge conflict. 删除文件内容只需不需拼接
     * ```
     *             <<<<<<< HEAD
     *             当前分支文件内容
     *             =======
     *             目标分支文件内容
     *             >>>>>>>
     * ```
     * 9. 进行Commit, message"merge [目标分支名] into [当前分支名]", parent1为当前分支,parent2为目标分支,
     * @param targetBranch 目标分支
     */
    public static void merge(String targetBranch){
        Stage curStage = new Stage();
        curStage = readObject(Stage.stage,Stage.class);
        if (!curStage.addition.isEmpty() || !curStage.removals.isEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        if(!Utils.join(Refs.heads,targetBranch).exists()){
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        if(readContentsAsString(HEAD).equals(targetBranch)){
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }

        Commit curcommit = Refs.getLastestCommit();
        Commit aimcommit = readObject(Utils.join(Objects.objects,readContentsAsString(Utils.join(Refs.heads,targetBranch))),Commit.class);
        List<String> parent = new ArrayList<>();
        parent.add(curcommit.getId());
        parent.add(aimcommit.getId());
        checkUntrackedConflict(curcommit,aimcommit);
        Commit lcp = getLatestCommonParent(curcommit,aimcommit);
        if(lcp.getId().equals(curcommit.getId())){
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (lcp.getId().equals(aimcommit.getId())) {
            message("Current branch fast-forwarded.");
            checkout3(targetBranch);
            System.exit(0);
        }
        Set<String> allFile = new HashSet<>(curcommit.getMap().keySet());
        allFile.addAll(aimcommit.getMap().keySet());
        allFile.addAll(lcp.getMap().keySet());
        for(String fileName : allFile){
            String curId = curcommit.getMap().get(fileName);
            String aimId = aimcommit.getMap().get(fileName);
            String parentId = lcp.getMap().get(fileName);
            if(!parentId.equals(aimId) && parentId.equals(curId)) curStage.addition.put(fileName,aimId);
            else if (parentId == null && curId == null && aimId != null) curStage.addition.put(fileName,aimId);
            else if (parentId != null && parentId.equals(curId) && aimId == null) curStage.removals.add(fileName);
            else if (parentId == null && curId != null && aimId != null && !curId.equals(aimId)) dealMerge(curStage,fileName,curId,aimId);
            else if (parentId != null && curId != null && aimId == null && !curId.equals(parentId)) dealMerge(curStage,fileName,parentId,curId);
            else if (parentId != null && aimId != null && curId == null && !aimId.equals(parentId)) dealMerge(curStage,fileName,parentId,aimId);
            else if (parentId != null && curId != null && aimId != null && !parentId.equals(curId) && !parentId.equals(aimId) && !curId.equals(aimId)) dealMerge(curStage,fileName,curId,aimId);
        }
        mergeCommit(parent,"merge "+targetBranch+" into "+readContentsAsString(HEAD));
    }
}