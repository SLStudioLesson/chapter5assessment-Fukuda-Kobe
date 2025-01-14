package com.taskapp.dataaccess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.taskapp.model.Log;

public class LogDataAccess {
    private final String filePath;


    public LogDataAccess() {
        filePath = "app/src/main/resources/logs.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public LogDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * ログをCSVファイルに保存します。
     * ファイルを保存したまま書き込む
     * 新しい行を追加する
     * 書き込む内容はcreateLineによりフォーマットする
     * @param log 保存するログ
     */
    public void save(Log log) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath, true))) {
            w.newLine();
            String line = createLine(log);
            w.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * すべてのログを取得します。
     * logListを宣言する
     * csvを読み込み、カンマで分割して配列に格納する
     * if文を用いてその行に異常がないか確認する
     *
     * @return すべてのログのリスト
     */
    // public List<Log> findAll() {
        // List<Log> logList = new ArrayList<>();
    //     try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
    // reader.readLine();
    // String line;
    // while ((line = reader.readLine()) != null) {
    //     String[] v = line.split(",");
    // }

    //     } catch (IOException e) {
    //         e.prinstStackTrace();
    //     }
    //     return logList;
    // }

    /**
     * 指定したタスクコードに該当するログを削除します。
     *
     * @see #findAll()
     * @param taskCode 削除するログのタスクコード
     */
    // public void deleteByTaskCode(int taskCode) {
    //     try () {

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    /**
     * ログをCSVファイルに書き込むためのフォーマットを作成します。
     *
     * @param log フォーマットを作成するログ
     * @return CSVファイルに書き込むためのフォーマット
     */
    private String createLine(Log log) {
        String line = log.getTaskCode() + "," + log.getChangeUserCode() + "," + log.getStatus() + "," + log.getChangeDate();
        return line;
    }

}