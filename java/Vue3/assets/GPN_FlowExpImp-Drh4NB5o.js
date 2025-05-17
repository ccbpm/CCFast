var U=Object.defineProperty;var u=(i,n,o)=>n in i?U(i,n,{enumerable:!0,configurable:!0,writable:!0,value:o}):i[n]=o;var w=(i,n,o)=>u(i,typeof n!="symbol"?n+"":n,o);var A=(i,n,o)=>new Promise((m,p)=>{var c=t=>{try{l(o.next(t))}catch(a){p(a)}},I=t=>{try{l(o.throw(t))}catch(a){p(a)}},l=t=>t.done?m(t.value):Promise.resolve(t.value).then(c,I);l((o=o.apply(i,n)).next())});import P from"./Entity-CqiUg-St.js";import{MapAttrs as B,MapAttr as K}from"./MapAttr-B1mxD3vP.js";import{MapData as T}from"./MapData-Ccsy8tbB.js";import{b5 as W,aM as b,G as F,l as f,aG as D,H as x}from"./entry/index-C6uBgOW5-1730430676707.js";import{downloadByData as N}from"./download-DS4Mvmi4.js";import"./Request-CTraI-GH.js";import"./Events-D9tOL1Ad.js";import"./form-Bg9E1Us6.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./EnumLab-CzismWql.js";import"./base64Conver-t-3tszFb.js";class X extends W{constructor(){super("GPN_FlowExpImp");w(this,"ExpAllData",`
  #### 帮助
   - 导出流程所有数据.
   - 草稿，空白除外.
   - 生成的文件放在TempFlowNoWorkID*.* , zzz流程编号， xxxxx工作ID.
   - 导出的数据都是xml格式，以目录存储.
   - 可以使用导入的模式，读取这里的文件进行导入。
  `);w(this,"ImpExcelFlowDB",`
  #### 帮助
   - 解决其他异构的流程数据导入到本流程模板的过程.
   - 解决历史已经完成的流程在ccbpm进行查询分析.
  ##### 选择模式说明
   - 模板数据格式为excel2013版本以上.
   - 必须有: 实例主键、流程标题、流程发起人账号、流程发起日期必选字段字段.
   - 发起日期格式为: yyyy-MM-dd HH:mm
   - 导入的流程是已经完成的流程.
   - 文件格式请参考： DataUserTempleteOfImp导入格式流程数据模版.xlsx
  `);w(this,"Imp",`
  #### 帮助
   - 上传模板、选择模式进行导入流程操作.
  ##### 选择模式说明
   - 作为新流程导入1：由ccbpm自动生成新的流程编号
   - 作为新流程导入2：使用流程模版里面的流程编号，如果该编号已经存在系统则会提示错误
   - 作为新流程导入3：使用流程模版里面的流程编号，如果该编号已经存在系统则会覆盖此流程
  `);w(this,"Exp",`
  #### 关于流程模板
   - ccbpm生成的流程模版是一个特定格式的xml文件。
   - 它是流程引擎模版与表单引擎模版的完整的组合体。
   - ccbpm的jflow与ccflow的流程引擎导出的流程模版通用。
   - 流程模版用于流程设计者的作品交换。
   - 在实施的过程中，我们可以把一个系统上的流程模版导入到另外一个系统中去。
    
  `);this.PageTitle="导入导出"}Init(){this.AddGroup("A","导入模板"),this.FileUpload("Imp","导入流程模板","请上传符合ccform表单格式的模式",this.Imp);const m=b.AtParaStringToJson("@0=作为新流程导入@1=作为新流程导入2@2=作为新流程导入3"),p=Object.keys(m),c=[];for(const I of p)c.push({No:I,Name:m[I]});this.SelectItemsByList("Imp.Way","选择模式",this.Imp,!1,JSON.stringify(c)),this.AddGroup("B","导出"),this.AddBlank("Exp","导出流程模板",this.Exp),this.AddBlank("DTSField","检查模板字段",this.HelpUn),this.AddGroup("C","流程数据导入"),this.AddBlank("ExpAllData","导出本流程所有数据",this.ExpAllData),this.AddBlank("ImpAllData","导人本流程所有数据",this.ExpAllData),this.FileUpload("ImpExcelFlowDB","Excel模式导入","请上传符合ccform表单格式的模式",this.ImpExcelFlowDB)}GenerSorts(){return A(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(o,m,p,c,I){return A(this,null,function*(){const l=this.PKVal;if(o=="Imp.Way"){const t=new x("BP.WF.HttpHandler.WF_Admin_AttrFlow");t.AddFile(this.UploadFile),t.AddPara("FlowNo",l),t.AddPara("ImpWay",p);const a=yield t.DoMethodReturnJson("Imp_Done");return new F(f.Message,(a==null?void 0:a.Msg)||"创建成功")}if(o=="Exp"){const t=new P("BP.WF.Flow",l);yield t.Init();const a=new x("BP.WF.HttpHandler.WF_Admin_CCBPMDesigner");a.AddPara("FlowNo",l);const r=yield a.DoMethodReturnString("ExpFlowTemplete");return N(r,t.getData().Name+".xml","xml"),new F(f.DoNothing,"")}if(o=="ExpAllData"){const t=new x("BP.WF.HttpHandler.WF_Admin_AttrFlow");t.AddFile(this.UploadFile),t.AddPara("FlowNo",l);const a=yield t.DoMethodReturnJson("Imp_ExpAllData");return new F(f.Message,(a==null?void 0:a.Msg)||"导入信息如下:"+a)}if(o=="ImpExcelFlowDB"){const t=new x("BP.WF.HttpHandler.WF_Admin_AttrFlow");t.AddFile(this.UploadFile),t.AddPara("FlowNo",l);const a=yield t.DoMethodReturnJson("Imp_ImpExcelFlowDB");return new F(f.Message,(a==null?void 0:a.Msg)||"导入信息如下:"+a)}if(o=="DTSField"){const t=["WF_Flow","WF_Node","WF_Cond"],a=JSON.parse(yield D.toJSON([])),r=new T;for(const d of t){r.setPKVal(d),(yield r.IsExits())==!1&&(r.Name=d,r.PTable=d,yield r.Insert());const y=new B;yield y.Retrieve("FK_MapData",d);const M=a.filter(h=>h.PTable==d);for(const h of M){const E=(yield D.GetEn(h.No))._enMap.attrs;for(const s of E){if(y.find(_=>_.KeyOfEn===s.Key))continue;const e=new K;e.FK_MapData=d,e.KeyOfEn=s.Key,e.Name=s.Desc,e.MyDataType=s.MyDataType,e.UIContralType=s.UIContralType,s.IsEnum&&(e.LGType=1),s.IsFK&&(e.LGType=2),e.UIWidth=s.UIWidth,e.UIHeight=s.UIHeight,e.MinLen=s.MinLength,e.MaxLen=s.MaxLength,e.UIBindKey=s.UIBindKey,e.UIRefKey=s.UIRefKeyValue,e.UIRefKeyText=s.UIRefKeyText,e.UIVisible=s.UIVisible,e.UIIsEnable=s.UIIsReadonly,e.UIIsLine=s.UIIsLine,e.DefVal=s.DefaultVal,e.MyPK=e.FK_MapData+"_"+e.KeyOfEn,yield e.Insert(),y.push(e)}}}}return new F(f.Message,"检查成功.")})}}export{X as GPN_FlowExpImp};
