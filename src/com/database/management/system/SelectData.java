package com.database.management.system;

public class SelectData implements ExecutionData{

    @Override
    public void execute(String query) {
        boolean isQueryValid = validateQuery(query);
        //fetchMetaData(isQueryValid);
        //fetchData
        //evaluate according to the query
    }

    @Override
    public boolean validateQuery(String query) {
        return false;
    }


}
