1. Dgraph入库的操作流程

现有操作流程介绍

1.1 json object的方式， 子实体支持单个object或者以JsonArray的格式

  # 以整个json对象put，如果json对象中存在实体之前的连接关系，子实体需要写回对应的uid到子实体中，
    这样建立的子实体才是唯一的，不会重复。见：NodeUtil.putEntity。

  拆分流程：
    * 子实体先入库，获取到了对应的uid后写回到子实体对象中，写回见：NodeUtil.putEntityUid。
    * 最后入库外层的实体对象


1.2 以rdf格式的方式, 子实体支持单个实体对象或者List<EntityNode>的方式

  # 同样需要建立对应的实体json对象，不过入库的时候会抽取其中的关系和属性值进行入库。
    不是以整个json对象直接入库，见：NodeUtil.insertEntity

    拆分流程：
      * 同样子实体需要先入库，获取到了对应的uid后写回到子实体对象中,写回见：NodeUtil.putEntityUid。
      * 最后再入外层的实体，同时也会入库外层实体与子实体的关系，关系通过获取子实体对应的uid绑定，
        所以就是为什么需要写回子实体的uid， 否则子实体的uid为null，无法建立实体之间的关系。



2. 更新日志：

    # 2018.5.12
         * 支持获取类似List<EntityNode>实体关系的解析
         * 支持最外层实体存在uid，rdf入库时直接使用该uid入库
         * 支持出现mutate exception时不能将retMap输入到entityId服务中，该uid无效

