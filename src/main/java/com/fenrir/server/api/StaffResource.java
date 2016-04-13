package com.fenrir.server.api;

import com.aol.micro.server.MicroserverApp;
import com.aol.micro.server.auto.discovery.Rest;
import com.fenrir.server.data.SQLManagerBuilder;
import com.fenrir.server.model.api.*;
import com.fenrir.server.model.db.*;
import com.fenrir.server.util.StringUtil;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.beetl.sql.core.SQLManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yume on 16-4-12.
 */
@Rest
@Path("/staff")
//@Api(value = "/staff", description = "This is test api")
public class StaffResource {

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
            @FormParam("end") String end      // yyyy-mm-dd hh:mm
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if(userTable == null)
            throw new Exception("用户未找到");

        Date startDate = StringUtil.getDateFromString(start);
        Date endDate = StringUtil.getDateFromString(end);

        List<HistoryModel.HistoryEntry> entryList = new ArrayList<>();

        List<HistoryTable> list = HistoryTable.selectHistoryByDate(userTable.getStaffId(), startDate, endDate);
        for(HistoryTable history : list) {
            GoodsTable goodsTable = GoodsTable.selectByBarCode(history.getGoodsBarCode());
            if(goodsTable == null) {
                goodsTable = new GoodsTable();
            }
            HistoryModel.HistoryEntry entry = new HistoryModel.HistoryEntry();
            entry.setTime(history.getTime());
            entry.setSpend(history.getSpend());
            entry.setName(goodsTable.getName());
            entry.setPayCount(history.getSpend());

            List<GoodsModel> goodsList = new ArrayList<>();
            goodsList.add(GoodsModel.of(goodsTable));
            entry.setPayGoods(goodsList);

            entryList.add(entry);
        }

        HistoryModel historyModel = new HistoryModel();
        historyModel.setHistory(entryList);

        return historyModel;
    }

    // =========== 充值历史查询 =============

    @POST
    @Path("api_getChargeHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public ChargeModel getChargeHistory(
            @FormParam("token") String token,
            @FormParam("start") String start, // yyyy-mm-dd hh:mm
            @FormParam("end") String end      // yyyy-mm-dd hh:mm
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if(userTable == null)
            throw new Exception("用户未找到");

        Date startDate = StringUtil.getDateFromString(start);
        Date endDate = StringUtil.getDateFromString(end);

        List<ChargeModel.ChargeEntry> entryList = new ArrayList<>();

        List<ChargeHistoryTable> list = ChargeHistoryTable.selectHistoryByDate(userTable.getStaffId(), startDate, endDate);
        for(ChargeHistoryTable history : list)
            entryList.add(ChargeModel.ChargeEntry.of(history));

        ChargeModel chargeModel = new ChargeModel();
        chargeModel.setChargeHistory(entryList);

        return chargeModel;
    }

    // =========== 购买 =============

    @POST
    @Path("api_payGoods")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel payGoods(
            @FormParam("token") String token,
            @FormParam("barCode") String barCode,
            @FormParam("count") float count
    ) throws Exception {
        UserTable userTable = UserTable.selectByToken(token);
        if(userTable == null)
            throw new Exception("Token 有误。");

        GoodsTable goodsTable = GoodsTable.selectByBarCode(barCode);
        if(goodsTable == null)
            throw new Exception("barCode 有误。");

        if(goodsTable.getCount() < count)
            throw new Exception("商品数量不够。");

        goodsTable.setCount(goodsTable.getCount() - count);
        goodsTable.update();

        HistoryTable historyTable = new HistoryTable();
        historyTable.setStaffId(userTable.getStaffId());
        historyTable.setGoodsBarCode(barCode);
        historyTable.setCount(count);
        historyTable.setSpend(count * goodsTable.getSalePrice());
        historyTable.setTime(Calendar.getInstance().getTime());
        historyTable.insert();

        userTable.setMoney(userTable.getMoney() - count * goodsTable.getSalePrice());
        userTable.save();

        return new StatusModel();
    }
}
