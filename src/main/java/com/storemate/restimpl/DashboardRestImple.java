package com.storemate.restimpl;

import com.storemate.constants.StoreMateConstants;
import com.storemate.rest.DashboardRest;
import com.storemate.service.DashboardService;
import com.storemate.utils.StoreMateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardRestImple implements DashboardRest {

    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getDashboard() {

            return dashboardService.getDashboard();
    }
}
