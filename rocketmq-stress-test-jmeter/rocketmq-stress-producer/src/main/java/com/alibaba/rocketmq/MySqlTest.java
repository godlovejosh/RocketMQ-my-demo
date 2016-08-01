package com.alibaba.rocketmq;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wuxing on 16/7/26.
 */
public class MySqlTest {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // 加载mysql驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            // 连接localhost上的mysql,并指定使用test数据库，用户名为root,密码为***
            conn = DriverManager.getConnection("jdbc:mysql://114.55.108.105/diamond",
                    "admin", "athene.admin");
            if (!conn.isClosed()) {
                System.out.println("数据库连接成功！"); //验证是否连接成功
            }

            Statement statement = conn.createStatement();
            //查询数据
            ResultSet rs = statement.executeQuery("select * from config_info limit 10");

            //输出结果集
            while (rs.next()) {
//                System.out.println("id=" + rs.getInt("Id") + ",name=" +
//                        rs.getString("Name"));
                System.out.println("id=" + rs.getInt("Id"));
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
