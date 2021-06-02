package net.kunmc.lab.blocklandingplugin;

public class ConfigData {

    /**
     * ブロックのスタート位置
     * 人の位置+startYからスタートする
     */
    public static final String START_Y_STRING = "startY";
    public static final String TASK_REPEAT_TIME_STRING = "taskRepeatTime";

    private int startY;

    private int taskRepeatTime;

    private ConfigData(){
        this.startY = 0;
        this.taskRepeatTime = 0;
    }

    public static ConfigData getInstance(){
        return ConfigInstanceHolder.INSTANCE;
    }

    public int getStartY(){
        return this.startY;
    }

    public void setStartY(int startY){
        this.startY = startY;
    }

    public int getTaskRepeatTime(){
        return taskRepeatTime ;
    }

    public void setTaskRepeatTime(int taskRepeatTime){
        this.taskRepeatTime = taskRepeatTime;
    }

    /**
     * Configクラスの唯一のインスタンスを保持する内部クラス
     */
    public static class ConfigInstanceHolder{
        private static final ConfigData INSTANCE = new ConfigData();
    }
}
