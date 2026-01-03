# 药品销售管理系统

## 项目简介
本项目为基于 Java Swing 的药品销售管理系统，采用 C/S 架构，支持局域网内客户端与服务器通信。系统实现了药品浏览、购物车、订单管理等核心功能，适用于药品销售场景的模拟与学习。

## 主要功能模块
1. 用户登录与认证
2. 密码加密存储（SHA-256）
3. 药品分类筛选
4. 药品详情查看
5. 购物车管理（添加、删除、修改数量、总价计算）
6. 订单提交与历史记录
7. 局域网 Socket 通信（客户端与服务器）

## 技术栈
1. Java SE 8 及以上
2. Swing GUI 框架
3. Socket 网络通信（TCP）
4. SHA-256 哈希加密（java.security 包）
5. 集合框架（ArrayList、HashMap 等）

## 目录结构说明
- Pages/ 目录下为主要功能类：
  - App.java：主程序入口
  - LoginFrame.java：登录界面
  - ShopList.java：药品列表与浏览
  - Cart.java：购物车管理
  - OrderHistory.java：订单历史
  - Constant.java：常量定义
- bin/ 目录：编译生成的 class 文件
- document/：开发文档存放目录

## 使用说明
1. 确保已安装 Java 8 及以上版本的 JDK
2. 克隆或下载ice_medical_server项目代码
3. 编译并运行服务器端代码，启动服务器
4. 克隆或下载ice_medical_system项目代码
5. 编译并运行客户端代码，启动客户端应用

## 其他说明
- 项目仅用于学习和课程设计，未接入真实数据库

## 致谢
感谢所有参与开发和测试的同学！
