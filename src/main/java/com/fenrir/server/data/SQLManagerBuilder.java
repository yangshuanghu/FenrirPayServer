package com.fenrir.server.data;

import lombok.Getter;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

/**
 * Created by yume on 16-4-12.
 */
public class SQLManagerBuilder {
    @Getter
    private static final String DB_NAME = "goods_db";

    private static SQLManager instance;

    public static SQLManager getMainSqlManager() {
        if(instance == null)
            synchronized (SQLManagerBuilder.class) {
                if(instance == null)
                    instance = getSqlManager();
            }

        return instance;
    }

    public static SQLManager getSqlManager() {
        ConnectionSource source = ConnectionSourceHelper.getSimple(
                "com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/" + DB_NAME,
                "",
                "root",
                "root"
        );

        DBStyle mysql = new MySqlStyle();

        SQLLoader loader = new ClasspathLoader("/sql");

        UnderlinedNameConversion nc = new UnderlinedNameConversion();
        SQLManager sqlManager = new SQLManager(
                mysql,
                loader,
                source,
                nc,
                new Interceptor[]{new DebugInterceptor()});

        return sqlManager;
    }
}
