package Servlets;


import DBConnector.ConnectionManager;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.Map.Entry;


public class ChartData extends HttpServlet {

    private ConnectionManager db = new ConnectionManager();
    static final int MOSTUSED = 5;



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        Connection conn = null;

        Statement stmnt = null;
        Statement stmnt2 = null;

        //Prepared Statements to query over db
        PreparedStatement psTransactionPerProtocol = null;
        PreparedStatement psAverageUse = null;
        PreparedStatement psTransactionsPerYear = null;
        PreparedStatement psTransactionsPerFinancial = null;
        PreparedStatement psTransactionsPerSubchain = null;
        PreparedStatement psTransactionsPerMessage = null;
        PreparedStatement psTransactionsPerDRM = null;
        PreparedStatement psTransactionsPerNotary = null;
        PreparedStatement psTransactionsPerUnknown = null;
        PreparedStatement psTransactionsPerEmpty = null;

        //Useful to get Data
        Map<String,List<Integer>> protocolChart = new HashMap<>();
        Map<Integer,Integer> transactionsPerYearMap = new LinkedHashMap<>();
        Map<String,Integer> transactionsPerCategoriesMap = new HashMap<>();
        Map<String,Integer> requestedCategoriesMap = new HashMap<>();
        Map<Integer, LinkedHashMap<List<String>,List<Integer>>> comparizonBetweenYearsMap = new LinkedHashMap<>();

        //Counters for every single transaction relative to a category
        Integer financialCount = 0;
        Integer notaryCount = 0;
        Integer subchainCount = 0;
        Integer drmCount = 0;
        Integer messageCount = 0;

        try{

            conn = db.getConnection();
            stmnt = conn.createStatement();
            stmnt2 = conn.createStatement();


            String protocolquery = "SELECT DISTINCT protocol FROM opreturn.opr";
            String yearQuery = "SELECT DISTINCT (year(txdate)) AS year FROM opreturn.opr";

            psTransactionPerProtocol = conn.prepareStatement("SELECT COUNT(protocol) AS count" +
                    " FROM opreturn.opr " +
                    "WHERE protocol =? ");

            psAverageUse = conn.prepareStatement(" select (((o.transactionperprotocol / p.transactionperyear)*100)/2) as averageutilization from(select count(protocol) as transactionperprotocol from opreturn.opr where protocol =?) o  cross join (select count(protocol) as transactionperyear from opreturn.opr) p ");

            psTransactionsPerYear = conn.prepareStatement(("SELECT count(protocol) AS transactionsperyear " +
                    "FROM opreturn.opr" +
                    " WHERE txdate LIKE ?"));

            psTransactionsPerFinancial = conn.prepareStatement("SELECT count(protocol) AS transactionsperfinancial " +
                    "FROM opreturn.opr " +
                    "WHERE (protocol =? OR protocol =? OR protocol =? OR protocol =? OR protocol =? OR protocol =? OR protocol=?)" +
                    "AND txdate LIKE ?");

            psTransactionsPerSubchain = conn.prepareStatement("SELECT count(protocol) AS transactionspersubchain " +
                    "FROM opreturn.opr " +
                    "WHERE protocol =? " +
                    "AND txdate LIKE ?");

            psTransactionsPerMessage = conn.prepareStatement("SELECT count(protocol) AS transactionspermessage " +
                    "FROM opreturn.opr " +
                    "WHERE protocol =? " +
                    "AND txdate LIKE ?");

            psTransactionsPerDRM = conn.prepareStatement("SELECT count(protocol) AS transactionsperDRM " +
                    "FROM opreturn.opr " +
                    "WHERE (protocol =? OR protocol =? OR protocol =?)" +
                    "AND txdate LIKE ?");

            psTransactionsPerUnknown = conn.prepareStatement("SELECT count(protocol) AS transactionsperunknown " +
                    "FROM opreturn.opr " +
                    "WHERE protocol =?" +
                    "AND txdate LIKE ?");

            psTransactionsPerEmpty = conn.prepareStatement("SELECT count(protocol) AS transactionsperempty " +
                    "FROM opreturn.opr " +
                    "WHERE protocol =?" +
                    "AND txdate LIKE ?");

            psTransactionsPerNotary = conn.prepareStatement("SELECT count(protocol) AS transactionspernotary " +
                    "FROM opreturn.opr " +
                    "WHERE (protocol =? OR protocol =? OR protocol =? OR protocol =? OR protocol =? OR protocol =? OR protocol=?  OR protocol =? OR protocol =? OR protocol =? OR protocol =? OR protocol =? OR protocol=?)" +
                    "AND txdate LIKE ?");

            ResultSet rs = stmnt.executeQuery(protocolquery); //Protocol Result Set
            ResultSet yrs = stmnt2.executeQuery(yearQuery); //Year result Set


            while(rs.next()){

                Integer transactionsPerProtocol;
                Integer averageUtilization;
                List<Integer> chartData = new ArrayList<>();

                String protocolDummy = rs.getString("protocol");

                ResultSet epprs; //Elements Per Protocol Result Set
                ResultSet aurs; //Average Utilization Result Set

                switch(protocolDummy){

                    case "unknown":

                        psTransactionPerProtocol.setString(1,"unknown");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Unknown",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"unknown");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "empty":
                        psTransactionPerProtocol.setString(1,"empty");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Empty",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"empty");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "proofofexistence":

                        psTransactionPerProtocol.setString(1,"proofofexistence");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"proofofexistence");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "openassets":
                        psTransactionPerProtocol.setString(1,"openassets");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Financial",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"openassets");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "counterparty":
                        psTransactionPerProtocol.setString(1,"counterparty");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Financial1",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"counterparty");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "coinspark":

                        psTransactionPerProtocol.setString(1,"coinspark");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Financial2",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"coinspark");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;
                    case "cryptocopyright":

                        psTransactionPerProtocol.setString(1,"cryptocopyright");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary1",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"cryptocopyright");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "blocksign":

                        psTransactionPerProtocol.setString(1,"blocksign");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary2",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"blocksign");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "nicosia":

                        psTransactionPerProtocol.setString(1,"nicosia");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary3",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"nicosia");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "lapreuve":
                        psTransactionPerProtocol.setString(1,"lapreuve");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary4",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"lapreuve");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "blockstore":
                        psTransactionPerProtocol.setString(1,"blockstore");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Subchain",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"blockstore");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "ascribe":
                        psTransactionPerProtocol.setString(1,"ascribe");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("DRM",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"ascribe");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "stampd":
                        psTransactionPerProtocol.setString(1,"stampd");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary5",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"stampd");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "blockai":
                        psTransactionPerProtocol.setString(1,"blockai");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("DRM1",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"blockai");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "bitproof":
                        psTransactionPerProtocol.setString(1,"bitproof");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary6",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"bitproof");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "stampery":
                        psTransactionPerProtocol.setString(1,"stampery");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary7",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"stampery");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "provebit":
                        psTransactionPerProtocol.setString(1,"provebit");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary8",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"provebit");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;


                    case "eternitywall":
                        psTransactionPerProtocol.setString(1,"eternitywall");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Message",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"eternitywall");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "monegraph":
                        psTransactionPerProtocol.setString(1,"monegraph");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("DRM2",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"monegraph");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;
                    case "colu":
                        psTransactionPerProtocol.setString(1,"colu");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Financial3",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"colu");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;
                    case "originalmy":
                        psTransactionPerProtocol.setString(1,"originalmy");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary9",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"originalmy");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "omni":
                        psTransactionPerProtocol.setString(1,"omni");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Financial4",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"omni");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;


                    case "remembr":
                        psTransactionPerProtocol.setString(1,"remembr");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary10",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"remembr");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;


                    case "helperbit":
                        psTransactionPerProtocol.setString(1,"helperbit");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Financial5",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"helperbit");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;


                    case "openchain":
                        psTransactionPerProtocol.setString(1,"openchain");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Financial6",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"openchain");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;
                    case "smartbit":
                        psTransactionPerProtocol.setString(1,"smartbit");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary11",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"smartbit");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;

                    case "notary":
                        psTransactionPerProtocol.setString(1,"notary");
                        epprs = psTransactionPerProtocol.executeQuery();
                        while(epprs.next()){
                            transactionsPerProtocol = epprs.getInt("count");
                            transactionsPerCategoriesMap.put("Notary12",transactionsPerProtocol);
                            chartData.add(transactionsPerProtocol);
                        }
                        psAverageUse.setString(1,"notary");
                        aurs = psAverageUse.executeQuery();
                        while(aurs.next()){
                            averageUtilization = aurs.getInt("averageutilization");
                            chartData.add(averageUtilization);
                        }
                        protocolChart.put(protocolDummy,chartData);
                        break;


                }

            }

            while(yrs.next()){

                Integer transactionsPerYear;
                Integer financialTransactions;
                Integer notaryTransactions;
                Integer emptyTransactions;
                Integer unknownTransactions;
                Integer drmTransactions;
                Integer subchainTransactions;
                Integer messageTransactions;

                Integer yearDummy = yrs.getInt("year");

                Map<String,HashMap<List<String>,List<Integer>>> categoryTransactionPerYearMap = new HashMap<>();
                LinkedHashMap<List<String>,List<Integer>> preliminarMap = new LinkedHashMap<>();

                List<String> preliminarStringList = new ArrayList<>();
                List<Integer> preliminarIntegerList = new ArrayList<>();

                ResultSet tpyrs; //Transactions Per Year Result Set
                ResultSet ydnrs; //Difference Between Years Result Set (Notary)
                ResultSet ydfrs; //Difference Between Years Result Set (Financial)
                ResultSet yders; //Difference Between Years Result Set (Empty)
                ResultSet ydurs; //Difference Between Years Result Set (Unknown)
                ResultSet yddrmrs; //Difference Between Years Result Set (DRM)
                ResultSet ydsrs; //Difference Between Years Result Set (Subchain)
                ResultSet ydmrs; //Difference Between Years Result Set (Message)

                switch(yearDummy){

                    case 2013:

                        psTransactionsPerYear.setString(1,"%2013%");
                        tpyrs =  psTransactionsPerYear.executeQuery();
                        while(tpyrs.next()){

                            transactionsPerYear = tpyrs.getInt("transactionsperyear");
                            transactionsPerYearMap.put(yearDummy,transactionsPerYear);
                        }

                        psTransactionsPerNotary.setString(1,"cryptocopyright");
                        psTransactionsPerNotary.setString(2,"blocksign");
                        psTransactionsPerNotary.setString(3,"proofofexistence");
                        psTransactionsPerNotary.setString(4,"nicosia");
                        psTransactionsPerNotary.setString(5,"lapreuve");
                        psTransactionsPerNotary.setString(6,"stampd");
                        psTransactionsPerNotary.setString(7,"bitproof");
                        psTransactionsPerNotary.setString(8,"stampery");
                        psTransactionsPerNotary.setString(9,"provebit");
                        psTransactionsPerNotary.setString(10,"originalmy");
                        psTransactionsPerNotary.setString(11,"remembr");
                        psTransactionsPerNotary.setString(12,"smartbit");
                        psTransactionsPerNotary.setString(13,"notary");
                        psTransactionsPerNotary.setString(14,"%2013%");

                        ydnrs = psTransactionsPerNotary.executeQuery();
                        while(ydnrs.next()){
                            notaryTransactions = ydnrs.getInt("transactionspernotary");
                            preliminarStringList.add("Notary");
                            preliminarIntegerList.add(notaryTransactions);
                        }
                        psTransactionsPerFinancial.setString(1,"openassets");
                        psTransactionsPerFinancial.setString(2,"counterparty");
                        psTransactionsPerFinancial.setString(3,"coinspark");
                        psTransactionsPerFinancial.setString(4,"colu");
                        psTransactionsPerFinancial.setString(5,"omni");
                        psTransactionsPerFinancial.setString(6,"helperbit");
                        psTransactionsPerFinancial.setString(7,"openchain");
                        psTransactionsPerFinancial.setString(8,"%2013%");

                        ydfrs = psTransactionsPerFinancial.executeQuery();
                        while(ydfrs.next()){
                            financialTransactions = ydfrs.getInt("transactionsperfinancial");
                            preliminarStringList.add("Financial");
                            preliminarIntegerList.add(financialTransactions);
                        }
                        psTransactionsPerEmpty.setString(1,"empty");
                        psTransactionsPerEmpty.setString(2,"%2013%");
                        yders = psTransactionsPerEmpty.executeQuery();
                        while(yders.next()){
                            emptyTransactions = yders.getInt("transactionsperempty");
                            preliminarStringList.add("Empty");
                            preliminarIntegerList.add(emptyTransactions);
                        }
                        psTransactionsPerUnknown.setString(1,"unknown");
                        psTransactionsPerUnknown.setString(2,"%2013%");
                        ydurs = psTransactionsPerUnknown.executeQuery();
                        while(ydurs.next()){
                            unknownTransactions = ydurs.getInt("transactionsperunknown");
                            preliminarStringList.add("Unknown");
                            preliminarIntegerList.add(unknownTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerDRM.setString(1,"ascribe");
                        psTransactionsPerDRM.setString(2,"blockai");
                        psTransactionsPerDRM.setString(3,"monegraph");
                        psTransactionsPerDRM.setString(4,"%2013%");
                        yddrmrs = psTransactionsPerDRM.executeQuery();
                        while(yddrmrs.next()){
                            drmTransactions = yddrmrs.getInt("transactionsperDRM");
                            preliminarStringList.add("DRM");
                            preliminarIntegerList.add(drmTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerMessage.setString(1,"eternitywall");
                        psTransactionsPerMessage.setString(2,"%2013%");
                        ydmrs = psTransactionsPerMessage.executeQuery();
                        while(ydmrs.next()){
                            messageTransactions = ydmrs.getInt("transactionspermessage");
                            preliminarStringList.add("Message");
                            preliminarIntegerList.add(messageTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerSubchain.setString(1,"blockstore");
                        psTransactionsPerSubchain.setString(2,"%2013%");
                        ydsrs = psTransactionsPerSubchain.executeQuery();
                        while(ydsrs.next()){
                           subchainTransactions = ydsrs.getInt("transactionspersubchain");
                            preliminarStringList.add("Subchain");
                            preliminarIntegerList.add(subchainTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);



                        break;

                    case 2014:

                        psTransactionsPerYear.setString(1,"%2014%");
                        tpyrs =  psTransactionsPerYear.executeQuery();
                        while(tpyrs.next()){

                            transactionsPerYear = tpyrs.getInt("transactionsperyear");
                            transactionsPerYearMap.put(yearDummy,transactionsPerYear);
                        }

                        psTransactionsPerNotary.setString(1,"cryptocopyright");
                        psTransactionsPerNotary.setString(2,"blocksign");
                        psTransactionsPerNotary.setString(3,"proofofexistence");
                        psTransactionsPerNotary.setString(4,"nicosia");
                        psTransactionsPerNotary.setString(5,"lapreuve");
                        psTransactionsPerNotary.setString(6,"stampd");
                        psTransactionsPerNotary.setString(7,"bitproof");
                        psTransactionsPerNotary.setString(8,"stampery");
                        psTransactionsPerNotary.setString(9,"provebit");
                        psTransactionsPerNotary.setString(10,"originalmy");
                        psTransactionsPerNotary.setString(11,"remembr");
                        psTransactionsPerNotary.setString(12,"smartbit");
                        psTransactionsPerNotary.setString(13,"notary");
                        psTransactionsPerNotary.setString(14,"%2014%");

                        ydnrs = psTransactionsPerNotary.executeQuery();
                        while(ydnrs.next()){
                            notaryTransactions = ydnrs.getInt("transactionspernotary");
                            preliminarStringList.add("Notary");
                            preliminarIntegerList.add(notaryTransactions);
                        }
                        psTransactionsPerFinancial.setString(1,"openassets");
                        psTransactionsPerFinancial.setString(2,"counterparty");
                        psTransactionsPerFinancial.setString(3,"coinspark");
                        psTransactionsPerFinancial.setString(4,"colu");
                        psTransactionsPerFinancial.setString(5,"omni");
                        psTransactionsPerFinancial.setString(6,"helperbit");
                        psTransactionsPerFinancial.setString(7,"openchain");
                        psTransactionsPerFinancial.setString(8,"%2014%");

                        ydfrs = psTransactionsPerFinancial.executeQuery();
                        while(ydfrs.next()){
                            financialTransactions = ydfrs.getInt("transactionsperfinancial");
                            preliminarStringList.add("Financial");
                            preliminarIntegerList.add(financialTransactions);
                        }
                        psTransactionsPerEmpty.setString(1,"empty");
                        psTransactionsPerEmpty.setString(2,"%2014%");
                        yders = psTransactionsPerEmpty.executeQuery();
                        while(yders.next()){
                            emptyTransactions = yders.getInt("transactionsperempty");
                            preliminarStringList.add("Empty");
                            preliminarIntegerList.add(emptyTransactions);
                        }
                        psTransactionsPerUnknown.setString(1,"unknown");
                        psTransactionsPerUnknown.setString(2,"%2014%");
                        ydurs = psTransactionsPerUnknown.executeQuery();
                        while(ydurs.next()){
                            unknownTransactions = ydurs.getInt("transactionsperunknown");
                            preliminarStringList.add("Unknown");
                            preliminarIntegerList.add(unknownTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerDRM.setString(1,"ascribe");
                        psTransactionsPerDRM.setString(2,"blockai");
                        psTransactionsPerDRM.setString(3,"monegraph");
                        psTransactionsPerDRM.setString(4,"%2014%");
                        yddrmrs = psTransactionsPerDRM.executeQuery();
                        while(yddrmrs.next()){
                            drmTransactions = yddrmrs.getInt("transactionsperDRM");
                            preliminarStringList.add("DRM");
                            preliminarIntegerList.add(drmTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerMessage.setString(1,"eternitywall");
                        psTransactionsPerMessage.setString(2,"%2014%");
                        ydmrs = psTransactionsPerMessage.executeQuery();
                        while(ydmrs.next()){
                            messageTransactions = ydmrs.getInt("transactionspermessage");
                            preliminarStringList.add("Message");
                            preliminarIntegerList.add(messageTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerSubchain.setString(1,"blockstore");
                        psTransactionsPerSubchain.setString(2,"%2014%");
                        ydsrs = psTransactionsPerSubchain.executeQuery();
                        while(ydsrs.next()){
                            subchainTransactions = ydsrs.getInt("transactionspersubchain");
                            preliminarStringList.add("Subchain");
                            preliminarIntegerList.add(subchainTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        break;

                    case 2015:

                        psTransactionsPerYear.setString(1,"%2015%");
                        tpyrs =  psTransactionsPerYear.executeQuery();
                        while(tpyrs.next()){

                            transactionsPerYear = tpyrs.getInt("transactionsperyear");
                            transactionsPerYearMap.put(yearDummy,transactionsPerYear);
                        }

                        psTransactionsPerNotary.setString(1,"cryptocopyright");
                        psTransactionsPerNotary.setString(2,"blocksign");
                        psTransactionsPerNotary.setString(3,"proofofexistence");
                        psTransactionsPerNotary.setString(4,"nicosia");
                        psTransactionsPerNotary.setString(5,"lapreuve");
                        psTransactionsPerNotary.setString(6,"stampd");
                        psTransactionsPerNotary.setString(7,"bitproof");
                        psTransactionsPerNotary.setString(8,"stampery");
                        psTransactionsPerNotary.setString(9,"provebit");
                        psTransactionsPerNotary.setString(10,"originalmy");
                        psTransactionsPerNotary.setString(11,"remembr");
                        psTransactionsPerNotary.setString(12,"smartbit");
                        psTransactionsPerNotary.setString(13,"notary");
                        psTransactionsPerNotary.setString(14,"%2015%");

                        ydnrs = psTransactionsPerNotary.executeQuery();
                        while(ydnrs.next()){
                            notaryTransactions = ydnrs.getInt("transactionspernotary");
                            preliminarStringList.add("Notary");
                            preliminarIntegerList.add(notaryTransactions);
                        }
                        psTransactionsPerFinancial.setString(1,"openassets");
                        psTransactionsPerFinancial.setString(2,"counterparty");
                        psTransactionsPerFinancial.setString(3,"coinspark");
                        psTransactionsPerFinancial.setString(4,"colu");
                        psTransactionsPerFinancial.setString(5,"omni");
                        psTransactionsPerFinancial.setString(6,"helperbit");
                        psTransactionsPerFinancial.setString(7,"openchain");
                        psTransactionsPerFinancial.setString(8,"%2015%");

                        ydfrs = psTransactionsPerFinancial.executeQuery();
                        while(ydfrs.next()){
                            financialTransactions = ydfrs.getInt("transactionsperfinancial");
                            preliminarStringList.add("Financial");
                            preliminarIntegerList.add(financialTransactions);
                        }
                        psTransactionsPerEmpty.setString(1,"empty");
                        psTransactionsPerEmpty.setString(2,"%2015%");
                        yders = psTransactionsPerEmpty.executeQuery();
                        while(yders.next()){
                            emptyTransactions = yders.getInt("transactionsperempty");
                            preliminarStringList.add("Empty");
                            preliminarIntegerList.add(emptyTransactions);
                        }
                        psTransactionsPerUnknown.setString(1,"unknown");
                        psTransactionsPerUnknown.setString(2,"%2015%");
                        ydurs = psTransactionsPerUnknown.executeQuery();
                        while(ydurs.next()){
                            unknownTransactions = ydurs.getInt("transactionsperunknown");
                            preliminarStringList.add("Unknown");
                            preliminarIntegerList.add(unknownTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerDRM.setString(1,"ascribe");
                        psTransactionsPerDRM.setString(2,"blockai");
                        psTransactionsPerDRM.setString(3,"monegraph");
                        psTransactionsPerDRM.setString(4,"%2015%");
                        yddrmrs = psTransactionsPerDRM.executeQuery();
                        while(yddrmrs.next()){
                            drmTransactions = yddrmrs.getInt("transactionsperDRM");
                            preliminarStringList.add("DRM");
                            preliminarIntegerList.add(drmTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerMessage.setString(1,"eternitywall");
                        psTransactionsPerMessage.setString(2,"%2015%");
                        ydmrs = psTransactionsPerMessage.executeQuery();
                        while(ydmrs.next()){
                            messageTransactions = ydmrs.getInt("transactionspermessage");
                            preliminarStringList.add("Message");
                            preliminarIntegerList.add(messageTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerSubchain.setString(1,"blockstore");
                        psTransactionsPerSubchain.setString(2,"%2015%");
                        ydsrs = psTransactionsPerSubchain.executeQuery();
                        while(ydsrs.next()){
                            subchainTransactions = ydsrs.getInt("transactionspersubchain");
                            preliminarStringList.add("Subchain");
                            preliminarIntegerList.add(subchainTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);


                        break;

                    case 2016:

                        psTransactionsPerYear.setString(1,"%2016%");
                        tpyrs =  psTransactionsPerYear.executeQuery();
                        while(tpyrs.next()){

                            transactionsPerYear = tpyrs.getInt("transactionsperyear");
                            transactionsPerYearMap.put(yearDummy,transactionsPerYear);
                        }

                        psTransactionsPerNotary.setString(1,"cryptocopyright");
                        psTransactionsPerNotary.setString(2,"blocksign");
                        psTransactionsPerNotary.setString(3,"proofofexistence");
                        psTransactionsPerNotary.setString(4,"nicosia");
                        psTransactionsPerNotary.setString(5,"lapreuve");
                        psTransactionsPerNotary.setString(6,"stampd");
                        psTransactionsPerNotary.setString(7,"bitproof");
                        psTransactionsPerNotary.setString(8,"stampery");
                        psTransactionsPerNotary.setString(9,"provebit");
                        psTransactionsPerNotary.setString(10,"originalmy");
                        psTransactionsPerNotary.setString(11,"remembr");
                        psTransactionsPerNotary.setString(12,"smartbit");
                        psTransactionsPerNotary.setString(13,"notary");
                        psTransactionsPerNotary.setString(14,"%2016%");

                        ydnrs = psTransactionsPerNotary.executeQuery();
                        while(ydnrs.next()){
                            notaryTransactions = ydnrs.getInt("transactionspernotary");
                            preliminarStringList.add("Notary");
                            preliminarIntegerList.add(notaryTransactions);
                        }
                        psTransactionsPerFinancial.setString(1,"openassets");
                        psTransactionsPerFinancial.setString(2,"counterparty");
                        psTransactionsPerFinancial.setString(3,"coinspark");
                        psTransactionsPerFinancial.setString(4,"colu");
                        psTransactionsPerFinancial.setString(5,"omni");
                        psTransactionsPerFinancial.setString(6,"helperbit");
                        psTransactionsPerFinancial.setString(7,"openchain");
                        psTransactionsPerFinancial.setString(8,"%2016%");

                        ydfrs = psTransactionsPerFinancial.executeQuery();
                        while(ydfrs.next()){
                            financialTransactions = ydfrs.getInt("transactionsperfinancial");
                            preliminarStringList.add("Financial");
                            preliminarIntegerList.add(financialTransactions);
                        }
                        psTransactionsPerEmpty.setString(1,"empty");
                        psTransactionsPerEmpty.setString(2,"%2016%");
                        yders = psTransactionsPerEmpty.executeQuery();
                        while(yders.next()){
                            emptyTransactions = yders.getInt("transactionsperempty");
                            preliminarStringList.add("Empty");
                            preliminarIntegerList.add(emptyTransactions);
                        }
                        psTransactionsPerUnknown.setString(1,"unknown");
                        psTransactionsPerUnknown.setString(2,"%2016%");
                        ydurs = psTransactionsPerUnknown.executeQuery();
                        while(ydurs.next()){
                            unknownTransactions = ydurs.getInt("transactionsperunknown");
                            preliminarStringList.add("Unknown");
                            preliminarIntegerList.add(unknownTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerDRM.setString(1,"ascribe");
                        psTransactionsPerDRM.setString(2,"blockai");
                        psTransactionsPerDRM.setString(3,"monegraph");
                        psTransactionsPerDRM.setString(4,"%2016%");
                        yddrmrs = psTransactionsPerDRM.executeQuery();
                        while(yddrmrs.next()){
                            drmTransactions = yddrmrs.getInt("transactionsperDRM");
                            preliminarStringList.add("DRM");
                            preliminarIntegerList.add(drmTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerMessage.setString(1,"eternitywall");
                        psTransactionsPerMessage.setString(2,"%2016%");
                        ydmrs = psTransactionsPerMessage.executeQuery();
                        while(ydmrs.next()){
                            messageTransactions = ydmrs.getInt("transactionspermessage");
                            preliminarStringList.add("Message");
                            preliminarIntegerList.add(messageTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerSubchain.setString(1,"blockstore");
                        psTransactionsPerSubchain.setString(2,"%2016%");
                        ydsrs = psTransactionsPerSubchain.executeQuery();
                        while(ydsrs.next()){
                            subchainTransactions = ydsrs.getInt("transactionspersubchain");
                            preliminarStringList.add("Subchain");
                            preliminarIntegerList.add(subchainTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);



                        break;

                    case 2017:

                        psTransactionsPerYear.setString(1,"%2017%");
                        tpyrs =  psTransactionsPerYear.executeQuery();
                        while(tpyrs.next()){

                            transactionsPerYear = tpyrs.getInt("transactionsperyear");
                            transactionsPerYearMap.put(yearDummy,transactionsPerYear);
                        }

                        psTransactionsPerNotary.setString(1,"cryptocopyright");
                        psTransactionsPerNotary.setString(2,"blocksign");
                        psTransactionsPerNotary.setString(3,"proofofexistence");
                        psTransactionsPerNotary.setString(4,"nicosia");
                        psTransactionsPerNotary.setString(5,"lapreuve");
                        psTransactionsPerNotary.setString(6,"stampd");
                        psTransactionsPerNotary.setString(7,"bitproof");
                        psTransactionsPerNotary.setString(8,"stampery");
                        psTransactionsPerNotary.setString(9,"provebit");
                        psTransactionsPerNotary.setString(10,"originalmy");
                        psTransactionsPerNotary.setString(11,"remembr");
                        psTransactionsPerNotary.setString(12,"smartbit");
                        psTransactionsPerNotary.setString(13,"notary");
                        psTransactionsPerNotary.setString(14,"%2017%");

                        ydnrs = psTransactionsPerNotary.executeQuery();
                        while(ydnrs.next()){
                            notaryTransactions = ydnrs.getInt("transactionspernotary");
                            preliminarStringList.add("Notary");
                            preliminarIntegerList.add(notaryTransactions);
                        }
                        psTransactionsPerFinancial.setString(1,"openassets");
                        psTransactionsPerFinancial.setString(2,"counterparty");
                        psTransactionsPerFinancial.setString(3,"coinspark");
                        psTransactionsPerFinancial.setString(4,"colu");
                        psTransactionsPerFinancial.setString(5,"omni");
                        psTransactionsPerFinancial.setString(6,"helperbit");
                        psTransactionsPerFinancial.setString(7,"openchain");
                        psTransactionsPerFinancial.setString(8,"%2017%");

                        ydfrs = psTransactionsPerFinancial.executeQuery();
                        while(ydfrs.next()){
                            financialTransactions = ydfrs.getInt("transactionsperfinancial");
                            preliminarStringList.add("Financial");
                            preliminarIntegerList.add(financialTransactions);
                        }
                        psTransactionsPerEmpty.setString(1,"empty");
                        psTransactionsPerEmpty.setString(2,"%2017%");
                        yders = psTransactionsPerEmpty.executeQuery();
                        while(yders.next()){
                            emptyTransactions = yders.getInt("transactionsperempty");
                            preliminarStringList.add("Empty");
                            preliminarIntegerList.add(emptyTransactions);
                        }
                        psTransactionsPerUnknown.setString(1,"unknown");
                        psTransactionsPerUnknown.setString(2,"%2017%");
                        ydurs = psTransactionsPerUnknown.executeQuery();
                        while(ydurs.next()){
                            unknownTransactions = ydurs.getInt("transactionsperunknown");
                            preliminarStringList.add("Unknown");
                            preliminarIntegerList.add(unknownTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerDRM.setString(1,"ascribe");
                        psTransactionsPerDRM.setString(2,"blockai");
                        psTransactionsPerDRM.setString(3,"monegraph");
                        psTransactionsPerDRM.setString(4,"%2017%");
                        yddrmrs = psTransactionsPerDRM.executeQuery();
                        while(yddrmrs.next()){
                            drmTransactions = yddrmrs.getInt("transactionsperDRM");
                            preliminarStringList.add("DRM");
                            preliminarIntegerList.add(drmTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerMessage.setString(1,"eternitywall");
                        psTransactionsPerMessage.setString(2,"%2017%");
                        ydmrs = psTransactionsPerMessage.executeQuery();
                        while(ydmrs.next()){
                            messageTransactions = ydmrs.getInt("transactionspermessage");
                            preliminarStringList.add("Message");
                            preliminarIntegerList.add(messageTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);

                        psTransactionsPerSubchain.setString(1,"blockstore");
                        psTransactionsPerSubchain.setString(2,"%2017%");
                        ydsrs = psTransactionsPerSubchain.executeQuery();
                        while(ydsrs.next()){
                            subchainTransactions = ydsrs.getInt("transactionspersubchain");
                            preliminarStringList.add("Subchain");
                            preliminarIntegerList.add(subchainTransactions);
                        }

                        preliminarMap.put(preliminarStringList,preliminarIntegerList);
                        comparizonBetweenYearsMap.put(yearDummy,preliminarMap);



                        break;
                }
            }



        }catch(SQLException e){

            e.printStackTrace();

        }finally {

            try {
                if (conn != null)

                    conn.close();

                if (stmnt != null)

                    stmnt.close();

                if(psTransactionsPerUnknown != null)

                    psTransactionsPerUnknown.close();

                if(stmnt2 != null)

                    stmnt2.close();

                if(psTransactionPerProtocol != null)

                    psTransactionPerProtocol.close();

                if(psAverageUse != null)

                    psAverageUse.close();

                if(psTransactionsPerDRM != null)

                    psTransactionsPerDRM.close();

                if(psTransactionsPerEmpty!= null)

                    psTransactionsPerEmpty.close();

                if(psTransactionsPerFinancial != null)

                    psTransactionsPerFinancial.close();

                if(psTransactionsPerMessage != null)

                    psTransactionsPerMessage.close();

                if(psTransactionsPerNotary != null)

                    psTransactionsPerNotary.close();

                if(psTransactionsPerSubchain != null)

                    psTransactionsPerSubchain.close();

                if(psTransactionsPerYear != null)

                    psTransactionsPerYear.close();

            } catch(SQLException e){

                e.printStackTrace();
            }
        }


        //Setting data to be generated in jsp
        for(String s : transactionsPerCategoriesMap.keySet()){
            if(s.contains("Unknown")){
                requestedCategoriesMap.put("Unknown",transactionsPerCategoriesMap.get("Unknown"));
            }
            if(s.contains("Empty")){
                requestedCategoriesMap.put("Empty",transactionsPerCategoriesMap.get("Empty"));
            }

            if(s.contains("Financial")){
                financialCount += transactionsPerCategoriesMap.get(s);
            }

            if(s.contains("Notary")){
                notaryCount += transactionsPerCategoriesMap.get(s);
            }

            if(s.contains("DRM")){
                drmCount += transactionsPerCategoriesMap.get(s);
            }

            if(s.contains("Subchain")){
                subchainCount += transactionsPerCategoriesMap.get(s);
            }

            if(s.contains("Message")){
                messageCount += transactionsPerCategoriesMap.get(s);
            }
        }

        requestedCategoriesMap.put("Financial",financialCount);
        requestedCategoriesMap.put("Notary",notaryCount);
        requestedCategoriesMap.put("DRM",drmCount);
        requestedCategoriesMap.put("Message",messageCount);
        requestedCategoriesMap.put("Subchain",subchainCount);


        List<Integer> transactionsProtocols = new ArrayList<>();




        for(List<Integer> l : protocolChart.values()){
            transactionsProtocols.add(l.get(0)); //First element only is needed
        }


        List<List<String>> collectionK = returnCollectionKeys((returnLasts(comparizonBetweenYearsMap)).values());
        List<List<Integer>> collectionV = returnCollectionValues((returnLasts(comparizonBetweenYearsMap)).values());

        JSONArray categoriesComparison = new JSONArray(collectionK.get(0));
        List<Integer> lastYearCategoriesTransactions = collectionV.get(0);
        List<Integer> preLastYearCategoriesTransactions = collectionV.get(1);




        //Preparing response
        response.setContentType("application/json");
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, "
                + "must-revalidate");



        //Setting request
        request.setAttribute("years",toIntegerListKeys(transactionsPerYearMap));
        request.setAttribute("transactions_years",toIntegerListValues(transactionsPerYearMap));
        request.setAttribute("protocolsJsonFormat",toJsonArrayKeys(protocolChart));
        request.setAttribute("transactionsProtocols",transactionsProtocols);
        request.setAttribute("transactionsCategories",toIntegerListValues(requestedCategoriesMap));
        request.setAttribute("categoriesJsonFormat",   toJsonArrayKeys(requestedCategoriesMap));
        request.setAttribute("transactionsOfMostUsed",toIntegerListValues(findGreatest(reduce(protocolChart),MOSTUSED)));
        request.setAttribute("mostUsedProtocols",toJsonArrayKeys(findGreatest(reduce(protocolChart),MOSTUSED)));
        request.setAttribute("lastsYears",toIntegerListKeys(comparizonBetweenYearsMap));
        request.setAttribute("categoriesComparison",categoriesComparison);
        request.setAttribute("lastYearCategoriesTransactions",lastYearCategoriesTransactions);
        request.setAttribute("preLastYearCategoriesTransactions",preLastYearCategoriesTransactions);
        request.getRequestDispatcher("WEB-INF/jsp/Charts.jsp").forward(request,response); //Send request to JSP

    }

    //Method to find Most Used Protocols. It requires a Comparator to compare entries of HashMap and a PriorityQueue to set protocols with the greatest number of transactions
    private  <K, V extends Comparable<? super V>> Map<K, V> findGreatest(Map<K, V> map, int n) {
        //Setting of a Comparator
       Comparator<? super Entry<K,V>> comparator =
               (Comparator<Entry<K, V>>) (e0, e1) -> {
                   V v0 = e0.getValue();
                   V v1= e1.getValue();
                   return v0.compareTo(v1);
               };
       //Setting the PriorityQueue
       PriorityQueue<Entry<K,V>> highest = new PriorityQueue<>(n,comparator);
       for (Entry<K,V> entry : map.entrySet()){
           highest.offer(entry);
           while(highest.size() > n){ //Deleting from Queue
               highest.poll();
           }
       }

       Map<K,V> result = new HashMap<>();
        //Putting protocols inside HashMap
        while (highest.size() > 0)
        {
            result.put(highest.peek().getKey(),highest.peek().getValue()); //Peeking Entry of most used protocol and putting it inside the resulting HashMap
            highest.poll(); //Deleting element from the queue
        }
        return result;


    }

    //Useful to get only few datas from HashMap
    private <K,V> Map<K,V> reduce(Map<K,List<V>> mapToReduce){

        Map<K,V> result = new HashMap<>();

        for(K key : mapToReduce.keySet()){
            result.put(key,mapToReduce.get(key).get(0));
        }

        return result;
    }

    private  <K,V> LinkedHashMap<K,V> returnLasts(Map<K,V> mapToAnalyze){

        LinkedHashMap<K,V> result = new LinkedHashMap<>();

        List<Entry<K,V>> entryList =  new ArrayList<>(mapToAnalyze.entrySet());
        Entry<K,V> lastEntry = entryList.get(entryList.size()-1);
        Entry<K,V> previousLastEntry = entryList.get(entryList.size()-2);

        result.put(lastEntry.getKey(),lastEntry.getValue());
        result.put(previousLastEntry.getKey(),previousLastEntry.getValue());

        return result;

    }

    private <K,V> List<K> toIntegerListKeys(Map<K,V> mapToAnalyze){

        List<K> listOfIntegersKeys = new ArrayList<>(mapToAnalyze.keySet());
        return listOfIntegersKeys;
    }

    private <K,V> List<V> toIntegerListValues(Map<K,V> mapToAnalyze){

        List<V> listOfIntegersKeys = new ArrayList<>(mapToAnalyze.values());
        return listOfIntegersKeys;
    }

    private <K,V> JSONArray toJsonArrayKeys(Map<K,V> mapToAnalyze){

        JSONArray listOfJSONKeys = new JSONArray(mapToAnalyze.keySet());
        return listOfJSONKeys;
    }

    private <K,V> JSONArray toJsonArrayValues(Map<K,V> mapToAnalyze){

        JSONArray listOfJSONValues = new JSONArray(mapToAnalyze.values());
        return listOfJSONValues;
    }

    private<K,V> List<K> returnCollectionKeys(Collection<LinkedHashMap<K,V>> myCollection){

        List<K> result = new ArrayList<>();

        for(LinkedHashMap<K,V> map : myCollection){
            for(K key : map.keySet()){
                result.add(key);
            }
        }

        return result;
    }

    private<K,V> List<V> returnCollectionValues(Collection<LinkedHashMap<K,V>> myCollection){

        List<V> result = new ArrayList<>();

        for(LinkedHashMap<K,V> map : myCollection){
            for(V value : map.values()){
                result.add(value);
            }
        }

        return result;
    }




}
