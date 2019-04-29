package com.connect;

import com.arcsoft.sdk_demo.model.StuInfo;

import java.util.List;

public class StuHolsJson {


    /**
     * status : true
     * msg : 提交成功
     * data : {"list":[{"stu_id":419},{"stu_id":423}],"count":2}
     */

    private boolean status;
    private String msg;
    private DataBean data;

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
         * list : [{"stu_id":419},{"stu_id":423}]
         * count : 2
         */

        private int count;
        private List<ListBean> list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * stu_id : 419
             */

            private int stu_id;

            public int getStu_id() {
                return stu_id;
            }

            public void setStu_id(int stu_id) {
                this.stu_id = stu_id;
            }
        }
    }

    public List<StuInfo> setStuLeaveInfo(List<StuInfo> stu){

        if(data.count>0) {
            int flag=0;
            for (int i=0;i<stu.size();i++) {
                flag=0;
                for (DataBean.ListBean d : data.list) {
                    if(d.stu_id==stu.get(i).stuId){
                        stu.get(i).isLeave=true;
                        stu.get(i).status=2;
                        flag=1;
                    }
                }
                if(flag==0){
                    stu.get(i).isLeave=false;
                    stu.get(i).status=3;
                }
            }
        }
        else{
            for (int i=0;i<stu.size();i++) {
                stu.get(i).isLeave=false;
                stu.get(i).status=3;
            }
        }
        return stu;

    }


}
