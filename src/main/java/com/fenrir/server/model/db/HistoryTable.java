package com.fenrir.server.model.db;

import com.fenrir.server.util.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class HistoryTable implements Table {
    private long id;
    private Date time;
    private String goodsBarCode;
    private float count;
    private String userToken;
    private float spend;

    @Override
    public long id() {
        return id;
    }

    public static List<HistoryTable> selectHistoryByStartDate(Date start) {
        return selectHistoryByDate(null, start, null);
    }

    public static List<HistoryTable> selectHistoryByEndDate(Date end) {
        return selectHistoryByDate(null, null, end);
    }

    public static List<HistoryTable> selectHistoryByDate(Date start, Date end) {
        return selectHistoryByDate(null, start, end);
    }

    public static List<HistoryTable> selectHistoryByDate(String userToken, Date start, Date end) {
        List<WhereEntry> whereEntryList = new ArrayList<>();
        if(!StringUtil.isEmpty(userToken))
            whereEntryList.add(
                    Table.eq(
                            "userToken",
                            StringUtil.getDateStringForMySql(start)));
        if(start != null)
            whereEntryList.add(
                    Table.greaterThanOrEq(
                            "time",
                            StringUtil.getDateStringForMySql(start)));
        if(end != null)
            Table.lessThanOrEq(
                    "time",
                    StringUtil.getDateStringForMySql(end));

        return Table.select(
                HistoryTable.class,
                whereEntryList.toArray(new WhereEntry[whereEntryList.size()]));
    }
}
