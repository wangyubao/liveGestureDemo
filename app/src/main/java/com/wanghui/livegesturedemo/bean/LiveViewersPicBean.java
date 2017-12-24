package com.wanghui.livegesturedemo.bean;

import java.io.Serializable;
import java.util.List;

/**获得观众头像和人数
 * Created by dell on 2016/5/10.
 */
public class LiveViewersPicBean implements Serializable {
    public static final String URL = "api/room/liveUserPicList.php";
    public static final String METHOD = "getViewersPic";
    /**
     * list : [{"uid":"170","head":"http://dev-img.huanpeng.com//userPic/170/4d5b9fb6f8f599fa78b0a7ab37676dc0.png"}]
     * total : 1
     */

    private String total;
    /**
     * uid : 170
     * head : http://dev-img.huanpeng.com//userPic/170/4d5b9fb6f8f599fa78b0a7ab37676dc0.png
     */

    private List<ListBean> list;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String uid;
        private String head;
        private int img;

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getHead() {
            return head;
        }

        public void setHead(String head) {
            this.head = head;
        }
    }
}
