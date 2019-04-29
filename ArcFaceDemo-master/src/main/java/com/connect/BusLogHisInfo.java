package com.connect;

import java.util.List;

public class BusLogHisInfo {


    /**
     * status : true
     * msg : 提交成功
     * data : {"status":true,"studentlist":[{"stu_id":142,"stu_name":"成一铭","record_type":"1","identity_type":"2","station_name":"华府东门","tel1":"18609285990","tel2":""},{"stu_id":110,"stu_name":"赵奕雯","record_type":"1","identity_type":"2","station_name":"曲江兰亭","tel1":"15339051156","tel2":""},{"stu_id":114,"stu_name":"颜睿","record_type":"1","identity_type":"2","station_name":"诸子阶西门","tel1":"18192899996","tel2":""}],"count":3}
     */

    private int line_id;
    private String log_date;
    private int  type;
    private int takenCount;
    private boolean status;
    private String msg;
    private DataBean data;

    public int getLine_id() {
        return line_id;
    }

    public String getLog_date() {
        return log_date;
    }

    public int getType() {
        return type;
    }

    public void setLine_id(int line_id) {
        this.line_id = line_id;
    }

    public void setLog_date(String log_date) {
        this.log_date = log_date;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTakenCount() {
        return takenCount;
    }

    public void setTakenCount(int takenCount) {
        this.takenCount = takenCount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * status : true
         * studentlist : [{"stu_id":142,"stu_name":"成一铭","record_type":"1","identity_type":"2","station_name":"华府东门","tel1":"18609285990","tel2":""},{"stu_id":110,"stu_name":"赵奕雯","record_type":"1","identity_type":"2","station_name":"曲江兰亭","tel1":"15339051156","tel2":""},{"stu_id":114,"stu_name":"颜睿","record_type":"1","identity_type":"2","station_name":"诸子阶西门","tel1":"18192899996","tel2":""}]
         * count : 3
         */

        private boolean status;
        private int count;
        private List<StudentlistBean> studentlist;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<StudentlistBean> getStudentlist() {
            return studentlist;
        }

        public void setStudentlist(List<StudentlistBean> studentlist) {
            this.studentlist = studentlist;
        }

        public static class StudentlistBean {
            /**
             * stu_id : 142
             * stu_name : 成一铭
             * record_type : 1
             * identity_type : 2
             * station_name : 华府东门
             * tel1 : 18609285990
             * tel2 :
             */

            private int stu_id;
            private String stu_name;
            private String record_type;
            private String identity_type;
            private String station_name;
            private String tel1;
            private String tel2;

            public int getStu_id() {
                return stu_id;
            }

            public void setStu_id(int stu_id) {
                this.stu_id = stu_id;
            }

            public String getStu_name() {
                return stu_name;
            }

            public void setStu_name(String stu_name) {
                this.stu_name = stu_name;
            }

            public String getRecord_type() {
                return record_type;
            }

            public void setRecord_type(String record_type) {
                this.record_type = record_type;
            }

            public String getIdentity_type() {
                return identity_type;
            }

            public void setIdentity_type(String identity_type) {
                this.identity_type = identity_type;
            }

            public String getStation_name() {
                return station_name;
            }

            public void setStation_name(String station_name) {
                this.station_name = station_name;
            }

            public String getTel1() {
                return tel1;
            }

            public void setTel1(String tel1) {
                this.tel1 = tel1;
            }

            public String getTel2() {
                return tel2;
            }

            public void setTel2(String tel2) {
                this.tel2 = tel2;
            }
        }
    }
}
