package net.kunmc.lab.blocklandingplugin.message;

public class GameMessage {
    public static final String LANDING = "LandingPlugin:";

    public static final String ERROR_LESS_ARGS = LANDING + "引数が不足しています";
    public static final String ERROR_CMD_SENDER_ERROR = LANDING + "実行者がプレイヤーではありません";
    public static final String ERROR_NOT_CHEST = LANDING + "足元のブロックがチェストではありません";
    public static final String ERROR_CANT_START = LANDING + "チームが存在しません";
    public static final String ERROR_NO_TEAM_CMD = LANDING + "チーム名の指定がありません";
    public static final String ERROR_NO_TEAM_ITEM = LANDING + "アイテムが読み込まれていないチームがあります。";


    public static String getConfigSet(String itemName){
        return LANDING + "コンフィグを更新しました（" + itemName + "）";
    }

    public static String getErrorNoTeamName(String teamName){
        return LANDING + "チーム「" + teamName + "」は存在しません";
    }

    public static String getLoadingChest(int sum){
        return LANDING + "チェストの中身を読み込みました（" + sum + "個）";
    }

    public static String getLoadingTeam(String teamNames){
        return LANDING + "チームを読み込みました（" + teamNames + "）";
    }
}
