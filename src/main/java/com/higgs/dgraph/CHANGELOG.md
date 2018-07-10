1. Dgraph入库的操作流程

现有操作流程介绍

# 1.1 json object的方式
  实体的表示: json object的形式，任何非json object的格式的数据，都需要考虑是否需要入库。
  子实体支持单个object或者以JsonArray的格式
  JsonArray: 内部是string，int等类型的可以插入为list type，如果内部还是jsonObject那么就是多个子实体
  以整个json对象put，如果json对象中存在实体之前的连接关系，子实体需要写回对应的uid到子实体中，
    这样建立的子实体才是唯一的，不会重复。见：NodeUtil.putEntity。

  ### 拆分流程：
    * 子实体先入库，获取到了对应的uid后写回到子实体对象中，写回见：NodeUtil.putEntityUid。
    * 最后入库外层的实体对象


# 1.2 以rdf格式的方式
  子实体支持单个实体对象或者List<EntityNode>的方式
  同样需要建立对应的实体json对象，不过入库的时候会抽取其中的关系和属性值进行入库。
    不是以整个json对象直接入库，见：NodeUtil.insertEntity

  ### 拆分流程
      * 同样子实体需要先入库，获取到了对应的uid后写回到子实体对象中,写回见：NodeUtil.putEntityUid。
      * 最后再入外层的实体，同时也会入库外层实体与子实体的关系，关系通过获取子实体对应的uid绑定，
        所以就是为什么需要写回子实体的uid， 否则子实体的uid为null，无法建立实体之间的关系。



# 2. 更新日志：

## 2018.5.12
        * 支持获取类似List<EntityNode>实体关系的解析
        * 支持最外层实体存在uid，rdf入库时直接使用该uid入库
        * 支持出现mutate exception[ DEADLINE_EXCEEDED ]时不能将retMap输入到entityId服务中，该uid无效
            (已确认确认是需要重置DgraphProto.Assigned，该异常会丢失数据, 可相应减少batch数和map的数量，and retry mutation)

        * 多线程修改一个实体的属性时: 导致transition abort, try @upsert in some attrs,
            but this attr must be index tokenizer
## 2018.5.17
        * 子实体的uid在返回uidMap之前,就已经写回到实体中。

        * 支持: 多个unique_ids写入到id服务器
## 2018.5.22
        * 支持单独添加edge facets的操作。

## 2018.5.24
        * 支持按照uid list检查dgraph中是否存在对应数据。(因为dgraph server突然挂了，重新启动后，从日志恢复的完整的数据了吗？？)

## 2018.5.29
        * uidReMapping的时候如果获取unique_ids为空时， 使用unique_id;
        * 增加对实体unique_id的检查: NodeUtil.checkUniqueId
        * 支持添加facet

## 2018.6.5

        * list type: schema中predicate声明为list type,重复赋值该predicate,不会覆盖

## 2018.7.5

        *  rdf文件所有实体的xid : type + : + unique_id （NodeUtil.getEntityNquads()）

        *  先使用bulk loader导入，然后导出database, 通过导出的rdf绑定外部id和uid之前的关系

        



# 3. 发现问题

    * 以rdf set { _:leia <name> "Princess Leia New" . } 的形式插入数据到dgraph，同一个批次下唯一一个unique_id返回对应的uid
       不同批次下面的set对应同样一个unique_id其返回的uid是不同的，所以检查uid是否存在很重要， 不然就有重复的实体存在。

       解决办法: 保存失败的批次数据，后面重新写该部分数据。 尝试更少的batch看超时的比例

    * json object 形式的入库: 部分子实体uid没有被重新填入（实体id服务没有该实体的时候)，该实体会被写入（导致uidFlatten出错）

    * 查询: uid(,,): 多个uid的方式query数据，返回结果是按uid排好序的，不是一一对应uid查询list的顺序。

    * dgraph中如果只存储uid中间的关系，那如果从其中获取到了一个uid, 该uid没有基本存储基本属性，如果获取详情，
        所有必须有唯一的unique_id存储到dgraph中,同样需要保留类型到dgraph中（entity_ids）

    * groupby的时候：predicate为空的时候，默认是不统计的，空字符的话可选择改成其他

    * facets的属性是覆盖式的，不是新增式的， 如果先前存在的属性，在修改facets时没有加入，那么先前属性就不存在了。

    * rdf file中nquad格式的facets:
        eg  _:候选人:26689f2b8292170d4221d03844295ea5 <candidate_dept>
        _:公司部门:7a9cfaf8eaec9aebc890b94a292717af (on_job=false,started_at=20010-10-01T00:00:00.000Z,ended_at=2012-12-01T00:00:00.000Z,salary=0.0) ."

      dataTime 要用引号包起来 （started_at="20010-10-01T00:00:00.000Z"), 不然解析不了

# 4. export dgraph database

    检查cluster leader node

       http://localhost:6080/state

    在leader node 节点运行

    ` curl localhost:8080/admin/export &` 导出database

    如果想要reuse导出db的uid? 并指定新生成的out目录下的p重新server？ 如何操作? 需要保留zw和w目录么


    详细情况参考：https://docs.dgraph.io/deploy/#export-database




