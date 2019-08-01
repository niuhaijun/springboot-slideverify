package com.niu.springbootslideverify.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import sun.misc.BASE64Encoder;

/**
 * 随机切割图片工具类
 *
 *
 * @Author: niuhaijun
 * @Date: 2019-08-01 17:32
 * @Version 1.0
 */
public class RandomCutPicUtils {

  /**
   * 背景图的宽、高
   */
  private static final int BG_WIDTH = 590;
  private static final int BG_HEIGHT = 360;

  /**
   * 模板图的宽、高
   */
  private static final int TEMPLATE_WIDTH = 200;
  private static final int TEMPLATE_HEIGHT = 100;

  /**
   * 圆的半径
   */
  private static final int CIRCLE_RADIO = 20;

  /**
   * 距离点
   */
  private static final int R1 = 10;

  // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

  /**
   * 生成抠图的轮廓
   */
  private static int[][] getTemplateImage() {

    int[][] data = new int[TEMPLATE_WIDTH][TEMPLATE_HEIGHT];
    double x2 = TEMPLATE_WIDTH - CIRCLE_RADIO;

    /**
     * 随机生成圆的位置
     */
    double h1 = CIRCLE_RADIO + Math.random() * (TEMPLATE_HEIGHT - 3 * CIRCLE_RADIO - R1);
    double po = CIRCLE_RADIO * CIRCLE_RADIO;

    double xBegin = TEMPLATE_WIDTH - CIRCLE_RADIO - R1;
    double yBegin = TEMPLATE_HEIGHT - CIRCLE_RADIO - R1;

    for (int i = 0; i < TEMPLATE_WIDTH; i++) {
      for (int j = 0; j < TEMPLATE_HEIGHT; j++) {

        // 计算 d3 = （i, j） (x2, h1)
        double d3 = Math.pow(i - x2, 2) + Math.pow(j - h1, 2);

        // 计算 d2 = （i, j） (h1, 2)
        double d2 = Math.pow(j - 2, 2) + Math.pow(i - h1, 2);

        if ((j <= yBegin && d2 <= po) || (i >= xBegin && d3 >= po)) {
          data[i][j] = 0;
        }
        else {
          data[i][j] = 1;
        }

      }
    }

    return data;
  }

  /**
   * 生成小图片、给大图片添加阴影
   *
   * @param bgImage       背景图
   * @param blankImage    抠图(空白)
   * @param templateImage 抠图模板
   * @param x             抠图开始坐标
   * @param y             抠图开始坐标
   */
  private static void cutByTemplate(BufferedImage bgImage, BufferedImage blankImage,
      int[][] templateImage, int x, int y) {

    for (int i = 0; i < TEMPLATE_WIDTH; i++) {
      for (int j = 0; j < TEMPLATE_HEIGHT; j++) {

        int rgb = templateImage[i][j];

        int rgb_ori = bgImage.getRGB(x + i, y + j);

        if (rgb == 1) {
          //抠图上复制对应颜色值
          blankImage.setRGB(i, j, rgb_ori);

//          int r = (0xff & rgb_ori);
//          int g = (0xff & (rgb_ori >> 8));
//          int b = (0xff & (rgb_ori >> 16));
//          rgb_ori = r + (g << 8) + (b << 16) + (150 << 24);
          //原图对应位置颜色变化
          bgImage.setRGB(x + i, y + j, rgb_ori & 0x363636);
        }
        else {
          //这里把背景设为透明
          blankImage.setRGB(i, j, rgb_ori & 0x00FFFFFF);
        }
      }
    }
  }

  /**
   * 获取大图，小图Base64码
   *
   * @param bgPath 背景图路径
   */
  public static Map<String, String> generateTwoImageBase64(String bgPath)
      throws Exception {

    Map<String, Integer> startPoint = getStartPoint();
    int xBegin = startPoint.get("xBegin");
    int yBegin = startPoint.get("yBegin");

    // 抠图轮廓
    int[][] templateImage = getTemplateImage();
    // 原始背景图
    BufferedImage bgImage = ImageIO.read(new FileInputStream(bgPath));
    // 空白抠图
    BufferedImage blankImage = new BufferedImage(TEMPLATE_WIDTH, TEMPLATE_HEIGHT,
        BufferedImage.TYPE_4BYTE_ABGR);

    cutByTemplate(bgImage, blankImage, templateImage, xBegin, yBegin);

    Map<String, String> resultMap = new HashMap<>(2);
    // 大图
    resultMap.put("big", getImageBASE64(bgImage));

    // 小图
    resultMap.put("small", getImageBASE64(blankImage));

    return resultMap;
  }

  /**
   * 获取大图，小图Base64码
   *
   * @param bgPath 背景图文件
   */
  public static Map<String, String> generateTwoImageBase64(File bgPath)
      throws Exception {

    Map<String, Integer> startPoint = getStartPoint();
    int xBegin = startPoint.get("xBegin");
    int yBegin = startPoint.get("yBegin");

    // 抠图轮廓
    int[][] templateImage = getTemplateImage();
    // 原始背景图
    BufferedImage bgImage = ImageIO.read(new FileInputStream(bgPath));
    // 空白抠图
    BufferedImage blankImage = new BufferedImage(TEMPLATE_WIDTH, TEMPLATE_HEIGHT,
        BufferedImage.TYPE_4BYTE_ABGR);

    cutByTemplate(bgImage, blankImage, templateImage, xBegin, yBegin);

    Map<String, String> resultMap = new HashMap<>(2);
    // 大图
    resultMap.put("big", getImageBASE64(bgImage));

    // 小图
    resultMap.put("small", getImageBASE64(blankImage));

    return resultMap;
  }

  /**
   * 获取大图，小图
   *
   * @param bgPath 背景图路径
   */
  public static Map<String, BufferedImage> generateTwoImage(String bgPath) throws Exception {

    Map<String, Integer> startPoint = getStartPoint();
    int xBegin = startPoint.get("xBegin");
    int yBegin = startPoint.get("yBegin");

    // 抠图轮廓
    int[][] templateImage = getTemplateImage();
    // 原始背景图
    BufferedImage bgImage = ImageIO.read(new FileInputStream(bgPath));
    // 抠图(空白)
    BufferedImage blankImage = new BufferedImage(TEMPLATE_WIDTH, TEMPLATE_HEIGHT,
        BufferedImage.TYPE_4BYTE_ABGR);

    cutByTemplate(bgImage, blankImage, templateImage, xBegin, yBegin);

    Map<String, BufferedImage> resultMap = new HashMap<>(2);
    //大图
    resultMap.put("big", bgImage);

    //小图
    resultMap.put("small", blankImage);

    return resultMap;
  }

  /**
   * 获取大图，小图
   *
   * @param bgPath 背景图文件
   */
  public static Map<String, BufferedImage> generateTwoImage(File bgPath) throws Exception {

    Map<String, Integer> startPoint = getStartPoint();
    int xBegin = startPoint.get("xBegin");
    int yBegin = startPoint.get("yBegin");

    // 抠图轮廓
    int[][] templateImage = getTemplateImage();
    // 原始背景图
    BufferedImage bgImage = ImageIO.read(new FileInputStream(bgPath));
    // 抠图(空白)
    BufferedImage blankImage = new BufferedImage(TEMPLATE_WIDTH, TEMPLATE_HEIGHT,
        BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = blankImage.createGraphics();
    graphics.setBackground(Color.white);

    cutByTemplate(bgImage, blankImage, templateImage, xBegin, yBegin);

    // 抗锯齿”的属性
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
    graphics.drawImage(blankImage, 0, 0, null);
    graphics.dispose();

    Map<String, BufferedImage> resultMap = new HashMap<>(2);
    //大图
    resultMap.put("big", bgImage);

    //小图
    resultMap.put("small", blankImage);

    return resultMap;
  }

  /**
   * 图片转BASE64
   */
  private static String getImageBASE64(BufferedImage image) throws IOException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(image, "png", out);
    //转成byte数组
    byte[] b = out.toByteArray();
    BASE64Encoder encoder = new BASE64Encoder();
    //生成base64编码
    return encoder.encode(b);
  }

  /**
   * 打印轮廓
   */
  private static void printTemplateImage(int[][] templateImage) {

    for (int i = 0; i < templateImage.length; i++) {
      for (int j = 0; j < templateImage[0].length; j++) {
        System.out.print(templateImage[i][j] + " ");
      }
      System.out.println();
    }
  }

  /**
   * 获取切图在背景图中的 开始点
   */
  private static Map<String, Integer> getStartPoint() {

    int x0 = 0;
    int x1 = BG_WIDTH - TEMPLATE_WIDTH;
    int xBegin = x0 + (int) (Math.random() * (x1 - x0));

    int y0 = 0;
    int y1 = BG_HEIGHT - TEMPLATE_HEIGHT;
    int yBegin = y0 + (int) (Math.random() * (y1 - y0));

    Map<String, Integer> map = new HashMap<>(2);
    map.put("xBegin", xBegin);
    map.put("yBegin", yBegin);
    return map;
  }

  /**
   * 图片文件输出
   *
   * @param image 图
   * @param imagePath 图的输出路径
   */
  public static void outputImageFile(BufferedImage image, String imagePath) throws Exception {

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(image, "png", os);
    byte[] newImages = os.toByteArray();
    FileOutputStream fos = new FileOutputStream(imagePath);
    fos.write(newImages);
    fos.close();
  }

  /**
   * 图片文件输出
   *
   * @param image 图
   * @param imagePath 图的输出路径
   */
  public static void outputImageFile(BufferedImage image, File imagePath) throws Exception {

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(image, "png", os);
    byte[] newImages = os.toByteArray();
    FileOutputStream fos = new FileOutputStream(imagePath);
    fos.write(newImages);
    fos.close();
  }

}
