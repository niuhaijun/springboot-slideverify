package com.niu.springbootslideverify.service.impl;

import com.niu.springbootslideverify.service.SlideVerifyService;
import com.niu.springbootslideverify.utils.RandomCutPicUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * 滑动认证服务类
 *
 *
 * @Author: niuhaijun
 * @Date: 2019-08-01 17:59
 * @Version 1.0
 */
@Service
public class SlideVerifyServiceImpl implements SlideVerifyService {


  /**
   * 将大图和小图都以文件的形式输出到本地
   */
  public void outputImageFile() throws Exception {

    int index = 1 + (int) (Math.random() * (20 - 1 + 1));

    Resource resource = new ClassPathResource("background/" + index + ".jpg");
    File bgPath = resource.getFile();

    Map<String, BufferedImage> resultMap = RandomCutPicUtils.generateTwoImage(bgPath);

    BufferedImage big = resultMap.get("big");
    BufferedImage small = resultMap.get("small");

    RandomCutPicUtils.outputImageFile(big,
        "/Users/niuhaijun/springboot-slideverify/src/main/resources/tmp/背景图.png");
    RandomCutPicUtils.outputImageFile(small,
        "/Users/niuhaijun/springboot-slideverify/src/main/resources/tmp/切图.png");
  }


}
