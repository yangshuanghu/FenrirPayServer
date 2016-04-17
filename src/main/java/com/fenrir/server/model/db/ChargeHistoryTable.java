package com.fenrir.server.model.db;

import com.fenrir.server.util.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fenrir.server.util.StringUtil.getTokenString;

/**
 * Created by yume on 16-4-13.
 */
@Data
public class ChargeHistoryTable implements Table {
    public static final int SHOW_FLAG_SHOW = 0;
    public static final int SHOW_FLAG_NONE = 1;

    private long id;
    private int staffId;
    private float charge;
    private Date chargeDate;
    private String token;
    private int showFlag;

    public static ChargeHistoryTable of(int staffId, float charge, Date time) {
        ChargeHistoryTable chargeHistoryTable = new ChargeHistoryTable();
        chargeHistoryTable.staffId = staffId;
        chargeHistoryTable.charge = charge;
        chargeHistoryTable.chargeDate = time;
        chargeHistoryTable.showFlag = SHOW_FLAG_SHOW;

        chargeHistoryTable.token = getTokenString(
                chargeHistoryTable.staffId,
                chargeHistoryTable.charge,
                chargeHistoryTable.chargeDate);

        return chargeHistoryTable;
    }

    @Override
    public long id() {
        return id;
    }

    public static ChargeHistoryTable selectHistoryByToken(String historyToken) {
        return Table.selectSingle(ChargeHistoryTable.class,
                Table.eq("token", historyToken));
    }

    public static List<ChargeHistoryTable> selectHistoryByStartDate(Date start, long offset, long limit) {
        return selectHistoryByDate(null, start, null, offset, limit);
    }

    public static List<ChargeHistoryTable> selectHistoryByEndDate(Date end, long offset, long limit) {
        return selectHistoryByDate(null, null, end, offset, limit);
    }

    public static List<ChargeHistoryTable> selectHistoryByDate(Date start, Date end, long offset, long limit) {
        return selectHistoryByDate(null, start, end, offset, limit);
    }

    public static List<ChargeHistoryTable> selectHistoryByDate(Integer staffId, Date start, Date end, long offset, long limit) {
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
                ChargeHistoryTable.class,
                offset,
                limit,
                whereEntryList.toArray(new WhereEntry[whereEntryList.size()]));
    }
}
