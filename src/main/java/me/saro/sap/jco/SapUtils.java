package me.saro.sap.jco;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoTable;

import me.saro.commons.Converter;
import me.saro.commons.function.ThrowableBiConsumer;
import me.saro.commons.function.ThrowableConsumer;

/**
 * Sap Util
 * @author      PARK Yong Seo
 * @since       0.1
 */
public class SapUtils {
    
    // this class have only static method
    private SapUtils() {
    }
    
    /**
     * JCoTable to List[Map[String, Object]]
     * @param table
     * jCo table
     * @return JCoTable to List<Map<String, Object>>
     */
    public static List<Map<String, Object>> toMapList(JCoTable table) {
        List<Map<String, Object>> rv = new ArrayList<>();

        if (!table.isEmpty()) {
            table.firstRow();
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                Converter.toStream(table).forEach(field -> map.put(field.getName(), field.getValue()));
                rv.add(map);
            } while (table.nextRow());
        }
        
        return rv;
    }
    
    /**
     * JCoTable to custom class
     * @param table
     * jCo table
     * @param createRow
     * create custom class row
     * @param bindField
     * bind field in custom class row
     * @return JCoTable to Custom Class
     */
    public static <R> List<R> toClass(JCoTable table, Supplier<R> createRow, ThrowableBiConsumer<R, JCoField> bindField) {
        List<R> rv = new ArrayList<>();

        if (!table.isEmpty()) {
            table.firstRow();
            do {
                R r = createRow.get();
                Converter.toStream(table).forEach(ThrowableConsumer.runtime(field -> bindField.accept(r, field)));
                rv.add(r);
            } while (table.nextRow());
        }
        
        return rv;
    }
    
    /**
     * date filter
     * "value" is "java.util.Date" then return "format String" <br>
     * "value" is not "java.util.Date" then return "value"
     * @param value data
     * @param format Date format you want
     * @return result
     */
    public static Object filterDate(Object value, String format) {
        if (value != null && "java.util.Date".equals(value.getClass().getName())) {
            return new SimpleDateFormat(format).format((Date)value);
        }
        return value;
    }
}
