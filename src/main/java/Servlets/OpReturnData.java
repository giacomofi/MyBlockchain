package Servlets;

import DBConnector.ConnectionManager;
import SupportClasses.MemoryCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class OpReturnData extends HttpServlet {

    //Connecting to DB
    private ConnectionManager db = new ConnectionManager();

    private MemoryCache<String,Map<String,List<String>>> cache = new MemoryCache<>(500,900,5);





    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Connection conn = null;


        //Statements - A statement for every SQL query
        Statement stmnt = null;
        PreparedStatement psTransactions = null;
        PreparedStatement psFirstAppearence = null;
        PreparedStatement psTotalSize = null;
        PreparedStatement psAverageSize = null;

        Map<String, List<String>> protocolTable = new TreeMap<>();

        //If there's something in cache, this piece of code won't get executed
        if(cache.size() == 0){
            try {
                conn = db.getConnection();
                stmnt = conn.createStatement();


                String protocolquery = "SELECT DISTINCT protocol FROM opreturn.opr";

                psTransactions = conn.prepareStatement("SELECT COUNT(protocol) AS count" +
                        " FROM opreturn.opr " +
                        "WHERE protocol =? ");

                psFirstAppearence = conn.prepareStatement("SELECT MIN(txdate) AS min" +
                        " FROM opreturn.opr " +
                        " WHERE protocol =?");

                psTotalSize = conn.prepareStatement("SELECT sum((length(metadata))/2) AS totalsize " +
                        "FROM opreturn.opr  " +
                        "WHERE protocol =?");

                psAverageSize = conn.prepareStatement("SELECT o.totalsize / p.numberofel AS average  " +
                        "FROM(SELECT sum((length(metadata))/2) AS totalsize FROM opreturn.opr WHERE protocol =?) o " +
                        "CROSS JOIN " +
                        "(SELECT count(metadata) AS numberofel FROM opreturn.opr WHERE protocol =?) p");

                ResultSet rs = stmnt.executeQuery(protocolquery); //Protocol Result Set


                while (rs.next()) {

                    //Dummies save MySQL db data

                    Integer elementsDummy;
                    Integer totalSizeDummy;
                    Integer averageSizeDummy;
                    Timestamp dateDummy;
                    String protocolDummy = rs.getString("protocol");
                    List<String> tableData = new ArrayList<>();

                    //Result Sets

                    ResultSet epprs; //Elements Per Protocol Result Set
                    ResultSet daters; //Txdate Result Sets
                    ResultSet totalsizers; //Total size of Metadata Per Protocol
                    ResultSet averagesizers; //Average size of Metadata per Protocol


                    switch (protocolDummy) {

                        case "unknown":
                            psTransactions.setString(1, "unknown");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "unknown");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "unknown");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "unknown");
                            psAverageSize.setString(2, "unknown");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Unknown");
                            tableData.add("???");
                            tableData.add("???");
                            protocolTable.put(protocolDummy, tableData);


                            break;

                        case "empty":
                            psTransactions.setString(1, "empty");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "empty");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "empty");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "empty");
                            psAverageSize.setString(2, "empty");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Empty");
                            tableData.add("--");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "factom":
                            psTransactions.setString(1, "factom");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "factom");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "factom");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "factom");
                            psAverageSize.setString(2, "factom");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Markle Root");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "proofofexistence":
                            psTransactions.setString(1, "proofofexistence");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "proofofexistence");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "proofofexistence");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "proofofexistence");
                            psAverageSize.setString(2, "proofofexistence");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "openassets":
                            psTransactions.setString(1, "openassets");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "openassets");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "openassets");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "openassets");
                            psAverageSize.setString(2, "openassets");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Financial");
                            tableData.add("Financial Record");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "counterparty":
                            psTransactions.setString(1, "counterparty");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "counterparty");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "counterparty");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "counterparty");
                            psAverageSize.setString(2, "counterparty");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Financial");
                            tableData.add("Financial Record");
                            tableData.add("P2PKH / MULTISIG");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "coinspark":
                            psTransactions.setString(1, "coinspark");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "coinspark");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "coinspark");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "coinspark");
                            psAverageSize.setString(2, "coinspark");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Financial");
                            tableData.add("Financial Record");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "cryptocopyright":
                            psTransactions.setString(1, "cryptocopyright");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "cryptocopyright");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "cryptocopyright");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "cryptocopyright");
                            psAverageSize.setString(2, "cryptocopyright");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "blocksign":
                            psTransactions.setString(1, "blocksign");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "blocksign");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "blocksign");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "blocksign");
                            psAverageSize.setString(2, "blocksign");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "nicosia":
                            psTransactions.setString(1, "nicosia");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "nicosia");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "nicosia");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "nicosia");
                            psAverageSize.setString(2, "nicosia");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash of Hashes");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "lapreuve":
                            psTransactions.setString(1, "lapreuve");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "lapreuve");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "lapreuve");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "lapreuve");
                            psAverageSize.setString(2, "lapreuve");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "blockstore":
                            psTransactions.setString(1, "blockstore");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "blockstore");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "blockstore");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "blockstore");
                            psAverageSize.setString(2, "blockstore");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Subchain");
                            tableData.add("Key-Value");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "ascribe":
                            psTransactions.setString(1, "ascribe");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "ascribe");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "ascribe");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "ascribe");
                            psAverageSize.setString(2, "ascribe");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("DRM");
                            tableData.add("Copyright Record");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "stampd":
                            psTransactions.setString(1, "stampd");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "stampd");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "stampd");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "stampd");
                            psAverageSize.setString(2, "stampd");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "blockai":
                            psTransactions.setString(1, "blockai");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "blockai");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "blockai");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "blockai");
                            psAverageSize.setString(2, "blockai");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("DRM");
                            tableData.add("Copyright Record");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "bitproof":
                            psTransactions.setString(1, "bitproof");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "bitproof");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "bitproof");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "bitproof");
                            psAverageSize.setString(2, "bitproof");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "stampery":
                            psTransactions.setString(1, "stampery");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "stampery");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "stampery");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "stampery");
                            psAverageSize.setString(2, "stampery");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Markle Root,Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "provebit":
                            psTransactions.setString(1, "provebit");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "provebit");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "provebit");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "provebit");
                            psAverageSize.setString(2, "provebit");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;


                        case "eternitywall":
                            psTransactions.setString(1, "eternitywall");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "eternitywall");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "eternitywall");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "eternitywall");
                            psAverageSize.setString(2, "eternitywall");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Message");
                            tableData.add("Text");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "monegraph":
                            psTransactions.setString(1, "monegraph");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "monegraph");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "monegraph");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "monegraph");
                            psAverageSize.setString(2, "monegraph");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("DRM");
                            tableData.add("Copyright Record");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "colu":
                            psTransactions.setString(1, "colu");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "colu");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "colu");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "colu");
                            psAverageSize.setString(2, "colu");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Financial");
                            tableData.add("Financial Record");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "originalmy":
                            psTransactions.setString(1, "originalmy");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "originalmy");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "originalmy");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "originalmy");
                            psAverageSize.setString(2, "originalmy");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "omni":
                            psTransactions.setString(1, "omni");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "omni");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "omni");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "omni");
                            psAverageSize.setString(2, "omni");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Financial");
                            tableData.add("Financial Record");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "remembr":
                            psTransactions.setString(1, "remembr");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "remembr");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "remembr");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "remembr");
                            psAverageSize.setString(2, "remembr");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "helperbit":
                            psTransactions.setString(1, "helperbit");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "helperbit");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "helperbit");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "helperbit");
                            psAverageSize.setString(2, "helperbit");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Financial");
                            tableData.add("Financial Record");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "openchain":
                            psTransactions.setString(1, "openchain");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "openchain");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "openchain");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "openchain");
                            psAverageSize.setString(2, "openchain");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Financial");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;
                        case "smartbit":
                            psTransactions.setString(1, "smartbit");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "smartbit");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "smartbit");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "smartbit");
                            psAverageSize.setString(2, "smartbit");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Merkle Root");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                        case "notary":
                            psTransactions.setString(1, "notary");
                            epprs = psTransactions.executeQuery();
                            while (epprs.next()) {
                                elementsDummy = epprs.getInt("count");
                                tableData.add(elementsDummy.toString());
                            }
                            psFirstAppearence.setString(1, "notary");
                            daters = psFirstAppearence.executeQuery();
                            while (daters.next()) {
                                dateDummy = daters.getTimestamp("min");
                                tableData.add(dateDummy.toString());
                            }
                            psTotalSize.setString(1, "notary");
                            totalsizers = psTotalSize.executeQuery();
                            while (totalsizers.next()) {
                                totalSizeDummy = totalsizers.getInt("totalsize");
                                tableData.add(totalSizeDummy.toString());
                            }
                            psAverageSize.setString(1, "notary");
                            psAverageSize.setString(2, "notary");
                            averagesizers = psAverageSize.executeQuery();
                            while (averagesizers.next()) {
                                averageSizeDummy = averagesizers.getInt("average");
                                tableData.add(averageSizeDummy.toString());
                            }
                            tableData.add("Notary");
                            tableData.add("Hash");
                            tableData.add("OP_RETURN");
                            protocolTable.put(protocolDummy, tableData);
                            break;

                    }
                }

                cache.put("protocolTable", protocolTable);

            } catch (SQLException e) {

                e.printStackTrace();

            } finally {

                try {

                    if (stmnt != null)
                        stmnt.close();

                    if (psTransactions != null)
                        psTransactions.close();

                    if (psFirstAppearence != null)
                        psFirstAppearence.close();

                    if (psTotalSize != null) {

                        psTotalSize.close();
                    }

                    if (psAverageSize != null) {

                        psAverageSize.close();
                    }

                    if (conn != null)
                        conn.close();

                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }

            request.setAttribute("protocolTable", protocolTable);
            request.getRequestDispatcher("/WEB-INF/jsp/OpReturnDataTable.jsp").forward(request, response);

    }

    //If there's something in cache, i'm going to get data directly from it
    else {
        request.setAttribute("protocolTable", cache.get("protocolTable"));
        request.getRequestDispatcher("/WEB-INF/jsp/OpReturnDataTable.jsp").forward(request, response);
     }

    }
}

