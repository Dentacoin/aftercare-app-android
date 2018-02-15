package com.dentacoin.dentacare.network.response;

import com.dentacoin.dentacare.model.DCRecord;

/**
 * Created by Atanas Chervarov on 10/7/17.
 */

public class DCRecordsSyncResponse {
    private DCRecord[] success;
    private DCFailedActivityRecord[] failed;

    public DCRecord[] getSuccess() {
        return success;
    }

    public DCFailedActivityRecord[] getFailed() {
        return failed;
    }
}
