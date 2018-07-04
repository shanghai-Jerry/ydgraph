package com.higgs.dgraph.node;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.higgs.utils.Util;

/**
 * User: JerryYou
 *
 * Date: 2018-05-08
 *
 * Copyright (c) 2018 devops
 *
 * 入库： 分两种形式 1. with json object 2. with rdf <uid_1>  <predicate> <uid_2>
 *
 * 多种形式的插入
 *
 * <0xd60> <friend> <0xd57> .
 * _:id <friend> <0xd57> .
 * _:id <name> "value1" .
 * _:id <age> "value2" .
 * 注：value 为int等类型都需""，具体类型转换由schema的类型定义决定
 *
 * <<licensetext>>
 */
public class EntityNode implements Serializable {

  private String pass;
  private String uid;
  private List<String> unique_ids = new ArrayList<>();
  // _:uniqueId <name> value
  // 检查是否存在实体的唯一标识
  private String unique_id;
  // 实体名称
  private String name;
  // 实体类型
  private String type;
  // 实体类别
  private Label has_label;

  private String label_name;

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  String getUnique_id() {
    return unique_id;
  }

  public void setUnique_id(String unique_id) {
    this.unique_id = unique_id;
  }

  public List<String> getUnique_ids() {
    return unique_ids;
  }

  public void setUnique_ids(List<String> unique_ids) {
    this.unique_ids = unique_ids;
  }

  public String getLabel_name() {
    return label_name;
  }

  public void setLabel_name(String label_name) {
    this.label_name = label_name;
  }

  public Label getHas_label() {
    return has_label;
  }

  public void setHas_label(Label has_label) {
    this.has_label = has_label;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  private boolean checkPredicateValue(Object predicate) {
    if(predicate == null) {
      return false;
    }
    if (predicate instanceof Integer || predicate instanceof Long) {
      long value = Long.valueOf(predicate.toString());
      if (value == 0) {
        return false;
      }
    } else if (predicate instanceof String) {
      if ("".equals(predicate)) {
        return false;
      }
    } else if (predicate instanceof Double || predicate instanceof Float) {
      double value = Double.valueOf(predicate.toString());
      if (value == 0) {
        return false;
      }
    } else if (predicate instanceof Boolean) {
      return true;
    }

    return true;
  }

  /**
   * 通过反射的形式获取uid
   * @param object 对象
   * @param clazz  对象运行时的class
   * @param methodName 获取uid的方法名
   * @return 返回uid
   */
  private String getDeclaredEdgeUid(Object object, Class clazz, String methodName) {
    String ret = "";
    try {
      Method[] methods = clazz.getMethods();
      for (Method method : methods) {
        if (methodName.equals(method.getName())) {
          Object result = method.invoke(object);
          return (String) result;
        }
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return ret;

  }

  /**
   * all fileld include sub EntityNode's uid
   * @param object 对象
   * @param clazz 对象运行时的class
   * @param pre 属性名
   * @param values 属性值
   * @param edges 子实体属性名
   * @param ids 子实体uid值
   * @param methodName 获取子实体的uid方法名
   */
  private void getDeclaredFields(Object object, Class clazz, List<String> pre, List<Object>
      values, List<String> edges, List<String> ids, String methodName) {
    Field[] fields = clazz.getDeclaredFields();
    Field.setAccessible(fields, true);
    for (Field field : fields) {
      try {
        String name = field.getName();
        Object value = field.get(object);
        Util.println("name:", name);
        Util.println("value:", value);
        // .. todo , 如果实体之前的关系通过List<EntityNode>的方式存在，那么以rdf方式如何建立实体之前的关系
        // .. todo, 以json object的方式写入只需要保证子实体的uid写回，无需进行获取uid进行绑定
        if (value instanceof EntityNode) {
          // 绑定单个实体之间的关系
          if (!"".equals(methodName)) {
            Util.println("  edge:", name);
            String uid = getDeclaredEdgeUid(value, value.getClass(), methodName);
            Util.println("  uid:", uid);
            Util.println("  value:", value);
            if (uid != null && !"".equals(uid)) {
              edges.add(name);
              ids.add(uid);
            }
          }
        } else if (value instanceof List) {
          if (((List) value).size() > 0) {
            if (((List) value).get(0) instanceof String) {
              // ..todo
            } else if (((List) value).get(0) instanceof  EntityNode ){
              // 绑定多个实体之间的关系
              List<EntityNode> entityNodes = (List<EntityNode>)value;
              for (EntityNode entityNode: entityNodes) {
                String uid = getDeclaredEdgeUid(entityNode, entityNode.getClass(), methodName);
                Util.println("  list uid:", uid);
                Util.println("  list value:", entityNode);
                if (uid != null && !"".equals(uid)) {
                  edges.add(name);
                  ids.add(uid);
                }
              }
            }
          }
        } else {
          // 一般属性值
          if ("uid".equals(name)) {
            continue;
          }

          // unique_id: 一般属性值, 需要入到dgraph中, 不然无法反向推断出某个uid具体对应unique_id
          // 同样type也需要入库到dgraph: 通过unique_id获取uid时，需要指定type
          Util.println("  other else:", name);
          if (checkPredicateValue(value)) {
            Util.println("  name:", name);
            Util.println("  att value:", value);
            pre.add(name);
            values.add(value);
          }
        }
      } catch (IllegalAccessException e) {
        System.out.println("##### Exception :" + e.getMessage());
      }
    }
  }

  /**
   * 获取属性，子实体属性
   * @param pre 属性名
   * @param values 属性值
   * @param edges 子实体属性名
   * @param ids 子实体uid
   */
  public void getValueMap(List<String> pre, List<Object> values, List<String> edges, List<String>
      ids, String methodName) {
    getDeclaredFields(this, this.getClass(), pre, values, edges, ids, methodName);
    getDeclaredFields(this, this.getClass().getSuperclass(), pre, values, edges, ids, methodName);

  }

  /**
   * 获取属性值
   * @param pre 属性名
   * @param values 属性值
   */
  @Deprecated
  public void getAttrValueMap(List<String> pre, List<Object> values) {
    getDeclaredFields(this, this.getClass(), pre, values, new ArrayList<String>(), new
        ArrayList<String>(), "");
    getDeclaredFields(this, this.getClass().getSuperclass(), pre, values, new ArrayList<String>()
        , new ArrayList<String>(), "");
  }

  /**
   * 获取子实体属性
   * @param edges 子实体属性
   * @param ids 子实体uid
   * @param methodName 获取uid的方法名
   */
  @Deprecated
  public void getEdgeValueMap(List<String> edges, List<String> ids, String methodName) {
    getDeclaredFields(this, this.getClass(), new ArrayList<String>(), new ArrayList<Object>(),
        edges, ids, methodName);
    System.out.println("##### super class :");
    getDeclaredFields(this, this.getClass().getSuperclass(), new ArrayList<String>(), new
        ArrayList<Object>(), edges, ids, methodName);
  }

}
