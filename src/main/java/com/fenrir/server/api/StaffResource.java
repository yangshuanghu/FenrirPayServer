package com.fenrir.server.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.fenrir.server.model.api.*;
import com.fenrir.server.model.db.*;
import com.fenrir.server.util.StringUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yume on 16-4-12.
 */
@Rest
@Path("/staff")
//@Api(value = "/staff", description = "This is test api")
public class StaffResource {

//    @GET
//    @Path("api_testInertGoods")
//    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel test() throws Exception {
//        GoodsTable goodsTable = new GoodsTable();
//        System.out.println(new String("笔记本（广博）".getBytes("utf8"), Charset.forName("utf8")));
//        goodsTable.setName("笔记本（广博）");
//        goodsTable.setBarCode("6922711027944");
//        goodsTable.setSalePrice(3.0f);
//        goodsTable.setCostPrice(7f);
//        goodsTable.setAmount(8);
//        goodsTable.setUnit("本");
//        goodsTable.setPackageNum(1);
//        goodsTable.setNote("淡黄色的子页，很适合用来做笔记。");
//        goodsTable.setClassName("办公用品");
//        goodsTable.setCreateDate(Calendar.getInstance().getTime());
//        goodsTable.insert();
//
//        return new StatusModel();

        UserTable userTable = new UserTable();
        userTable.setToken("123456");
        userTable.setUsername("测试管理员");
        userTable.setName("AdminTest");
        userTable.setPermission(UserTable.PERMISSION_ADMIN);
        userTable.setPassword("123456");
        userTable.setStaffId(999);
        userTable.setMoney(0);
        userTable.setPoint(0);

        userTable.save();

        return new StatusModel();

//        throw new ServerErrorException("商品未找到", Response.Status.NO_CONTENT);
//        return GoodsTable.selectByBarCode("6922711027944");
    }

    // =========== 用户管理 =============
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

        return UserInfo.of(userTable);
    }

    @POST
    @Path("api_userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo getUserInfo(
            @FormParam("token")     String token
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if(userTable == null)
            throw new Exception("用户未找到");

        return UserInfo.of(userTable);
    }

    @POST
    @Path("api_modifyUserInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel modifyUserInfo(
            @FormParam("name")     String name,
            @FormParam("password") String password,
            @FormParam("newpassword") String newPassword
    ) throws Exception {
        UserTable userTable = UserTable.selectByNamePwd(name, password);
        if(userTable == null)
            throw new Exception("账户验证错误");
        userTable.password(newPassword);

        return new StatusModel();
    }

    // =========== 商品查询 =============

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

    // =========== 购买历史查询 =============

    @POST
    @Path("api_getPayHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public HistoryModel getPayHistory(
            @FormParam("token") String token,
            @FormParam("start") String start, // yyyy-mm-dd hh:mm
            @FormParam("end") String end,      // yyyy-mm-dd hh:mm
            @FormParam("offset") long offset,
            @FormParam("limit") long limit
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if(userTable == null)
            throw new Exception("用户未找到");

        Date startDate = StringUtil.getDateFromString(start);
        Date endDate = StringUtil.getDateFromString(end);

        List<HistoryModel.HistoryEntry> entryList = new ArrayList<>();

        List<HistoryTable> list = HistoryTable.selectHistoryByDate(userTable.getStaffId(), startDate, endDate, offset, limit);
        for(HistoryTable history : list) {
            GoodsTable goodsTable = GoodsTable.selectByBarCode(history.getGoodsBarCode());
            if(goodsTable == null) {
                goodsTable = new GoodsTable();
            }
            HistoryModel.HistoryEntry entry = new HistoryModel.HistoryEntry();
            entry.setTime(history.getPayTime());
            entry.setSpend(history.getSpend());
            entry.setName(goodsTable.getName());
            entry.setPayCount(history.getSpend());
            entry.setHistoryToken(history.getToken());

            List<GoodsModel> goodsList = new ArrayList<>();
            goodsList.add(GoodsModel.of(goodsTable));
            entry.setPayGoods(goodsList);

            entryList.add(entry);
        }

        HistoryModel historyModel = new HistoryModel();
        historyModel.setHistory(entryList);
        historyModel.setCount(Table.count(HistoryTable.class, new HashMap()));

        return historyModel;
    }

    @POST
    @Path("api_deletePayHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel deletePayHistory(
            @FormParam("token") String token,
            @FormParam("historyToken") String historyToken
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if (userTable == null)
            throw new Exception("用户未找到");

        HistoryTable table = HistoryTable.selectHistoryByToken(historyToken);
        if(table == null || userTable.getStaffId() != table.getStaffId())
            throw new Exception("购买记录未找到");

        return new StatusModel();
    }

    // =========== 充值历史查询 =============

    @POST
    @Path("api_getChargeHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public ChargeModel getChargeHistory(
            @FormParam("token") String token,
            @FormParam("start") String start, // yyyy-mm-dd hh:mm
            @FormParam("end") String end,      // yyyy-mm-dd hh:mm
            @FormParam("offset") Long offset,
            @FormParam("limit") Long limit
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if(userTable == null)
            throw new Exception("用户未找到");

        Date startDate = StringUtil.getDateFromString(start);
        Date endDate = StringUtil.getDateFromString(end);

        List<ChargeModel.ChargeEntry> entryList = new ArrayList<>();

        List<ChargeHistoryTable> list = ChargeHistoryTable.selectHistoryByDate(userTable.getStaffId(), startDate, endDate, offset, limit);
        for(ChargeHistoryTable history : list)
            entryList.add(ChargeModel.ChargeEntry.of(history));

        ChargeModel chargeModel = new ChargeModel();
        chargeModel.setChargeHistory(entryList);

        return chargeModel;
    }

    @POST
    @Path("api_deleteChargeHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel deleteChargeHistory(
            @FormParam("token") String token,
            @FormParam("chargeToken") String chargeToken
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if (userTable == null)
            throw new Exception("用户未找到");

        ChargeHistoryTable table = ChargeHistoryTable.selectHistoryByToken(chargeToken);
        if(table == null || userTable.getStaffId() != table.getStaffId())
            throw new Exception("充值记录未找到");

        return new StatusModel();
    }

    // =========== 购买 =============

    @POST
    @Path("api_payGoods")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel payGoods(
            @FormParam("token") String token,
            @FormParam("barCode") String barCode,
            @FormParam("amount") int count
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if(userTable == null)
            throw new Exception("Token 有误。");

        GoodsTable goodsTable = GoodsTable.selectByBarCode(barCode);
        if(goodsTable == null)
            throw new Exception("barCode 有误。");

        if(goodsTable.getAmount() < count)
            throw new Exception("商品数量不够。");

        goodsTable.setAmount(goodsTable.getAmount() - count);
        goodsTable.update();

        HistoryTable historyTable = HistoryTable.of(
                barCode,
                count,
                userTable.getStaffId(),
                count * goodsTable.getSalePrice());
        historyTable.insert();

        userTable.setMoney(userTable.getMoney() - count * goodsTable.getSalePrice());
        userTable.save();

        return new StatusModel();
    }
}
