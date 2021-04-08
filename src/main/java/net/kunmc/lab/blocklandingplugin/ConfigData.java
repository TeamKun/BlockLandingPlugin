package net.kunmc.lab.blocklandingplugin;

public class ConfigData {

    /**
     * ブロックのスタート位置
     * 人の位置+startYからスタートする
     */
    public static final String START_Y_STRING = "startY";

    public int startY;

    private ConfigData(){
        this.startY = 0;
    }

    /**
     * @return
     */
    public static ConfigData getInstance(){
        return ConfigInstanceHolder.INSTANCE;
    }

    /**
     * @return
     */
    public int getStartY(){
        return startY;
    }

    /**
     * @param startY
     */
    public void setStartY(int startY){
        this.startY = startY;
    }

    /**
     * Configクラスの唯一のインスタンスを保持する内部クラス
     */
    public static class ConfigInstanceHolder{
        private static final ConfigData INSTANCE = new ConfigData();
    }
}
