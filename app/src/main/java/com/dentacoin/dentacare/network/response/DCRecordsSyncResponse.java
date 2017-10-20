package com.dentacoin.dentacare.network.response;

import com.dentacoin.dentacare.model.DCActivityRecord;

/**
 * Created by Atanas Chervarov on 10/7/17.
 */

public class DCRecordsSyncResponse {
    private DCActivityRecord[] success;
    private DCFailedActivityRecord[] failed;

    public DCActivityRecord[] getSuccess() {
        return success;
    }

    public DCFailedActivityRecord[] getFailed() {
        return failed;
    }
}
