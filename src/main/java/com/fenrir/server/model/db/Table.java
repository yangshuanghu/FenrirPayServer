package com.fenrir.server.model.db;

import com.fenrir.server.data.SQLManagerBuilder;
import org.beetl.sql.core.SQLManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static javafx.scene.input.KeyCode.T;

/**
 * Created by yume on 16-4-12.
 */
public interface Table {
    static final SQLManager sqlManager = SQLManagerBuilder.getMainSqlManager();

    static final String SQL_SELECT = "SELECT * FROM %s.%s %s;";

    long id();

    default void save() {
        if(exists())
            update();
        else
            insert();
    }

    default void delete() {
        sqlManager.deleteById(this.getClass(), id());
    }

    default void update() {
        sqlManager.updateById(this);
    }

    default void insert() {
        sqlManager.insert(this);
    }

    default boolean exists() {
        return id() > 0
                && selectSingle(this.getClass(), eq("id", String.valueOf(id()))) != null;
    }

    default String tableName() {
        return tableName(this.getClass());
    }

    static <T extends Table> List<T> all(Class<T> tableClazz) {
        return sqlManager.all(tableClazz);
    }

    static <T extends Table> T selectSingle(Class<T> tableClazz,
                                            WhereEntry... paramList) {
        List<T> list = Table.select(tableClazz, paramList);

        return list.size() == 0 ? null : list.get(0);
    }

    static <T extends Table> List<T> select(Class<T> tableClazz,
                                            WhereEntry... paramList) {
        return select(tableClazz, 0, 0, paramList);
    }

    static <T extends Table> List<T> select(Class<T> tableClazz,
                                            long offset,
                                            long limit,
                                            WhereEntry... paramList) {
        String where = "";
        if(paramList != null && paramList.length != 0) {
            StringBuilder paramString = new StringBuilder("WHERE ");
            for(WhereEntry entry : paramList)
                paramString.append(entry.toString())
                        .append(" ");
            where = paramString.toString();
        }

        String sql = String.format(SQL_SELECT, SQLManagerBuilder.getDB_NAME(), tableName(tableClazz), where);

        if(offset == 0 && limit == 0)
            return sqlManager.execute(sql, tableClazz, null);
        else
            return sqlManager.execute(sql, tableClazz, null, offset, limit);
    }

    static <T extends Table> T selectSingle(Class<T> tableClazz,
                                            Consumer<Map<String, Object>> paramFun) {
        List<T> list = Table.select(tableClazz, paramFun);

        return list.size() == 0 ? null : list.get(0);
    }

    static <T extends Table> List<T> select(Class<T> tableClazz,
                                            Consumer<Map<String, Object>> paramFun) {
        return select(tableClazz, paramFun, 0, 0);
    }

    static <T extends Table> List<T> select(Class<T> tableClazz,
                                            Consumer<Map<String, Object>> paramFun,
                                            long offset,
                                            long limit) {
        Map<String, Object> map = new HashMap<>();
        paramFun.accept(map);
        return select(tableClazz, map, offset, limit);
    }

    static <T extends Table> List<T> select(Class<T> tableClazz, Map<String, Object> params) {
        return select(tableClazz, params);
    }

    static <T extends Table> List<T> select(Class<T> tableClazz, Map<String, Object> params, long offset, long limit) {
        String where = "";
        if(params != null && params.size() != 0) {
            StringBuilder paramString = new StringBuilder("WHERE ");
            for(Map.Entry<String, Object> entry : params.entrySet())
                paramString.append(entry.getKey())
                        .append(" = ")
                        .append("\"")
                        .append(entry.getValue())
                        .append("\" ");
            where = paramString.toString();
        }

        String sql = String.format(SQL_SELECT, SQLManagerBuilder.getDB_NAME(), tableName(tableClazz), where);

        if(offset == 0 && limit == 0)
            return sqlManager.execute(sql, tableClazz, null);
        else
            return sqlManager.execute(sql, tableClazz, null, offset, limit);
    }

    static String tableName(Class<? extends Table> clazz) {
        return sqlManager.getNc().getTableName(clazz);
    }

    static long count(Class<? extends Table> tableClazz) {
        return sqlManager.allCount(tableClazz);
    }

    static long count(Class<? extends Table> tableClazz, Map<String, Object> params) {
        return sqlManager.longValue("select amount(1) from goods_db.history_table", null);
    }

    static <T> T selectById(Class<T> tableClazz, long id) {
        return sqlManager.unique(tableClazz, id);
    }

    static WhereEntry greaterThan(String key, String value) {
        return WhereEntry.of(key, WhereEntry.greaterThan, value);
    }

    static WhereEntry greaterThanOrEq(String key, String value) {
        return WhereEntry.of(key, WhereEntry.greaterThanOrEq, value);
    }

    static WhereEntry lessThan(String key, String value) {
        return WhereEntry.of(key, WhereEntry.lessThan, value);
    }

    static WhereEntry lessThanOrEq(String key, String value) {
        return WhereEntry.of(key, WhereEntry.lessThanOrEq, value);
    }

    static WhereEntry eq(String key, String value) {
        return WhereEntry.of(key, WhereEntry.eq, value);
    }

    static WhereEntry notEq(String key, String value) {
        return WhereEntry.of(key, WhereEntry.notEq, value);
    }

    public static class WhereEntry{
        public static final String greaterThan     = ">";
        public static final String greaterThanOrEq = ">=";
        public static final String lessThan        = "<";
        public static final String lessThanOrEq    = "<=";
        public static final String eq              = "=";
        public static final String notEq           = "<>";

        private String key;
        private String symbol;
        private String value;

        private WhereEntry(){}

        private static WhereEntry of(String key, String symbol, String value) {
            WhereEntry whereEntry = new WhereEntry();
            whereEntry.key = key;
            whereEntry.symbol = symbol;
            whereEntry.value = value;

            return whereEntry;
        }

        public String toString() {
            return String.format("%s %s \"%s\"", key, symbol, value);
        }
    }
}
