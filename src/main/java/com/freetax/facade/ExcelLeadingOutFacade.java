package com.freetax.facade;

import com.freetax.mybatis.user.entity.User;
import com.freetax.mybatis.user.entity.UserExcel;
import com.freetax.mybatis.user.service.UserService;
import com.freetax.utils.PropertiesLoader;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author zhurui
 * @Date 2017/11/29 11:54
 */
@Service
public class ExcelLeadingOutFacade {

    @Autowired
    private UserService userService;

    private static Logger log = LoggerFactory.getLogger(ExcelLeadingOutFacade.class);
    public Map excelLeadingOutByUser(){
        Map resault = new HashMap();
        try {
            //t.xls为要新建的文件名
            //String path = "d:/1";
            //查询帖子保存路径
            String path = PropertiesLoader.getValue("excel.domain");
            User user = new User();
            UUID uuid = UUID.randomUUID();
            //查询出所有xml导入的帖子
            List<UserExcel> users = userService.queryUserByAll();
            String urlname = "/" + getDateString()+ uuid + ".xls";
            log.info("...................."+path + urlname);
            WritableWorkbook book = Workbook.createWorkbook(new File(path + urlname));
            //生成名为“第一页”的工作表，参数0表示这是第一页
            WritableSheet sheet = book.createSheet("第一页", 0);

            /*PostXml postXml = new PostXml();
            Field[] fields = postXml.getClass().getDeclaredFields();*/
            String title[] = {"id", "邮箱", "手机", "头像", "注册时间", "信息来源", "姓名", "公司名称"};
            //设计表头
            for (int i = 0; i < title.length; i++) {
                sheet.addCell(new Label(i, 0, title[i]));
            }
           /* Post post = new Post();
            //获取对象长度
            Field[] p = post.getClass().getDeclaredFields();*/
            //遍历循环出集合中每一个元素，写到表中
            addExcelFileElement(sheet, users);
            //
            //写入数据
            book.write();
            if (book != null) {
                //关闭流
                book.close();
            }
            //用于返回文件路径
            String reurl = PropertiesLoader.getValue("excel.file.url");
            //String reurl = "d:/1/";
            reurl += "excel" + urlname;
            resault.put("code", 200);
            resault.put("date", reurl);
            resault.put("massger", "成功");
            return resault;
        } catch (Exception e) {
            e.printStackTrace();
            resault.put("code", 400);
            resault.put("date", -1);
            resault.put("massger", "失败");
            return resault;
        }
    }

    public String getDateString(){
        Long l = new Date().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(l);
        String name = simpleDateFormat.format(date);
        return name;
    }

    /**
     * 为excel文件添加元素
     *
     * @param sheet
     * @param users
     * @throws IllegalAccessException
     * @throws WriteException
     */
    private void addExcelFileElement(WritableSheet sheet, List<UserExcel> users) throws Exception {
        for (int i = 0; i < users.size(); i++) {
            UserExcel userExcel = users.get(i);
            //写到表格中
            setExcelCell(sheet, i, userExcel);
        }
    }

    /**
     * 表格中填入数据
     * @param sheet
     * @param i
     * @param user
     * @throws WriteException
     */
    //{"id", "邮箱", "手机", "头像", "注册时间", "信息来源", "姓名", "公司名称"}
    private void setExcelCell(WritableSheet sheet, int i, UserExcel user) throws Exception {
        //获取对象长度
        Field[] p = user.getClass().getDeclaredFields();
        int k = 0;
        while (k < p.length) {
            if (k == 0) {
                sheet.addCell(new Label(k, i + 1, user.getId().toString()));
            }
            if (k == 1) {
                sheet.addCell(new Label(k, i + 1, user.getEmail()));
            }
            if (k == 2) {
                sheet.addCell(new Label(k, i + 1, user.getPhone()));
            }
            if (k == 3) {
                sheet.addCell(new Label(k, i + 1, user.getPhoto()));
            }
            if (k == 4) {
                SimpleDateFormat sdf1= new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

                SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sheet.addCell(new Label(k, i + 1, sdf2.format(sdf1.parse(user.getIntime().toString())) + ""));
            }
            if (k == 5) {
                if (user.getInfosource() == 1){
                    sheet.addCell(new Label(k, i + 1, "招商加盟"));
                } else {
                    sheet.addCell(new Label(k, i + 1, "业务咨询"));
                }

            }
            if (k == 6) {
                sheet.addCell(new Label(k, i + 1, user.getName()));
            }
            if (k == 7) {
                sheet.addCell(new Label(k, i + 1, user.getCompany()));
            }
            k++;
        }
    }


}
