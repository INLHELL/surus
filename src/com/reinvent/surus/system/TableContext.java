package com.reinvent.surus.system;

import com.reinvent.surus.mapping.EntityService;
import com.reinvent.surus.model.Bucket;
import com.reinvent.surus.model.Constants;
import com.reinvent.surus.model.Example;
import com.reinvent.surus.model.ExampleComplex;
import com.reinvent.surus.primarykey.HPrimaryKey;
import com.reinvent.surus.primarykey.IntegerPrimaryKey;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bohdan Mushkevych
 * Description: holds relationship between table name, primary key and the pool manager
 */

class ContextMapping {
    TimeQualifier timeQualifier;
    PoolManager poolManager;

    ContextMapping(TimeQualifier timeQualifier, PoolManager poolManager) {
        this.timeQualifier = timeQualifier;
        this.poolManager = poolManager;
    }
}

public class TableContext {
    private final static Map<String, ContextMapping> CONTEXT = new HashMap<String, ContextMapping>();
    private static Logger log = Logger.getLogger(TableContext.class);

    static {
        CONTEXT.put(Constants.TABLE_EXAMPLE, new ContextMapping(
                TimeQualifier.HOURLY, new PoolManager<ExampleComplex>(
                    Constants.TABLE_EXAMPLE, ExampleComplex.class, new HPrimaryKey<ExampleComplex>(ExampleComplex.class, new EntityService<ExampleComplex>(ExampleComplex.class)))));
        CONTEXT.put(Constants.TABLE_BUCKET, new ContextMapping(
                TimeQualifier.HOURLY, new PoolManager<Bucket>(Constants.TABLE_BUCKET, Bucket.class, new IntegerPrimaryKey())));
    }

    private static ContextMapping getContextMapping(String tableName) {
        if (!TableContext.CONTEXT.containsKey(tableName)) {
            String msg = String.format("Table %s is unknown to synergy-hadoop", tableName);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return TableContext.CONTEXT.get(tableName);
    }

    public static PoolManager getPoolManager(String tableName) {
        ContextMapping mapping = getContextMapping(tableName);
        return mapping.poolManager;
    }

    public static void register(String tableName, ContextMapping mapping) {
         TableContext.CONTEXT.put(tableName, mapping);
    }

    public static boolean containsTable(String tableName) {
        return TableContext.CONTEXT.containsKey(tableName);
    }

    public static TimeQualifier getTimeQualifier(String tableName) {
        ContextMapping mapping = getContextMapping(tableName);
        return mapping.timeQualifier;
    }
}
