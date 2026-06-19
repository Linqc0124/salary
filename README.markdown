# 员工工资管理系统

本项目是一个基于 Java Web 技术栈 开发的薪资管理系统，用于实现企业内部员工薪资与用户信息管理。系统支持用户登录、薪资查询以及管理员对员工数据的维护等功能。

后端采用 Spring + MyBatis + JSP + Tomcat 架构，实现前后端分离的基础结构（JSP 作为视图层）。

🛠 技术栈  
- Java 17+  
- Spring Framework  
- MyBatis / MyBatis-Plus  
- JSP + JSTL  
- Tomcat 9  
- MySQL 8+  
- Druid 数据库连接池  
- Maven  
- Lombok  


🚀 功能模块  
👤 用户模块  
- 用户登录 / 退出  
- 修改密码  
- 查看个人信息  
💰 薪资模块  
- 查看个人薪资信息  
- 管理员维护薪资数据  
🧑‍💼 管理员模块  
- 员工信息管理（增删改查）  
- 薪资数据管理  
- 系统数据初始化  


🗄 数据库设计

数据库文件： salary_system.sql
使用 MySQL 执行 
导入方式：
```
CREATE DATABASE salary_system DEFAULT CHARACTER SET utf8mb4;
USE salary_system;
SOURCE salary_system.sql;
```
预设登录账密
管理员 admin amin
人事管理员 hr admin
财务管理员 finance amin
财务管理员 manager amin
审计员 audit amin

有可能会出现密码过期，直接在mysql里修改user表中的update_time