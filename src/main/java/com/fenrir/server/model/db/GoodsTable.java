package com.fenrir.server.model.db;

import com.fenrir.server.util.StringUtil;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class GoodsTable implements Table {
    private long id;
    private String name;
    private String barCode;
    private Float salePrice;
    private Float costPrice;
    private Integer count;
    private String unit;
    private Integer packageNum;
    private String note;
    private String className;
    private Date createDate;
    private Date updateDate;

    @Override
    public long id() {
        return id;
    }

    public static GoodsTable selectByBarCode(String barCode) {
        if(StringUtil.isEmpty(barCode))
            return null;

        return Table.selectSingle(GoodsTable.class, map -> map.put("bar_code", barCode));
    }

    public static List<GoodsTable> all() {
        return sqlManager.all(GoodsTable.class);
    }

    public static List<GoodsTable> selectByClass(String className) {
        if(StringUtil.isEmpty(className))
            return new ArrayList<>();

        return Table.select(GoodsTable.class, map -> map.put("class_name", className));
    }
}
