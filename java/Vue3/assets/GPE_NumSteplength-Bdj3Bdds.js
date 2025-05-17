var u=Object.defineProperty;var c=(r,e,t)=>e in r?u(r,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[e]=t;var h=(r,e,t)=>c(r,typeof e!="symbol"?e+"":e,t);var g=(r,e,t)=>new Promise((p,n)=>{var m=i=>{try{s(t.next(i))}catch(a){n(a)}},o=i=>{try{s(t.throw(i))}catch(a){n(a)}},s=i=>i.done?p(i.value):Promise.resolve(i.value).then(m,o);s((t=t.apply(r,e)).next())});import{MapAttr as S}from"./MapAttr-B1mxD3vP.js";import{b as f,a as l}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class G extends A{constructor(){super("GPE_NumSteplength");h(this,"Desc1",` 
  #### 帮助
   - 定义: 为了方便用户数据采集，使用+ - 符号来完成文本框的数值数据录入, 点击+ - 一次的增量或者减量数据.
   - 比如: 设置步长为10，没点击一次+ 文本框的数值就在原来的数据上增加10.
   - 禁用步长： 文本框不是使用 + - 图标 不启用.
   #### 效果图
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/NumSteplength/Img/NumSteplength.png "屏幕截图.png")
  `);h(this,"Desc2",` 
  #### 帮助
  - 请输入步长参数,不能为0或负数.
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/NumSteplength/Img/NumSteplength.png "屏幕截图.png")

....`);this.PageTitle="步长输入"}Init(){this.entity=new f,this.KeyOfEn=l.DoWay,this.AddGroup("A","数值输入值限制"),this.Blank("0","禁用步长",this.Desc1),this.SingleTB("1","启用步长输入",l.Tag,this.Desc2,"格式:0.5")}BtnClick(t,p,n){if(t==p||t===n)throw new Error("Method not implemented.")}AfterSave(t,p){return g(this,null,function*(){var o;if(this.entity==null)return;const n=(o=this.entity)==null?void 0:o.AttrOfOper,m=new S(n);yield m.Retrieve(),t==="0"?m.SetPara("NumSteplength",""):m.SetPara("NumSteplength",this.entity.Tag)})}}export{G as GPE_NumSteplength};
