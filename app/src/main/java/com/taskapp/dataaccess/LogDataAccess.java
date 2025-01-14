package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
     * logオブジェクトを作製し、logListに格納してlogListを返す。
     * @return すべてのログのリスト
     */
    public List<Log> findAll() {
        List<Log> logList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] v = line.split(",");
                if (v.length != 4) continue;
                Log log = new Log(Integer.parseInt(v[0]), Integer.parseInt(v[1]), Integer.parseInt(v[2]),LocalDate.parse(v[3]));
                logList.add(log);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logList;
    }

    /**
     * 指定したタスクコードに該当するログを削除します。
     * findAllを呼び出し、現在のcsvをリスト化したものを取得する
     * ファイルに書き込んでいく際、受け取ったtaskCodeと同一のオブジェクトに関して書き込みをスキップする
     *
     * @see #findAll()
     * @param taskCode 削除するログのタスクコード
     */
    public void deleteByTaskCode(int taskCode) {
        List<Log> logList = findAll();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
            w.write("Task_Code,Change_User_Code,Status,Change_Date");
            String line;
            for (Log log : logList) {
                if (log.getTaskCode() == taskCode) continue;
                line = createLine(log);
                w.newLine();
                w.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ログをCSVファイルに書き込むためのフォーマットを作成します。
     * 特に説明なし
     * @param log フォーマットを作成するログ
     * @return CSVファイルに書き込むためのフォーマット
     */
    private String createLine(Log log) {
        String line = log.getTaskCode() + "," + log.getChangeUserCode() + "," + log.getStatus() + "," + log.getChangeDate();
        return line;
    }

}