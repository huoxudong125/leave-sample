# leaveRecord-sample
## 一、说明

本代码源于极客时间《DDD实战课》，DDD知识体系和代码详解可参考专栏。

在《DDD实战课》专栏第18节中我们用事件风暴完成了“在线请假考勤”项目的领域建模和微服务设计。
我们一起从程序员的视角去看看用DDD方法设计和开发出来的微服务代码到底是什么样的？

## 二、项目回顾

> “在线请假考勤”项目中，请假的核心业务流程是：“请假人填写请假单提交审批。根据请假人身份、请假类型和请假天数进行校验并确定审批规则。根据审批规则确定审批人，逐级提交上级审批，逐级核批通过则完成审批，否则审批不通过则退回申请人。”

在第18节的DDD领域建模和微服务设计中，我们已经拆分出了两个微服务：`请假`和`考勤`微服务。
本部分是请假微服务的示例代码，采用的开发语言和数据库分别是：`Java`、`Spring boot`和`PostgreSQL`。

## 三、请假微服务采用的DDD设计思想

请假微服务中用到了很多DDD设计思想和方法，主要包括以下几点。

1. 聚合的管理：聚合根、实体和值对象的关系。

2. 聚合数据的初始化和持久化：工厂和仓储模式。

3. 聚合的解耦：聚合代码的解耦、跨聚合的服务调用和对象解耦。

4. 领域事件管理：领域事件实体结构、持久化和事件发布。

5. DDD分层架构：基础层、领域层、应用层和用户接口层的协作。

6. 服务的分层与协作：实体方法、领域服务、应用服务、接口服务，服务的组合和编排，跨多个聚合的服务管理和协同。

7. 对象的分层和转换：DTO、DO和PO等对象在不同层的转换和实现过程。

8. 微服务之间的访问：登录和认证服务。

## REFERENCE
[USING MYSQL IN SPRING BOOT VIA SPRING DATA JPA AND HIBERNATE](https://blog.netgloo.com/2014/10/27/using-mysql-in-spring-boot-via-spring-data-jpa-and-hibernate/)

## DEMO CODE
### Person
#### Create person 
`POST` http://localhost:8080/person
       
- Request (`Content-Type`:`application/json;charset=UTF-8`)
```json
{
    "personId":"1",
    "personName": "test",
    "roleId": "1",
    "personType": "INTERNAL",
    "createTime": "2020-01-23 12:00:00",
    "lastModifyTime": "2020-01-23 12:00:00",
    "status": "ENABLE"
}
```
- Response
```json
{
    "status": "SUCCESS",
    "msg": null,
    "data": null
}
```
