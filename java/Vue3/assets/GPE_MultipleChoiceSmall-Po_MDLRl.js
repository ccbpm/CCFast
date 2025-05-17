var h=Object.defineProperty;var g=(t,e,i)=>e in t?h(t,e,{enumerable:!0,configurable:!0,writable:!0,value:i}):t[e]=i;var m=(t,e,i)=>g(t,typeof e!="symbol"?e+"":e,i);var s=(t,e,i)=>new Promise((o,p)=>{var n=l=>{try{r(i.next(l))}catch(a){p(a)}},c=l=>{try{r(i.throw(l))}catch(a){p(a)}},r=l=>l.done?o(l.value):Promise.resolve(l.value).then(n,c);r((i=i.apply(t,e)).next())});import{b as u,a as M}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as S}from"./PageBaseGroupEdit-IicyYiex.js";import{SysEnumMains as E}from"./SysEnumMain-CBhfewK0.js";import{SFTables as C}from"./SFTable-BlM1UBse.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./Help-D0bDMZWg.js";import"./SysEnum-DlgPT0C2.js";import"./SFDBSrc-DKIMsnoa.js";class B extends S{constructor(){super("GPE_MultipleChoiceSmall");m(this,"Desc0",`
  #### 帮助
   - 就是用户录入相对固定的文本时，在文本框里提前输入相关的选项，可以直接进行多选操作。
  #### 应用场景
   - 比如，请假类型，出行方式等
  #### 效果图
  - 按文本输入的值
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall1.png "屏幕截图.png")
  - 按系统外键表计算
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall.png "屏幕截图.png")

  `);m(this,"Desc1",`
  #### 说明
  值用逗号分开,比如: 飞机,火车,轮船,火箭,其他 
  #### 配置图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmallPeizhi.png "屏幕截图.png")
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall1.png "屏幕截图.png")
  
  `);m(this,"Desc2",`
  #### 说明
  系统用枚举值作为该字段的多选.
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall.png "屏幕截图.png")
  
  `);m(this,"Desc3",`
  #### 说明
  系统用系统外键作为该字段的多选.
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSmall/Img/MultipleChoiceSmall.png "屏幕截图.png")
  
  `);this.PageTitle="小范围多选"}Init(){return s(this,null,function*(){this.entity=new u,this.KeyOfEn=M.DoWay,yield this.entity.InitDataForMapAttr("MultipleChoiceSmall",this.GetRequestVal("PKVal")),this.AddGroup("A","小范围多选"),this.Blank("0","不设置",this.Desc0),this.SingleTB("1","按文本输入的值","Tag1",this.Desc1,""),this.SingleDDLEntities("2","按照枚举值","Tag1",this.Desc2,new E,!1),this.SingleDDLEntities("3","按照系统外键表计算","Tag1",this.Desc3,new C,!1),this.SingleTBSQL("4","按照SQL计算","Tag4","查询sql返回No,Name两列")})}AfterSave(i,o){if(i==o)throw new Error("Method not implemented.")}BtnClick(i,o,p){if(i==o||i===p)throw new Error("Method not implemented.")}}export{B as GPE_MultipleChoiceSmall};
