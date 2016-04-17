package com.fenrir.server.model.api;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class HistoryModel {
    private List<HistoryEntry> history;
    private long count;

    @Data
    public static class HistoryEntry {
        private Date time;
        private String name;
        private float payCount;
        private float spend;
        private String historyToken;
        private List<GoodsModel> payGoods;
    }
}
