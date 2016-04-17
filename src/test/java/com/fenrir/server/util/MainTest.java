package com.fenrir.server.util;

import com.fenrir.server.model.db.HistoryTable;
import org.junit.Test;

/**
 * Created by yume on 16-4-14.
 */
public class MainTest {
    @Test
    public void testToken() {
        HistoryTable table;
        for(int i = 0; i < 10; i++) {
            table = HistoryTable.of("asdascaw", 2, 3, 2.4f);
            System.out.println(table.toString());
        }
    }
}
