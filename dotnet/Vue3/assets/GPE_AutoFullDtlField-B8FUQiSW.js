var p=Object.defineProperty;var d=(e,i,t)=>i in e?p(e,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):e[i]=t;var s=(e,i,t)=>d(e,typeof i!="symbol"?i+"":i,t);var a=(e,i,t)=>new Promise((l,r)=>{var m=o=>{try{n(t.next(o))}catch(u){r(u)}},F=o=>{try{n(t.throw(o))}catch(u){r(u)}},n=o=>o.done?l(o.value):Promise.resolve(o.value).then(m,F);n((t=t.apply(e,i)).next())});import{bb as A,ba as c}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as g}from"./PageBaseGroupEdit-JIgqoTiq.js";import{GPEAutoFullDtlField as E}from"./GPEAutoFullDtlField-DFX7nY9n.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class G extends g{constructor(){super("GPE_AutoFullDtlField");s(this,"Desc0",`
  #### 帮助
   - 不启用：不对主表字段进行从表列的数学计算。
   - 对从表列求值：当前是主表字段，对从表的列进行求和、平均、最大、最小计算. 
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDtlField/Img/AutoFull.png "屏幕截图.png") 
  `);s(this,"Desc1",`

  #### 帮助
  - 当前是主表字段，对从表的列进行求和、平均、最大、最小计算.  
  - 点击设置进入详细设置.
  - 配置图例1
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDtlField/Img/AutoFullBiaodan.png "屏幕截图.png") 
  - 配置图例2
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDtlField/Img/AutoFullBiaodan2.png "屏幕截图.png") 

  - 运行图例
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDtlField/Img/AutoFull.png "屏幕截图.png") 

  `);this.PageTitle="对从表列求值"}Init(){return a(this,null,function*(){this.entity=new A,this.KeyOfEn=c.DoWay,yield this.entity.InitDataForMapAttr("NumEnterLimit",this.GetRequestVal("PKVal")),this.AddGroup("A","对从表列求值"),this.Blank("0","不启用",this.Desc0),this.AddEntity("1","启用设置",new E,this.Desc1)})}AfterSave(t,l){if(t==l)throw new Error("Method not implemented.")}BtnClick(t,l,r){if(t==l||t===r)throw new Error("Method not implemented.")}}export{G as GPE_AutoFullDtlField};
