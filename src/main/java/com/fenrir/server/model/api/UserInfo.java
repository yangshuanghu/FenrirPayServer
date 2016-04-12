package com.fenrir.server.model.api;

import com.fenrir.server.model.db.UserTable;
import lombok.Data;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class UserInfo {
    private String name;
    private String username;
    private int staffId;
    private String token;

    public static UserInfo of(UserTable userTable) {
        UserInfo userInfo = new UserInfo();
        userInfo.name = userTable.getName();
        userInfo.username = userTable.getUsername();
        userInfo.staffId = userTable.getStaffId();
        userInfo.token = userTable.getToken();

        return userInfo;
    }
}
