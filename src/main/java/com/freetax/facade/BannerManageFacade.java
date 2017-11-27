package com.freetax.facade;

import com.freetax.mybatis.bannerManage.entity.BannerManage;
import com.freetax.mybatis.bannerManage.entity.IndexInformation;
import com.freetax.mybatis.bannerManage.service.BannerManageService;
import com.freetax.mybatis.information.entity.Information;
import com.freetax.mybatis.information.service.InformationService;
import com.freetax.utils.ImcisionImgUtils;
import com.freetax.utils.MovisionOssClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Author zhurui
 * @Date 2017/11/13 9:48
 */
@Service
public class BannerManageFacade {

    @Autowired
    private BannerManageService bannerManageService;

    @Autowired
    private InformationService informationService;

    @Autowired
    private ImcisionImgUtils imcisionImgUtils;

    @Autowired
    private MovisionOssClient movisionOssClient;

    public IndexInformation queryIndexBannerOrInformation(){
        IndexInformation indexInformation = new IndexInformation();
        List<BannerManage> blist =  bannerManageService.queryIndexBannerOrInformation();
        List<BannerManage> carouselFigure = new ArrayList();//轮播图
        List toPlan = new ArrayList();//筹划背景
        List information = new ArrayList();//资讯
        if (blist != null) {
            int k = 0;
            for (int i = 0; i < blist.size(); i++) {
                switch (blist.get(i).getLocation()) {
                    //上方banner
                    case 1:
                        indexInformation.setBanner(blist.get(i).getBanner());
                        continue;
                    //主页滚动banner
                    case 2:
                        carouselFigure.add(blist.get(i));
                        k++;
                        continue;
                    //税务筹划背景图片
                    case 3:
                        if (blist.get(i).getOrderid() == 1){
                            toPlan.add(blist.get(i));
                        }else if (blist.get(i).getOrderid() == 2) {
                            toPlan.add(blist.get(i));
                        }
                        continue;
                    //收入对比图
                    case 6:
                        indexInformation.setIncomeComparison(blist.get(i).getBanner());
                        continue;
                }
            }
            indexInformation.setCarouselFigure(carouselFigure);
            indexInformation.setToPlan(toPlan);
            //查询精选资讯
            List<Information> informations = informationService.queryInformationIsHot();
            indexInformation.setInformation(informations);
        }else {
            return null;
        }
        return indexInformation;
    }

    /**
     * 查询pc中banner
     * @param type
     * @return
     */
    public List<BannerManage> queryBannerByType(String type){
        return bannerManageService.queryBannerByType(Integer.parseInt(type));
    }



    public String bannerImgIncision(MultipartFile file,String x,String y,String w,String h){
        //1上传到服务器
        Map m = movisionOssClient.uploadMultipartFileObject(file);
        String url = String.valueOf(m.get("url"));//获取上传到服务器上的原图
        System.out.println("上传banner的原图url==" + url);

        /*//切割图片
        Map whs = new HashMap();
        whs.put("w", w);
        whs.put("h", h);
        whs.put("x", x);
        whs.put("y", y);
        Map tmpurl = imcisionImgUtils.imgCuttingUpload(url, whs);

        //3获取本地服务器中切割完成后的图片
        String tmpurls = String.valueOf(tmpurl.get("new"));
        System.out.println("切割完成后图片路径===" + tmpurls);*/
        return url;
    }


    /**
     * 新增广告管理
     * @param banner
     * @param describe
     * @param orderid
     * @param title
     * @param location
     */
    public void insertAdvertisement(String banner,String describe,String orderid,String title,String location){
        BannerManage bannerManage = new BannerManage();
        if (StringUtils.isNotEmpty(banner)){
            bannerManage.setBanner(banner);
        }
        if (StringUtils.isNotEmpty(describe)){
            bannerManage.setDes(describe);
        }
        if (StringUtils.isNotEmpty(orderid)){
            bannerManage.setOrderid(Integer.parseInt(orderid));
        }
        if (StringUtils.isNotEmpty(title)){
            bannerManage.setTitle(title);
        }
        if (StringUtils.isNotEmpty(location)){
            bannerManage.setLocation(Integer.parseInt(location));
        }
        bannerManage.setIsdel(0);
        bannerManage.setIntime(new Date());
        bannerManageService.insertAdvertisement(bannerManage);
    }

    /**
     * 编辑广告
     * @param banner
     * @param describe
     * @param orderid
     * @param title
     * @param location
     */
    public void updateAdvertisement(String banner,String describe,String orderid,String title,String location){
        BannerManage bannerManage = new BannerManage();
        if (StringUtils.isNotEmpty(banner)){
            bannerManage.setBanner(banner);
        }
        if (StringUtils.isNotEmpty(describe)){
            bannerManage.setDes(describe);
        }
        if (StringUtils.isNotEmpty(orderid)){
            bannerManage.setOrderid(Integer.parseInt(orderid));
        }
        if (StringUtils.isNotEmpty(title)){
            bannerManage.setTitle(title);
        }
        if (StringUtils.isNotEmpty(location)){
            bannerManage.setLocation(Integer.parseInt(location));
        }
        bannerManageService.updateAdvertisement(bannerManage);
    }

    /**
     * 根据id查询广告
     * @param id
     * @return
     */
    public BannerManage queryAdvertisementById(String id){
        return bannerManageService.queryAdvertisementById(Integer.parseInt(id));
    }


    /**
     * 条件查询广告列表
     * @param title
     * @param location
     * @return
     */
    public List<BannerManage> queryAdvertisementByList(String title,String location){
        BannerManage bannerManage = new BannerManage();
        if (StringUtils.isNotEmpty(title)){
            bannerManage.setTitle(title);
        }
        if (StringUtils.isNotEmpty(location)){
            bannerManage.setLocation(Integer.parseInt(location));
        }
        return bannerManageService.queryAdvertisementByList(bannerManage);
    }

    /**
     * 删除广告
     * @param id
     */
    public void deleteAdvertisement(String id){
        BannerManage bannerManage = new BannerManage();
        bannerManage.setId(Integer.parseInt(id));
        bannerManage.setIsdel(1);
        bannerManageService.deleteAdvertisement(bannerManage);
    }

}
