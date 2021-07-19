package com.database.management.system;

public interface ExecutionData {
    void execute(String query);
    boolean validateQuery(String query);
}
