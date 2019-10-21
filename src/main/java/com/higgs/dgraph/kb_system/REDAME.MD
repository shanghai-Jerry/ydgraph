# Dgraph Operation

on version 1.1.x

[主入口](https://github.com/shanghai-Jerry/ydgraph/blob/master/src/main/java/com/higgs/dgraph/kb_system/LoadMain.java)

[测试入口](https://github.com/shanghai-Jerry/ydgraph/blob/master/src/main/java/com/higgs/dgraph/kb_system/LoadMainTest.java)

## 1. 实体分类

从1.1开始，支持实体类型分类（dgraph.type）



## 2. 入库draph

* 实体入库

* 属性入库

* 关系入库

对应csv格式数据文件的解析参考 
[KbParseData](https://github.com/shanghai-Jerry/ydgraph/blob/master/src/main/java/com/higgs/dgraph/kb_system/KbParseData.java)

该文件封装了不同csv文件数据格式的解析过程，如有特殊格式，请自己编写对应的解析函数。