package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     * 実装の流れ
     * 新しいリストを宣言する。
     * tasks.csvを読み込み、その1行を分割して配列に格納する
     * その後、Taskオブジェクトを生成して、用意したリストに格納していき、
     * 最後にリストを返す
     * Taskオブジェクト生成の際、repUserオブジェクトが必要になるので、
     * UserDataAccessのfindByCodeメソッドを呼び出してrepUserを取得する
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> taskList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] v = line.split(",");
                if (v.length != 4) continue;
                int repUserCode = Integer.parseInt(v[3]);
                User repUser = userDataAccess.findByCode(repUserCode);
                Task task = new Task(Integer.parseInt(v[0]), v[1], Integer.parseInt(v[2]), repUser);
                taskList.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskList;
    }

    /**
     * タスクをCSVに保存します。
     * ファイルを保存したまま書き込む
     * 新しい行を作製し、createLineメソッドでフォーマットした文字列を書き込む
     * @param task 保存するタスク
     */
    public void save(Task task) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath, true))) {
            w.newLine();
            String line = createLine(task);
            w.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * csvを読み込み、カンマで分割して配列に格納する
     * 取得したいtaskCodeがあるとき、その行のデータをもとにtaskオブジェクトを生成して返す
     * codeが該当しない場合、nullを返す
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int taskCode) {
        Task task = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] v = line.split(",");
                int csvCode = Integer.parseInt(v[0]);
                if (v.length != 4) continue;
                if (csvCode != taskCode) continue;
                User user = userDataAccess.findByCode(Integer.parseInt(v[3]));
                task = new Task(csvCode, v[1], Integer.parseInt(v[2]), user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return task;
    }

    /**
     * タスクデータを更新します。
     * findAllでtaskListを取得する
     * 拡張for文を用いて書き込みをしていく
     * そのとき、taskListのtaskCodeとupdateTaskのtaskCodeが一致するときは、
     * updateTaskの内容をcsvに書き込む。
     * そうでないときは、taskListの内容を書き込んでいく
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        List<Task> taskList = findAll();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
            w.write("Code,Name,Status,Rep_User_Code");
            String line;
            for (Task task : taskList) {
                if (task.getCode() == updateTask.getCode()) {
                    line = createLine(updateTask);
                } else {
                    line = createLine(task);
                }
                w.newLine();
                w.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * findAllで現在のcsvをListにしたものを取得する
     * 新たにfileに書き込みをしていく
     * 引数として受け取ったcodeと、taskListのあるオブジェクトのcodeが一致する場合は、そのtaskの書き込みをスキップする
     * @param code 削除するタスクのコード
     */
    public void delete(int code) {
        List<Task> taskList = findAll();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filePath))) {
            w.write("Code,Name,Status,Rep_User_Code");
            String line;
            for (Task task : taskList) {
                if (task.getCode() == code) continue;
                line = createLine(task);
                w.newLine();
                w.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * Taskオブジェクトのアクセサを用いてcsvに書き込むようにカンマで繋いでフォーマットする
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task) {
        String line = task.getCode() + "," + task.getName() + "," + task.getStatus() + "," + task.getRepUser().getCode();
        return line;
    }
}