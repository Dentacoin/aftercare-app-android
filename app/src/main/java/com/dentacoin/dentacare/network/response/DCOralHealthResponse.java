package com.dentacoin.dentacare.network.response;

import com.dentacoin.dentacare.model.DCOralHealthItem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Atanas Chervarov on 10/1/17.
 */

public class DCOralHealthResponse {

    private DCOralHealthItem[] array;

    public ArrayList<DCOralHealthItem> getArray() {
        ArrayList<DCOralHealthItem> items = new ArrayList<>();
        if (array != null) {
            items.addAll(Arrays.asList(array));
        }
        return items;
    }
}
