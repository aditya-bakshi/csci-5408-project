package com.database.management.system;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ExecutionData {
    void execute(String query) throws FileNotFoundException, IOException;
    boolean validateQuery(String query) throws FileNotFoundException;
}
