1. Dgraph入库的操作流程

现有操作流程介绍

1.1 json object的方式， 子实体支持单个object或者以JsonArray的格式

  以整个json对象put，如果json对象中存在实体之前的连接关系，子实体需要写回对应的uid到子实体中，
    这样建立的子实体才是唯一的，不会重复。见：NodeUtil.putEntity。

  拆分流程：
    * 子实体先入库，获取到了对应的uid后写回到子实体对象中，写回见：NodeUtil.putEntityUid。
    * 最后入库外层的实体对象


1.2 以rdf格式的方式, 子实体支持单个实体对象或者List<EntityNode>的方式

  同样需要建立对应的实体json对象，不过入库的时候会抽取其中的关系和属性值进行入库。
    不是以整个json对象直接入库，见：NodeUtil.insertEntity

    拆分流程：
      * 同样子实体需要先入库，获取到了对应的uid后写回到子实体对象中,写回见：NodeUtil.putEntityUid。
      * 最后再入外层的实体，同时也会入库外层实体与子实体的关系，关系通过获取子实体对应的uid绑定，
        所以就是为什么需要写回子实体的uid， 否则子实体的uid为null，无法建立实体之间的关系。



2. 更新日志：

    2018.5.12
         * 支持获取类似List<EntityNode>实体关系的解析
         * 支持最外层实体存在uid，rdf入库时直接使用该uid入库
         * 支持出现mutate exception[ DEADLINE_EXCEEDED ]时不能将retMap输入到entityId服务中，该uid无效
            (已确认确认是需要重置DgraphProto.Assigned，该异常会丢失数据, 可相应减少batch数和map的数量，and retry mutation)

         * 多线程修改一个实体的属性时: 导致transition abort, try @upsert in some attrs,
            but this attr must be index tokenizer

    2018.5.17
         * 子实体的uid在返回uidMap之前,就已经写回到实体中。

         * 支持: 多个unique_ids写入到id服务器

    2018.5.22
         * 支持单独添加edge facets的操作。


3. 发现问题

    以rdf set { _:leia <name> "Princess Leia New" . } 的形式插入数据到dgraph，同一个批次下唯一一个unique_id返回对应的uid
      不同批次下面的set对应同样一个unique_id其返回的uid是不同的，所以检查uid是否存在很重要， 不然就有重复的实体存在。

      解决办法: 保存失败的批次数据，后面重新写该部分数据。 尝试更少的batch看超时的比例

    json object 形式的入库: 部分子实体uid没有被重新填入（实体id服务没有该实体的时候)，该实体会被写入（导致uidFlatten出错）

    查询: uid(,,): 多个uid的方式query数据，返回结果是按uid排好序的，不是一一对应uid查询list的顺序。

