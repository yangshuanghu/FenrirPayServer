package com.fenrir.server.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.fenrir.server.model.api.GoodsModel;
import com.fenrir.server.model.api.StatusModel;
import com.fenrir.server.model.api.UserInfo;
import com.fenrir.server.model.db.*;
import com.fenrir.server.util.StringUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yume on 16-4-12.
 */
@Rest
@Path("/admin")
public class AdminResource {

    @POST
    @Path("api_login")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo userLogin(
            @FormParam("name")     String name,
            @FormParam("password") String password
    ) throws Exception {
        UserTable userTable = UserTable.selectByNamePwd(name, password);
        if(userTable == null)
            throw new Exception("账户验证错误");
        if(!userTable.isAdmin())
            throw new Exception("此用户非管理员");

        return UserInfo.of(userTable);
    }

    // ============== 商品管理 ===============
    @POST
    @Path("api_insertGoods")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel insertGoods(
            @FormParam("token") String adminToken,
            @FormParam("name") String name,
            @FormParam("barCode") String barCode,
            @FormParam("salePrice") Float salePrice,
            @FormParam("costPrice") Float costPrice,
            @FormParam("amount") Integer count,
            @FormParam("unit") String unit,
            @FormParam("packageNum") Integer packageNum,
            @FormParam("note") String note,
            @FormParam("className") String className
    ) throws Exception {
        checkAdmin(adminToken);

        GoodsTable goodsTable = new GoodsTable();
        goodsTable.setName(name);
        goodsTable.setBarCode(barCode);
        goodsTable.setSalePrice(salePrice);
        goodsTable.setCostPrice(costPrice);
        goodsTable.setAmount(count);
        goodsTable.setUnit(unit);
        goodsTable.setPackageNum(packageNum);
        goodsTable.setNote(note);
        goodsTable.setClassName(className);
        goodsTable.setCreateDate(Calendar.getInstance().getTime());
        goodsTable.insert();

        return new StatusModel();
    }

    @POST
    @Path("api_saveGoods")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel saveGoods(
            @FormParam("token") String adminToken,
            @FormParam("name") String name,
            @FormParam("barCode") String barCode,
            @FormParam("salePrice") Float salePrice,
            @FormParam("costPrice") Float costPrice,
            @FormParam("amount") Integer count,
            @FormParam("unit") String unit,
            @FormParam("packageNum") Integer packageNum,
            @FormParam("note") String note,
            @FormParam("className") String className
    ) throws Exception {
        checkAdmin(adminToken);

        GoodsTable goodsTable = GoodsTable.selectByBarCode(barCode);
        if(goodsTable == null) {
            goodsTable = new GoodsTable();
            goodsTable.setCreateDate(Calendar.getInstance().getTime());
        } else {
            goodsTable.setUpdateDate(Calendar.getInstance().getTime());
        }

        goodsTable.setName(name);
        goodsTable.setBarCode(barCode);
        goodsTable.setSalePrice(salePrice);
        goodsTable.setCostPrice(costPrice);
        goodsTable.setAmount(count);
        goodsTable.setUnit(unit);
        goodsTable.setPackageNum(packageNum);
        goodsTable.setNote(note);
        goodsTable.setClassName(className);

        goodsTable.save();

        return new StatusModel();
    }

    @POST
    @Path("api_findGoodsItem")
    @Produces(MediaType.APPLICATION_JSON)
    public GoodsModel findGoodsItem(
            @FormParam("barCode") String barCode
    ) throws Exception {
        GoodsTable goodsTable = GoodsTable.selectByBarCode(barCode);
        if(goodsTable == null)
            throw new Exception("商品未找到");

        return GoodsModel.of(goodsTable);
    }

    @POST
    @Path("api_getAllGoods")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GoodsModel> getAllGoods() {
        return GoodsTable.all()
                .stream()
                .map(GoodsModel::of)
                .collect(Collectors.toList());
    }

    @POST
    @Path("api_findByClass")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GoodsModel> findByClass(
            @FormParam("className") String className
    ) {
        List<GoodsTable> list = GoodsTable.selectByClass(className);

        return list.stream()
                .map(GoodsModel::of)
                .collect(Collectors.toList());
    }

    // ============== 用户管理 ===============

    @POST
    @Path("api_alluserinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserInfo> getAllUserInfo(
            @FormParam("token") String adminToken
    ) throws Exception {
        checkAdmin(adminToken);

        List<UserTable> userList = Table.all(UserTable.class);

        return userList.stream()
                .map(UserInfo::of)
                .collect(Collectors.toList());
    }

    @POST
    @Path("api_userallinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public UserTable getUserAllInfo(
            @FormParam("token") String adminToken,
            @FormParam("staffId") Integer staffId
    ) throws Exception {
        checkAdmin(adminToken);

        UserTable userTable = UserTable.selectByStaffId(staffId);
        if(userTable == null)
            throw new Exception("StaffId:" + staffId + " 不存在");

        return userTable;
    }

    @POST
    @Path("api_deleteInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel deleteUserInfo(
            @FormParam("token") String adminToken,
            @FormParam("staffId") Integer staffId
    ) throws Exception {
        checkAdmin(adminToken);

        UserTable userTable = UserTable.selectByStaffId(staffId);
        if(userTable != null)
            userTable.delete();

        return new StatusModel();
    }

    @POST
    @Path("api_register")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel registerStaff(
            @FormParam("token") String adminToken,
            @FormParam("name")     String name,
            @FormParam("password") String password,
            @FormParam("username") String username,
            @FormParam("staffId")  int staffId,
            @FormParam("point")  float point,
            @FormParam("money")  float money
    ) throws Exception {
        checkAdmin(adminToken);

//        if(UserTable.hasStaff(staffId))
//            throw new Exception("StaffId " + staffId + " 已存在。");
        if(StringUtil.isEmpty(name) || StringUtil.isEmpty(password))
            throw new Exception("Name 或者 Password 不能为空。");
        UserTable user = UserTable.selectByStaffId(staffId);
        if(user == null)
            user = UserTable.of(name, password, staffId);

        user.setUsername(username);
        user.setPoint(point);
        user.setMoney(money);

        user.resetToken();
        user.save();

        return new StatusModel();
    }

    // ============== 充值管理 ===============

    @POST
    @Path("api_chargemoney")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel chargeMoney(
            @FormParam("token") String adminToken,
            @FormParam("staffId") Integer staffId,
            @FormParam("charge") Float charge
    ) throws Exception {
        checkAdmin(adminToken);

        UserTable userTable = UserTable.selectByStaffId(staffId);
        if(userTable == null)
            throw new Exception("StaffId:" + staffId + " 不存在");

        userTable.setMoney(userTable.getMoney() + charge);
        userTable.save();

        ChargeHistoryTable historyTable = ChargeHistoryTable.of(staffId, charge, Calendar.getInstance().getTime());
        historyTable.insert();

        return new StatusModel();
    }

    @POST
    @Path("api_chargepoint")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel chargePoint(
            @FormParam("token") String adminToken,
            @FormParam("staffId") Integer staffId,
            @FormParam("chargePoint") Float chargePoint
    ) throws Exception {
        checkAdmin(adminToken);

        UserTable userTable = UserTable.selectByStaffId(staffId);
        if(userTable == null)
            throw new Exception("StaffId:" + staffId + " 不存在");

        userTable.setPoint(userTable.getPoint() + chargePoint);
        userTable.save();

        return new StatusModel();
    }

    private static void checkAdmin(String token) throws Exception {
        if(!checkIsAdmin(token))
            throw new Exception("该用户没有管理权限");
    }

    private static boolean checkIsAdmin(String token) {
        UserTable userTable = UserTable.selectByToken(token);
        return userTable != null && userTable.isAdmin();
    }
}
