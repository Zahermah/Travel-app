package com.zahergoesapp.travelappilka.Modules;

import java.util.List;

/**
 * Created by Zaher on 2016-12-12.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
