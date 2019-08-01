package com.niu.springbootslideverify;

import com.niu.springbootslideverify.service.impl.SlideVerifyServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootSlideverifyApplicationTests {


  @Autowired
  private SlideVerifyServiceImpl service;

  @Test
  public void contextLoads() throws Exception {

    service.outputImageFile();

  }

}
