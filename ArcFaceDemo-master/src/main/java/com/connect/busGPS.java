package com.connect;

import java.util.List;

public class busGPS {


    /**
     * code : 0
     * msg : 0
     * count : 1
     * data : [{"busid":2,"busnumber":"5111","lon":"108.93984","lat":"34.34127","time":"2018-11-08 00:00:00","speed":"4","postxt":"在附近"}]
     */

    private int code;
    private int msg;
    private int count;
    private int onoff;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getOnoff() {
        return onoff;
    }

    public void setOnoff(int onoff) {
        this.onoff = onoff;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * busid : 2
         * busnumber : 5111
         * lon : 108.93984
         * lat : 34.34127
         * time : 2018-11-08 00:00:00
         * speed : 4
         * postxt : 在附近
         */

        private int busid;
        private String busnumber;
        private String lon;
        private String lat;
        private String time;
        private String speed;
        private String postxt;

        public int getBusid() {
            return busid;
        }

        public void setBusid(int busid) {
            this.busid = busid;
        }

        public String getBusnumber() {
            return busnumber;
        }

        public void setBusnumber(String busnumber) {
            this.busnumber = busnumber;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getPostxt() {
            return postxt;
        }

        public void setPostxt(String postxt) {
            this.postxt = postxt;
        }
    }
}
