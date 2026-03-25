package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.writeContents;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * private String message ——日志信息,即本次修改内容
     * private long timestamp ——时间戳
     * private Map<String, String> fileMap ——本次修改的文件,键为文件名,值为哈希值
     * private List<String> parent ——父节点的commit的哈希值,普通提交只有一个,合并提交会有两个
     * private String id ——该commit对象的哈希值
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private String message;
    private long timestamp;
    private Map<String,String> fileMap;
    private List<String> parent;
    private String id;

    public Commit(){
        message = "initial commit.";
        timestamp = 0;
        fileMap = new HashMap<String,String>();
        parent = new ArrayList<>();
        setId();
    }

    public void setId(){
        id = Utils.sha1(Utils.serialize(message),Utils.serialize(timestamp),Utils.serialize((Serializable) fileMap), Utils.serialize((Serializable) parent));
    }

    public String getMessage(){
        return message;
    }

    public void setTimestamp(){
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp(){
        return timestamp;
    }

    public String getId(){
        return id;
    }

    public List<String> getParent(){
        return parent;
    }
    public HashMap<String,String> getMap(){
        return (HashMap<String, String>) fileMap;
    }

    public void setFileMap(Map<String,String> parent){
        fileMap = new HashMap<>(parent);
    }

    public void setMessage(String message1){
        message = message1;
    }

    public void setParent(List<String> parent1){
        parent = parent1;
    }

    public static Commit creatNewCommit(Repository.Stage curStage){
        Commit parentCommit = Repository.Refs.getLastestCommit();
        Commit curCommit = new Commit();
        curCommit.setFileMap(parentCommit.getMap());
        for(String removeFile : curStage.getRemovals()) curCommit.getMap().remove(removeFile);
        for(Map.Entry<String,String> addFile : curStage.getAddition().entrySet()){
            curCommit.getMap().put(addFile.getKey(),addFile.getValue());
        }
        writeContents(Repository.Stage.stage,null);
        curCommit.getParent().add(parentCommit.getId());
        curCommit.setTimestamp();
        curCommit.setId();
        return curCommit;
    }

    /**
     * 完成日志格式要求如下.
     * ===
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     * @return 上述结构.
     */
    @Override
    public String toString(){
        StringBuilder re = new StringBuilder();
        re.append("===").append('\n').append("commit ").append(this.id).append('\n');
        if(parent.size() == 2) re.append("Merge: ").append(parent.get(0).substring(0,7)).append(" ").append(parent.get(1).substring(0,7)).append('\n');
        re.append("Date: ").append(Utils.getTimestamp(this)).append('\n').append(message).append('\n');
        return re.toString();
    }
}