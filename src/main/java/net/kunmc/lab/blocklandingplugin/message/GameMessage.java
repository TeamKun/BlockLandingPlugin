package net.kunmc.lab.blocklandingplugin.message;

public class GameMessage {
    public static final String LANDING = "LandingPlugin:";

    public static final String ERROR_LESS_ARGS = LANDING + "引数が不足しています";
    public static final String ERROR_CMD_SENDER_ERROR = LANDING + "実行者がプレイヤーではありません";
    public static final String ERROR_NOT_CHEST = LANDING + "足元のブロックがチェストではありません";
    public static final String ERROR_CANT_START = LANDING + "チームが設定されていません";
    public static final String ERROR_NO_TEAM_CMD = LANDING + "チーム名の指定がありません";

    public static String getErrorNoTeamName(String teamName){
        return "チーム「" + teamName + "」は存在しません";
    }

    public static String getLoadingChest(int sum){
        return "チェストの中身を読み込みました（" + sum + "個）";
    }

    public static String getLoadingTeam(String teamNames){
        return "チームを読み込みました（" + teamNames + "）";
    }
}
