package com.example.project;

/**
 * Object of report, contain relevant data about reports, to know which users need to block from add new products and reports on other
 */
public class Report {
    //properties
    private final String whoAdd;

    /**
     * constructor
     * @param whoAdd - who add
     * @param whoReport - who report
     */
    public Report(String whoAdd, String whoReport) {
        this.whoAdd = whoAdd;
        MySql.insertReports(whoAdd, whoReport);
    }

    /**
     * count how much report have against specific user, if have 5 report he will block from the permission to report
     * @return - return the number of reports against the user
     */
    public int countWhoAdd() {
        return MySql.countWhoAdd(whoAdd);
    }
}