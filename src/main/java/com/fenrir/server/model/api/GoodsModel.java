package com.fenrir.server.model.api;

import com.fenrir.server.model.db.GoodsTable;
import lombok.Data;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class GoodsModel {
    private String name;
    private String barCode;
    private Float salePrice;
    private Float costPrice;
    private Integer count;
    private String unit;
    private Integer packageNum;
    private String note;
    private String className;

    public static GoodsModel of(GoodsTable goodsTable) {
        GoodsModel goodsModel = new GoodsModel();

        goodsModel.name = goodsTable.getName();
        goodsModel.barCode = goodsTable.getBarCode();
        goodsModel.salePrice = goodsTable.getSalePrice();
        goodsModel.costPrice = goodsTable.getCostPrice();
        goodsModel.count = goodsTable.getCount();
        goodsModel.unit = goodsTable.getUnit();
        goodsModel.packageNum = goodsTable.getPackageNum();
        goodsModel.note = goodsTable.getNote();
        goodsModel.className = goodsTable.getClassName();

        return goodsModel;
    }
}
