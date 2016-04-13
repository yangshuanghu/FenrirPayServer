package com.fenrir.server.model.db;

import com.fenrir.server.util.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yume on 16-4-13.
 */
@Data
public class ChargeHistoryTable implements Table {
    private long id;
    private int staffId;
    private float charge;
    private Date time;

    public static ChargeHistoryTable of(int staffId, float charge, Date time) {
        ChargeHistoryTable chargeHistoryTable = new ChargeHistoryTable();
        chargeHistoryTable.staffId = staffId;
        chargeHistoryTable.charge = charge;
        chargeHistoryTable.time = time;

        return chargeHistoryTable;
    }

    @Override
    public long id() {
        return id;
    }

    public static List<ChargeHistoryTable> selectHistoryByStartDate(Date start) {
        return selectHistoryByDate(null, start, null);
    }

    public static List<ChargeHistoryTable> selectHistoryByEndDate(Date end) {
        return selectHistoryByDate(null, null, end);
    }

    public static List<ChargeHistoryTable> selectHistoryByDate(Date start, Date end) {
        return selectHistoryByDate(null, start, end);
    }

    public static List<ChargeHistoryTable> selectHistoryByDate(Integer staffId, Date start, Date end) {
        List<WhereEntry> whereEntryList = new ArrayList<>();
        if(staffId != null)
            whereEntryList.add(
                    Table.eq(
                            "staff_id",
                            String.valueOf(staffId)));
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
                ChargeHistoryTable.class,
                whereEntryList.toArray(new WhereEntry[whereEntryList.size()]));
    }
}
