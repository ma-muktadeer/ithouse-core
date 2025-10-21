package com.ihouse.core.jasper;

import com.ihouse.core.jdbc.service.JdbcService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

public final class CoreJasperService {
    private static final JdbcService jdbcService = new JdbcService();

    public CoreJasperService() {
    }

    public static JasperPrint generateReport(Resource jrxm, Map<String, Object> parameters) throws JRException, IOException {
        try (var con = jdbcService.getConnection()) {
            return generateJasper(jrxm, parameters, con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JasperPrint generateReport(InputStream jrxm, Map<String, Object> parameters) throws JRException, IOException {
        try (var con = jdbcService.getConnection()) {
            return generateJasper(jrxm, parameters, con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JasperPrint generateReport(Resource jrxm, Map<String, Object> propertyMap, ResultSet rs) throws JRException, IOException {
        return doGenerateReport(jrxm, propertyMap, rs);
    }

    public static JasperPrint generateReport(Resource jrxm, Map<String, Object> propertyMap, JRResultSetDataSource ds) throws JRException, IOException {
        return generateJasper(jrxm, propertyMap, ds);
    }

    public static JasperPrint generateReport(Resource jrxm, Map<String, Object> propertyMap, JRBeanCollectionDataSource beanCollectionDataSource) throws JRException, IOException {
        return generateJasper(jrxm, propertyMap, beanCollectionDataSource);
    }

    public static JasperPrint generateReport(Resource jrxm, ResultSet rs) throws JRException, IOException {
        return doGenerateReport(jrxm, null, rs);
    }

    public static JasperPrint generateReport(InputStream jrxm, ResultSet rs) throws Exception {
        return doGenerateReport(jrxm, rs);
    }

    public static JasperPrint generateReport(InputStream jrxm, Map<String, Object> propertyMap, JRBeanCollectionDataSource beanCollectionDataSource) throws Exception {
        return doGenerateReport(jrxm, propertyMap, beanCollectionDataSource);
    }

    private static JasperPrint doGenerateReport(Resource jrxm, Map<String, Object> propertyMap, ResultSet rs) throws IOException, JRException {

        return generateJasper(jrxm, propertyMap, new JRResultSetDataSource(rs));
    }

    private static JasperPrint doGenerateReport(InputStream jrxm, ResultSet rs) throws JRException {
        return generateJasper(jrxm, (Map<String, Object>) null, new JRResultSetDataSource(rs));
    }


    private static JasperPrint doGenerateReport(InputStream jrxm, Map<String, Object> propertyMap, JRBeanCollectionDataSource beanCollectionDataSource) throws JRException {
        return generateJasper(jrxm, propertyMap, beanCollectionDataSource);
    }

    private static JasperPrint generateJasper(InputStream jrxm, Map<String, Object> propertyMap, JRDataSource jrResultSetDataSource) throws JRException {
        Validate.notNull(jrxm, "jrxml can not be null", new Object[0]);
        return JasperFillManager.fillReport(jrxm, propertyMap, jrResultSetDataSource);
    }

    private static JasperPrint generateJasper(InputStream jrxm, Map<String, Object> propertyMap, Connection con) throws JRException {
        Validate.notNull(jrxm, "jrxml can not be null", new Object[0]);
        return JasperFillManager.fillReport(jrxm, propertyMap, con);
    }

    private static JasperPrint generateJasper(Resource jrxm, Map<String, Object> propertyMap, JRDataSource jrResultSetDataSource) throws IOException, JRException {
        return generateJasper(jrxm.getInputStream(), propertyMap, jrResultSetDataSource);
    }

    private static JasperPrint generateJasper(Resource jrxm, Map<String, Object> propertyMap, Connection con) throws IOException, JRException {
        return generateJasper(jrxm.getInputStream(), propertyMap, con);
    }

}
