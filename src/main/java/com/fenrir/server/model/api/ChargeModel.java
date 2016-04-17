package com.fenrir.server.model.api;

import com.fenrir.server.model.db.ChargeHistoryTable;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by yume on 16-4-13.
 */
@Data
public class ChargeModel {
    private List<ChargeEntry> chargeHistory;

    @Data
    public static class ChargeEntry {
        private float charge;
        private Date time;
        private String chargeToken;

        public static ChargeEntry of(ChargeHistoryTable chargeHistoryTable) {
            ChargeEntry chargeEntry = new ChargeEntry();
            chargeEntry.charge = chargeHistoryTable.getCharge();
            chargeEntry.time = chargeHistoryTable.getChargeDate();
            chargeEntry.chargeToken = chargeHistoryTable.getToken();

            return chargeEntry;
        }
    }
}
