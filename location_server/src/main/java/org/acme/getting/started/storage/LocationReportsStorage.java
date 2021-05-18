package org.acme.getting.started.storage;

import org.acme.getting.started.model.LocationReport;

import java.util.concurrent.ConcurrentHashMap;

public class LocationReportsStorage {
    public static ConcurrentHashMap<Integer, LocationReport> locationReports = new ConcurrentHashMap<>();
}
