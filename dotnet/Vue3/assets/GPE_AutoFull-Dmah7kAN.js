var A=Object.defineProperty;var c=(a,i,t)=>i in a?A(a,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[i]=t;var l=(a,i,t)=>c(a,typeof i!="symbol"?i+"":i,t);var m=(a,i,t)=>new Promise((n,o)=>{var e=r=>{try{u(t.next(r))}catch(p){o(p)}},F=r=>{try{u(t.throw(r))}catch(p){o(p)}},u=r=>r.done?n(r.value):Promise.resolve(r.value).then(e,F);u((t=t.apply(a,i)).next())});import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-IicyYiex.js";import{D as d}from"./entry/index-C6uBgOW5-1730430676707.js";import{b as s,a as g}from"./MapExt-DtQWKcAY.js";import{MapAttr as E}from"./MapAttr-B1mxD3vP.js";import"./Help-D0bDMZWg.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Events-D9tOL1Ad.js";class P extends h{constructor(){super("GPE_AutoFull");l(this,"Desc1",` 
  #### 帮助
  说明：自动计算就是对字段之前进行数学基本计算。可以对主表的字段进行计算，也可以对从表的字段进行计算。
  #### 应用场景
  定货时，有单位，有数量，自动求合计。
  #### 效果图
   - 主表计算-效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullzhuyanshi.png "屏幕截图.png") 
   - 从表计算-效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullcongBiaodan.png "屏幕截图.png")  
  `);l(this,"Desc2",` 
  #### 帮助
  1. 如果是主表: 就是主表字段之间的计算，比如: @A+@B
  2. 如果是从表: 表达式就是列之间的计算,比如: @DanJia*@ShuLiang
  3. 仅仅支持数值类型的计算，比如：float,int,decimal类型的数据字段。
  4. 字段表达式不区分大小写，比如: @DanJia*@ShuLiang与@danjia*@shuliang是一样的
  #### 主表自动计算-图例
  - 配置图例1
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullzhu.png "屏幕截图.png") 
  - 配置图例2
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullzhu1.png "屏幕截图.png") 
  - 运行效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullzhuyanshi.png "屏幕截图.png") 
  #### 从表自动计算-图例

  - 配置图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullCong1.png "屏幕截图.png") 


  - 运行效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullcongBiaodan.png "屏幕截图.png") 
....`);this.PageTitle="自动计算"}Init(){return m(this,null,function*(){this.entity=new s,this.KeyOfEn=g.DoWay;const t=this.GetRequestVal("PKVal"),n=new E(t);yield n.Retrieve();const o=new s,e="AutoFull";o.MyPK=n.MyPK+"_"+e,(yield o.RetrieveFromDBSources())==0&&(o.FK_MapData=n.FK_MapData,o.DoWay=0,o.ExtType=e,o.AttrOfOper=n.KeyOfEn,o.Tag1=1,yield o.Insert()),this.entity=o,this.AddGroup("A","自动计算"),this.Blank("0","禁用",this.Desc1),this.SingleTB("1","启用自动计算",g.Tag,this.Desc2,"格式:@DanJia*@JinE",d.AppString)})}AfterSave(t,n){if(t==n)throw new Error("Method not implemented.")}BtnClick(t,n,o){if(t==n||t===o)throw new Error("Method not implemented.")}}export{P as GPE_AutoFull};
