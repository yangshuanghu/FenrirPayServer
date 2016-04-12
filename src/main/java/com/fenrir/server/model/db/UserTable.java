package com.fenrir.server.model.db;

import com.fenrir.server.util.StringUtil;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class UserTable implements Table {
    static final String SQL_SELECT_BY_ID = "SELECT * FROM %s.%s WHERE id = %d;";

    private long id;
    private String name;
    private String password;
    private String username;
    private int staffId;
    private String token;

    @Override
    public long id() {
        return id;
    }

    public static boolean hasStaff(int staffId) {
        Map<String, Object> map = new HashMap<>();
        map.put("staff_id", staffId);
        return Table.select(UserTable.class, map).size() > 0;
    }

    public static UserTable selectByStaffId(String staffId) {
        if(StringUtil.isEmpty(staffId))
            return null;

        return Table.selectSingle(UserTable.class, map -> map.put("staff_id", staffId));
    }

    public static UserTable selectByToken(String token) {
        if(StringUtil.isEmpty(token))
            return null;

        return Table.selectSingle(UserTable.class, map -> map.put("token", token));
    }

    public static UserTable selectByNamePwdStaffId(String name, String password, int staffId) {
        return Table.selectSingle(UserTable.class,
                map -> {
                    map.put("name", name);
                    map.put("password", password);
                    map.put("staff_id", staffId);
                });
    }

    public static UserTable selectByNamePwd(String name, String password) {
        return Table.selectSingle(UserTable.class,
                map -> {
                    map.put("name", name);
                    map.put("password", password);
                });
    }

    public static UserTable of(String name, String password, int staffId) {
        UserTable userTable = new UserTable();
        userTable.setName(name);
        userTable.setPassword(password);
        userTable.setStaffId(staffId);
        userTable.setToken(getToken(name, password, staffId));

        return userTable;
    }

    private static String getToken(String name, String password, int staffId) {
        return StringUtil.getMD5String(name + ":" + password + " : " + staffId);
    }
}
