package com.fenrir.server.model.db;

import com.fenrir.server.util.StringUtil;
import lombok.Data;

import java.util.*;

import static com.fenrir.server.util.StringUtil.getTokenString;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class HistoryTable implements Table {
    public static final int SHOW_FLAG_SHOW = 0;
    public static final int SHOW_FLAG_NONE = 1;

    private long id;
    private Date payTime;

    private String goodsBarCode;
    private int payCount;
    private int staffId;
    private float spend;

    private String token;
    private int showFlag;

    public static HistoryTable of(String goodsBarCode, int count, int staffId, float spend) {
        HistoryTable table = new HistoryTable();
        table.goodsBarCode = goodsBarCode;
        table.payCount = count;
        table.staffId = staffId;
        table.spend = spend;
        table.payTime = Calendar.getInstance().getTime();
        table.showFlag = SHOW_FLAG_SHOW;

        table.token = getTokenString(
                                table.goodsBarCode,
                                table.payCount,
                                table.staffId,
                                table.spend,
                                table.payTime);

        return table;
    }

    @Override
    public long id() {
        return id;
    }

    public static HistoryTable selectHistoryByToken(String historyToken) {
        return Table.selectSingle(HistoryTable.class,
                Table.eq("token", historyToken));
    }

    public static List<HistoryTable> selectHistoryByStartDate(Date start, long offset, long limit) {
        return selectHistoryByDate(null, start, null, offset, limit);
    }

    public static List<HistoryTable> selectHistoryByEndDate(Date end, long offset, long limit) {
        return selectHistoryByDate(null, null, end, offset, limit);
    }

    public static List<HistoryTable> selectHistoryByDate(Date start, Date end, long offset, long limit) {
        return selectHistoryByDate(null, start, end, offset, limit);
    }

    public static List<HistoryTable> selectHistoryByDate(Integer staffId, Date start, Date end, long offset, long limit) {
        List<WhereEntry> whereEntryList = new ArrayList<>();
        if(staffId != null)
            whereEntryList.add(
                    Table.eq(
                            "staff_id",
                            String.valueOf(staffId)));
        if(start != null)
            whereEntryList.add(
                    Table.greaterThanOrEq(
                            "chargeDate",
                            StringUtil.getDateStringForMySql(start)));
        if(end != null)
            Table.lessThanOrEq(
                    "chargeDate",
                    StringUtil.getDateStringForMySql(end));

        return Table.select(
                HistoryTable.class,
                offset,
                limit,
                whereEntryList.toArray(new WhereEntry[whereEntryList.size()]));
    }
}
