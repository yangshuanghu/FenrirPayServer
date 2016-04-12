package com.fenrir.server.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.fenrir.server.model.api.StatusModel;
import com.fenrir.server.model.api.UserInfo;
import com.fenrir.server.model.db.GoodsTable;
import com.fenrir.server.model.db.HistoryTable;
import com.fenrir.server.model.db.Table;
import com.fenrir.server.model.db.UserTable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yume on 16-4-12.
 */
@Rest
@Path("/staff")
public class AdminResource {
    @POST
    @Path("api_insertGoods")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel insertGoods(
            @FormParam("name") String name,
            @FormParam("barCode") String barCode,
            @FormParam("salePrice") Float salePrice,
            @FormParam("costPrice") Float costPrice,
            @FormParam("count") Float count,
            @FormParam("unit") String unit,
            @FormParam("packageNum") Float packageNum,
            @FormParam("note") String note,
            @FormParam("className") String className
    ) {
        GoodsTable goodsTable = new GoodsTable();
        goodsTable.setName(name);
        goodsTable.setBarCode(barCode);
        goodsTable.setSalePrice(salePrice);
        goodsTable.setCostPrice(costPrice);
        goodsTable.setCount(count);
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
            @FormParam("name") String name,
            @FormParam("barCode") String barCode,
            @FormParam("salePrice") Float salePrice,
            @FormParam("costPrice") Float costPrice,
            @FormParam("count") Float count,
            @FormParam("unit") String unit,
            @FormParam("packageNum") Float packageNum,
            @FormParam("note") String note,
            @FormParam("className") String className
    ) throws Exception {
        GoodsTable goodsTable = GoodsTable.selectByBarCode(barCode);
        if(goodsTable == null) {
            goodsTable = new GoodsTable();
            goodsTable.setUpdateDate(Calendar.getInstance().getTime());
        }

        goodsTable.setName(name);
        goodsTable.setBarCode(barCode);
        goodsTable.setSalePrice(salePrice);
        goodsTable.setCostPrice(costPrice);
        goodsTable.setCount(count);
        goodsTable.setUnit(unit);
        goodsTable.setPackageNum(packageNum);
        goodsTable.setNote(note);
        goodsTable.setClassName(className);

        goodsTable.save();

        return new StatusModel();
    }

    @POST
    @Path("api_alluserinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserInfo> getAllUserInfo() throws Exception {
        List<UserTable> userList = Table.all(UserTable.class);

        return userList.stream()
                .map(UserInfo::of)
                .collect(Collectors.toList());
    }

    @POST
    @Path("api_deleteInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusModel deleteUserInfo(
            @FormParam("staffId") String staffId
    ) throws Exception {
        UserTable userTable = UserTable.selectByStaffId(staffId);
        if(userTable != null)
            userTable.delete();

        return new StatusModel();
    }
}
