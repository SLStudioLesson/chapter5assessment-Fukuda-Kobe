package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     * statusを0,1,2の場合によって文字列を変更する。
     * そのあと、そのタスクを担当しているのかが自分なのかその他の人かによって出力内容を変更して出力する
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        List<Task> taskList = taskDataAccess.findAll();
        taskList.forEach(t -> {
            String status = "未着手";
            if (t.getStatus() == 1) {
                status = "着手中";
            } else if (t.getStatus() == 2) {
                status = "完了";
            }
            if (t.getRepUser().getName().equals(loginUser.getName())) {
                System.out.println(t.getCode() + ". タスク名：" + t.getName() + ", 担当者名：あなたが担当しています, ステータス：" + status);
            } else {
                System.out.println(t.getCode() + ". タスク名：" + t.getName() + ", 担当者名：" + t.getRepUser().getName() + "が担当しています, ステータス：" + status);
            }
        });
        }

    /**
     * 新しいタスクを保存します。
     * 入力してもらったユーザーコードが存在するか確認し、存在しない場合AppExceptionを投げる
     * 存在している場合は、入力データをもとにTaskオブジェクトを生成し、taskDataAccessのsaveメソッドを呼び出す
     * また、同時にlogDataAccessのsaveメソッドも呼び出す
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode, User loginUser) throws AppException {
        User repUser = userDataAccess.findByCode(repUserCode);
        if (repUser == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }
        Task newTask = new Task(code, name, 0, repUser);
        taskDataAccess.save(newTask);

        Log log = new Log(code, loginUser.getCode(), 0, LocalDate.now());
        logDataAccess.save(log);
    }

    /**
     * タスクのステータスを変更します。
     * taskDataAccess.findByCodeで該当するタスクを取得する
     * 該当するタスクがなかったときはnullで返ってくれうので、AppExceptionでメッセージを投げる
     * 該当するタスクのStatusと、更新後のStatusに乖離があればAppExceptionでメッセージを投げる
     * 問題がなければ、更新したStatusでタスクオブジェクトを生成してtaskDataAccessのupdateで処理をする
     * また、Logオブジェクトも作製し、logDataAccessのsaveでlogを残す
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status, User loginUser) throws AppException {
        Task task = taskDataAccess.findByCode(code);
        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }
        int taskStatus = task.getStatus();
        if (taskStatus + 1 != status) {
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }
        Task chanegeTaskStatus = new Task(code, task.getName(), status, task.getRepUser());
        taskDataAccess.update(chanegeTaskStatus);

        Log log = new Log(code, loginUser.getCode(), status, LocalDate.now());
        logDataAccess.save(log);
    }

    /**
     * タスクを削除します。
     * 受け取ったtaskCodeをもとに、findByCodeを用いて該当するタスクオブジェクトを取得する
     * タスクオブジェクトがnullのとき、タスクオブジェクトのstatusが完了でないとき、AppExceptionを投げる
     * 受け取ったtaskCodeをもとに、taskDataAccessのdelete、logDataAccessのdeleteを呼び出す
     * 最後に、削除が完了したことを出力する
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    public void delete(int code) throws AppException {
        Task deleteTask = taskDataAccess.findByCode(code);
        List<Log> log = logDataAccess.findAll();
        if (deleteTask == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }
        if (deleteTask.getStatus() != 2) {
            throw new AppException("ステータスが完了のタスクを選択してください");
        }
        taskDataAccess.delete(code);
        logDataAccess.deleteByTaskCode(code);
        System.out.println(deleteTask.getName() + "の削除が完了しました。");
    }
}