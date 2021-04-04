package net.kunmc.lab.blocklandingplugin;

public class Config {
    private static Config config = null;

    private Config(){
        //configからデータ取得
    }

    public static Config getInstance(){
        if(config == null){
            config = new Config();
        }

        return config;
    }
}
