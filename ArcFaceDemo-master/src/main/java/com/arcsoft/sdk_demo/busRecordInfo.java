package com.arcsoft.sdk_demo;

import java.util.List;

public class busRecordInfo {

    /**
     * busnumber : 5111
     * lineId : 1
     * order : 1
     * totalCount : 50
     * qingjiaCount : 10
     * chengcheCount : 20
     * weichengCount : 1
     * studentlist : [{"stuid":143,"stuname":"周子涧","datetime":"2018","date":1,"postxt":"在哪","status":"请假","reason":""}]
     */
    public String lineName;

    private String busnumber;
    private int busId;
    private int lineId;
    private int order;
    private int totalCount;
    private int qingjiaCount;
    private int chengcheCount;
    private int weichengCount;
    private String datetime;
    private int date;
    private List<StudentlistBean> studentlist;

    public String getBusnumber() {
        return busnumber;
    }

    public void setBusnumber(String busnumber) {
        this.busnumber = busnumber;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getQingjiaCount() {
        return qingjiaCount;
    }

    public void setQingjiaCount(int qingjiaCount) {
        this.qingjiaCount = qingjiaCount;
    }

    public int getChengcheCount() {
        return chengcheCount;
    }

    public void setChengcheCount(int chengcheCount) {
        this.chengcheCount = chengcheCount;
    }

    public int getWeichengCount() {
        return weichengCount;
    }

    public void setWeichengCount(int weichengCount) {
        this.weichengCount = weichengCount;
    }


    public String getDatetime() {
        return this.datetime;
    }
    public void setDatetime(String dt) {
        this.datetime = dt;
    }
    public void setDate(int date) {
        this.date = date;
    }


    public List<StudentlistBean> getStudentlist() {
        return studentlist;
    }

    public void setStudentlist(List<StudentlistBean> studentlist) {
        this.studentlist = studentlist;
    }

    public static class StudentlistBean {
        /**
         * stuid : 143
         * stuname : 周子涧
         * datetime : 2018
         * date : 1
         * postxt : 在哪
         * status : 请假
         * reason :
         */

        private int stuid;
        private String stuname;
        private String datetime;
        private int date;
        private String postxt;
        private int status;
        private String reason;
        private String manual;

        public void setManual(String manual) {
            this.manual = manual;
        }

        public int getStuid() {
            return stuid;
        }

        public void setStuid(int stuid) {
            this.stuid = stuid;
        }

        public String getStuname() {
            return stuname;
        }

        public void setStuname(String stuname) {
            this.stuname = stuname;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public String getPostxt() {
            return postxt;
        }

        public void setPostxt(String postxt) {
            this.postxt = postxt;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
