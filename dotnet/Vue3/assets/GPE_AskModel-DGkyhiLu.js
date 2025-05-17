var u=Object.defineProperty;var h=(s,e,t)=>e in s?u(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var n=(s,e,t)=>h(s,typeof e!="symbol"?e+"":e,t);import{I as A,K as f,U as g,L as S}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as l}from"./PageBaseGroupEdit-JIgqoTiq.js";class c extends l{constructor(){super("GPE_RecAddModel");n(this,"Desc0",`
  #### 帮助
  - 关键字查询是接受用户输入一个关键字，在整个报表的显示列中使用like查询(外键、枚举、数值类型的除外)
  - 关键字搜索提示, 默认为:请输入关键字...
  #### 效果图
  -  ![输入图片说明](/src/resource/CCBill/SearchCond/SearchKey.png "屏幕截图.png")  
 `);n(this,"Desc1",`
  #### 帮助
  - 选择特定字段，在报表中根据 like 模糊查询
  #### 配置图
  ![输入图片说明](/src/resource/CCBill/SearchCond/StringSearchKeysetting.png "屏幕截图.png")  
  #### 效果图
  ![输入图片说明](/src/resource/CCBill/SearchCond/StringSearchKey.png "屏幕截图.png")  
  `);this.PageTitle="记录增加模式"}AfterSave(t,r){if(t==r)return null}BtnClick(t,r,a){}Init(){this.entity=new o,this.KeyOfEn="RecAddModel",this.AddGroup("A","按照微信"),this.Blank("0","增加一次","一个微信ID只能增加一次"),this.Blank("Day","每天1次","一个微信ID，每天可以填写一次."),this.Blank("Week","每周1次","一个微信ID，每周可，以填写一次."),this.Blank("Week2","每2周","一个微信ID，每2周，可以填写一次."),this.Blank("M","每月","一个微信ID，每月可以填写一次."),this.Blank("JD","每极度","一个微信ID，每季度可以填写一次."),this.Blank("HalfYear","半年","一个微信ID，每半年可以填写一次."),this.Blank("Year","年度","一个微信ID，每年可以填写一次.")}}const k=Object.freeze(Object.defineProperty({__proto__:null,GPE_RecAddModel:c},Symbol.toStringTag,{value:"Module"}));class i extends l{constructor(){super("GPE_Welcome");n(this,"Desc0",`
  #### 帮助
  - 用户保存后，提示的信息.
  - 比如: 欢迎您参与调查.
  #### 效果图
  - 
 `);n(this,"Desc1",`
  #### 帮助
  - 需要接受协议才能进入.
  #### 效果图
  - 
 `);this.PageTitle="欢迎词"}AfterSave(t,r){if(t==r)return null}BtnClick(t,r,a){}Init(){this.entity=new o,this.KeyOfEn="WelcomeModel",this.AddGroup("A","欢迎词"),this.SingleRichTxt("0","欢迎词信息","WelcomeDoc","请输入欢迎的信息",this.Desc0),this.SingleRichTxt("1","欢迎词协议","WelcomeDoc","请输入欢迎的信息",this.Desc1),this.SingleRichTxt("2","需要支付费用","WelcomeDoc","请输入欢迎的信息",this.HelpUn)}}const y=Object.freeze(Object.defineProperty({__proto__:null,GPE_Welcome:i},Symbol.toStringTag,{value:"Module"}));class d extends l{constructor(){super("GPE_SaveAfterTodo");n(this,"Desc0",`
  #### 帮助
  - 用户保存后，提示的信息.
  - 比如: 感谢您的参与!!!
  #### 效果图
  - 
 `);this.PageTitle="保存后做什么?"}AfterSave(t,r){if(t==r)return null}BtnClick(t,r,a){}Init(){this.entity=new o,this.KeyOfEn="SaveAfterTodo",this.AddGroup("A","处理方式"),this.SingleRichTxt("0","提示信息","SaveAfterDoc","请输入提示的信息",this.Desc0)}}const I=Object.freeze(Object.defineProperty({__proto__:null,GPE_SaveAfterTodo:d},Symbol.toStringTag,{value:"Module"}));class o extends A{constructor(e){super("TS.CCBill.FrmAsk"),e&&(this.No=e)}get HisUAC(){const e=new g;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new S("Sys_MapData","问卷表单");return e.GroupBarShowModel=1,e.AddTBStringPK("No",null,"编号",!0,!0,1,100,100,!1),e.EnDesc="问卷表单",e.AddTBInt("AskModel",0,"问卷模式",!1,!1,!1),e.AddTBString("RecAddModel","0","记录增加模式",!1,!1,0,100,100,!0),e.AddTBInt("WelcomeModel",0,"欢迎词",!1,!1,!1),e.AddTBString("WelcomeDoc","0","内容",!1,!1,0,500,100,!0),e.AddTBInt("SaveAfterTodo",0,"保存后执行内容",!1,!1,!1),e.AddTBString("SaveAfterDoc","0","内容",!1,!1,0,500,100,!0),e.AddGroupMethod("问卷设置"),e.AddRM_GPE(new p,"icon-drop"),e.AddRM_GPE(new c,"icon-drop"),e.AddRM_GPE(new i,"icon-drop"),e.AddRM_GPE(new d,"icon-drop"),e.AddGroupMethod("分享方式"),e.AddRM_HelpDocs("生成链接","?FrmID=@No&Type=Link","我的链接"),e.AddRM_HelpDocs("微信二维码","?FrmID=@No&Type=WX","我的链接"),e.AddRM_HelpDocs("QQ二维码","?FrmID=@No&Type=QQ","我的链接"),this._enMap=e,this._enMap}CCFormAPI(){return"tabOpen@"+`
    #### 帮助
    -  ccform提供两个类的接口， 功能页面调用与
    #### 新建接口
    - 新建一问卷记录的链接. 
    - /WF/Port.vue?DoWhat=NewFrmAskRec&FrmID=xxxx
    - 打开一问卷记录的链接. 
    - /WF/Port.vue?DoWhat=OpenFrmAskRec&FrmID=xxxx&OID=xxxx
    `}}class D extends f{get GetNewEntity(){return new o}constructor(){super()}}const B=Object.freeze(Object.defineProperty({__proto__:null,FrmAsk:o,FrmAsks:D},Symbol.toStringTag,{value:"Module"}));class p extends l{constructor(){super("GPE_AskModel");n(this,"Desc0",`
  #### 帮助
  - 关键字查询是接受用户输入一个关键字，在整个报表的显示列中使用like查询(外键、枚举、数值类型的除外)
  - 关键字搜索提示, 默认为:请输入关键字...
  #### 效果图
  -  ![输入图片说明](/src/resource/CCBill/SearchCond/SearchKey.png "屏幕截图.png")  
 `);n(this,"Desc1",`
  #### 帮助
  - 选择特定字段，在报表中根据 like 模糊查询
  #### 配置图
  ![输入图片说明](/src/resource/CCBill/SearchCond/StringSearchKeysetting.png "屏幕截图.png")  
  #### 效果图
  ![输入图片说明](/src/resource/CCBill/SearchCond/StringSearchKey.png "屏幕截图.png")  
  `);this.PageTitle="问卷模式"}AfterSave(t,r){if(t==r)return null}BtnClick(t,r,a){}Init(){this.entity=new o,this.KeyOfEn="AskModel",this.AddGroup("A","问卷模式"),this.Blank("0","表单模式","不设置"),this.Blank("1","考卷模式",this.Desc0)}}const P=Object.freeze(Object.defineProperty({__proto__:null,GPE_AskModel:p},Symbol.toStringTag,{value:"Module"}));export{B as F,k as G,y as a,I as b,P as c};
