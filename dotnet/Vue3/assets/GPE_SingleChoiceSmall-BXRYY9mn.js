var S=Object.defineProperty;var p=(t,i,e)=>i in t?S(t,i,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[i]=e;var o=(t,i,e)=>p(t,typeof i!="symbol"?i+"":i,e);var g=(t,i,e)=>new Promise((n,m)=>{var s=l=>{try{r(e.next(l))}catch(a){m(a)}},c=l=>{try{r(e.throw(l))}catch(a){m(a)}},r=l=>l.done?n(l.value):Promise.resolve(l.value).then(s,c);r((e=e.apply(t,i)).next())});import{b as h,a as u}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as E}from"./PageBaseGroupEdit-IicyYiex.js";import{SysEnumMains as C}from"./SysEnumMain-CBhfewK0.js";import{SFTables as d}from"./SFTable-BlM1UBse.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./Help-D0bDMZWg.js";import"./SysEnum-DlgPT0C2.js";import"./SFDBSrc-DKIMsnoa.js";class B extends E{constructor(){super("GPE_SingleChoiceSmall");o(this,"Desc0",`
  #### 帮助
   - 就是用户录入相对固定的文本时，在文本框里提前输入相关的选项，可以直接进行单项选择。
  #### 应用场景
   - 比如，请假类型，出行方式等
  #### 效果图
  - 按文本输入的值
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/SingleChoiceSmall/Img/SingleChouceSmall1.png "屏幕截图.png")
  - 按系统外键表计算
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/SingleChoiceSmall/Img/SingleChouceSmall.png "屏幕截图.png")
  `);o(this,"Desc1",`
  #### 说明
   - 值用逗号分开,比如: 飞机,火车,轮船,火箭,其他
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/SingleChoiceSmall/Img/SingleChouceSmallPeizhi.png "屏幕截图.png") 
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/SingleChoiceSmall/Img/SingleChouceSmall1.png "屏幕截图.png")
  
  `);o(this,"Desc2",`
  #### 说明
   - 系统用枚举值作为该字段的单选
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/SingleChoiceSmall/Img/SingleChouceSmall.png "屏幕截图.png")

  `);o(this,"Desc3",`
  #### 说明
   - 系统用系统外键作为该字段的单选
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/SingleChoiceSmall/Img/SingleChouceSmall.png "屏幕截图.png")
  
  `);this.PageTitle="小范围单选"}Init(){return g(this,null,function*(){this.entity=new h,this.KeyOfEn=u.DoWay,yield this.entity.InitDataForMapAttr("SingleChoiceSmall",this.GetRequestVal("PKVal")),this.AddGroup("A","小范围单选"),this.Blank("0","不设置",this.Desc0),this.SingleTB("1","按文本输入的值","Tag1",this.Desc1,""),this.SingleDDLEntities("2","按照枚举值","Tag1",this.Desc2,new C,!1),this.SingleDDLEntities("3","按照系统外键表计算","Tag1",this.Desc3,new d,!1),this.SingleTBSQL("4","按照SQL计算","Tag4","查询sql返回No,Name两列")})}AfterSave(e,n){if(e==n)throw new Error("Method not implemented.")}BtnClick(e,n,m){if(e==n||e===m)throw new Error("Method not implemented.")}}export{B as GPE_SingleChoiceSmall};
