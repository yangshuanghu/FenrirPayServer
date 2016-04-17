package com.fenrir.server.model.db;

import com.fenrir.server.util.StringUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class UserTable implements Table {
    static final String SQL_SELECT_BY_ID = "SELECT * FROM %s.%s WHERE id = %d;";

    public static final int PERMISSION_STAFF = 1;
    public static final int PERMISSION_ADMIN = 2;

    private long id;
    private String name;
    private String password;
    private String username;
    private int staffId;
    private String token;
    private float point;
    private float money;

    private int permission = PERMISSION_STAFF;

    @Override
    public long id() {
        return id;
    }

    public void password(String originPwd) {
        setPassword(genPassword(originPwd));
    }

    public static String genPassword(String originPwd) {
        return StringUtil.getMD5String(originPwd);
    }

    public void resetToken() {
        token = StringUtil.getTokenString(name, password, staffId);
    }

    public boolean isAdmin() {
        return permission == PERMISSION_ADMIN;
    }

    public static boolean isAdmin(int staffId) {
        UserTable userTable = selectByStaffId(staffId);
        return userTable != null && userTable.isAdmin();
    }

    public static boolean hasStaff(int staffId) {
        Map<String, Object> map = new HashMap<>();
        map.put("staff_id", staffId);
        return Table.select(UserTable.class, map).size() > 0;
    }

    public static UserTable selectByStaffId(int staffId) {
        if(staffId == 0)
            return null;

        return Table.selectSingle(UserTable.class, map -> map.put("staff_id", staffId));
    }

    public static UserTable selectByToken(String token) {
        if(StringUtil.isEmpty(token))
            return null;

        return Table.selectSingle(UserTable.class, map -> map.put("token", token));
    }

    public static UserTable selectByNamePwdStaffId(String name, String originPwd, int staffId) {
        return Table.selectSingle(UserTable.class,
                map -> {
                    map.put("name", name);
                    map.put("password", genPassword(originPwd));
                    map.put("staff_id", staffId);
                });
    }

    public static UserTable selectByNamePwd(String name, String originPwd) {
        return Table.selectSingle(UserTable.class,
                map -> {
                    map.put("name", name);
                    map.put("password", genPassword(originPwd));
                });
    }

    public static UserTable of(String name, String originPwd, int staffId) {
        UserTable userTable = new UserTable();
        userTable.setName(name);
        userTable.password(originPwd);
        userTable.setStaffId(staffId);
        userTable.resetToken();

        return userTable;
    }
}
