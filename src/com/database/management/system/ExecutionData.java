package com.database.management.system;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * @author Aditya
 *
 */
public interface ExecutionData {
    void execute() throws FileNotFoundException, IOException;
    boolean validateQuery(String query) throws FileNotFoundException;
}
