package com.connect;

public class StatusJson {

    /**
     * status : true
     * msg : 获取成功
     * data : {"list":[{"family_tel":"13572155199","license_tag":"陕AR5111","com_tel":"02989318646","stu_name":"王芊予","teacher_tel":null,"bus_teacher_tel":"15398040739","bus_teacher_name":"王艳艳","driver_tel":null},{"family_tel":"13572155199","license_tag":"陕AN9100","com_tel":"02989318646","stu_name":"王辰予","teacher_tel":null,"bus_teacher_tel":"18991388271","bus_teacher_name":"石改宁","driver_tel":null}],"count":2}
     */

    private boolean status;
    private String msg;

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

}
