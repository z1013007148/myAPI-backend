#项目背景  
API接口调用平台，帮助企业、个人统一开放接口，减少沟通成本，避免重复造轮子，为业务高效赋能。  

普通用户：注册登录(还未开发)，开通接口调用权限（还未开发），使用接口。  

管理员：调用统计和可视化分析接口调用情况，管理员发布接口、下线接口、新增接口。  

##主要功能： 

API接入  
鉴权认证  
防止攻击（隐藏接口服务）  
统计调用次数  
计费  
日志  
流量保护  

##架构图:  
![image](.\img\架构图.png)

##技术选型:  
Spring Boot  
Spring Boot Starter(SDK开发)  
Mysql数据库  
MyBatis-Plus  
Dubbo (RPC)  
Nacos (注册中心)  
Spring Cloud Gateway (网关、限流、日志等实现)  
Swagger+Knife4j (接口文档生成)  
Hutool、Gson等工具库  