package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.ItemVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.ItemModel;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
//解决跨域
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private PromoService promoService;


    //创建商品
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes = {})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name="title") String title,
                                       @RequestParam(name="description") String description,
                                       @RequestParam(name="price") BigDecimal price,
                                       @RequestParam(name="stock") Integer stock,
                                       @RequestParam(name="imgUrl") String imgUrl) throws BusinessException {

        //封装service请求用来创建商品
        //尽量让Controller层简单，让Service层负责，把服务逻辑尽可能聚合在Service层内部，实现流转处理
        //创建给service层的
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        //创建商品，返回itemModel
        ItemModel itemModelForReturn = itemService.createItem(itemModel);

        //返回给前端
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);

        //将vo转json给页面
        return  CommonReturnType.create(itemVO);

    }

    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name="id") Integer id){

        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_"+id);

        if(itemModel == null){
            itemModel = itemService.getItemById(id);
            redisTemplate.opsForValue().set("item_"+id,itemModel);
            redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
        }

        ItemVO itemVO = convertVOFromModel(itemModel);

        return CommonReturnType.create(itemVO);

    }

    //商品详情页浏览
    @RequestMapping(value = "/publishpromo",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType publishpromo(@RequestParam(name="id") Integer id){

        promoService.publishPromo(id);

        return CommonReturnType.create(null);
        
    }

    //商品列表页浏览
    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList =  itemService.listItem();
        //将model转为vo
        //使用stream api 将list内的itemModel转为ItemVO
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {

            ItemVO itemVO = this.convertVOFromModel(itemModel);
            return itemVO;

        }).collect(Collectors.toList());

        return CommonReturnType.create(itemVOList);
    }


    private ItemVO convertVOFromModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);

        if(itemModel.getPromoModel()!=null){
            //有正在进行或即将开始的秒杀活动
            //设置秒杀活动状态
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            //设置秒杀活动id
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            //设置秒杀活动开始时间
            itemVO.setStartTime(itemModel.getPromoModel().getStartTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            //秒杀价格
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            //该商品没有秒杀活动
            itemVO.setPromoStatus(0);
        }

        return itemVO;
    }

}
