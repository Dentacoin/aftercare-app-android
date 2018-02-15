package com.dentacoin.dentacare.network.response;

import com.dentacoin.dentacare.model.DCRecord;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Atanas Chervarov on 9/27/17.
 */

public class DCActivityRecordsResponse {
    private DCRecord[] array;

    public ArrayList<DCRecord> getArray() {
        ArrayList<DCRecord> records = new ArrayList<>();
        if (array != null) {
            records.addAll(Arrays.asList(array));
        }
        return records;
    }
}
