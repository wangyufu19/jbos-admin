package com.jbos.admin.common.bigdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * BigDataProcessor
 *
 * @author youfu.wang
 * @date 2023/7/10
 **/
@Service
public class BigDataProcessor {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private Connection connection=null;
    private PreparedStatement stmt=null;

    public void process() throws SQLException {
//        jdbcTemplate.execute("TRUNCATE user");
//        BatchSqlUpdate bsu = new BatchSqlUpdate(jdbcTemplate.getDataSource(), " insert into user(id,name) values (?,?)");
//        bsu.setBatchSize(1000);
//        bsu.setTypes(new int[]{Types.VARCHAR, Types.VARCHAR});
//
//        for (int i=0;i<1000000;i++) {
//            bsu.update(new Object[]{""+i, "k"+i});
//        }
//        bsu.flush();
        int batchSize=10000;
        connection=this.jdbcTemplate.getDataSource().getConnection();
        stmt = connection.prepareStatement("TRUNCATE user");
        stmt.executeUpdate();
        stmt = connection.prepareStatement("insert into user(id,name) values (?,?)");
        connection.setAutoCommit(false);
        int i=1;
        while (i<=1000001) {
            stmt.setInt(1, i);
            stmt.setString(2, "k"+i);
            stmt.addBatch();
            if ( i % batchSize == 0 ) {
                stmt.executeBatch();
                connection.commit();
            }
            i++;
        }
        if ( i % batchSize != 0 ) {
            stmt.executeBatch();
            connection.commit();
        }
        stmt.close();
    }
}
