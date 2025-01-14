package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.taskapp.model.User;

public class UserDataAccess {
    private final String filePath;

    public UserDataAccess() {
        filePath = "app/src/main/resources/users.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public UserDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * メールアドレスとパスワードを基にユーザーデータを探します。
     * users.csvを読み込み、1行をカンマで分割して配列に格納する
     * 格納した要素のうち、メールアドレスとパスワードにあたる要素を、引数のメールアドレスとパスワードが一致するとき、
     * loginUserのオブジェクトを生成して返す
     * @param email メールアドレス
     * @param password パスワード
     * @return 見つかったユーザー
     */
    public User findByEmailAndPassword(String email, String password) {
        User loginUser = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 4) continue;
                if (!(values[2].equals(email)) || !(values[3].equals(password))) continue;
                loginUser = new User(Integer.parseInt(values[0]), values[1], values[2], values[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return loginUser;
    }

    /**
     * コードを基にユーザーデータを取得します。
     * users.csvを読みこみ、1行を分割して配列に格納する
     * 配列のインデックス0の要素をintに変換し、findByCodeメソッドの引数と一致するとき、
     * その行でUserオブジェクトを生成し、それを返す
     * @param code 取得するユーザーのコード
     * @return 見つかったユーザー
     */
    public User findByCode(int code) {
        User repUser = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] v = line.split(",");
                int csvCode = Integer.parseInt(v[0]);
                if (v.length != 4) continue;
                if (csvCode != code) continue;
                repUser = new User(csvCode, v[1], v[2], v[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repUser;
    }
}
