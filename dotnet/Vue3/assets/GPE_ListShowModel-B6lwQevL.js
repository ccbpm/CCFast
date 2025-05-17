var p=Object.defineProperty;var m=(o,t,e)=>t in o?p(o,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[t]=e;var i=(o,t,e)=>m(o,typeof t!="symbol"?t+"":t,e);import{PageBaseGroupEdit as D}from"./PageBaseGroupEdit-IicyYiex.js";import{MapDtl as d,MapDtlAttr as n}from"./MapDtl-C1-EwBHu.js";import{ListShoModel2D as s}from"./ListShoModel2D-CjNQewXk.js";import{ListShoModel3D as r}from"./ListShoModel3D-B8uEvAvK.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";class u extends D{constructor(){super("GPE_ListShowModel");i(this,"Desc0",`
  #### 帮助
  - 定义: 从表展示方式为表格的形式。
  #### 表格模式效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/ListShowModelBiaoge.png "从表模式")  
  `);i(this,"Desc1",`
  #### 帮助
  - 定义: 从表展示方式为卡片的形式。
  - 场景：数据量比较多, 有从表.
  #### 卡片模式效果图
  - 卡片
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/ListShowModelkapian.png "卡片模式")  

  #### 局部放大
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/ListShowModelkapian1.png "卡片模式")  

  `);i(this,"Desc2",`
  #### 帮助
  - 定义: 从表展示方式为自定义URL。
  - 说明：该模式下，是当现在的从表不能满足客户对数据展现采集的要求，需要写一个自定义的url实现，但是数据还是要存储在当前从表里面来。
  - 比如：对输入的复杂的计算,目前的从表不能控制到位.
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/SelfUrl.png "自定义rul")  
  `);i(this,"Desc6",`
  #### 帮助
  - 表单描述: 损益表模式，左侧是一个树结构的目录，右侧有两列数值的类型数据，本期，基期（同期）.
  #### 创建步骤
  1. 首先在字典库创建一个树结构字段, =>数据源 ==>本机数据源 ==>字典 ==>新建字典 ==>选择树结构字典，并维护数据内容.
  2. 根目录数据不计算.
  3. 在当前表单里，增加一个外键类型的字段，并选择该树结构的外键字典.
  4. 在从表属性里，找到展示模式=》选择损益表的模式。

  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/ListShowMode6.png "自定义rul")  
  `);i(this,"DescD2",`
  #### 帮助
  - 如下图所示.
  - 从表里有三个字段，两个外键或者枚举类型的字段，一个数值类型的字段. 
  - 需要交叉数据展现模式，用于数据采集或者展现。
  - 从左侧开始,第1个字段是第1维度,头部是第2维度. 
  #### 表格模式效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/D2.png "自定义rul")  
  - 维度1：年级.
  - 维度2：政治面貌.
  `);i(this,"DescD3Top",`
  #### 帮助
  - 如下图所示.
  - 从表里有四个字段，三个外键或者枚举类型，一个数值类型的字段. 
  - 需要交叉数据展现模式，用于数据采集或者展现。
  #### 表格模式效果图
  - 展现效果
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/D3Top.png "3维度左侧")  
  - 存储效果
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/YSTable.png "3维度左侧")  
  `);i(this,"DescD3Left",`
  #### 帮助
  - 如下图所示.
  - 从表里有四个字段，三个外键或者枚举类型，一个数值类型的字段. 
  - 需要交叉数据展现模式，用于数据采集或者展现。
  #### 表格模式效果图
  - 展现效果
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/D3Left.png "3维度左侧")  
  - 存储效果
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/ListShowModel/Img/YSTable.png "3维度左侧")  
  `);this.PageTitle="展示模式"}Init(){this.entity=new d,this.KeyOfEn="ListShowModel",this.AddGroup("A","常规模式"),this.Blank("0","表格(默认)",this.Desc0),this.Blank("1","卡片模式",this.Desc1),this.SingleTB("2","自定义URL",n.UrlDtl,this.Desc2,"请输入自定义的url"),this.AddGroup("B","报表模式"),this.AddEntity("3","2维表",new s,this.DescD2),this.AddEntity("4","3维表(左)",new r,this.DescD3Left),this.AddEntity("5","3维表(上)",new r,this.DescD3Top),this.AddGroup("C","固定行表格");const e=this.RefPKVal;this.SelectItemsByList("6","损益表模式",this.Desc6,!1,` SELECT KeyOfEn No, Name FROM Sys_MapAttr WHERE FK_MapData='${e}'  AND UIContralType=1 `,"InitDBAttrs"),this.AddEntity("7","大类小类",new s,this.DescD3Top)}BtnClick(e,l,h){}AfterSave(e,l){}}export{u as GPE_ListShowModel};
