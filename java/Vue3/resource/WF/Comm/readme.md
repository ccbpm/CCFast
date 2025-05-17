### 通用组件列表. 
 1. 本目录下文件都是通用组件文件与通用功能文件.
 1. 本文件说明了该文件如何使用.
 1. 每个组件都有自己的特性,特性化的说明在组件说明文档.

#### 00.批量修改: Ens.vue
   - 说明: 实现对实体的批量修改,可以对行进行编辑，没有分页功能，多用于字典类的数据维护，数据量较小，比如角色类型。
   - 调用格式: Ens.vue?EnName=类名&查询条件字段1=字段值1&查询条件字段2=字段值2&OrderBy=要排序的字段名.
   - 示例: Ens.Vue?EnsName=BP.Port.Emps&IsEnable=1&OrderBy=Idx
   - 效果图: 暂无

#### 01.En.vue 调用规则
   - 希望作为组件打开
   - En.vue 是个组件， 不应该从直接从路由读取参数.
   - EnPage.vue 是个页面，如果需要从路由调起，应该跳转到EnPage.vue

#### 01.查询组件: Search.vue
   -  示例: Search.Vue?EnsName=BP.Port.Emps&para1=val1
   -  示例: Search.Vue?EnsName=BP.Port.Emps&条件字段1=字段值1&OrderBy=排序字段.

#### 06. 批处理: Batch.vue
   -  批处理: Batch.Vue?EnsName=BP.Port.Emps&para1=val1

#### 02. 分析组件: Group.vue
    - 示例: Group.Vue?EnsName=BP.Port.Emps&para1=val1

#### 03. 平铺组件: Panel.vue
    - 说明: 对于没有分类的实体，数据量太多的单元格的模式数据展现. 比如: 系统列表.
    - 示例: Panel.Vue?EnsName=BP.Port.Emps

#### 04. 平铺分组组件: PanelGroup.vue
   - 说明: 对于有分类的实体的展示. 比如：流程类别流程,表单类别表单,方法类别2方法.
   - 示例: PanelGroup.Vue?EnName=PanelGroup_FlowSort2Flow&para1=val1
   - EnName 是必选参数.
   - 需要提供两个数据源, 分组数据实体，列表数据实体.
   - 展现模式: 图标展现, 列表展现.
   - 对于列表展现.
   - 可适用实体维护操作，比如:实体方法的维护PG_Group2Method, 系统中的模块菜单维护.

#### 04. 列表显示页面: GenerList.vue
   - 说明: 用于显示通用显示列表. 比如:待办、发起、在途、草稿、近期发起、近期参与
   - 这些有如下特征: 1.数据量小，不需要分页. 2.要求展示单一简单。3.需要有查询条件,关键字查询,日期范围查询.
   - 可以指定分组特征, 比如待办,按流程、节点、发起人分组.
   - 仅提供一个数据源,就是列表数据源,所有的查询是等待数据一次性返回过来在前段执行查询.
   - 分组数据也是动态计算,进行分组.
   - 有行操作,比如:在途,点击一行的焦点字段就弹出工作查看器, 对一行数据撤销,催办操作.
   - 实例: GenerList?EnName=GL_Todolist&para1=val1&para2=val2
   - 不适用实体维护操作.

#### 05. 树形编辑: TreeEns.vue
   -  示例: /WF/Comm/TreeEns.Vue?EnName=TreeEns_Dept2Emp
   -  是一个实体,从基类继承下来的.

#### 07. 卡片组件(包含相关功能): En.vue  修改指定的实体. 
   -  示例: En.Vue?EnName=BP.Port.Emp&No=zhangsan

#### 08. 卡片组件: EnOnly.vue  修改指定的实体，不包含相关功能.
   -  示例: EnOnly.Vue?EnName=BP.Port.Emp&No=zhangsan

#### 09. 从表数据-批量修改 DtlBach.vue
   -  调用方法： DtlBatch.vue?EnName=BP.WF.Template.NodeExt&PK=103&EnsName=BP.WF.Template.NodeToolbars&RefKey=FK_Node&RefVal=103

#### 10. 通用列表. GenerList.vue 显示待办，草稿，在途数据. 
   -  说明: 一个具有查询条件的简单列表. 数据量不大，需要分页、多种方式展现
   -  应用场景: 待办、在途、草稿
   -  调用示例： GenerList.vue?EnName=GL_Todolist

#### 11. 实体属性修改 GPE. 
   -  说明: 对实体的一个枚举值，或者外键值进行个性化的修改，比如：节点实体中的,接受
   -  应用示例: 节点实体的接收人规则, 表单方案.
   -  调用格式: 
   -  示例: /src/WF/Comm/UIEntity/GroupPageEdit.vue?EnName=GL_Todolist
   -  示例: /src/WF/Comm/UIEntity/GroupPageEdit.vue?EnName=GL_Todolist

#### 12. 实体新建 GPN.
   -  说明: 新建实体就是创建实体的时候，选择不同的类型，比如：新建流程，新建表单. 
   -  使用这样的前置导航新建让用户操作体验更好. 
   -  示例1: 新建流程 /src/WF/Comm/UIEntity/GroupPageNew.vue?EnName=GPN_NewFlow
   -  示例2: 新建表单 /src/WF/Comm/UIEntity/GroupPageNew.vue?EnName=GPN_NewFrm
   -  图例:

#### 13. 功能页面.
   - 该页面是一个大杂烩,就是对简单的不能通用的可以个性化的页面处理.
   -  Func.vue?EnName=Func_XXXX;


PanelGroup 与 GenerList的区别.
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
共性:
1. 实现基础分组展示效果, 比如: 表单类别与表单. 模块与菜单的展示.
2. 能够使用GL实现的不用PG.

PanelGroup特性:
0. 多用于两个实体之间的关系数据维护，比如:模块菜单\表单类别表单.
1. 需要两个数据源. 一个是分组实体，一个是数据源实体.
2. 需要设置关联的RefKey 把两个数据实体关联起来.
3. 多用一为维护.
4. 分组下，可以数据为空,但是GL的分组数据是从一个数据源上求出来的.
5. 一个实体，可以从一个分组移动到另外一个分组，不需要写特别的代码。
5. 分组移动顺序，实体之间移动顺序也不需要写代码.

GenerList 特征:
0. 多用于数据展示，对没有分页的数据源进行展示.
1. 提供一个数据源, 不是实体的数据源. 比如:待办、在途接口.
2. 可以有多个分组字段, 比如显示待办,可以按流程、发起人、停留节点显示分组数据展示.
3. 多种展现方式，Table(草稿\待办), Group(待办), Windows(发起流程), BigIcon(系统列表).
4. 多用于数据展示.


   
