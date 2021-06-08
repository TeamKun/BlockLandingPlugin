package net.kunmc.lab.blocklandingplugin.message;

public class GameMessage {
    public static final String LANDING = "LandingPlugin:";

    public static final String ERROR_LESS_ARGS = LANDING + "引数が不足しています";
    public static final String ERROR_CMD_SENDER_ERROR = LANDING + "実行者がプレイヤーではありません";
    public static final String ERROR_NOT_CHEST = LANDING + "足元のブロックがチェストではありません";
    public static final String ERROR_CANT_START = LANDING + "チームが存在しません";
    public static final String ERROR_NO_TEAM_CMD = LANDING + "チーム名の指定がありません";
    public static final String ERROR_NONE_TEAM_CMD = LANDING + "チームが読み込まれていません";


    public static String getConfigSet(String itemName, int settingNumber) {
        return LANDING + "コンフィグ[" + itemName + "]を[" + settingNumber + "]に更新しました";
    }

    public static String getErrorNoTeamName(String teamName) {
        return LANDING + "チーム「" + teamName + "」は存在しません";
    }

    public static String getLoadingChest(int sum) {
        return LANDING + "チェストの中身を読み込みました（" + sum + "個）";
    }

    public static String getLoadingTeam(String teamNames) {
        return LANDING + "チームを読み込みました（" + teamNames + "）";
    }

    public static String getErrorNoItem(String teamName) {
        return LANDING + "チーム「" + teamName + "」にアイテムが設定されていません";
    }

    public static String getNoTeamMember(String teamName) {
        return LANDING + "チーム「" + teamName + "」にオンラインなメンバーがいません";
    }

}
