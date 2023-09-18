# 项目背景  
API接口调用平台，帮助企业、个人统一开放接口，减少沟通成本，避免重复造轮子，为业务高效赋能。  

普通用户：使用接口。  

管理员：调用统计和可视化分析接口调用情况，管理员发布接口、下线接口、新增接口。  

## 主要功能： 

API接入  
鉴权认证  
防止攻击（隐藏接口服务）  
统计调用次数（异常回滚）  
日志  
负载均衡  
限流  


## 架构图:  
![image](https://github.com/z1013007148/myAPI-backend/blob/master/img/%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

## 技术选型:  
Spring Boot  
Spring Boot Starter(SDK开发)  
Mysql数据库  
MyBatis-Plus  
Redis (缓存、限流、防重放)  
RabbitMQ（异常回滚）  
Dubbo (RPC)  
Nacos (注册中心)  
Spring Cloud Gateway (网关、负载均衡、日志等实现)  
Swagger+Knife4j (接口文档生成)  
Hutool、Gson等工具库  
