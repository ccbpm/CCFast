var A=Object.defineProperty;var c=(i,a,t)=>a in i?A(i,a,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[a]=t;var p=(i,a,t)=>c(i,typeof a!="symbol"?a+"":a,t);var s=(i,a,t)=>new Promise((n,o)=>{var r=e=>{try{u(t.next(e))}catch(l){o(l)}},F=e=>{try{u(t.throw(e))}catch(l){o(l)}},u=e=>e.done?n(e.value):Promise.resolve(e.value).then(r,F);u((t=t.apply(i,a)).next())});import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-JIgqoTiq.js";import{bb as g,ba as m,aL as d,aD as E}from"./entry/index-M8VErHPE-1727507756861.js";import"./Help-D0bDMZWg.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class L extends h{constructor(){super("GPE_AutoFull");p(this,"Desc1",` 
  #### 帮助
  说明：自动计算就是对字段之前进行数学基本计算。可以对主表的字段进行计算，也可以对从表的字段进行计算。
  #### 应用场景
  定货时，有单位，有数量，自动求合计。
  #### 效果图
   - 主表计算-效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullzhuyanshi.png "屏幕截图.png") 
   - 从表计算-效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFull/Img/AutoFullcongBiaodan.png "屏幕截图.png")  
  `);p(this,"Desc2",` 
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
....`);this.PageTitle="自动计算"}Init(){return s(this,null,function*(){this.entity=new g,this.KeyOfEn=m.DoWay;const t=this.GetRequestVal("PKVal"),n=new d(t);yield n.Retrieve();const o=new g,r="AutoFull";o.MyPK=n.MyPK+"_"+r,(yield o.RetrieveFromDBSources())==0&&(o.FK_MapData=n.FK_MapData,o.DoWay=0,o.ExtType=r,o.AttrOfOper=n.KeyOfEn,o.Tag1=1,yield o.Insert()),this.entity=o,this.AddGroup("A","自动计算"),this.Blank("0","禁用",this.Desc1),this.SingleTB("1","启用自动计算",m.Tag,this.Desc2,"格式:@DanJia*@JinE",E.AppString)})}AfterSave(t,n){if(t==n)throw new Error("Method not implemented.")}BtnClick(t,n,o){if(t==n||t===o)throw new Error("Method not implemented.")}}export{L as GPE_AutoFull};
