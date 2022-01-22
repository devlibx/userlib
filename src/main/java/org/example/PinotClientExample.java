package org.example;

import org.apache.pinot.client.Connection;
import org.apache.pinot.client.ConnectionFactory;
import org.apache.pinot.client.Request;
import org.apache.pinot.client.ResultSetGroup;
import org.apache.pinot.client.ResultSet;

/**
 * Demonstrates the use of the pinot-client to query Pinot from Java
 */
public class PinotClientExample {

    public static void main(String[] args) {

        // pinot connection
        String zkUrl = "192.168.0.126:2181";
        String pinotClusterName = "PinotCluster";
        Connection pinotConnection = ConnectionFactory.fromZookeeper(zkUrl + "/" + pinotClusterName);

        String query = "SELECT * FROM order3 ";

        // set queryType=sql for querying the sql endpoint
        Request pinotClientRequest = new Request("sql", query);
        ResultSetGroup pinotResultSetGroup = pinotConnection.execute(pinotClientRequest);
        ResultSet resultTableResultSet = pinotResultSetGroup.getResultSet(0);

        int numRows = resultTableResultSet.getRowCount();
        int numColumns = resultTableResultSet.getColumnCount();
        System.out.println(numRows + " " + numColumns);
        System.out.println(resultTableResultSet.getInt(0));
    }
}